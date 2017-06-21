package wuxian.me.spidermaster.framework.agent;

import io.netty.channel.socket.SocketChannel;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidermaster.biz.agent.HeartbeatRequestProducer;
import wuxian.me.spidermaster.framework.agent.connection.MessageSender;
import wuxian.me.spidermaster.framework.agent.onrequest.OnRpcRequest;
import wuxian.me.spidermaster.framework.agent.onrequest.OnRpcRequestHandler;
import wuxian.me.spidermaster.framework.agent.request.DefaultCallback;
import wuxian.me.spidermaster.framework.agent.request.IRpcCallback;
import wuxian.me.spidermaster.framework.agent.connection.IConnectCallback;
import wuxian.me.spidermaster.framework.agent.connection.SpiderConnector;
import wuxian.me.spidermaster.framework.agent.request.RpcResponseHandler;
import wuxian.me.spidermaster.framework.rpc.RpcRequest;
import wuxian.me.spidermaster.framework.rpc.RpcResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wuxian on 26/5/2017.
 */
public class SpiderClient implements IClient {

    private Thread connectThread;
    private boolean connected = false;
    private SocketChannel channel;
    private MessageSender sender = new MessageSender(this);
    private Thread heartbeatThread;
    private String serverIp;
    private int serverPort;

    public void init() {
        sender.init();
        ProviderScan.scanAndCollect();
    }

    public SocketChannel channel() {
        return channel;
    }

    public void asyncConnect(final String serverIp, final int serverPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;

        if (connected) {
            return;
        }

        SpiderConnector connector = new SpiderConnector(
                serverIp, serverPort, new IConnectCallback() {
            public void onSuccess(SocketChannel channel) {
                SpiderClient.this.channel = channel;  //save channel

                SpiderClient.this.channel.pipeline()
                        .addLast(new RpcResponseHandler(SpiderClient.this))
                        .addLast(new OnRpcRequestHandler(SpiderClient.this));
                connected = true;

                sender.onConncetSuccess();
                LogManager.info("connect success,channel " + channel);
            }

            public void onFail() {
                LogManager.info("connect fail");

                connected = false;
                onSocketClosed();
            }

            public void onException() {
                connected = false;

            }

            public void onClosed() {
                connected = false;

                onSocketClosed();
            }
        });
        connectThread = new Thread(connector);
        connectThread.setName("ConnectionThread");
        connectThread.start();

    }

    public boolean isConnected() {
        return connected;
    }

    private void onSocketClosed() {
        connected = false;
        if (heartbeatThread != null) {
            heartbeatThread.interrupt();
        }
        asyncConnect(serverIp, serverPort);  //断线之后 立刻连接
    }

    //Todo:什么情况下需要客户端主动调用关闭连接？
    public void doDisconnectFromServer() {
        channel.close().syncUninterruptibly();

        onSocketClosed();
    }

    public void onReceiveMessage(RpcRequest request) {
        LogManager.info("onReceiveMessage: " + Thread.currentThread().getName());
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

                if (isInterrupted()) {
                    return;
                }

                while (true) {
                    if (sender != null) {
                        sender.put(new HeartbeatRequestProducer().produce()
                                , DefaultCallback.ins());
                    }
                    try {
                        sleep(15 * 1000);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        };

        heartbeatThread.setName("heartbeatThread");
        heartbeatThread.start();
    }

    public void onRpcResponse(RpcResponse response) {

        LogManager.info("SpiderClient.onRpcResponse response: " + response.toString());
        LogManager.info("currentThread: " + Thread.currentThread().getName());

        if (sender != null) {
            sender.onRpcResponse(response);

            RpcRequest req = sender.getRequestBy(response.requestId);
            if (req != null) {
                if (req.methodName.equals(registerRpc)) { //只有注册成功了才发起心跳
                    startHeartbeatThread();
                }
            }
        }
    }

    private final String registerRpc = new HeartbeatRequestProducer().produce().methodName;

    //Todo
    public void asyncSendMessage(RpcRequest request, long timeout, IRpcCallback callback) {
        ;
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
