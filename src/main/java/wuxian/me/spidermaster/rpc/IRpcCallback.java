package wuxian.me.spidermaster.rpc;

/**
 * Created by wuxian on 26/5/2017.
 */
public interface IRpcCallback {

    void onSent();

    void onResponseSuccess(RpcResponse response);

    void onResponseFail();

}
