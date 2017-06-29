package wuxian.me.spidermaster.biz.agent;

import wuxian.me.spidercommon.model.Proxy;
import wuxian.me.spidermaster.framework.agent.request.RequestIdGen;
import wuxian.me.spidermaster.biz.master.control.StatusEnum;
import wuxian.me.spidermaster.framework.rpc.RpcRequest;
import wuxian.me.spidermaster.framework.common.GsonProvider;
import wuxian.me.spidermaster.framework.rpc.RpcMethodName;

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
