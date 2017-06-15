package wuxian.me.spidermaster.master.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidermaster.master.biz.BizErrorExcepiton;
import wuxian.me.spidermaster.master.biz.IBizHandler;
import wuxian.me.spidermaster.rpc.RpcRequest;
import wuxian.me.spidermaster.rpc.RpcResponse;
import wuxian.me.spidermaster.rpc.RpcRetCode;

/**
 * Created by wuxian on 11/6/2017.
 */
public class AgentRpcRequestHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private SocketChannel channel;

    public AgentRpcRequestHandler(SocketChannel channel) {
        this.channel = channel;
    }

    //Todo: heartbeat不用返回一个值？
    private boolean dealIfHeartbeat() {
        return false;
    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest request) throws Exception {
        if (dealIfHeartbeat()) {
            return;
        }

        RpcResponse response = new RpcResponse();
        response.requestId = request.requestId;

        LogManager.info("AgentRpcRequestHandler channelRead0");
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
            response.retCode = RpcRetCode.FAIL.ordinal(); //Todo:加一些错误返回码
        }

        channelHandlerContext.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
