package wuxian.me.spidermaster.rpc;

/**
 * Created by wuxian on 26/5/2017.
 */
public class RpcRequest {

    public String requestId;

    public String methodName;

    public String datas;

    @Override
    public String toString() {
        return "RpcRequest{" +
                "requestId='" + requestId + '\'' +
                ", methodName='" + methodName + '\'' +
                ", datas='" + datas + '\'' +
                '}';
    }
}
