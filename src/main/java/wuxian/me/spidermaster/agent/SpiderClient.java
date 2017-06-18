package wuxian.me.spidermaster.agent;

import io.netty.channel.socket.SocketChannel;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidermaster.agent.biz.HeartbeatRequestProducer;
import wuxian.me.spidermaster.agent.biz.ReportStatusRequestProducer;
import wuxian.me.spidermaster.agent.connector.IConnectCallback;
import wuxian.me.spidermaster.agent.connector.SpiderConnector;
import wuxian.me.spidermaster.agent.rpccore.OnRpcRequest;
import wuxian.me.spidermaster.agent.rpccore.RequestSender;
import wuxian.me.spidermaster.rpc.DefaultCallback;
import wuxian.me.spidermaster.rpc.IRpcCallback;
import wuxian.me.spidermaster.rpc.RpcRequest;
import wuxian.me.spidermaster.rpc.RpcResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wuxian on 26/5/2017.
 */
public class SpiderClient implements IClient {

    private Thread connectThread;
    private boolean connected = false;
    private SocketChannel channel;
    private RequestSender sender = new RequestSender(this);
    private Thread heartbeatThread;

    public void init() {
        sender.init();
    }

    public SocketChannel channel() {
        return channel;
    }

    public void asyncConnect(final String serverIp, final int serverPort) {
        if (connected) {
            return;
        }

        SpiderConnector connector = new SpiderConnector(
                serverIp, serverPort, this, new IConnectCallback() {
            public void onSuccess(SocketChannel channel) {
                SpiderClient.this.channel = channel;  //save channel
                connected = true;

                sender.onConncet();
                LogManager.info("connect success,channel " + channel);
            }

            public void onFail() {
                LogManager.info("connect fail");

            }

            public void onException() {

            }

            public void onClosed() {

            }
        });
        connectThread = new Thread(connector);
        connectThread.setName("ConnectionThread");
        connectThread.start();

    }

    public boolean isConnected() {
        return connected;
    }

    //Todo:
    public void disconnectFromServer() {

    }

    public void onMessage(RpcRequest request) {
        LogManager.info("onMessage: " + Thread.currentThread().getName());
        if (request == null) {
            return;
        }

        if (!requestMap.containsKey(request)) {
            return;
        }
        requestMap.get(request).onRpcRequest(request);
    }

    private void startHeartbeatThread() {

        heartbeatThread = new Thread() {
            @Override
            public void run() {

                while (true) {
                    if (sender != null) {
                        sender.put(new HeartbeatRequestProducer().produce()
                                , DefaultCallback.ins());
                    }

                    try {
                        sleep(15 * 1000);
                    } catch (InterruptedException e) {
                        ;
                    }
                }
            }
        };

        heartbeatThread.setName("heartbeatThread");
        heartbeatThread.start();
    }

    public void onRpcResponse(RpcResponse response) {

        startHeartbeatThread();

        LogManager.info("SpiderClient.onRpcResponse response: " + response.toString());
        LogManager.info("currentThread: " + Thread.currentThread().getName());

        if (sender != null) {
            sender.onRpcResponse(response);
        }
    }

    public void asyncSendMessage(RpcRequest request, IRpcCallback callback) {
        sender.put(request, callback);
    }

    public void onDisconnectByServer() {
        connected = false;
    }

    private static Map<RpcRequest, OnRpcRequest> requestMap = new HashMap<RpcRequest, OnRpcRequest>();

    //Todo:用于接受master的指令 目前并无相关需求
    //业务层只需实现OnRpcRequest接口并注册即可
    public static void registerMessageNotify(RpcRequest request, OnRpcRequest onRpcRequest) {
        if (request == null || onRpcRequest == null) {
            return;
        }

        if (requestMap.containsKey(request)) {
            return;
        }
        requestMap.put(request, onRpcRequest);
    }

}
