package wuxian.me.spidermaster.benchmark;

import java.text.SimpleDateFormat;
import java.util.Date;

//Todo:进行社会主义改造
public class ProducerBenchmark {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void run(String[] args) throws Exception {

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
