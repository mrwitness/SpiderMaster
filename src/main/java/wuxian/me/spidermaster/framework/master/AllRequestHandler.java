package wuxian.me.spidermaster.framework.master;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import org.apache.log4j.Logger;
import wuxian.me.spidermaster.framework.master.handler.HandlerExcepiton;
import wuxian.me.spidermaster.framework.master.handler.HandlerManager;
import wuxian.me.spidermaster.framework.master.handler.IRpcRequestHandler;
import wuxian.me.spidermaster.framework.rpc.RpcRequest;
import wuxian.me.spidermaster.framework.rpc.RpcResponse;
import wuxian.me.spidermaster.framework.rpc.RpcRetCode;

/**
 * Created by wuxian on 11/6/2017.
 * <p>
 * Rpc Request统一路由。
 */
public class AllRequestHandler extends SimpleChannelInboundHandler<RpcRequest> {

    static Logger logger = Logger.getLogger("server");
    private SocketChannel channel;

    public AllRequestHandler(SocketChannel channel) {
        this.channel = channel;

        logger.info("init request handlers");
        //Todo: 流程逻辑优化
        HandlerManager.scanAndCollectHandlers();
    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest request) throws Exception {

        logger.debug("RPC Request received --> " + request.toString());
        boolean needResponse = true;
        RpcResponse response = new RpcResponse();
        response.requestId = request.requestId;

        IRpcRequestHandler handler = HandlerManager.findHandlerBy(request.methodName);
        if (handler != null) {
            try {
                Object ret = handler.handleRequest(request, channel);
                response.result = ret;
                response.retCode = RpcRetCode.SUCCESS.ordinal();
                needResponse = handler.needResponse();  //其实只有heartbeat无需response。
            } catch (HandlerExcepiton e) {
                response.retCode = RpcRetCode.FAIL.ordinal();
            }

        } else {
            logger.info("fail to find any class to handle the request");
            response.retCode = RpcRetCode.FAIL.ordinal(); //Todo:设计一些错误返回码
        }

        if (needResponse) {
            logger.info("Rpc Response send: " + response.toString());
            channelHandlerContext.writeAndFlush(response);
        }

    }
}
