package wuxian.me.spidermaster.framework.common;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import wuxian.me.spidercommon.util.FileUtil;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by wuxian on 27/5/2017.
 */
public class SpiderConfig {

    public static String DEFAULT_PATH = "/conf/spider.properties";

    private SpiderConfig() {
    }

    public static void init() {
        initFrom(DEFAULT_PATH);
    }

    public static int spiderMode;

    public static String masterIp;

    public static int masterPort;

    public static String bizScan;

    public static void initFrom(String path) {
        Properties pro = new Properties();
        FileInputStream in = null;
        boolean success = false;
        try {
            in = new FileInputStream(FileUtil.getCurrentPath()
                    + path);
            pro.load(in);
            success = true;
        } catch (FileNotFoundException e) {

        } catch (IOException e) {
            ;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                    ;
                }

            }
        }

        if (!success) {
            pro = null;
        }

        spiderMode = parse(pro, "spiderMode", 0);  //0:agent 1:master

        masterIp = parse(pro, "masterIp", "127.0.0.1");

        masterPort = parse(pro, "masterPort", 3434);

        bizScan = parse(pro, "bizScan", "");

    }

    private static String parse(@NotNull Properties pro, String key, String defValue) {
        if (pro == null) {
            return defValue;
        }

        try {
            return pro.getProperty(
                    key, defValue);
        } catch (Exception e) {
            return defValue;
        }
    }

    private static long parse(@Nullable Properties pro, String key, long defValue) {
        if (pro == null) {
            return defValue;
        }

        try {
            return Long.parseLong(pro.getProperty(
                    key, String.valueOf(defValue)));
        } catch (Exception e) {
            return defValue;
        }
    }

    private static int parse(@Nullable Properties pro, String key, int defValue) {
        if (pro == null) {
            return defValue;
        }

        try {
            return Integer.parseInt(pro.getProperty(
                    key, String.valueOf(defValue)));
        } catch (Exception e) {
            return defValue;
        }
    }

    private static boolean parse(@Nullable Properties pro, String key, boolean defValue) {
        if (pro == null) {
            return defValue;
        }

        try {
            return Boolean.parseBoolean(pro.getProperty(
                    key, String.valueOf(defValue)));
        } catch (Exception e) {
            return defValue;
        }
    }
}
