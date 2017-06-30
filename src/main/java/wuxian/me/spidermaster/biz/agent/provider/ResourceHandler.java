package wuxian.me.spidermaster.biz.agent.provider;

import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidermaster.framework.agent.onrequest.OnRpcRequest;
import wuxian.me.spidermaster.framework.rpc.RpcRequest;

/**
 * Created by wuxian on 21/6/2017.
 */
public class ResourceHandler implements OnRpcRequest {

    public ResourceHandler() {
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
