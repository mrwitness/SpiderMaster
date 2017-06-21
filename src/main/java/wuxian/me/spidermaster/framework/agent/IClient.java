package wuxian.me.spidermaster.framework.agent;

import io.netty.channel.socket.SocketChannel;
import wuxian.me.spidermaster.framework.agent.request.IRpcCallback;
import wuxian.me.spidermaster.framework.rpc.RpcRequest;
import wuxian.me.spidermaster.framework.rpc.RpcResponse;

/**
 * Created by wuxian on 26/5/2017.
 */
public interface IClient {

    void init();

    SocketChannel channel();

    void asyncConnect(String serverIp, int serverPort);

    boolean isConnected();

    //主动调用disconnect
    void doDisconnectFromServer();

    Object onReceiveMessage(RpcRequest request);

    void onRpcResponse(RpcResponse response);

    void asyncSendMessage(RpcRequest request, IRpcCallback callback);

    void onDisconnectByServer();

}
