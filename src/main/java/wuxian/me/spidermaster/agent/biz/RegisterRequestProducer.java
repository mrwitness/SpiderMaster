package wuxian.me.spidermaster.agent.biz;

import wuxian.me.spidercommon.model.SpiderFeature;
import wuxian.me.spidermaster.agent.rpccore.RequestIdGen;
import wuxian.me.spidermaster.rpc.RpcRequest;
import wuxian.me.spidermaster.util.RpcBizName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuxian on 11/6/2017.
 * <p>
 */

@RpcBizName(methodName = "register")
public class RegisterRequestProducer extends BaseRequestProducer {

    public List<String> classList = null;

    public List<String> urlPatternList = null;

    public RegisterRequestProducer(List<String> classList, List<String> urlPatternList) {

        this.classList = classList;
        this.urlPatternList = urlPatternList;
    }

    public RpcRequest produce() {
        int len = 0;

        if (classList == null || urlPatternList == null) {
            len = 0;
        } else {
            int a = classList.size();
            int b = urlPatternList.size();

            len = a <= b ? a : b;
        }


        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.requestId = String.valueOf(RequestIdGen.genId());
        rpcRequest.methodName = getRpcBizName();

        List<SpiderFeature> featureList = new ArrayList<SpiderFeature>();

        if (len > 0) {
            for (int i = 0; i < len; i++) {
                SpiderFeature feature = new SpiderFeature();

                feature.className = classList.get(i);
                feature.urlPattern = urlPatternList.get(i);
                featureList.add(feature);
            }
        }

        rpcRequest.datas = GsonProvider.gson().toJson(featureList);

        return rpcRequest;
    }
}
