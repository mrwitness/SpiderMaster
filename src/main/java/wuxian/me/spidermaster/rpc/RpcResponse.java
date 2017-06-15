package wuxian.me.spidermaster.rpc;

/**
 * Created by wuxian on 26/5/2017.
 */
public class RpcResponse {

    public String requestId;

    public int retCode;

    public Throwable error;

    public Object result;

    @Override
    public String toString() {
        return "RpcResponse{" +
                "requestId='" + requestId + '\'' +
                ", retCode=" + retCode +
                ", error=" + error +
                ", result=" + result +
                '}';
    }
}
