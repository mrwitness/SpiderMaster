package wuxian.me.spidermaster;

import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidercommon.model.HttpUrlNode;
import wuxian.me.spidercommon.util.ProcessUtil;
import wuxian.me.spidercommon.util.ShellUtil;
import wuxian.me.spidercommon.util.SignalManager;
import wuxian.me.spidermaster.biz.agent.SpiderAgent;
import wuxian.me.spidermaster.framework.common.SpiderConfig;
import wuxian.me.spidermaster.framework.master.MasterServer;
import wuxian.me.spidermaster.framework.common.StatusEnum;
import wuxian.me.spidermaster.framework.agent.request.IRpcCallback;
import wuxian.me.spidermaster.framework.master.handler.HandlerScanner;
import wuxian.me.spidermaster.framework.rpc.RpcResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuxian on 18/5/2017.
 */
public class Main {

    private SignalManager signalManager = new SignalManager();

    public void start() {

        SpiderConfig.init();
        ShellUtil.init();

        signalManager.init();
        signalManager.registerOnSystemKill(new SignalManager.OnSystemKill() {
            public void onSystemKilled() {
                LogManager.info("onSystemKilled");

                ShellUtil.killProcessBy(ProcessUtil.getCurrentProcessId());
            }
        });

        if (SpiderConfig.spiderMode == 0) {     //agent mode
            startAgent();
        } else {                                //master mode
            startMaster();
        }
    }

    private void startAgent() {
        LogManager.info("startAgent...");

        SpiderAgent.init();

        final SpiderAgent agent = new SpiderAgent();
        agent.start();

        List<Class<?>> classList = new ArrayList<Class<?>>(1);
        classList.add(Main.class);

        List<HttpUrlNode> httpUrlNodeList = new ArrayList<HttpUrlNode>(1);
        HttpUrlNode node = new HttpUrlNode();
        node.baseUrl="hello_world";
        httpUrlNodeList.add(node);

        //Todo:如何实现无侵入式装入业务
        agent.registerToMaster(classList, httpUrlNodeList, new IRpcCallback() {
            @Override
            public void onSent() {

            }

            @Override
            public void onResponseSuccess(RpcResponse response) {
                agent.reportAgentStatus(StatusEnum.SWITCH_PROXY);

                agent.reportAgentStatus(StatusEnum.BLOCKED);
            }

            @Override
            public void onResponseFail() {

            }
        });
    }

    private void startMaster() {
        LogManager.info("startMaster...");
        new MasterServer(SpiderConfig.masterIp, SpiderConfig.masterPort)
                .start();
    }

    public static void main(String[] args) {

        if (true) {
            SpiderConfig.init();
            HandlerScanner.scanAndCollect();
        } else {
            new Main().start();
        }

    }
}
