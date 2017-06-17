package wuxian.me.spidermaster.agent.biz;

import wuxian.me.spidermaster.agent.rpccore.RequestIdGen;
import wuxian.me.spidermaster.rpc.RpcRequest;
import wuxian.me.spidermaster.util.RpcMethodName;

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
