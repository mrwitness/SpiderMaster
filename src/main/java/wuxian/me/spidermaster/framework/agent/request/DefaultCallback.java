package wuxian.me.spidermaster.framework.agent.request;

import wuxian.me.spidermaster.framework.rpc.RpcResponse;

/**
 * Created by wuxian on 17/6/2017.
 */
public class DefaultCallback implements IRpcCallback {

    private static DefaultCallback callback = new DefaultCallback();

    private DefaultCallback() {
    }

    public static DefaultCallback ins() {
        return callback;
    }


    public void onSent() {

    }

    public void onResponseSuccess(RpcResponse response) {

    }

    public void onResponseFail() {

    }

    @Override
    public void onTimeout() {

    }
}
