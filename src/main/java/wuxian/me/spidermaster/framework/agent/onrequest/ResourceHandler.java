package wuxian.me.spidermaster.framework.agent.onrequest;

import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidermaster.framework.agent.ProviderScan;
import wuxian.me.spidermaster.framework.agent.SpiderClient;
import wuxian.me.spidermaster.framework.master.provider.Requestor;
import wuxian.me.spidermaster.framework.rpc.RpcRequest;

/**
 * Created by wuxian on 21/6/2017.
 */
public class ResourceHandler implements OnRpcRequest {

    private ResourceHandler() {
    }

    public static void init() {

        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.methodName = Requestor.REQUEST_RESROURCE;
        //Todo: 优化下不用手动注册
        SpiderClient.registerMessageNotify(rpcRequest, new ResourceHandler());
    }

    @Override
    public Object onRpcRequest(RpcRequest request) {
        LogManager.info("ResourceHandler.onRpcRequest");
        String resource = request.datas;

        Object o = ProviderScan.provideResource(resource);

        if (o == null) {
            LogManager.info("onRpcRequest return null");
        } else {
            LogManager.info("onRpcRequest return " + o.toString());
        }


        return o;
    }

    @Override
    public String toString() {
        return "ResourceHandler{}";
    }
}
