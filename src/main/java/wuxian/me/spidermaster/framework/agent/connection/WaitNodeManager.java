package wuxian.me.spidermaster.framework.agent.connection;

import com.sun.istack.internal.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wuxian on 28/6/2017.
 */
public class WaitNodeManager {

    private Map<String, Long> waitMap = new HashMap<String, Long>();
    private WaitNodeThread waitNodeThread;

    private OnNodeTimeout onNodeTimeout;

    public WaitNodeManager() {
        this(null);
    }

    public WaitNodeManager(@Nullable OnNodeTimeout onNodeTimeout) {
        this.onNodeTimeout = onNodeTimeout;
    }

    public void init() {
        waitNodeThread = new WaitNodeThread(waitMap);
        if (this.onNodeTimeout != null) {
            waitNodeThread.setOnTimeout(onNodeTimeout);
        }

        waitNodeThread.start();
    }

    public void addWaitNode(String requestId, long timeout) {

        long wakeup = System.currentTimeMillis() + timeout;

        synchronized (waitMap) {
            waitMap.put(requestId, wakeup);
            waitNodeThread.onAddNode(requestId, wakeup);
        }


    }

    public void removeWaitNode(String requestId) {
        if (requestId == null) {
            return;
        }

        Long timeout = null;

        synchronized (waitMap) {
            if (waitMap.containsKey(requestId)) {
                timeout = waitMap.get(requestId);
                waitMap.remove(requestId);
            }

            if (timeout != null) {
                waitNodeThread.onRemoveNode(requestId, timeout);
            }
        }

    }

    public interface OnNodeTimeout {
        void onNodeTimeout(String reqId);
    }
}
