package wuxian.me.spidermaster.rpc.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidermaster.rpc.RpcRequest;

/**
 * Created by wuxian on 26/5/2017.
 * <p>
 * 业务逻辑handler
 */
public class SpiderBizHandler extends ChannelInboundHandlerAdapter {

    private static String PRE = "SpiderBiz: ";

    private void info(String content) {
        if (content == null || content.length() == 0) {
            return;
        }

        LogManager.info(PRE + content);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);

        info("channelRegistered");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        info("channelActive");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

        info("channelInactive");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);

        info("channelRead");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);

        info("channelReadComplete");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);

        ctx.close();
    }

    //Fixme:
    protected void channelRead0(ChannelHandlerContext channelHandlerContext,
                                RpcRequest rpcRequest) throws Exception {
    }
}
