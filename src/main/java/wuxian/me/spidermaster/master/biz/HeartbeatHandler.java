package wuxian.me.spidermaster.master.biz;

import io.netty.channel.socket.SocketChannel;
import wuxian.me.spidermaster.util.RpcBizName;
import wuxian.me.spidermaster.rpc.RpcRequest;

/**
 * Created by wuxian on 11/6/2017.
 * <p>
 * Todo:
 */

@RpcBizName(methodName = "heartbeat")
public class HeartbeatHandler extends BaseBizHandler {

    public void handleRequest(RpcRequest request, SocketChannel channel) {

    }
}
