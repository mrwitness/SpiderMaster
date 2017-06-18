package wuxian.me.spidermaster.master;

import com.sun.istack.internal.NotNull;
import wuxian.me.spidercommon.util.IpPortUtil;
import wuxian.me.spidermaster.master.biz.AgentRegisterHandler;
import wuxian.me.spidermaster.master.biz.ReportStatusHandler;
import wuxian.me.spidermaster.master.core.BizHandlerRegistration;
import wuxian.me.spidermaster.util.exception.IpPortNotValidException;

/**
 * Created by wuxian on 27/5/2017.
 */
public class SpiderMaster {

    private String serverIp;
    private int serverPort;

    private MasterServer server;

    public SpiderMaster(@NotNull String serverIp, int serverPort) {

        this.serverIp = serverIp;

        this.serverPort = serverPort;

        if (!IpPortUtil.isValidIpPort(serverIp + ":" + serverPort)) {
            throw new IpPortNotValidException();
        }

        this.server = new MasterServer(serverIp, serverPort);
    }

    public void start() {
        BizHandlerRegistration.registerBizHandler(new AgentRegisterHandler());
        BizHandlerRegistration.registerBizHandler(new ReportStatusHandler());
        BizHandlerRegistration.registerBizHandler(new ReportStatusHandler());

        this.server.start();
    }
}
