package wuxian.me.spidermaster.master.core;

import com.sun.istack.internal.Nullable;
import wuxian.me.spidermaster.master.biz.IBizHandler;
import wuxian.me.spidermaster.util.RpcMethodName;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wuxian on 11/6/2017.
 * <p>
 * master能够处理的业务必须在这里注册
 */
public class BizHandlerRegistration {

    private static Map<String, IBizHandler> handlerMap = new HashMap<String, IBizHandler>();

    private BizHandlerRegistration() {
    }


    public static <T extends IBizHandler> void registerBizHandler(T handler) {
        if (handler == null) {
            return;
        }
        RpcMethodName annotation = (handler.getClass().getAnnotation(RpcMethodName.class));
        if (annotation == null) {
            return;
        }

        String method = annotation.methodName();
        if (method == null || method.length() == 0) {  //not valid
            return;
        }

        if (handlerMap.containsKey(method)) {
            return;
        }

        handlerMap.put(method, handler);
    }

    @Nullable
    public static IBizHandler findHandlerBy(String methodName) {
        if (methodName == null || methodName.length() == 0) {
            return null;
        }
        return handlerMap.get(methodName);
    }

}
