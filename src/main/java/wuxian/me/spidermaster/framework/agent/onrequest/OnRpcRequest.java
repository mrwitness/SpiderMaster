package wuxian.me.spidermaster.framework.agent.onrequest;

import wuxian.me.spidermaster.framework.rpc.RpcRequest;

/**
 * Created by wuxian on 10/6/2017.
 */
public interface OnRpcRequest {

    void onRpcRequest(RpcRequest request);
}
