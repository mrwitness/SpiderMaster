package wuxian.me.spidermaster.biz.agent;

import com.sun.istack.internal.Nullable;
import wuxian.me.spidercommon.model.SpiderFeature;
import wuxian.me.spidermaster.biz.model.RegisterReqModel;
import wuxian.me.spidermaster.framework.agent.request.RequestIdGen;
import wuxian.me.spidermaster.framework.rpc.RpcRequest;
import wuxian.me.spidermaster.framework.common.GsonProvider;
import wuxian.me.spidermaster.framework.rpc.RpcMethodName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuxian on 11/6/2017.
 */

@RpcMethodName(methodName = "register")
public class RegisterRequestProducer extends BaseRequestProducer {

    public List<String> classList = null;

    public List<String> urlPatternList = null;

    public List<String> roleList = null;

    public RegisterRequestProducer(List<String> classList, List<String> urlPatternList) {

        this(classList, urlPatternList, null);

    }

    public RegisterRequestProducer(List<String> classList, List<String> urlPatternList, @Nullable List<String> roles) {
        this.classList = classList;
        this.urlPatternList = urlPatternList;
        this.roleList = roles;
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

        RegisterReqModel model = new RegisterReqModel();
        model.featureList = featureList;
        model.providerList = roleList;

        rpcRequest.datas = GsonProvider.gson().toJson(model);

        return rpcRequest;
    }
}
