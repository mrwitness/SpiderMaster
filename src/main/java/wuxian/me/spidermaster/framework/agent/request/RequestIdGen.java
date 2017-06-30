package wuxian.me.spidermaster.framework.agent.request;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by wuxian on 11/6/2017.
 */
public class RequestIdGen {

    private static AtomicLong id = new AtomicLong(0);

    private RequestIdGen() {
    }

    public static String genId() {
        return getIdgenPre() + "_" + id.incrementAndGet();
    }


    public static String getIdgenPre() {

        String time = String.valueOf(System.currentTimeMillis());
        int len = time.length();

        return time.substring(len - 6);
    }


}
