package wuxian.me.spidermaster.biz.provider;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import wuxian.me.spidercommon.log.LogManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by wuxian on 21/6/2017.
 */
public class ResourcePool {

    private ResourcePool() {
    }

    private static Map<String, Resource> resourcePool = new ConcurrentHashMap<String, Resource>();

    private static Map<String, Lock> lockMap = new ConcurrentHashMap<String, Lock>();

    private static Map<Lock, Condition> conditionMap = new ConcurrentHashMap<Lock, Condition>();

    @Nullable
    public static Resource getResourceBy(String reqId, String resource) {

        if (reqId == null || resource == null || !resourcePool.containsKey(reqId)) {
            return null;
        }

        return resourcePool.get(reqId);

    }

    public static void putResource(String reqId, Resource resource) {
        if (reqId == null || resource == null) {
            return;
        }

        resourcePool.put(reqId, resource);

        Lock lock = getLock(reqId);
        Condition condition = getConditionBy(lock);

        lock.lock();
        try {
            condition.signalAll();
        } catch (IllegalMonitorStateException e) {
            LogManager.error("putResource " + e.getMessage());
        } finally {
            lock.unlock();
        }



    }

    public static void waitForResource(String reqId, String resource) {
        if (reqId == null || resource == null || resource.length() == 0) {
            return;
        }

        Lock lock = getLock(reqId);
        Condition condition = getConditionBy(lock);

        lock.lock();
        try {
            condition.await();
        } catch (InterruptedException e) {
            ;
        } finally {
            lock.unlock();
        }
    }

    public static void waitForResource(String reqId, String resource, long timeout) {
        if (reqId == null || resource == null || resource.length() == 0) {
            return;
        }

        Lock lock = getLock(reqId);
        Condition condition = getConditionBy(lock);

        lock.lock();
        try {
            condition.await(timeout, null);
        } catch (InterruptedException e) {
            ;
        } finally {
            lock.unlock();
        }
    }


    private static Condition getConditionBy(@NotNull Lock lock) {
        if (conditionMap.containsKey(lock)) {
            return conditionMap.get(lock);
        }
        Condition condition = lock.newCondition();
        conditionMap.put(lock, condition);
        return condition;
    }

    private static Lock getLock(@NotNull String id) {

        if (lockMap.containsKey(id)) {
            return lockMap.get(id);
        }
        Lock lock = new ReentrantLock();
        lockMap.put(id, lock);

        return lock;
    }
}
