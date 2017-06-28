package wuxian.me.spidermaster.framework.agent;

import io.netty.channel.socket.SocketChannel;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidermaster.biz.agent.HeartbeatRequestProducer;
import wuxian.me.spidermaster.framework.agent.connection.MessageSender;
import wuxian.me.spidermaster.framework.agent.onrequest.OnRpcRequest;
import wuxian.me.spidermaster.framework.agent.onrequest.OnRpcRequestHandler;
import wuxian.me.spidermaster.framework.agent.onrequest.ResourceHandler;
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

        ResourceHandler.init();
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

        LogManager.info("SpiderClient begin to connect ip:" + serverIp + " ip:" + serverPort);
        SpiderConnector connector = new SpiderConnector(
                serverIp, serverPort, new IConnectCallback() {
            public void onSuccess(SocketChannel channel) {

                LogManager.info("SpiderClient connect success");
                SpiderClient.this.channel = channel;  //save channel

                SpiderClient.this.channel.pipeline()
                        .addLast(new RpcResponseHandler(SpiderClient.this))
                        .addLast(new OnRpcRequestHandler(SpiderClient.this));
                connected = true;

                sender.onConncetSuccess();
            }

            public void onFail() {

                connected = false;
                onSocketClosed(false);
            }

            public void onException() {
                connected = false;

            }

            public void onClosed() {
                connected = false;

                onSocketClosed(false);
            }
        });
        connectThread = new Thread(connector);
        connectThread.setName("ConnectionThread");
        connectThread.start();

    }

    public boolean isConnected() {
        return connected;
    }

    private void onSocketClosed(boolean isClient) {
        connected = false;
        if (heartbeatThread != null) {
            heartbeatThread.interrupt();
        }

        sender.onForceDisconnect();

        if (!isClient) {
            asyncConnect(serverIp, serverPort);  //server主动断的 那么在断线之后 立刻进行重新连接
        }

    }


    public void forceDisconnect() {
        channel.close().syncUninterruptibly();

        onSocketClosed(true);
    }

    public Object onReceiveMessage(RpcRequest request) {
        LogManager.info("onReceiveMessage: request:" + request.methodName);
        if (request == null) {
            return null;
        }

        if (!requestMap.containsKey(request.methodName)) {
            LogManager.error("can't handle request");
            return null;
        }

        OnRpcRequest onRpcRequest = requestMap.get(request.methodName);
        LogManager.info("find handler: " + onRpcRequest.toString());

        return onRpcRequest.onRpcRequest(request);

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
                        sleep(5 * 1000);
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

    //with timeout
    public void asyncSendMessage(RpcRequest request, Long timeout, IRpcCallback callback) {
        sender.put(request, callback, timeout);
    }

    public void asyncSendMessage(RpcRequest request, IRpcCallback callback) {
        sender.put(request, callback);
    }

    public void onDisconnected() {
        connected = false;
    }

    private static Map<String, OnRpcRequest> requestMap = new HashMap<String, OnRpcRequest>();

    public static void registerMessageNotify(RpcRequest request, OnRpcRequest onRpcRequest) {
        if (request == null || onRpcRequest == null) {
            return;
        }

        if (requestMap.containsKey(request.methodName)) {
            return;
        }
        requestMap.put(request.methodName, onRpcRequest);
    }

}
