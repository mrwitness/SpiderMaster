package wuxian.me.spidermaster.rpc;

/**
 * Created by wuxian on 15/6/2017.
 */
public enum RpcRetCode {

    SUCCESS(1),

    FAIL(0);

    private int code;

    RpcRetCode(int code) {
        this.code = code;
    }
}
