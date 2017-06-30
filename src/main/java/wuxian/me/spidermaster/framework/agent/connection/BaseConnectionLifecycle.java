package wuxian.me.spidermaster.framework.agent.connection;

import io.netty.channel.socket.SocketChannel;

/**
 * Created by wuxian on 30/6/2017.
 */
public abstract class BaseConnectionLifecycle implements ConnectionLifecycle {
    @Override
    public void onConnectionBuilded(SocketChannel channel) {

    }

    @Override
    public void onConnectFail() {

    }

    @Override
    public void onConnectException() {

    }

    @Override
    public void onConnectionClosed(boolean isClient) {

    }
}
