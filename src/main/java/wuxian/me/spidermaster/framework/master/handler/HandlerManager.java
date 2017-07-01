package wuxian.me.spidermaster.framework.master.handler;

import com.sun.istack.internal.Nullable;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidercommon.util.ClassHelper;
import wuxian.me.spidermaster.framework.common.InitEnvException;
import wuxian.me.spidermaster.framework.common.SpiderConfig;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by wuxian on 20/6/2017.
 */
public class HandlerManager {

    private static Map<String, IRpcRequestHandler> handlerMap = new HashMap<String, IRpcRequestHandler>();

    private HandlerManager() {
    }


    public static void scanAndCollectHandlers() throws InitEnvException {

        String pack = SpiderConfig.bizScan;
        if (pack == null || pack.length() == 0) {
            throw new InitEnvException("bizScan in spider.properties not set");
        }

        try {
            Set<Class<?>> classSet = ClassHelper.getClasses(pack);

            for (Class clazz : classSet) {
                checkAndCollect(clazz);
            }
        } catch (IOException e) {
            ;
        }

    }

    private static void checkAndCollect(Class clazz) {

        LogManager.info("HandlerManager.check " + clazz.getName());

        if (Modifier.isAbstract(clazz.getModifiers())) {
            return;
        }

        try {
            clazz.asSubclass(IRpcRequestHandler.class);
        } catch (ClassCastException e) {
            return;
        }


        try {
            Constructor constructor = clazz.getConstructor();

            int modifier = constructor.getModifiers() & Modifier.PUBLIC;
            if (modifier != Modifier.PUBLIC) {
                return;
            }

            IRpcRequestHandler o = (IRpcRequestHandler) constructor.newInstance(null);
            handlerMap.put(o.getMethodName(), o);
            LogManager.info("find handler: " + clazz.getName());

            return;

        } catch (NoSuchMethodException e) {

        } catch (InstantiationException e) {

        } catch (IllegalAccessException e) {

        } catch (InvocationTargetException e) {

        }

    }

    @Nullable
    public static IRpcRequestHandler findHandlerBy(String methodName) {
        if (methodName == null || methodName.length() == 0) {
            return null;
        }
        return handlerMap.get(methodName);
    }
}