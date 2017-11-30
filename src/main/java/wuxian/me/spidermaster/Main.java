package wuxian.me.spidermaster;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import wuxian.me.spidercommon.model.HttpUrlNode;
import wuxian.me.spidercommon.util.FileUtil;
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

    static Logger logger = Logger.getLogger(Main.class);

    private SignalManager signalManager = new SignalManager();

    public void start() {

        PropertyConfigurator.configure(FileUtil.getCurrentPath() + "/conf/log4j.properties");

        logger.info("init SpiderConfig");
        SpiderConfig.init();

        logger.info("init shellutil");
        ShellUtil.init();

        logger.info("init signal manager");
        signalManager.init();

        if (SpiderConfig.spiderMode == 0) {
            startAgent();

        } else if (SpiderConfig.spiderMode == 1) {
            startServer();

        }

        SignalManager.registerOnSystemKill(new SignalManager.OnSystemKill() {

            public void onSystemKilled() {
                logger.info("onSystemKilled");

                ShellUtil.killProcessBy(ProcessUtil.getCurrentProcessId());
            }
        });
    }

    private void startAgent() {
        logger.info("startAgent...");

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

    private void startServer() {
        logger.info("start master server...");

        final MasterServer server = new MasterServer(SpiderConfig.masterIp, SpiderConfig.masterPort);

        SignalManager.registerOnSystemKill(new SignalManager.OnSystemKill() {
            //@Override
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
