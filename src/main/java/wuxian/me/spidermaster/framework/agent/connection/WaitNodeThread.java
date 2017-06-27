package wuxian.me.spidermaster.framework.agent.connection;

import com.sun.istack.internal.NotNull;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by wuxian on 28/6/2017.
 */
public class WaitNodeThread extends Thread {

    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    private Map<String, Long> waitMap;

    private String currentReqId;

    private long wakeupTime = -1;
    private boolean timeUpdated = false;
    private boolean isWait = false;

    private WaitNodeManager.OnNodeTimeout onNodeTimeout;

    public void setOnTimeout(WaitNodeManager.OnNodeTimeout nodeTimeout) {
        this.onNodeTimeout = nodeTimeout;
    }

    public void onAddNode(String reqId, long wakeup) {

        boolean up = false;

        if (wakeupTime == -1) {
            up = true;
        } else if (wakeup < wakeupTime) {
            up = true;
        }

        if (up) {
            currentReqId = reqId;
            wakeupTime = wakeup;
            timeUpdated = true;

            if (isWait) {
                lock.lock();
                try {
                    condition.signalAll();
                    isWait = false;
                } finally {
                    lock.unlock();
                }
            } else {
                interrupt();
            }
        }
    }

    public void onRemoveNode(String reqId, long wakeup) {

        if (currentReqId != null && currentReqId.equals(reqId)) {
            wakeupTime = -1;
            currentReqId = null;
            //找到当前的最小值

            Iterator<Map.Entry<String, Long>> iterator = waitMap.entrySet().iterator();

            while (iterator.hasNext()) { //多线程下这里会出bug
                if (wakeupTime == -1) {
                    wakeupTime = iterator.next().getValue();
                    currentReqId = iterator.next().getKey();
                } else {
                    if (iterator.next().getValue() < wakeupTime) {
                        wakeupTime = iterator.next().getValue();
                        currentReqId = iterator.next().getKey();
                    }
                }
            }

            if (wakeupTime != -1) {
                timeUpdated = true;

                if (isWait) {
                    lock.lock();
                    try {
                        condition.signalAll();
                        isWait = false;
                    } finally {
                        lock.unlock();
                    }
                } else {
                    interrupt();
                }
            }
        }
    }

    public WaitNodeThread(@NotNull Map<String, Long> waitMap) {
        this.waitMap = waitMap;

        setName("WaitNodeThread");
    }

    @Override
    public void run() {

        if (isInterrupted()) {  //ignore
            ;
        }

        while (true) {

            while (waitMap.isEmpty()) {
                lock.lock();
                try {
                    isWait = true;
                    condition.awaitUninterruptibly();
                } finally {
                    lock.unlock();
                }

            }

            boolean b = trySleepTime(wakeupTime - System.currentTimeMillis());

            if (b) {
                if (onNodeTimeout != null) {
                    onNodeTimeout.onNodeTimeout(currentReqId);
                }

                waitMap.remove(currentReqId);
                onRemoveNode(currentReqId, wakeupTime);
            }
        }
    }

    private boolean trySleepTime(long time) {

        try {
            sleep(time);

            return true;
        } catch (InterruptedException e) {
            if (timeUpdated) {
                timeUpdated = false;
                return trySleepTime(wakeupTime - System.currentTimeMillis());
            } else {
                return false;
            }
        }
    }


}
