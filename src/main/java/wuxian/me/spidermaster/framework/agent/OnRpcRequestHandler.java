package wuxian.me.spidermaster.framework.agent;

import com.sun.istack.internal.NotNull;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import wuxian.me.spidermaster.framework.agent.IClient;
import wuxian.me.spidermaster.framework.common.GsonProvider;
import wuxian.me.spidermaster.framework.rpc.RpcRequest;
import wuxian.me.spidermaster.framework.rpc.RpcResponse;
import wuxian.me.spidermaster.framework.rpc.RpcRetCode;

/**
 * Created by wuxian on 9/6/2017.
 */
public class OnRpcRequestHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private IClient client;

    public OnRpcRequestHandler(@NotNull IClient client) {
        this.client = client;
    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext
            , RpcRequest request) throws Exception {

        Object o = this.client.onReceiveMessage(request);

        RpcResponse response = new RpcResponse();
        response.requestId = request.requestId;
        response.retCode = RpcRetCode.SUCCESS.ordinal();

        if (o != null) {
            o = GsonProvider.gson().toJson(o);
        }
        response.result = o;

        channelHandlerContext.writeAndFlush(response);
    }
}
