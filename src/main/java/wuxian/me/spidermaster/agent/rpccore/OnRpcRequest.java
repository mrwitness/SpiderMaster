package wuxian.me.spidermaster.agent.rpccore;

import wuxian.me.spidermaster.rpc.RpcRequest;

/**
 * Created by wuxian on 10/6/2017.
 */
public interface OnRpcRequest {

    void onRpcRequest(RpcRequest request);
}
