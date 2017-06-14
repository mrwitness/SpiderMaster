package wuxian.me.spidermaster.master.biz;

import io.netty.channel.socket.SocketChannel;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidermaster.util.RpcBizName;
import wuxian.me.spidermaster.rpc.RpcRequest;

/**
 * Created by wuxian on 18/5/2017.
 * <p>
 * Todo:
 */

@RpcBizName(methodName = "register")
public class RegisterHandler extends BaseBizHandler {

    public void handleRequest(RpcRequest request, SocketChannel channel) {

        LogManager.info("receive register rpc");
        LogManager.info("current thread: "+Thread.currentThread());
        LogManager.info("current channel: "+channel.toString());
    }
}
