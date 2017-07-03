package wuxian.me.spidermaster.biz.provider;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import wuxian.me.spidercommon.log.LogManager;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
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

    private static Set<Resource> resourceSet = new HashSet<Resource>();
    private static Set<String> waitSet = new HashSet<String>();

    @Nullable
    public static Resource getResourceFromSet(String resourceName) {
        Resource ret = null;
        synchronized (resourceSet) {
            for (Resource res : resourceSet) {

                if (resourceName.equals(res.name)) {
                    ret = res;
                    resourceSet.remove(res);
                }
            }
        }

        return ret;

    }

    @Nullable
    public static Resource getResourceFromWaitmap(String reqId, String resource) {
        if (reqId == null || resource == null || !resourcePool.containsKey(reqId)) {
            return null;
        }

        Resource ret = resourcePool.get(reqId);
        resourcePool.remove(ret);
        waitSet.remove(reqId);

        return ret;

    }

    public static void putResource(String reqId, Resource resource) {
        if (reqId == null || resource == null) {
            return;
        }

        synchronized (waitSet) {
            if (!waitSet.contains(reqId)) {  //not waited by condition
                resourceSet.add(resource);
                return;
            }
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
        waitForResource(reqId, resource, -1);
    }

    public static void waitForResource(String reqId, String resource, long timeout) {
        if (reqId == null || resource == null || resource.length() == 0) {
            return;
        }

        synchronized (waitSet) {
            if (!waitSet.contains(reqId)) {
                waitSet.add(reqId);
            }
        }

        Lock lock = getLock(reqId);
        Condition condition = getConditionBy(lock);

        lock.lock();
        try {
            if (timeout == -1) {
                condition.await();
            } else {
                condition.await(timeout, TimeUnit.MILLISECONDS);
            }

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
