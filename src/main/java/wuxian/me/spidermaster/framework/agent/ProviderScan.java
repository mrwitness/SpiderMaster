package wuxian.me.spidermaster.framework.agent;

import wuxian.me.spidercommon.util.ClassHelper;
import wuxian.me.spidermaster.framework.master.provider.Provider;
import wuxian.me.spidermaster.framework.common.SpiderConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by wuxian on 21/6/2017.
 */
public class ProviderScan {

    private static List<String> roleSet = new ArrayList<String>();

    public static void scanAndCollect() {

        String pack = SpiderConfig.providerScan;

        if (pack == null || pack.length() == 0) {
            return;  //允许系统中没有provider
        }

        try {
            Set<Class<?>> classSet = ClassHelper.getClasses(pack);

            for (Class clazz : classSet) {
                performCheckAndCollect(clazz);
            }
        } catch (IOException e) {
            ;
        }

    }

    private static void performCheckAndCollect(Class clazz) {

        if (clazz == null) {
            return;
        }
        Provider annotation = (Provider) (clazz.getAnnotation(Provider.class));
        if (annotation == null) {
            return;
        }

        roleSet.add(clazz.getName());
    }

    public static List<String> getRoles() {
        return roleSet;
    }
}
