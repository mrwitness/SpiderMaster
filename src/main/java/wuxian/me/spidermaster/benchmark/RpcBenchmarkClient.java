package wuxian.me.spidermaster.benchmark;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
 * 作为run_client.sh的脚本入口
 * 实际读取xxx.properties中的classname作为ClientRunnable的实现类
 * --> 在这里是@SimpleProcessorBenchmarkClientRunnable
 * --> 实际项目里需要 新建一个实现AbstractClientRunnable的类 然后配置到xxx.properties.classname
 */
public class RpcBenchmarkClient extends AbstractBenchmarkClient {

    public static void main(String[] args) throws Exception {
        new RpcBenchmarkClient().run(args);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public ClientRunnable getClientRunnable(String targetIP, int targetPort, int clientNums, int rpcTimeout,
                                            CyclicBarrier barrier, CountDownLatch latch, long startTime, long endTime) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
        String runnable = properties.getProperty("classname");
        Class[] parameterTypes = new Class[]{String.class, int.class, int.class, int.class, CyclicBarrier.class,
                CountDownLatch.class, long.class, long.class};
        Object[] parameters = new Object[]{targetIP, targetPort, clientNums, rpcTimeout, barrier, latch, startTime,
                endTime};
        return (ClientRunnable) Class.forName(runnable).getConstructor(parameterTypes).newInstance(parameters);
    }
}