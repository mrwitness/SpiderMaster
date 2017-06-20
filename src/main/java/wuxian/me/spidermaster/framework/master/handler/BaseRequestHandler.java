package wuxian.me.spidermaster.framework.master.handler;


import wuxian.me.spidermaster.framework.rpc.RpcMethodName;

/**
 * Created by wuxian on 11/6/2017.
 */
public abstract class BaseRequestHandler implements IRpcRequestHandler {

    public BaseRequestHandler() {
    }

    public final String getMethodName() {
        RpcMethodName annotation = (getClass().getAnnotation(RpcMethodName.class));
        if (annotation == null) {
            return "";
        }

        String method = annotation.methodName();
        if (method == null) {  //not valid
            return "";
        }

        return method;
    }
}
