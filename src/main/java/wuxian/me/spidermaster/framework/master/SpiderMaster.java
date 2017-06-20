package wuxian.me.spidermaster.framework.master;

import com.sun.istack.internal.NotNull;
import wuxian.me.spidercommon.util.IpPortUtil;
import wuxian.me.spidermaster.biz.master.AgentRegisterHandler;
import wuxian.me.spidermaster.biz.master.ReportStatusHandler;
import wuxian.me.spidermaster.framework.common.IpPortNotValidException;

/**
 * Created by wuxian on 27/5/2017.
 */
public class SpiderMaster {

    private MasterServer server;

    public SpiderMaster(@NotNull String serverIp, int serverPort) {

        if (!IpPortUtil.isValidIpPort(serverIp + ":" + serverPort)) {
            throw new IpPortNotValidException();
        }

        this.server = new MasterServer(serverIp, serverPort);
    }

    public void start() {
        HandlerRegistration.registerBizHandler(new AgentRegisterHandler());
        HandlerRegistration.registerBizHandler(new ReportStatusHandler());
        HandlerRegistration.registerBizHandler(new ReportStatusHandler());

        this.server.start();
    }
}
