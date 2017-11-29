package wuxian.me.spidermaster;

import org.junit.Test;
import wuxian.me.spidermaster.biz.agent.SpiderAgent;
import wuxian.me.spidermaster.biz.control.Spider;

public class ConnectionTest {

    //Todo
    @Test
    public void testConnectionWithNoLimit() {

        SpiderAgent.init();
        while (true) {

            for(int i=0;i<50;i++) {
                new SpiderAgent().start();
            }

            try{
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                //ignore
            }
        }

    }



}
