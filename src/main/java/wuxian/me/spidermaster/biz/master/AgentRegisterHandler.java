package wuxian.me.spidermaster.biz.master;

import com.google.gson.reflect.TypeToken;
import io.netty.channel.socket.SocketChannel;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidercommon.model.SpiderFeature;
import wuxian.me.spidermaster.framework.common.GsonProvider;
import wuxian.me.spidermaster.framework.master.control.Agent;
import wuxian.me.spidermaster.framework.master.control.AgentRecorder;
import wuxian.me.spidermaster.framework.common.Spider;
import wuxian.me.spidermaster.framework.common.StatusEnum;
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
public class AgentRegisterHandler extends BaseRequestHandler {

    public Object handleRequest(RpcRequest request, SocketChannel channel) throws HandlerExcepiton {

        LogManager.info("receive register rpc");
        LogManager.info("current thread: " + Thread.currentThread());
        LogManager.info("current channel: " + channel.toString());

        String data = request.datas;

        List<SpiderFeature> featureList = GsonProvider.gson().fromJson(data,new TypeToken<List<SpiderFeature>>(){}.getType());

        if(featureList == null ) {
            featureList = new ArrayList<SpiderFeature>();
        }

        Agent agent = new Agent();
        agent.setCurrentState(StatusEnum.REGISTERED);
        agent.setChannel(channel);

        List<Spider> spiderList = new ArrayList<Spider>();
        for(SpiderFeature feature:featureList) {
            spiderList.add(Spider.fromFeature(feature));
        }

        agent.setSpiderList(spiderList);
        AgentRecorder.recordAgent(agent);

        return RpcRetCode.SUCCESS.ordinal();
    }
}
