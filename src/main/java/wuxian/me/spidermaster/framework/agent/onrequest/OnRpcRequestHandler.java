package wuxian.me.spidermaster.framework.agent.onrequest;

import com.sun.istack.internal.NotNull;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import wuxian.me.spidermaster.framework.agent.IClient;
import wuxian.me.spidermaster.framework.rpc.RpcRequest;
import wuxian.me.spidermaster.framework.rpc.RpcResponse;
import wuxian.me.spidermaster.framework.rpc.RpcRetCode;

/**
 * Created by wuxian on 9/6/2017.
 * <p>
 * 命令响应模式
 */
public class OnRpcRequestHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private IClient client;

    public OnRpcRequestHandler(@NotNull IClient client) {

        this.client = client;
    }

    //返回一个默认的response
    protected void channelRead0(ChannelHandlerContext channelHandlerContext
            , RpcRequest request) throws Exception {
        this.client.onReceiveMessage(request);  //Todo:后续扩展业务在这里处理

        RpcResponse response = new RpcResponse();
        response.requestId = request.requestId;
        response.retCode = RpcRetCode.SUCCESS.ordinal();

        channelHandlerContext.writeAndFlush(response);

    }
}
