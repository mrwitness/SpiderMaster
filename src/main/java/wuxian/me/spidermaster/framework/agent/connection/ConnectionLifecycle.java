package wuxian.me.spidermaster.framework.agent.connection;

import io.netty.channel.socket.SocketChannel;

/**
 * Created by wuxian on 10/6/2017.
 */
public interface ConnectionLifecycle {

    void onConnectionBuilded(SocketChannel channel);

    void onConnectFail();

    void onConnectException();

    void onConnectionClosed(SocketChannel channel, boolean isClient);
}
