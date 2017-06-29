package wuxian.me.spidermaster.biz.master;

import io.netty.channel.socket.SocketChannel;
import wuxian.me.spidermaster.biz.master.control.Agent;
import wuxian.me.spidermaster.biz.master.control.AgentRecorder;
import wuxian.me.spidermaster.biz.master.control.StatusEnum;
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
        Agent agent = AgentRecorder.findByChannel(channel);

        if (agent != null) {
            StatusEnum statusEnum = StatusEnum.values()[Integer.parseInt(request.datas)];
            agent.setCurrentState(statusEnum);

            AgentRecorder.recordAgent(agent);
        }

        return RpcRetCode.SUCCESS.ordinal();
    }
}
