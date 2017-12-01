package wuxian.me.spidermaster.framework.agent;

import io.netty.channel.socket.SocketChannel;
import org.apache.log4j.Logger;
import wuxian.me.spidermaster.framework.agent.connection.BaseConnectionLifecycle;
import wuxian.me.spidermaster.framework.agent.connection.MessageSender;
import wuxian.me.spidermaster.framework.agent.request.IRpcCallback;
import wuxian.me.spidermaster.framework.agent.connection.SpiderConnector;
import wuxian.me.spidermaster.framework.agent.request.RpcResponseHandler;
import wuxian.me.spidermaster.framework.master.handler.HandlerExcepiton;
import wuxian.me.spidermaster.framework.master.handler.IRpcRequestHandler;
import wuxian.me.spidermaster.framework.rpc.RpcRequest;
import wuxian.me.spidermaster.framework.rpc.RpcResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wuxian on 26/5/2017.
 */
public class SpiderClient implements IClient {
    static Logger logger = Logger.getLogger("client");
    private SocketChannel channel;
    private MessageSender sender = new MessageSender(this);
    private String serverIp;
    private int serverPort;

    public void init() {
        logger.info("init rpc message sender");
        sender.init();

        SpiderConnector.addConnectCallback(new BaseConnectionLifecycle() {
            public void onConnectionBuilded(SocketChannel channel) {
                logger.info("spider client success connect to server,connection: " + channel.toString());
                SpiderClient.this.channel = channel;  //save channel

                SpiderClient.this.channel.pipeline()
                        .addLast(new RpcResponseHandler(SpiderClient.this))
                        .addLast(new OnRpcRequestHandler(SpiderClient.this));

                sender.onConncetSuccess();
            }

            public void onConnectionClosed(SocketChannel channel, boolean isClient) {
                logger.info("connection: " + channel.toString() + " closed by" + (isClient ? "client" : "server"));
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

    //Fixme: 这里的SpiderConnector竟然是singleton模式。。。
    public void asyncConnect(final String serverIp, final int serverPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        SpiderConnector.getInstance().connectTo(serverIp, serverPort);
    }

    public boolean isConnected() {
        return SpiderConnector.getInstance().isConnected();
    }

    public void forceDisconnect() {
        SpiderConnector.getInstance().forceDisconnect();
    }

    public Object onReceiveMessage(RpcRequest request) {
        if (request == null) {
            return null;
        }

        if (!requestMap.containsKey(request.methodName)) {
            logger.error("fail to find handler to handle the request");
            return null;
        }

        IRpcRequestHandler onRpcRequest = requestMap.get(request.methodName);
        //logger.info("find handler: " + onRpcRequest.toString());

        try {
            return onRpcRequest.handleRequest(request, null);
        } catch (HandlerExcepiton e) {
            return null;
        }
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

    private static Map<String, IRpcRequestHandler> requestMap = new HashMap<String, IRpcRequestHandler>();

    public static void registerRpcHandler(RpcRequest request, IRpcRequestHandler onRpcRequest) {
        if (request == null || onRpcRequest == null) {
            return;
        }
        registerRpcHandler(request.methodName, onRpcRequest);
    }

    public static void registerRpcHandler(String method, IRpcRequestHandler onRpcRequest) {
        if (method == null || method.length() == 0 || onRpcRequest == null) {
            return;
        }
        if (requestMap.containsKey(method)) {
            return;
        }
        requestMap.put(method, onRpcRequest);
    }
}
