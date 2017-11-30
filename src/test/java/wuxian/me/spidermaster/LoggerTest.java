package wuxian.me.spidermaster;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;
import wuxian.me.spidercommon.util.FileUtil;

public class LoggerTest {

    @Test
    public void testLog() {

        PropertyConfigurator.configure(FileUtil.getCurrentPath() + "/conf/log4j.properties");

        Logger logger1 = Logger.getLogger("server");
        logger1.info("server 1");
        logger1.debug("server 2");
        logger1.error("server 3");

        Logger logger2 = Logger.getLogger("client");
        logger2.info("client 1");

    }
}
