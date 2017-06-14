package wuxian.me.spidermaster.master.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import wuxian.me.spidercommon.log.LogManager;

/**
 * Created by wuxian on 12/6/2017.
 */
public class DummyInboundHandler extends ChannelInboundHandlerAdapter {

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        LogManager.info("DummyInboundHandler.channelRead");

        super.channelRead(ctx, msg);
    }

    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);

        LogManager.info("DummyInboundHandler.channelRegistered");
    }


    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        LogManager.info("DummyInboundHandler.channelActive");
    }

    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        LogManager.info("DummyInboundHandler.channelReadComplete");

        super.channelReadComplete(ctx);
    }

}
