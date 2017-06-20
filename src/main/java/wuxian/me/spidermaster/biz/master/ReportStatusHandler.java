package wuxian.me.spidermaster.biz.master;

import io.netty.channel.socket.SocketChannel;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidermaster.framework.master.control.Agent;
import wuxian.me.spidermaster.framework.master.control.AgentRecorder;
import wuxian.me.spidermaster.framework.common.StatusEnum;
import wuxian.me.spidermaster.framework.master.handler.BaseRequestHandler;
import wuxian.me.spidermaster.framework.master.handler.HandlerExcepiton;
import wuxian.me.spidermaster.framework.rpc.RpcRetCode;
import wuxian.me.spidermaster.framework.rpc.RpcMethodName;
import wuxian.me.spidermaster.framework.rpc.RpcRequest;

/**
 * Created by wuxian on 11/6/2017.
 */

@RpcMethodName(methodName = "reportStatus")
public class ReportStatusHandler extends BaseRequestHandler {

    public Object handleRequest(RpcRequest request, SocketChannel channel) throws HandlerExcepiton {

        LogManager.info("ReportStatusHandler.handleRequest");
        Agent agent = AgentRecorder.findByChannel(channel);
        LogManager.info("find Agent:" + agent);

        if (agent != null) {
            StatusEnum statusEnum = StatusEnum.values()[Integer.parseInt(request.datas)];
            agent.setCurrentState(statusEnum);

            AgentRecorder.recordAgent(agent);
        }

        return RpcRetCode.SUCCESS.ordinal();
    }
}
