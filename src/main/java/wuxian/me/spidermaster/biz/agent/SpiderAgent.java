package wuxian.me.spidermaster.biz.agent;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import wuxian.me.spidercommon.model.HttpUrlNode;
import wuxian.me.spidercommon.util.IpPortUtil;
import wuxian.me.spidermaster.biz.agent.provider.ProviderScan;
import wuxian.me.spidermaster.framework.agent.SpiderClient;
import wuxian.me.spidermaster.framework.agent.request.DefaultCallback;
import wuxian.me.spidermaster.framework.agent.request.IRpcCallback;
import wuxian.me.spidermaster.framework.agent.connection.NioEnv;
import wuxian.me.spidermaster.biz.master.control.StatusEnum;
import wuxian.me.spidermaster.framework.rpc.RpcRequest;
import wuxian.me.spidermaster.framework.rpc.RpcResponse;
import wuxian.me.spidermaster.framework.common.SpiderConfig;
import wuxian.me.spidermaster.framework.common.IpPortNotValidException;

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
        NioEnv.init();
        ProviderScan.scanAndCollect();
        spiderClient.init();
        spiderClient.asyncConnect(serverIp, serverPort);
    }

    public void requestProxy(IRpcCallback cb) {
        RpcRequest rpcRequest = new GetproxyRequestProducer().produce();
        spiderClient.asyncSendMessage(rpcRequest, cb);
    }

    public void forDisconnect() {
        spiderClient.forceDisconnect();
    }

    public void registerToMaster(@Nullable List<Class<?>> classList, @Nullable List<HttpUrlNode> nodeList, final IRpcCallback callback) {
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


        RpcRequest rpcRequest = new RegisterRequestProducer(clazList, patternList, ProviderScan.getProviderList()).produce();

        spiderClient.asyncSendMessage(rpcRequest
                , new IRpcCallback() {
                    public void onSent() {
                        if (callback != null) {
                            callback.onSent();
                        }
                    }

                    public void onResponseSuccess(RpcResponse response) {

                        if (callback != null) {
                            callback.onResponseSuccess(response);
                        }
                    }

                    public void onResponseFail() {

                        if (callback != null) {
                            callback.onResponseFail();
                        }
                    }

                    @Override
                    public void onTimeout() {

                    }
                });
    }

    public void registerToMaster(@Nullable List<Class<?>> classList, @Nullable List<HttpUrlNode> nodeList) {
        registerToMaster(classList, nodeList, null);
    }


    public void reportAgentStatus(StatusEnum statusEnum) {
        RpcRequest rpcRequest = new ReportStatusRequestProducer(statusEnum).produce();

        spiderClient.asyncSendMessage(rpcRequest, DefaultCallback.ins());
    }
}
