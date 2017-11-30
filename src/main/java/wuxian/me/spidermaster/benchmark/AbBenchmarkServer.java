package wuxian.me.spidermaster.benchmark;


import java.text.SimpleDateFormat;
import java.util.Date;

//Todo:进行社会主义改造
public abstract class AbBenchmarkServer {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void run(String[] args) throws Exception {

        //1 必须给出5个参数 port端口,最大线程数,responseSize?,transporter?,serialization?
        if (args == null || args.length != 5) {
            throw new IllegalArgumentException(
                    "must give these args: listenPort | maxThreads | responseSize | transporter | serialization");
        }
        int listenPort = Integer.parseInt(args[0]);
        int maxThreads = Integer.parseInt(args[1]);
        final int responseSize = Integer.parseInt(args[2]);
        String transporter = args[3];
        String serialization = args[4];
        System.out.println(dateFormat.format(new Date()) + " ready to start server,listenPort is: " + listenPort
                + ",maxThreads is:" + maxThreads + ",responseSize is:" + responseSize
                + " bytes,transporter is:" + transporter + ",serialization is:" + serialization);

        //Todo
        /*
        StringBuilder url = new StringBuilder();
        url.append("exchange://0.0.0.0:");
        url.append(listenPort);
        url.append("?transporter=");
        url.append(transporter);
        url.append("&serialization=");
        url.append(serialization);
        url.append("&threads=");
        url.append(maxThreads);

        Exchangers.bind(url.toString(), new ExchangeHandlerAdapter() {

            public Object reply(ExchangeChannel channel, Object message) throws RemotingException {
                return new ResponseObject(responseSize); // 发送响应
            }
        });
        */
    }

}
