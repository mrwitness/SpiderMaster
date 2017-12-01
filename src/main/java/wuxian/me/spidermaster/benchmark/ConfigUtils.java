package wuxian.me.spidermaster.benchmark;

import wuxian.me.spidercommon.util.FileUtil;

import java.io.FileInputStream;
import java.util.*;


//
public class ConfigUtils {

    private static volatile Properties PROPERTIES;

    private ConfigUtils() {
    }

    public static Properties getProperties() {

        if (PROPERTIES == null) {
            synchronized (ConfigUtils.class) {
                if (PROPERTIES == null) {
                    Properties properties = new Properties();
                    try {
                        FileInputStream input = new FileInputStream(FileUtil.getCurrentPath() + "/conf/benchmark.properties");
                        try {
                            properties.load(input);
                        } finally {
                            input.close();
                        }
                    } catch (Throwable e) {
                        //logger.warn("Failed to load " + fileName + " file from " + fileName + "(ingore this file): " + e.getMessage(), e);
                    }
                    PROPERTIES = properties;
                }
            }
        }
        return PROPERTIES;
    }
}
