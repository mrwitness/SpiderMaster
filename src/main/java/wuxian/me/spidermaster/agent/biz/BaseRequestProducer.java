package wuxian.me.spidermaster.agent.biz;

import wuxian.me.spidermaster.util.RpcBizName;

/**
 * Created by wuxian on 11/6/2017.
 */
public abstract class BaseRequestProducer implements IRequestProducer {

    protected final String getRpcBizName() {
        RpcBizName annotation = (getClass().getAnnotation(RpcBizName.class));
        if (annotation == null) {
            return "";
        }

        String method = annotation.methodName();
        if (method == null) {
            return "";
        }
        return method;
    }
}
