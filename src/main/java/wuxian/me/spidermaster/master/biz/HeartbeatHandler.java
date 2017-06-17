package wuxian.me.spidermaster.master.biz;

import io.netty.channel.socket.SocketChannel;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidermaster.rpc.RpcRetCode;
import wuxian.me.spidermaster.util.RpcMethodName;
import wuxian.me.spidermaster.rpc.RpcRequest;

/**
 * Created by wuxian on 11/6/2017.
 */

@RpcMethodName(methodName = "heartbeat")
public class HeartbeatHandler extends BaseBizHandler {

    public Object handleRequest(RpcRequest request, SocketChannel channel) throws BizErrorExcepiton {

        LogManager.info("HeartbeatHandler.handleRequest");
        return RpcRetCode.SUCCESS.ordinal();
    }
}
