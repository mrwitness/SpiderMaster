package wuxian.me.spidermaster.biz.master.provider;

import io.netty.channel.socket.SocketChannel;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidermaster.framework.master.handler.BaseRequestHandler;
import wuxian.me.spidermaster.framework.master.handler.HandlerExcepiton;
import wuxian.me.spidermaster.biz.master.provider.*;
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

        LogManager.info("RequestSourceHandler.handleRequest");
        String resource = request.datas;
        if (resource == null || resource.length() == 0) {
            return null;
        }

        SocketChannel providerChannel = ProviderManager.findProviderChannel(resource);
        if (providerChannel == null) {
            return null;
        }

        LogManager.info("find channel: " + providerChannel.toString() + " who will handle the source request");
        providerChannel.writeAndFlush(request);
        LogManager.info("wait for resource...");

        ResourcePool.waitForResource(request.requestId, resource);//Todo:timeout的处理

        //Fixme:分布式下有可能是同一个requestId
        Resource res = ResourcePool.getResourceBy(request.requestId, resource);  //依然有可能为null

        if (res == null) {
            LogManager.info("get resource fail");
        } else {
            LogManager.info("get resource: " + res.toString());
        }
        return res;
    }
}
