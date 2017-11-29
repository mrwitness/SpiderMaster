package wuxian.me.spidermaster.benchmark.dubbo;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
 * RpcClient.java
 *
 * @author tony.chenl
 */
public class RpcClient extends AbstractClientRunnable {
    private static String message = null;
    private static int length = 100;

    static {
        length = Integer.valueOf(System.getProperty("message.length", "1000"));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append("a");
        }
        message = sb.toString();
    }

    /**
     * @param targetIP
     * @param targetPort
     * @param clientNums
     * @param rpcTimeout
     * @param barrier
     * @param latch
     * @param startTime
     * @param endTime
     */
    public RpcClient(String targetIP, int targetPort, int clientNums, int rpcTimeout, CyclicBarrier barrier,
                     CountDownLatch latch, long startTime, long endTime) {
        super(targetIP, targetPort, clientNums, rpcTimeout, barrier, latch, startTime, endTime);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    //@Override
    //Todo
    public Object invoke(ServiceFactory serviceFactory) {

        /*
        DemoService demoService = (DemoService) serviceFactory.get(DemoService.class);
        Object result = demoService.sendRequest(message);
        return result;
        */
       /*if(result.equals(message)){
            return result;
        }else{
            throw new RuntimeException("Result Error");
        }*/

        return null;
    }
}
