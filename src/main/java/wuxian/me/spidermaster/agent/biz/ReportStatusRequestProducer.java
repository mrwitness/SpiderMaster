package wuxian.me.spidermaster.agent.biz;

import wuxian.me.spidercommon.model.Proxy;
import wuxian.me.spidermaster.agent.rpccore.RequestIdGen;
import wuxian.me.spidermaster.master.agentcontroll.StatusEnum;
import wuxian.me.spidermaster.rpc.RpcRequest;
import wuxian.me.spidermaster.util.GsonProvider;
import wuxian.me.spidermaster.util.RpcMethodName;

/**
 * Created by wuxian on 11/6/2017.
 */

@RpcMethodName(methodName = "reportStatus")
public class ReportStatusRequestProducer extends BaseRequestProducer {

    private StatusEnum status;

    //把proxy加入则必须给它们搞一个model出来... --> 后续想想怎么改
    private Proxy proxy;

    public ReportStatusRequestProducer(StatusEnum statusEnum) {
        this(statusEnum, null);
    }

    public ReportStatusRequestProducer(StatusEnum status, Proxy proxy) {

        this.status = status;
        this.proxy = proxy;
    }

    public RpcRequest produce() {

        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.requestId = String.valueOf(RequestIdGen.genId());
        rpcRequest.methodName = getRpcBizName();

        rpcRequest.datas = GsonProvider.gson().toJson(status.ordinal());

        return rpcRequest;
    }
}
