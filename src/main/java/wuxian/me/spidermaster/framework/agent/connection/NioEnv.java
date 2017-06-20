package wuxian.me.spidermaster.framework.agent.connection;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import wuxian.me.spidermaster.framework.rpc.RpcDecoder;
import wuxian.me.spidermaster.framework.rpc.RpcEncoder;
import wuxian.me.spidermaster.framework.rpc.RpcRequest;
import wuxian.me.spidermaster.framework.rpc.RpcResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuxian on 19/6/2017.
 */
public class NioEnv {

    private static EventLoopGroup group = null;
    private static Bootstrap bootstrap = null;
    private static boolean inited = false;

    private NioEnv() {
    }

    public static void init() {
        inited = true;

        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();

        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10 * 1000);
        bootstrap.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true);
    }

    public static void unInit() {
        inited = false;
        group.shutdownGracefully();
    }

    public static Bootstrap getAgentBootstrap() {
        return getAgentBootstrap(null);
    }

    public static Bootstrap getAgentBootstrap(final OnChannelInit initializer) {
        if (!inited) {
            return null;
        }

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel channel) throws Exception {
                if (initializer != null) {
                    initializer.onChannelInit(channel);
                }
                List<Class<?>> classList = new ArrayList<Class<?>>();
                classList.add(RpcResponse.class);
                classList.add(RpcRequest.class);
                channel.pipeline()
                        .addLast(new RpcEncoder(classList))
                        .addLast(new RpcDecoder(classList));
            }
        });

        return bootstrap;
    }

    public interface OnChannelInit {
        void onChannelInit(SocketChannel channel);
    }
}
