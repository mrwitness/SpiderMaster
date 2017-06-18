package wuxian.me.spidermaster.master;

import com.sun.istack.internal.NotNull;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidermaster.master.agentcontroll.AgentRecorder;
import wuxian.me.spidermaster.master.agentcontroll.ConnectionManager;
import wuxian.me.spidermaster.master.core.AgentRpcRequestHandler;
import wuxian.me.spidermaster.rpc.RpcDecoder;
import wuxian.me.spidermaster.rpc.RpcEncoder;
import wuxian.me.spidermaster.rpc.RpcRequest;
import wuxian.me.spidermaster.rpc.RpcResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuxian on 18/5/2017.
 * Todo:断线重连api设计
 */
public class MasterServer {

    private boolean started = false;

    private String host = null;
    private int port;

    public MasterServer(@NotNull String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        if (started) {
            return;
        }
        started = true;

        LogManager.info("MasterServer starting...");

        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(boss, worker).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {

                            ConnectionManager.recordConnection(socketChannel);

                            List<Class<?>> classList = new ArrayList<Class<?>>();
                            classList.add(RpcResponse.class);
                            classList.add(RpcRequest.class);

                            socketChannel.pipeline()
                                    .addLast(new RpcDecoder(classList))
                                    .addLast(new RpcEncoder(classList))
                                    .addLast(new AgentRpcRequestHandler(socketChannel))
                            ;
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.AUTO_READ, false)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            LogManager.info("Master Server begin to bind port: "+port);
            ChannelFuture future = bootstrap.bind(host, port).sync();
            LogManager.info("Bind success");

            AgentRecorder.startPrintAgentThread();

            future.channel().read();

            future.channel().closeFuture().sync();

            LogManager.info("MasterServer Socket closed");
        } catch (InterruptedException e) {

        } finally {
            worker.shutdownGracefully();
            boss.shutdownGracefully();
        }

    }
}
