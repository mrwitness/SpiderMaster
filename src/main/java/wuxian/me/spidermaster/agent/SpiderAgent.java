package wuxian.me.spidermaster.agent;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidercommon.model.HttpUrlNode;
import wuxian.me.spidercommon.util.IpPortUtil;
import wuxian.me.spidermaster.agent.biz.RegisterRequestProducer;
import wuxian.me.spidermaster.rpc.IRpcCallback;
import wuxian.me.spidermaster.rpc.RpcRequest;
import wuxian.me.spidermaster.util.SpiderConfig;
import wuxian.me.spidermaster.util.exception.IpPortNotValidException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuxian on 27/5/2017.
 */
public class SpiderAgent {

    private String serverIp;
    private int serverPort;

    private SpiderClient spiderClient;

    public static void init() {
        SpiderConfig.init();
        //ShellUtil.init();
    }

    public SpiderAgent() {
        this(SpiderConfig.masterIp, SpiderConfig.masterPort);
    }

    public SpiderAgent(@NotNull String serverIp, int serverPort) {

        this.serverIp = serverIp;

        this.serverPort = serverPort;

        if (!IpPortUtil.isValidIpPort(serverIp + ":" + serverPort)) {
            throw new IpPortNotValidException();
        }

        spiderClient = new SpiderClient();

    }

    public void start() {
        spiderClient.init();
        spiderClient.asyncConnect(serverIp, serverPort);
    }

    public void registerToMaster(@Nullable List<Class<?>> classList, @Nullable List<HttpUrlNode> nodeList) {
        if (classList == null) {

            classList = new ArrayList<Class<?>>();
        }

        if (nodeList == null) {
            nodeList = new ArrayList<HttpUrlNode>();
        }


        List<String> clazList = new ArrayList<String>();
        for (Class<?> clazz : classList) {
            clazList.add(clazz.getName());
        }

        List<String> patternList = new ArrayList<String>();
        for (HttpUrlNode node : nodeList) {
            patternList.add(node.baseUrl);
        }

        RpcRequest rpcRequest = new RegisterRequestProducer(clazList, patternList).produce();

        LogManager.info("registerToMaster,rpc: "+rpcRequest);
        spiderClient.asyncSendMessage(rpcRequest
                , new IRpcCallback() {
                    public void onSent() {

                    }

                    public void onResponseSuccess() {
                        LogManager.info("register rpc success");
                    }

                    public void onResponseFail() {
                        LogManager.info("register rpc fail");
                    }
                });

    }

    //Todo:
    public void reportStatusToMaster() {
        ;
    }
}
