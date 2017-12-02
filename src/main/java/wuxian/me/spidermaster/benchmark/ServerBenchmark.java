package wuxian.me.spidermaster.benchmark;

import org.apache.log4j.PropertyConfigurator;
import wuxian.me.spidercommon.util.FileUtil;
import wuxian.me.spidermaster.framework.master.MasterServer;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerBenchmark {

    static {
        PropertyConfigurator.configure(FileUtil.getCurrentPath() + "/conf/log4j.properties");
    }

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) throws Exception {
        new ServerBenchmark().run(args);
    }

    public void run(String[] args) throws Exception {
        if (args == null || args.length < 2) {
            throw new IllegalArgumentException(
                    "must give these args: listenPort | maxThreads");
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
