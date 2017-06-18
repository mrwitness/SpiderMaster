package wuxian.me.spidermaster.agent.rpccore;

import com.sun.istack.internal.NotNull;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import wuxian.me.spidermaster.agent.IClient;
import wuxian.me.spidermaster.rpc.RpcRequest;
import wuxian.me.spidermaster.rpc.RpcResponse;
import wuxian.me.spidermaster.rpc.RpcRetCode;

import java.util.Map;

/**
 * Created by wuxian on 9/6/2017.
 * <p>
 * 命令响应模式
 */
public class MasterRpcRequestHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private IClient client;

    public MasterRpcRequestHandler(@NotNull IClient client) {

        this.client = client;
    }

    //返回一个默认的response
    protected void channelRead0(ChannelHandlerContext channelHandlerContext
            , RpcRequest request) throws Exception {
        this.client.onMessage(request);  //Todo:后续扩展业务在这里处理

        RpcResponse response = new RpcResponse();
        response.requestId = request.requestId;
        response.retCode = RpcRetCode.SUCCESS.ordinal();

        channelHandlerContext.writeAndFlush(response);

    }
}
