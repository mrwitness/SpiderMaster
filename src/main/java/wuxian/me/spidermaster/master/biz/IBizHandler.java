package wuxian.me.spidermaster.master.biz;

import io.netty.channel.socket.SocketChannel;
import wuxian.me.spidermaster.rpc.RpcRequest;

/**
 * Created by wuxian on 11/6/2017.
 */
public interface IBizHandler {

    void handleRequest(RpcRequest request, SocketChannel channel);

    String getMethodName();
}
