package wuxian.me.spidermaster.biz.master;

import io.netty.channel.socket.SocketChannel;
import wuxian.me.spidercommon.model.SpiderFeature;
import wuxian.me.spidermaster.biz.master.control.Spider;
import wuxian.me.spidermaster.biz.model.RegisterReqModel;
import wuxian.me.spidermaster.framework.common.GsonProvider;
import wuxian.me.spidermaster.biz.master.control.Agent;
import wuxian.me.spidermaster.biz.master.control.AgentRecorder;
import wuxian.me.spidermaster.biz.master.control.StatusEnum;
import wuxian.me.spidermaster.framework.master.handler.BaseRequestHandler;
import wuxian.me.spidermaster.framework.master.handler.HandlerExcepiton;
import wuxian.me.spidermaster.framework.rpc.RpcRetCode;
import wuxian.me.spidermaster.framework.rpc.RpcMethodName;
import wuxian.me.spidermaster.framework.rpc.RpcRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuxian on 18/5/2017.
 */

@RpcMethodName(methodName = "register")
public class RegistryHandler extends BaseRequestHandler {

    public Object handleRequest(RpcRequest request, SocketChannel channel) throws HandlerExcepiton {

        String data = request.datas;

        RegisterReqModel model = GsonProvider.gson().fromJson(data, RegisterReqModel.class);

        if (model == null) {
            return RpcRetCode.FAIL.ordinal(); //Todo
        }

        List<SpiderFeature> featureList = model.featureList;
        if(featureList == null ) {
            featureList = new ArrayList<SpiderFeature>();
        }

        Agent agent = new Agent();
        agent.setCurrentState(StatusEnum.REGISTERED);
        agent.setChannel(channel);
        agent.setProviderList(model.providerList);

        List<Spider> spiderList = new ArrayList<Spider>();
        for(SpiderFeature feature:featureList) {
            spiderList.add(Spider.fromFeature(feature));
        }
        agent.setSpiderList(spiderList);
        AgentRecorder.recordAgent(agent);

        return RpcRetCode.SUCCESS.ordinal();
    }
}
