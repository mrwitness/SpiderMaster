package wuxian.me.spidermaster.biz.agent;

import wuxian.me.spidermaster.framework.agent.request.BaseRequestProducer;
import wuxian.me.spidermaster.framework.agent.request.RequestIdGen;
import wuxian.me.spidermaster.framework.rpc.RpcRequest;
import wuxian.me.spidermaster.framework.rpc.RpcMethodName;

/**
 * Created by wuxian on 11/6/2017.
 */

@RpcMethodName(methodName = "heartbeat")
public class HeartbeatRequestProducer extends BaseRequestProducer {

    public RpcRequest produce() {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.requestId = String.valueOf(RequestIdGen.genId());
        rpcRequest.methodName = getRpcBizName();

        return rpcRequest;
    }
}
