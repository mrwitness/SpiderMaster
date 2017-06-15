package wuxian.me.spidermaster.master.biz;

import io.netty.channel.socket.SocketChannel;
import wuxian.me.spidermaster.util.RpcMethodName;
import wuxian.me.spidermaster.rpc.RpcRequest;

/**
 * Created by wuxian on 11/6/2017.
 * <p>
 * Todo:
 */

@RpcMethodName(methodName = "reportStatus")
public class ReportStatusHandler extends BaseBizHandler {

    public void handleRequest(RpcRequest request, SocketChannel channel) {

    }
}
