package wuxian.me.spidermaster.framework.agent.request;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by wuxian on 11/6/2017.
 * <p>
 * 每一个client的id是独立的,因此这里没有碰撞的危险。
 */
public class RequestIdGen {

    private static AtomicLong id = new AtomicLong(0);

    private RequestIdGen() {
    }

    public static long genId() {

        return id.incrementAndGet();
    }
}
