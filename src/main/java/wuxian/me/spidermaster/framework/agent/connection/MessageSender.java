package wuxian.me.spidermaster.framework.agent.connection;


import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import org.apache.log4j.Logger;
import wuxian.me.spidermaster.framework.agent.IClient;
import wuxian.me.spidermaster.framework.agent.request.IRpcCallback;
import wuxian.me.spidermaster.framework.rpc.RpcRequest;
import wuxian.me.spidermaster.framework.rpc.RpcResponse;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created by wuxian on 10/6/2017.
 */
public class MessageSender {

    static Logger logger = Logger.getLogger("client");
    private Map<String, IRpcCallback> callbackMap = new ConcurrentHashMap<String, IRpcCallback>();
    private Queue<RpcRequest> requestQueue = new LinkedBlockingQueue<RpcRequest>();
    private Map<String, RpcRequest> requestMap = new ConcurrentHashMap<String, RpcRequest>();
    private IClient client;

    private Thread dispatchThread;

    private WaitNodeManager waitNodeManager;

    public MessageSender(@NotNull IClient client) {

        this.client = client;
    }

    public RpcRequest getRequestBy(String key) {
        if (key == null || key.length() == 0) {
            return null;
        }
        return requestMap.get(key);
    }

    public void onConncetSuccess() {
        synchronized (dispatchThread) {
            dispatchThread.notifyAll();
        }
    }

    public void onConnectionClosed(boolean isClient) {
        ;
    }

    private boolean canPoll() {

        if (!client.isConnected() || client.channel() == null || client.channel().isShutdown()) {
            return false;
        }

        if (requestQueue.isEmpty()) {
            return false;
        }

        return true;
    }

    public void init() {
        dispatchThread = new Thread() {
            @Override
            public void run() {

                while (true) {

                    while (!canPoll()) {
                        synchronized (this) {
                            try {
                                wait();
                            } catch (InterruptedException e) {
                                ;
                            }
                        }
                    }

                    RpcRequest rpcRequest = requestQueue.poll();
                    logger.info("RPC Request send: " + rpcRequest.toString());
                    try {
                        client.channel().writeAndFlush(rpcRequest).await();
                    } catch (InterruptedException e) {
                        logger.error("sender InterruptedExcepiton");
                    }
                }


            }
        };
        dispatchThread.setName("dispatchRpcRequestThread");

        logger.debug("start dispatch rpc request thread ");
        dispatchThread.start();

        waitNodeManager = new WaitNodeManager(new WaitNodeManager.OnNodeTimeout() {
            @Override
            public void onNodeTimeout(String reqId) {
                if (callbackMap.containsKey(reqId)) {
                    callbackMap.get(reqId).onTimeout();
                }
            }
        });

        logger.debug("init waitNodeManager");
        waitNodeManager.init();
    }

    public void put(RpcRequest request, @Nullable IRpcCallback onRpcReques, Long timeout) {
        if (request == null) {
            return;
        }

        if (callbackMap.containsKey(request.requestId)) {
            return;
        }

        boolean notify = (client != null && client.channel() != null && requestQueue.isEmpty());

        requestQueue.add(request);
        requestMap.put(request.requestId, request);

        if (onRpcReques != null) {

            callbackMap.put(request.requestId, onRpcReques);

        }

        if (timeout != null) {
            waitNodeManager.addWaitNode(request.requestId, timeout);
        }

        if (notify) {
            synchronized (dispatchThread) {
                dispatchThread.notifyAll();
            }
        }
    }


    public void put(RpcRequest request, @Nullable IRpcCallback onRpcReques) {
        put(request, onRpcReques, null);
    }

    public void onRpcResponse(RpcResponse response) {
        if (response == null || response.requestId == null || response.requestId.length() == 0) {
            return;
        }

        waitNodeManager.removeWaitNode(response.requestId);

        if (callbackMap.containsKey(response.requestId)) {
            callbackMap.get(response.requestId).onResponseSuccess(response);
        }
    }
}
