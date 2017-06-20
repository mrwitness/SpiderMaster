package wuxian.me.spidermaster.framework.master;

import com.sun.istack.internal.Nullable;
import wuxian.me.spidermaster.framework.master.handler.IRpcRequestHandler;
import wuxian.me.spidermaster.framework.rpc.RpcMethodName;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wuxian on 11/6/2017.
 * <p>
 * master能够处理的业务必须在这里注册,否则无法识别
 * Todo: 用扫描来实现 不用手动注册
 */
public class HandlerRegistration {

    private static Map<String, IRpcRequestHandler> handlerMap = new HashMap<String, IRpcRequestHandler>();

    private HandlerRegistration() {
    }


    public static <T extends IRpcRequestHandler> void registerBizHandler(T handler) {
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
    public static IRpcRequestHandler findHandlerBy(String methodName) {
        if (methodName == null || methodName.length() == 0) {
            return null;
        }
        return handlerMap.get(methodName);
    }

}
