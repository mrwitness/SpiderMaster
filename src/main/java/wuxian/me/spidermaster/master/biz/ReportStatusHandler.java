package wuxian.me.spidermaster.master.biz;

import io.netty.channel.socket.SocketChannel;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidermaster.master.agentcontroll.Agent;
import wuxian.me.spidermaster.master.agentcontroll.AgentRecorder;
import wuxian.me.spidermaster.master.agentcontroll.StatusEnum;
import wuxian.me.spidermaster.rpc.RpcRetCode;
import wuxian.me.spidermaster.util.RpcMethodName;
import wuxian.me.spidermaster.rpc.RpcRequest;

/**
 * Created by wuxian on 11/6/2017.
 */

@RpcMethodName(methodName = "reportStatus")
public class ReportStatusHandler extends BaseBizHandler {

    public Object handleRequest(RpcRequest request, SocketChannel channel) throws BizErrorExcepiton {

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
