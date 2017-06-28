package wuxian.me.spidermaster.framework.agent.connection;

import com.sun.istack.internal.NotNull;
import wuxian.me.spidercommon.log.LogManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by wuxian on 28/6/2017.
 * Used to clear timeout node in waitmap.
 */
public class WaitNodeThread extends Thread {

    private SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    private Map<String, Long> waitMap;

    private AtomicLong currentReqId = new AtomicLong(-1);

    private AtomicLong wakeupTime = new AtomicLong(-1);
    private AtomicBoolean timeUpdated = new AtomicBoolean(false);
    private AtomicBoolean isWait = new AtomicBoolean(false);

    private WaitNodeManager.OnNodeTimeout onNodeTimeout;

    public void setOnTimeout(WaitNodeManager.OnNodeTimeout nodeTimeout) {
        this.onNodeTimeout = nodeTimeout;
    }

    private String formatUnitTime(long time) {
        return sdf.format(new Date(time));
    }

    public void onAddNode(String reqId, long wakeup) {
        boolean up = false;
        synchronized (waitMap) {
            LogManager.info("onAddNode reqId:" + reqId + " wakeup: " + formatUnitTime(wakeup));

            if (wakeupTime.get() == -1) {
                up = true;
            } else if (wakeup < wakeupTime.get()) {
                up = true;
            }

            if (up) {
                currentReqId = new AtomicLong(Long.parseLong(reqId));
                wakeupTime = new AtomicLong(wakeup);
                timeUpdated = new AtomicBoolean(true);

                if (isWait.get()) {
                    lock.lock();
                    try {
                        condition.signalAll();
                        isWait = new AtomicBoolean(false);
                    } finally {
                        lock.unlock();
                    }
                } else {
                    interrupt();
                }
            }
        }

    }

    public void onRemoveNode(String reqId, long wakeup) {
        LogManager.info("onRemoveNode reqId:" + reqId + " wakeup:" + wakeup + " " + formatUnitTime(wakeup));
        if (Long.parseLong(reqId) == currentReqId.get()) {

            synchronized (waitMap) {

                if (Long.parseLong(reqId) == currentReqId.get()) {
                    waitMap.remove(reqId);

                    currentReqId = new AtomicLong(-1);
                    wakeupTime = new AtomicLong(-1);

                    Iterator<Map.Entry<String, Long>> iterator = waitMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        if (wakeupTime.get() == -1) {
                            Map.Entry<String, Long> entry = iterator.next();
                            wakeupTime = new AtomicLong(entry.getValue());
                            currentReqId = new AtomicLong(Long.parseLong(entry.getKey()));
                        } else {
                            Map.Entry<String, Long> entry = iterator.next();
                            if (entry.getValue() < wakeupTime.get()) {
                                wakeupTime = new AtomicLong(entry.getValue());
                                currentReqId = new AtomicLong(Long.parseLong(entry.getKey()));
                            }
                        }
                    }

                    if (wakeupTime.get() != -1) {
                        timeUpdated = new AtomicBoolean(true);

                        if (isWait.get()) {
                            lock.lock();
                            try {
                                condition.signalAll();
                                isWait = new AtomicBoolean(false);
                            } finally {
                                lock.unlock();
                            }
                        } else {
                            interrupt();
                        }
                    }
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

            boolean empty = false;

            synchronized (waitMap) {
                empty = waitMap.isEmpty();
            }
            while (empty) {
                lock.lock();
                LogManager.info("waitMap empty,wait");
                try {
                    isWait = new AtomicBoolean(true);
                    condition.awaitUninterruptibly();
                } finally {
                    lock.unlock();

                    synchronized (waitMap) {
                        empty = waitMap.isEmpty();
                    }
                }
            }

            boolean b = trySleepTime(wakeupTime.get() - System.currentTimeMillis());

            if (b) {
                if (onNodeTimeout != null) {
                    onNodeTimeout.onNodeTimeout(currentReqId.toString());
                }
                synchronized (waitMap) {
                    waitMap.remove(currentReqId);  //not working???
                    onRemoveNode(currentReqId.toString(), wakeupTime.get());
                }
            } else {
                clearNodeByTime(wakeupTime.get());
            }
        }
    }

    //in case there are node not being cleared
    private void clearNodeByTime(long time) {
        synchronized (waitMap) {
            Iterator<Map.Entry<String, Long>> iterator = waitMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Long> entry = iterator.next();
                long wakeupTime = entry.getValue();
                String reqId = entry.getKey();

                if (wakeupTime <= time) {
                    waitMap.remove(reqId);
                    if (onNodeTimeout != null) {
                        onNodeTimeout.onNodeTimeout(reqId);
                    }
                }
            }
        }
    }

    private boolean trySleepTime(long time) {

        if (time <= 0) {
            return false;
        }
        try {
            sleep(time);

            return true;
        } catch (InterruptedException e) {
            if (timeUpdated.get()) {
                timeUpdated = new AtomicBoolean(false);
                return trySleepTime(wakeupTime.get() - System.currentTimeMillis());
            } else {
                return false;
            }
        }
    }


}
