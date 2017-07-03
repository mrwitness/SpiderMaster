package wuxian.me.spidermaster.biz.master;

import io.netty.channel.socket.SocketChannel;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidermaster.framework.common.GsonProvider;
import wuxian.me.spidermaster.framework.master.ConnectionManager;
import wuxian.me.spidermaster.framework.master.handler.BaseRequestHandler;
import wuxian.me.spidermaster.framework.master.handler.HandlerExcepiton;
import wuxian.me.spidermaster.biz.provider.*;
import wuxian.me.spidermaster.framework.rpc.RpcMethodName;
import wuxian.me.spidermaster.framework.rpc.RpcRequest;

/**
 * Created by wuxian on 21/6/2017.
 * <p>
 * 基本可以看作是框架级别的能力:rpc请求转发。
 */
@RpcMethodName(methodName = Requestor.REQUEST_RESROURCE)
public class RequestSourceHandler extends BaseRequestHandler {

    @Override
    public Object handleRequest(RpcRequest request, SocketChannel channel) throws HandlerExcepiton {

        LogManager.info("in RequestSourceHandler.handleRequest");
        String resource = request.datas;
        if (resource == null || resource.length() == 0) {
            return null;
        }

        SocketChannel providerChannel = ProviderManager.findProviderChannel(resource);
        if (providerChannel == null) {
            return null;
        }

        if (!ConnectionManager.containsChannel(providerChannel)) {  //连接已失效
            return null;
        }

        providerChannel.writeAndFlush(request);

        Resource res = null;
        if (!channel.eventLoop().equals(providerChannel.eventLoop())) {

            LogManager.info("wait for resource: " + resource);
            ResourcePool.waitForResource(request.requestId, resource, 5000);//默认5s超时

            res = ResourcePool.getResourceFromWaitmap(request.requestId, resource);
        } else {
            //if using the same eventloop can't wait,otherwise will switch to deadlock state
            res = ResourcePool.getResourceFromSet(resource);
        }

        if (res == null) {
            LogManager.info("get resource fail");

            return null;
        } else {
            LogManager.info("success get resource: " + res.toString());
        }

        return GsonProvider.gson().toJson(res);  //currently must be convert to String(because of Gson.toJson) which should be Fixme!
    }
}
