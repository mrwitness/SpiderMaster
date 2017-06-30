package wuxian.me.spidermaster.framework.master.handler;

import io.netty.channel.socket.SocketChannel;
import wuxian.me.spidermaster.framework.rpc.RpcRequest;

/**
 * Created by wuxian on 11/6/2017.
 */
public interface IRpcRequestHandler {

    Object handleRequest(RpcRequest request, SocketChannel channel) throws HandlerExcepiton;

    String getMethodName();

    boolean needResponse();
}
