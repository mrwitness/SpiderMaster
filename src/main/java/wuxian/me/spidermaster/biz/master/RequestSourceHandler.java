package wuxian.me.spidermaster.biz.master;

import io.netty.channel.socket.SocketChannel;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidermaster.framework.master.handler.BaseRequestHandler;
import wuxian.me.spidermaster.framework.master.handler.HandlerExcepiton;
import wuxian.me.spidermaster.framework.master.provider.*;
import wuxian.me.spidermaster.framework.rpc.RpcMethodName;
import wuxian.me.spidermaster.framework.rpc.RpcRequest;

/**
 * Created by wuxian on 21/6/2017.
 *
 * Todo:转移到另一个framework包下
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

        LogManager.info("begin search channel which can handle this resource");
        SocketChannel providerChannle = ProviderManager.findProviderChannle(resource);
        if (providerChannle == null) {

            LogManager.info("No channel found");
            return null;
        }

        LogManager.info("find channel: " + providerChannle.toString());

        providerChannle.writeAndFlush(request);  //将请求转发

        LogManager.info("wait for resource...");
        ResourcePool.waitForResource(request.requestId, resource);//Todo timeout

        LogManager.info("after waitForResource");

        Resource ret = ResourcePool.getResourceBy(request.requestId, resource);  //依然有可能为null

        if (ret == null) {
            LogManager.info("get ret null");
        } else {
            LogManager.info("get ret: " + ret);
        }

        return ret;
    }
}
