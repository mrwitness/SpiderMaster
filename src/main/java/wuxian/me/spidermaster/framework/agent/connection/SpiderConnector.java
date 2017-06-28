package wuxian.me.spidermaster.framework.agent.connection;

import com.sun.istack.internal.Nullable;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.SocketChannel;
import wuxian.me.spidercommon.log.LogManager;

/**
 * Created by wuxian on 9/6/2017.
 */
public class SpiderConnector implements Runnable {

    private String host;
    private int port;
    private SocketChannel socketChannel;

    private IConnectCallback connectCallback;

    public SpiderConnector(String host, int port, @Nullable IConnectCallback callback) {
        this.host = host;
        this.port = port;
        this.connectCallback = callback;
    }

    public void connectTo(String host, int port) {

        Bootstrap bootstrap = NioEnv.getAgentBootstrap(new NioEnv.OnChannelInit() {
            @Override
            public void onChannelInit(SocketChannel channel) {
                SpiderConnector.this.socketChannel = channel;
            }
        });

        ChannelFuture future = bootstrap.connect(host, port);
        future.awaitUninterruptibly();

        if (future.isCancelled()) {
            if (connectCallback != null) {
                connectCallback.onFail();
            }
            return;

        } else if (!future.isSuccess()) {
            if (connectCallback != null) {
                connectCallback.onFail();
            }
            return;
        }

        connectCallback.onSuccess(socketChannel);

        socketChannel.closeFuture().syncUninterruptibly();

        if (connectCallback != null) {
            connectCallback.onClosed();
        }
        LogManager.info("SpiderConnector.closed");
    }

    public void run() {
        connectTo(host, port);
    }

}
