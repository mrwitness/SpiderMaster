package wuxian.me.spidermaster.agent.rpccore;

import com.sun.istack.internal.Nullable;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import wuxian.me.spidermaster.agent.IClient;
import wuxian.me.spidermaster.rpc.RpcResponse;

/**
 * Created by wuxian on 27/5/2017.
 * <p>
 * used to connect to @SpiderMaster
 */
public class AgentRpcResponseHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private IClient client;

    public AgentRpcResponseHandler(@Nullable IClient client) {
        this.client = client;
    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext,
                                RpcResponse rpcResponse) throws Exception {
        if (client == null) {
            return;
        }
        client.onRpcResponse(rpcResponse);
    }

}
