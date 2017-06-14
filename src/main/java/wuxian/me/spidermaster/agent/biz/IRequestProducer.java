package wuxian.me.spidermaster.agent.biz;

import wuxian.me.spidermaster.rpc.RpcRequest;

/**
 * Created by wuxian on 11/6/2017.
 */
public interface IRequestProducer {

    RpcRequest produce();
}
