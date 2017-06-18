package wuxian.me.spidermaster.master.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidermaster.master.biz.BizErrorExcepiton;
import wuxian.me.spidermaster.master.biz.HeartbeatHandler;
import wuxian.me.spidermaster.master.biz.IBizHandler;
import wuxian.me.spidermaster.rpc.RpcRequest;
import wuxian.me.spidermaster.rpc.RpcResponse;
import wuxian.me.spidermaster.rpc.RpcRetCode;

/**
 * Created by wuxian on 11/6/2017.
 */
public class AgentRpcRequestHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private SocketChannel channel;

    private final String heartbeat = new HeartbeatHandler().getMethodName();

    public AgentRpcRequestHandler(SocketChannel channel) {
        this.channel = channel;
    }

    private boolean dealIfHeartbeat(String name) {
        if (name != null && heartbeat.equals(name)) {
            return true;
        }
        return false;
    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest request) throws Exception {

        RpcResponse response = new RpcResponse();
        response.requestId = request.requestId;

        if (dealIfHeartbeat(request.methodName)) {  //heartbeat不用返回什么
            LogManager.info("heartbeat,ignore");
            return;
        }

        LogManager.info("AgentRpcRequestHandler channelRead0 requestId:" + request.requestId);
        IBizHandler handler = BizHandlerRegistration.findHandlerBy(request.methodName);
        if (handler != null) {
            LogManager.info("getRpcRequest,rpcName: " + request.methodName + " handlerClass: " + handler.getClass());
            try {
                Object ret = handler.handleRequest(request, channel);

                response.result = ret;
                response.retCode = RpcRetCode.SUCCESS.ordinal();

            } catch (BizErrorExcepiton e) {
                response.retCode = RpcRetCode.FAIL.ordinal();
            }

        } else {
            response.retCode = RpcRetCode.FAIL.ordinal(); //Todo:设计一些错误返回码
        }

        channelHandlerContext.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
