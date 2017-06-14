package wuxian.me.spidermaster.agent.rpccore;

import com.sun.istack.internal.NotNull;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import wuxian.me.spidermaster.agent.IClient;
import wuxian.me.spidermaster.rpc.RpcRequest;
import wuxian.me.spidermaster.rpc.RpcResponse;

import java.util.Map;

/**
 * Created by wuxian on 9/6/2017.
 * <p>
 * 这里的agent是被命令的一方 因此应该立马返回一个标准回应
 */
public class MasterRpcRequestHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private IClient client;

    public MasterRpcRequestHandler(@NotNull IClient client) {

        this.client = client;
    }

    //Todo:是否需要返回一个默认的response
    protected void channelRead0(ChannelHandlerContext channelHandlerContext
            , RpcRequest request) throws Exception {
        this.client.onMessage(request);  //
    }
}
