package wuxian.me.spidermaster.benchmark;

import wuxian.me.spidermaster.framework.master.MasterServer;

import java.text.SimpleDateFormat;
import java.util.Date;

//server benchmark
public class ServerBenchmark {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void run(String[] args) throws Exception {
        if (args == null || args.length != 5) {
            throw new IllegalArgumentException(
                    "must give these args: listenPort | maxThreads | responseSize | transporter | serialization");
        }
        int listenPort = Integer.parseInt(args[0]);
        int maxThreads = Integer.parseInt(args[1]);
        System.out.println(dateFormat.format(new Date()) + " ready to start server,listenPort is: " + listenPort
                + ",maxThreads is:" + maxThreads);

        MasterServer server = new MasterServer("127.0.0.1", listenPort);
        server.setThreadNum(maxThreads);
        server.start();
    }
}
