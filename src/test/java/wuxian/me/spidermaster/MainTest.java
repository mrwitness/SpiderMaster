package wuxian.me.spidermaster;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidermaster.biz.agent.HeartbeatRequestProducer;
import wuxian.me.spidermaster.biz.provider.Resource;
import wuxian.me.spidermaster.framework.agent.connection.WaitNodeManager;
import wuxian.me.spidermaster.framework.agent.request.RequestIdGen;
import wuxian.me.spidermaster.framework.common.GsonProvider;
import wuxian.me.spidermaster.framework.rpc.RpcDecoder;
import wuxian.me.spidermaster.framework.rpc.RpcEncoder;
import wuxian.me.spidermaster.framework.rpc.RpcRequest;
import wuxian.me.spidermaster.framework.rpc.RpcResponse;

import javax.management.AttributeList;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by wuxian on 28/6/2017.
 */
public class MainTest {

    @Test
    public void testSerialize() throws Exception {
        List<Class<?>> classList = new ArrayList<Class<?>>();
        classList.add(RpcResponse.class);
        classList.add(RpcRequest.class);

        RpcEncoder encoder = new RpcEncoder(classList);
        RpcDecoder decoder = new RpcDecoder(classList);

        RpcRequest rpcRequest = new HeartbeatRequestProducer().produce();
        ByteBuf buf = Unpooled.buffer();
        encoder.encode(null, rpcRequest, buf);

        //LogManager.info(new String(buf.array()));
        List<Object> list = new ArrayList<Object>();
        //decoder.decode(null,buf,list);
        //buf.clear();

        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.requestId = rpcRequest.requestId;
        Resource resource = new Resource();
        resource.name = "proxy";
        resource.data = "123456";
        rpcResponse.result = GsonProvider.gson().toJson(resource);

        encoder.encode(null, rpcResponse, buf);
        decoder.decode(null, buf, list);

    }

    @Test
    public void testIDGen() {
        System.out.println(RequestIdGen.getIdgenPre());
    }

    WaitNodeManager manager;

    @Test
    public void testWaitNode() {

        manager = new WaitNodeManager(new WaitNodeManager.OnNodeTimeout() {
            @Override
            public void onNodeTimeout(String reqId) {
                LogManager.info("onNodeTimeout  reqId:" + reqId);
            }
        });

        manager.init();

        for (int i = 0; i < 5; i++) {
            new AddNodeThread(manager, i).start();

            Random random = new Random();
            long sleep = random.nextInt(1000);

            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {

            }
        }

        try {
            Thread.sleep(8);
        } catch (InterruptedException e) {

        }

        for (int i = 5; i < 9; i++) {
            new AddNodeThread(manager, i).start();

            Random random = new Random();
            long sleep = random.nextInt(1000);

            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {

            }
        }

        while (true) {

        }

    }

    public static class AddNodeThread extends Thread {

        private WaitNodeManager manager;
        private int index;

        public AddNodeThread(WaitNodeManager manager, int index) {
            this.manager = manager;
            this.index = index;
        }

        public void run() {
            Random random = new Random();
            long timeout = (random.nextInt(8) + 1) * 1000;
            manager.addWaitNode(String.valueOf(index), timeout);
        }
    }

}