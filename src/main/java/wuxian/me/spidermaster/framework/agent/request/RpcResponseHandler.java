package wuxian.me.spidermaster.framework.agent.request;

import com.sun.istack.internal.Nullable;
import io.netty.channel.*;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidermaster.framework.agent.IClient;
import wuxian.me.spidermaster.framework.rpc.RpcResponse;

/**
 * Created by wuxian on 27/5/2017.
 */
public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private IClient client;

    public RpcResponseHandler(@Nullable IClient client) {
        this.client = client;
    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext,
                                RpcResponse rpcResponse) throws Exception {
        if (client == null) {
            return;
        }
        client.onRpcResponse(rpcResponse);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
