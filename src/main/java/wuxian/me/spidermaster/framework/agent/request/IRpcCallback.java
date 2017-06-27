package wuxian.me.spidermaster.framework.agent.request;

import wuxian.me.spidermaster.framework.rpc.RpcResponse;

/**
 * Created by wuxian on 26/5/2017.
 */
public interface IRpcCallback {

    void onSent();

    void onResponseSuccess(RpcResponse response);

    void onResponseFail();

    void onTimeout();

}
