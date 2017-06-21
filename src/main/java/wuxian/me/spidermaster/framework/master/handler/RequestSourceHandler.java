package wuxian.me.spidermaster.framework.master.handler;

import io.netty.channel.socket.SocketChannel;
import wuxian.me.spidermaster.framework.master.provider.*;
import wuxian.me.spidermaster.framework.rpc.RpcMethodName;
import wuxian.me.spidermaster.framework.rpc.RpcRequest;

/**
 * Created by wuxian on 21/6/2017.
 */
@RpcMethodName(methodName = Requestor.REQUEST_RESROURCE)
public class RequestSourceHandler extends BaseRequestHandler {

    @Override
    public Object handleRequest(RpcRequest request, SocketChannel channel) throws HandlerExcepiton {

        String resource = request.datas;
        if (resource == null || resource.length() == 0) {
            return null;
        }

        SocketChannel providerChannle = ProviderManager.findProviderChannle(resource);
        if (providerChannle == null) {
            return null;
        }

        providerChannle.writeAndFlush(request);  //将请求转发

        ResourcePool.waitForResource(request.requestId, resource);//Todo timeout

        return ResourcePool.getResourceBy(request.requestId, resource);  //依然有可能为null
    }
}
