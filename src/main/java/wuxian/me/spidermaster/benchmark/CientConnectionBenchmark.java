package wuxian.me.spidermaster.benchmark;

import com.sun.istack.internal.Nullable;
import com.sun.tools.internal.xjc.model.nav.EagerNClass;
import io.netty.channel.socket.SocketChannel;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import wuxian.me.spidercommon.util.FileUtil;
import wuxian.me.spidermaster.biz.agent.SpiderAgent;
import wuxian.me.spidermaster.framework.agent.connection.BaseConnectionLifecycle;
import wuxian.me.spidermaster.framework.agent.connection.ConnectionLifecycle;
import wuxian.me.spidermaster.framework.common.SpiderConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

public class CientConnectionBenchmark {

    static {
        PropertyConfigurator.configure(FileUtil.getCurrentPath() + "/conf/log4j.properties");
    }

    static Logger logger = Logger.getLogger(CientConnectionBenchmark.class);

    public Properties properties = ConfigUtils.getProperties();
    private int warmup = 3;

    public static void main(String[] args) throws Exception {
        new CientConnectionBenchmark().run();
    }

    public void run() throws Exception {

        SpiderConfig.initFrom(FileUtil.getCurrentPath() + "/conf/connection_benchmark.properties");

        String host = properties.getProperty("masterIp", "127.0.0.1");
        int port = Integer.parseInt(properties.getProperty("masterPort","3434"));
        int concurrents = Integer.parseInt(properties.getProperty("concurrents","5"));
        int timeout = Integer.parseInt(properties.getProperty("timeout","100"));

        CyclicBarrier barrier = new CyclicBarrier(concurrents);
        CountDownLatch latch = new CountDownLatch(concurrents);
        List<ClientRunnable> runnables = new ArrayList<ClientRunnable>();

        //1000*1000 ns = 1 ms
        long beginTime = System.nanoTime() / 1000L + warmup * 1000 * 1000L;
        for (int i = 0; i < concurrents; i++) {
            ClientRunnable runnable = new ConnectionRunnable(host, port, timeout, barrier
                    , latch, beginTime, beginTime + 30 * 1000 * 1000);   //默认运行30s
            runnables.add(runnable);
        }

        logger.info("startRunnables");
        startRunnables(runnables);
        latch.await();
    }

    private void startRunnables(List<ClientRunnable> runnables) {
        for (int i = 0; i < runnables.size(); i++) {
            final ClientRunnable runnable = runnables.get(i);
            Thread thread = new Thread(runnable, "benchmarkclient-" + i);
            thread.start();
        }
    }

    //@AbstractClientRunnable
    static class ConnectionRunnable implements ClientRunnable {

        private CyclicBarrier barrier;

        private CountDownLatch latch;

        private String host;
        private int port;
        private int timeout;
        private long startTime;
        private long endTime;

        private CountDownLatch waitLatch;

        public ConnectionRunnable(String host, int port, int timeout
                , CyclicBarrier barrier, CountDownLatch countDownLatch
                , long startTime, long endTime) {

            this.host = host;
            this.port = port;
            this.timeout = timeout;
            this.barrier = barrier;
            this.latch = countDownLatch;
            this.startTime = startTime;
            this.endTime = endTime;

            waitLatch = new CountDownLatch(1);
        }

        private boolean running = true;

        @Override
        public void run() {

            try {
                barrier.await();
            } catch (Exception e) {
                // IGNORE
            }
            realRun();
            latch.countDown();
        }

        private void realRun() {
            while (running) {  //过期机制
                long beginTime = System.nanoTime() / 1000L;
                if (beginTime >= endTime) {
                    running = false;
                    break;
                }

                if (beginTime <= startTime) {
                    continue;
                }

                final SpiderAgent agent = new SpiderAgent(host, port);
                agent.addConnectionCallback(new BaseConnectionLifecycle() {
                    @Override
                    public void onConnectionBuilded(SocketChannel channel) {

                        logger.info("onConnectionBuilded,currentThread: " + Thread.currentThread().getName());
                        waitLatch.countDown();

                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {

                        } finally {
                            agent.forDisconnect();
                        }
                    }

                    @Override
                    public void onConnectFail() {
                        logger.info("onConnectionFail,currentThread: " + Thread.currentThread().getName());
                        waitLatch.countDown();
                    }

                    @Override
                    public void onConnectException() {
                        logger.info("onConnectException,currentThread: " + Thread.currentThread().getName());
                        waitLatch.countDown();
                    }

                    @Override
                    public void onConnectionClosed(SocketChannel channel, boolean isClient) {
                        logger.info("onConnectionClosed,currentThread: " + Thread.currentThread().getName());
                    }
                });
                logger.info("agent.connect,currentThread: " + Thread.currentThread().getName());
                agent.connect();

                long currentTime = System.nanoTime() / 1000L;
                if (currentTime >= endTime) {
                    running = false;
                    break;
                }
                try {
                    waitLatch.await((endTime - System.nanoTime()) / 1000, TimeUnit.MICROSECONDS);
                } catch (InterruptedException e) {
                    ;
                } finally {
                    running = false;
                }

            }

        }

        @Nullable
        @Override
        public List<long[]> getResults() {
            return null;
        }
    }

}
