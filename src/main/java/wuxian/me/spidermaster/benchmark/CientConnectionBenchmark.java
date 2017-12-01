package wuxian.me.spidermaster.benchmark;

import com.sun.istack.internal.Nullable;
import wuxian.me.spidermaster.biz.agent.SpiderAgent;
import wuxian.me.spidermaster.framework.common.SpiderConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class CientConnectionBenchmark {

    public Properties properties = ConfigUtils.getProperties();

    public void run(String[] args) throws Exception {

        SpiderConfig.init();//Todo: initFrom

        String host = properties.getProperty("serverip", "127.0.0.1");
        int port = Integer.parseInt(properties.getProperty("serverport"), 3434);
        int concurrents = Integer.parseInt(properties.getProperty("concurrents"), 100);
        int timeout = Integer.parseInt(properties.getProperty("timeout"), 100);


        CyclicBarrier barrier = new CyclicBarrier(concurrents);
        CountDownLatch latch = new CountDownLatch(concurrents);
        List<ClientRunnable> runnables = new ArrayList<ClientRunnable>();

        //1000*1000 ns = 1 ms
        long beginTime = System.nanoTime() / 1000L + 30 * 1000 * 1000L;
        for (int i = 0; i < concurrents; i++) {
            ClientRunnable runnable = new ConnectionRunnable(host, port, timeout, barrier
                    , latch, beginTime, beginTime + 30 * 1000 * 1000);   //默认运行30s
            runnables.add(runnable);
        }

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

                long currentTime = System.nanoTime() / 1000L;  //30s warn up
                if (beginTime <= startTime) {
                    continue;
                }

                SpiderAgent agent = new SpiderAgent();
                agent.start(); //Todo:社会主义改造
            }

        }

        @Nullable
        @Override
        public List<long[]> getResults() {
            return null;
        }
    }

}
