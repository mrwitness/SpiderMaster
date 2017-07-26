package wuxian.me.spidermaster.biz.agent;

import wuxian.me.spidermaster.framework.agent.request.RequestIdGen;
import wuxian.me.spidermaster.biz.provider.Requestor;
import wuxian.me.spidermaster.framework.rpc.RpcRequest;
/**
 * Created by wuxian on 21/6/2017.
 * <p>
 * Used to replace "switch proxy function in @SpiderSDK"
 */
@Requestor(request = "proxy")
public class GetproxyRequestProducer extends BaseRequestProducer {

    @Override
    public RpcRequest produce() {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.requestId = String.valueOf(RequestIdGen.genId());
        rpcRequest.methodName = getRpcBizName();
        rpcRequest.datas = getRequestResourceName();
        return rpcRequest;
    }
}
