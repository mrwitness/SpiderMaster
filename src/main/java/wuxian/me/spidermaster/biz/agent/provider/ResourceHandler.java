package wuxian.me.spidermaster.biz.agent.provider;

import io.netty.channel.socket.SocketChannel;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidermaster.biz.provider.Requestor;
import wuxian.me.spidermaster.framework.master.handler.BaseRequestHandler;
import wuxian.me.spidermaster.framework.master.handler.HandlerExcepiton;
import wuxian.me.spidermaster.framework.rpc.RpcMethodName;
import wuxian.me.spidermaster.framework.rpc.RpcRequest;

/**
 * Created by wuxian on 21/6/2017.
 */
@RpcMethodName(methodName = Requestor.REQUEST_RESROURCE)
public class ResourceHandler extends BaseRequestHandler {
    public ResourceHandler() {
    }

    @Override
    public String toString() {
        return "ResourceHandler{}";
    }

    @Override
    public Object handleRequest(RpcRequest request, SocketChannel channel) throws HandlerExcepiton {

        LogManager.info("ResourceHandler.onRpcRequest");
        String resource = request.datas;

        Object res = ProviderScanner.provideResource(resource);

        if (res == null) {
            LogManager.info("rpc response return null");
        } else {
            LogManager.info("rpc response return " + res.toString());
        }

        return res;
    }

}
