package wuxian.me.spidermaster.framework.agent.connection;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.SocketChannel;
import wuxian.me.spidercommon.log.LogManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by wuxian on 9/6/2017.
 */
public class SpiderConnector implements Runnable {

    private String host;
    private int port;
    private SocketChannel socketChannel;

    private Thread connectionThread = null;
    private AtomicBoolean connected = new AtomicBoolean(false);
    private AtomicBoolean isConnecting = new AtomicBoolean(false);

    private static List<ConnectionLifecycle> connectCallbackList = new ArrayList<ConnectionLifecycle>();

    public static void addConnectCallback(ConnectionLifecycle callback) {
        if (callback != null && !connectCallbackList.contains(callback)) {
            connectCallbackList.add(callback);
        }
    }

    public static boolean removeCallback(ConnectionLifecycle callback) {

        if (callback != null && connectCallbackList.contains(callback)) {
            connectCallbackList.remove(callback);
            return true;
        }

        return false;
    }

    private static SpiderConnector ins = new SpiderConnector();

    public static SpiderConnector getInstance() {
        return ins;
    }

    private SpiderConnector() {
    }

    public void connectTo(final String host, final int port) {
        this.host = host;
        this.port = port;

        if (isConnecting.get() || connected.get()) {  //already connectiong
            return;
        }

        connectionThread = new Thread(this);
        connectionThread.setName("SpiderConnectorThread");
        connectionThread.start();
    }

    private boolean clientClose = false;

    public void forceDisconnect() {
        if (socketChannel != null) {
            clientClose = true;
            socketChannel.close().syncUninterruptibly();
        }
    }

    public void run() {
        clientClose = false;

        Bootstrap bootstrap = NioEnv.getAgentBootstrap(new NioEnv.OnChannelInit() {
            @Override
            public void onChannelInit(SocketChannel channel) {
                SpiderConnector.this.socketChannel = channel;
            }
        });

        ChannelFuture future = bootstrap.connect(host, port);
        future.awaitUninterruptibly();

        if (future.isCancelled()) {

            for (ConnectionLifecycle connectCallback : connectCallbackList) {
                connectCallback.onConnectFail();
            }

            isConnecting.set(false);
            connected.set(false);

            return;

        } else if (!future.isSuccess()) {
            for (ConnectionLifecycle connectCallback : connectCallbackList) {
                connectCallback.onConnectFail();
            }
            isConnecting.set(false);
            connected.set(false);
            return;
        }

        isConnecting.set(false);
        connected.set(true);

        for (ConnectionLifecycle connectCallback : connectCallbackList) {
            connectCallback.onConnectionBuilded(socketChannel);
        }

        socketChannel.closeFuture().syncUninterruptibly();

        connected.set(false);

        for (ConnectionLifecycle connectCallback : connectCallbackList) {
            connectCallback.onConnectionClosed(socketChannel, clientClose);
        }
        LogManager.info("SpiderConnector.closed");
    }

    public boolean isConnected() {
        return connected.get();
    }

}
