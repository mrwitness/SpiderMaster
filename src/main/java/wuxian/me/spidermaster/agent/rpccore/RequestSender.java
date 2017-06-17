package wuxian.me.spidermaster.agent.rpccore;


import com.sun.istack.internal.NotNull;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidermaster.agent.IClient;
import wuxian.me.spidermaster.rpc.IRpcCallback;
import wuxian.me.spidermaster.rpc.RpcRequest;
import wuxian.me.spidermaster.rpc.RpcResponse;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by wuxian on 10/6/2017.
 */
public class RequestSender {

    private Map<String, IRpcCallback> requestMap = new ConcurrentHashMap<String, IRpcCallback>();
    private Queue<RpcRequest> requestQueue = new LinkedBlockingQueue<RpcRequest>();
    private IClient client;

    private Thread dispatchThread;

    public RequestSender(@NotNull IClient client) {

        this.client = client;
    }

    public void onConncet() {
        synchronized (dispatchThread) {
            dispatchThread.notifyAll();
        }
    }

    public void init() {
        dispatchThread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    while (true) {
                        LogManager.info("dispatchThread.run");

                        boolean canPoll = (client != null && client.channel() != null && !requestQueue.isEmpty());
                        if (!canPoll) {
                            synchronized (this) {
                                try {
                                    wait();
                                } catch (InterruptedException e) {
                                    ;
                                }
                            }
                        }

                        RpcRequest rpcRequest = requestQueue.poll();
                        LogManager.info("before send rpc request... " + rpcRequest.toString());
                        try {
                            LogManager.info("before flush");
                            client.channel().writeAndFlush(rpcRequest).await();
                            LogManager.info("finish flush");
                        } catch (InterruptedException e) {
                            LogManager.error("sender InterruptedExcepiton");
                        }
                    }
                }

            }
        };
        dispatchThread.setName("dispatchRpcRequestThread");
        dispatchThread.start();
    }


    public void put(RpcRequest request, IRpcCallback onRpcReques) {

        LogManager.info("RequestSender.put");
        if (request == null) {
            return;
        }

        if (requestMap.containsKey(request.requestId)) {
            return;
        }
        requestMap.put(request.requestId, onRpcReques);

        boolean notify = (client != null && client.channel() != null && requestQueue.isEmpty());

        requestQueue.add(request);
        if (notify) {
            synchronized (dispatchThread) {
                dispatchThread.notifyAll();
            }
        }
    }

    public void onRpcResponse(RpcResponse response) {
        if (response == null || response.requestId == null || response.requestId.length() == 0) {
            return;
        }

        if (!requestMap.containsKey(response.requestId)) {
            return;
        }


        requestMap.get(response.requestId).onResponseSuccess(response);
    }
}
