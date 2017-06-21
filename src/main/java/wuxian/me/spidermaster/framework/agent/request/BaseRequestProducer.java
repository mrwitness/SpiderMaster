package wuxian.me.spidermaster.framework.agent.request;

import com.sun.istack.internal.Nullable;
import wuxian.me.spidermaster.framework.master.provider.Requestor;
import wuxian.me.spidermaster.framework.rpc.RpcMethodName;

/**
 * Created by wuxian on 11/6/2017.
 */
public abstract class BaseRequestProducer implements IRequestProducer {

    protected final String getRpcBizName() {

        Requestor requestor = (getClass()).getAnnotation(Requestor.class);
        if (requestor != null) {
            return Requestor.REQUEST_RESROURCE;
        }

        RpcMethodName annotation = (getClass().getAnnotation(RpcMethodName.class));
        if (annotation == null) {
            return "";
        }

        String method = annotation.methodName();
        if (method == null) {
            return "";
        }

        return method;
    }

    @Nullable
    protected final String getRequestResourceName() {
        Requestor requestor = (getClass()).getAnnotation(Requestor.class);
        if (requestor != null) {
            return requestor.request();
        }

        return null;
    }
}
