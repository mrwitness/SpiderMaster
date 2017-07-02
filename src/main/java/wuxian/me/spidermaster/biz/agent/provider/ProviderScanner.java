package wuxian.me.spidermaster.biz.agent.provider;

import com.sun.istack.internal.Nullable;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidercommon.util.ClassHelper;
import wuxian.me.spidermaster.biz.provider.IProvider;
import wuxian.me.spidermaster.biz.provider.Provider;
import wuxian.me.spidermaster.framework.common.SpiderConfig;
import wuxian.me.spidermaster.biz.provider.Resource;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wuxian on 21/6/2017.
 */
public class ProviderScanner {

    private static Map<String, IProvider> providerMap = new ConcurrentHashMap<String, IProvider>();

    private static Map<String, Method> methodMap = new ConcurrentHashMap<String, Method>();

    public static List<String> getProviderList() {
        List<String> list = new ArrayList<String>();
        list.addAll(providerMap.keySet());

        return list;
    }

    public static void scanAndCollectProviders() {

        String pack = SpiderConfig.providerScan;

        if (pack == null || pack.length() == 0) {
            return;  //允许系统中没有provider
        }

        try {
            Set<Class<?>> classSet = ClassHelper.getClasses(pack);  //Fixme:这个函数有bug 并不能扫描到所有类

            for (Class clazz : classSet) {
                performCheckAndCollect(clazz);
            }
        } catch (IOException e) {
            ;
        }
    }

    /**
     * 合格的provider
     * 1 被@Provider注解
     * 2 实现IProvider接口
     * 3 构造函数为public
     * @param clazz
     */
    public static void performCheckAndCollect(Class clazz) {
        if (clazz == null) {
            return;
        }

        Provider annotation = (Provider) (clazz.getAnnotation(Provider.class));
        if (annotation == null) {
            return;
        }

        Constructor constructor = null;
        try {
            constructor = clazz.getConstructor();
            int modifier = constructor.getModifiers() & Modifier.PUBLIC;
            if (modifier != Modifier.PUBLIC) {
                return;
            }

        } catch (NoSuchMethodException e) {

        }

        IProvider provider = null;
        boolean invokeSuccess = false;

        try {
            provider = (IProvider) constructor.newInstance(null); //for java6

            clazz.asSubclass(IProvider.class);

            invokeSuccess = true;
        } catch (InstantiationException e) {

        } catch (IllegalAccessException e) {

        } catch (InvocationTargetException e) {

        } catch (ClassCastException e) {

        }

        if (!invokeSuccess) {
            return;
        }


        try {
            Method method = clazz.getMethod("provide");
            providerMap.put(annotation.provide(), provider);
            methodMap.put(annotation.provide(), method);

            LogManager.info("find provider:" + clazz.getName() + " provide:" + annotation.provide());

        } catch (NoSuchMethodException e) {

            return;
        }

    }

    private static IProvider getProviderIns(String name) {
        if (name == null || name.length() == 0) {
            return null;
        }
        return providerMap.get(name);
    }

    @Nullable
    public static Method getMethod(String name) {
        if (name == null || name.length() == 0) {
            return null;
        }
        return methodMap.get(name);
    }

    @Nullable
    public static Resource provideResource(String name) {

        if (name == null || name.length() == 0) {
            return null;
        }

        IProvider provider = getProviderIns(name);
        Method method = getMethod(name);

        if (provider == null || method == null) {
            return null;
        }

        try {
            Object o = method.invoke(provider, null);

            Resource resource = new Resource();
            resource.name = name;
            resource.data = o;

            return resource;
        } catch (IllegalAccessException e) {

        } catch (InvocationTargetException e) {

        }

        return null;
    }


}
