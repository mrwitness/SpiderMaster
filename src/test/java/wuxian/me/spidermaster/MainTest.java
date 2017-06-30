package wuxian.me.spidermaster;

import org.junit.Test;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidermaster.framework.agent.connection.WaitNodeManager;
import wuxian.me.spidermaster.framework.agent.request.RequestIdGen;

import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by wuxian on 28/6/2017.
 */
public class MainTest {

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