package wuxian.me.spidermaster;

import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidercommon.model.HttpUrlNode;
import wuxian.me.spidercommon.util.ProcessUtil;
import wuxian.me.spidercommon.util.ShellUtil;
import wuxian.me.spidercommon.util.SignalManager;
import wuxian.me.spidermaster.biz.agent.SpiderAgent;
import wuxian.me.spidermaster.biz.control.AgentRecorder;
import wuxian.me.spidermaster.biz.control.StatusEnum;
import wuxian.me.spidermaster.framework.common.SpiderConfig;
import wuxian.me.spidermaster.framework.master.MasterServer;
import wuxian.me.spidermaster.framework.agent.request.IRpcCallback;
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

        if (SpiderConfig.spiderMode == 0) {
            startAgent();

        } else if (SpiderConfig.spiderMode == 1) {
            startMaster();

        }

        SignalManager.registerOnSystemKill(new SignalManager.OnSystemKill() {

            public void onSystemKilled() {
                LogManager.info("onSystemKilled");

                ShellUtil.killProcessBy(ProcessUtil.getCurrentProcessId());
            }
        });
    }

    private void startAgent() {
        LogManager.info("startAgent...");

        SpiderAgent.init();

        final SpiderAgent agent = new SpiderAgent();
        agent.start();

        SignalManager.registerOnSystemKill(new SignalManager.OnSystemKill() {
            @Override
            public void onSystemKilled() {
                agent.forDisconnect();
            }
        });

        List<Class<?>> classList = new ArrayList<Class<?>>(1);
        classList.add(Main.class);

        List<HttpUrlNode> httpUrlNodeList = new ArrayList<HttpUrlNode>(1);
        HttpUrlNode node = new HttpUrlNode();
        node.baseUrl="hello_world";
        httpUrlNodeList.add(node);

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

            @Override
            public void onTimeout() {

            }
        });
    }

    private void startMaster() {
        LogManager.info("startMaster...");

        final MasterServer server = new MasterServer(SpiderConfig.masterIp, SpiderConfig.masterPort, new MasterServer.ServerLifecycle() {
            @Override
            public void onBindSuccess(String ip, int port) {
                AgentRecorder.startPrintThread();
            }

            @Override
            public void onShutdown() {

            }
        });

        SignalManager.registerOnSystemKill(new SignalManager.OnSystemKill() {
            @Override
            public void onSystemKilled() {
                server.forceClose();
            }
        });

        server.start();

    }


    public static void main(String[] args) {
        new Main().start();
    }
}
