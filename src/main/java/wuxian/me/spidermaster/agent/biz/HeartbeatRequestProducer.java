package wuxian.me.spidermaster.agent.biz;

import wuxian.me.spidermaster.rpc.RpcRequest;
import wuxian.me.spidermaster.util.RpcMethodName;

/**
 * Created by wuxian on 11/6/2017.
 * <p>
 * Todo:
 */

@RpcMethodName(methodName = "heartbeat")
public class HeartbeatRequestProducer extends BaseRequestProducer {

    public RpcRequest produce() {
        return null;
    }
}
