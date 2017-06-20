package wuxian.me.spidermaster.framework.agent.request;

import wuxian.me.spidermaster.framework.rpc.RpcRequest;

/**
 * Created by wuxian on 11/6/2017.
 */
public interface IRequestProducer {

    RpcRequest produce();
}
