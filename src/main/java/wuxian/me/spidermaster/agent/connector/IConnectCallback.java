package wuxian.me.spidermaster.agent.connector;

import io.netty.channel.socket.SocketChannel;

/**
 * Created by wuxian on 10/6/2017.
 */
public interface IConnectCallback {

    void onSuccess(SocketChannel channel);

    void onFail();

    void onException();

    void onClosed();
}
