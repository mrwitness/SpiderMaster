package wuxian.me.spidermaster.framework.agent.onrequest;

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
        String resource = request.datas;

        return ProviderScan.provideResource(resource);
    }
}
