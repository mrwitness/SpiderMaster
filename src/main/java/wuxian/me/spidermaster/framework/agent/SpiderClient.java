package wuxian.me.spidermaster.framework.agent;

import io.netty.channel.socket.SocketChannel;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidermaster.framework.agent.connection.BaseConnectionLifecycle;
import wuxian.me.spidermaster.framework.agent.connection.MessageSender;
import wuxian.me.spidermaster.framework.agent.onrequest.OnRpcRequest;
import wuxian.me.spidermaster.framework.agent.onrequest.OnRpcRequestHandler;
import wuxian.me.spidermaster.framework.agent.request.IRpcCallback;
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

    private SocketChannel channel;
    private MessageSender sender = new MessageSender(this);
    private String serverIp;
    private int serverPort;

    public void init() {
        sender.init();

        SpiderConnector.addConnectCallback(new BaseConnectionLifecycle() {

            public void onConnectionBuilded(SocketChannel channel) {

                LogManager.info("SpiderClient connect success");
                SpiderClient.this.channel = channel;  //save channel

                SpiderClient.this.channel.pipeline()
                        .addLast(new RpcResponseHandler(SpiderClient.this))
                        .addLast(new OnRpcRequestHandler(SpiderClient.this));

                sender.onConncetSuccess();
            }

            public void onConnectionClosed(boolean isClient) {

                LogManager.info("SpiderClient.onConnectionClosed isClient:" + isClient);
                sender.onConnectionClosed(isClient);

                if (!isClient) { //若是server主动断开的 那么立即重连
                    asyncConnect(serverIp, serverPort);
                }
            }
        });
    }

    public SocketChannel channel() {
        return channel;
    }

    public void asyncConnect(final String serverIp, final int serverPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;

        LogManager.info("SpiderClient begin to connect ip:" + serverIp + " ip:" + serverPort);

        SpiderConnector.getInstance().connectTo(serverIp, serverPort);
    }

    public boolean isConnected() {
        return SpiderConnector.getInstance().isConnected();
    }

    public void forceDisconnect() {
        SpiderConnector.getInstance().forceDisconnect();
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


    public void onRpcResponse(RpcResponse response) {
        if (sender != null) {
            sender.onRpcResponse(response);
        }
    }

    public void asyncSendMessage(RpcRequest request, Long timeout, IRpcCallback callback) {
        sender.put(request, callback, timeout);
    }

    public void asyncSendMessage(RpcRequest request, IRpcCallback callback) {
        sender.put(request, callback);
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
