package wuxian.me.spidermaster.framework.master;

import com.sun.istack.internal.NotNull;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import wuxian.me.spidermaster.framework.common.GsonProvider;
import wuxian.me.spidermaster.framework.master.provider.Resource;
import wuxian.me.spidermaster.framework.master.provider.ResourcePool;
import wuxian.me.spidermaster.framework.rpc.RpcResponse;

/**
 * Created by wuxian on 21/6/2017.
 * Rpc Response
 */
public class ResponseHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private SocketChannel channel;

    public ResponseHandler(@NotNull SocketChannel channel) {
        this.channel = channel;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext
            , RpcResponse rpcResponse) throws Exception {

        String reqId = rpcResponse.requestId;
        String data = (String) rpcResponse.result;

        if (data == null || data.length() == 0) {
            return;
        }
        Resource resource = GsonProvider.gson().fromJson(data, Resource.class);

        if (resource != null) {
            ResourcePool.putResource(reqId, resource);
        }
    }
}
