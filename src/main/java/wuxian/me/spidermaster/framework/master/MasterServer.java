package wuxian.me.spidermaster.framework.master;

import com.sun.istack.internal.NotNull;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidercommon.util.IpPortUtil;
import wuxian.me.spidermaster.framework.common.InitEnvException;
import wuxian.me.spidermaster.framework.master.handler.HandlerManager;
import wuxian.me.spidermaster.framework.rpc.RpcDecoder;
import wuxian.me.spidermaster.framework.rpc.RpcEncoder;
import wuxian.me.spidermaster.framework.rpc.RpcRequest;
import wuxian.me.spidermaster.framework.rpc.RpcResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuxian on 18/5/2017.
 * Framework的职责越轻量越好。因此framework的职责：
 * 1 管理连接
 * 2 管理rpc handler,其他业务全部在这个框架之上进行延伸并放入biz包下,比如说转发请求能力,比如说管理agent能力。
 */
public class MasterServer {

    private boolean started = false;
    private String host = null;
    private int port;

    private ServerLifecycle lifecycle = null;

    public MasterServer(@NotNull String host, int port, ServerLifecycle lifecycle) {
        this.host = host;
        this.port = port;
        this.lifecycle = lifecycle;

        if (!IpPortUtil.isValidIpPort(host + ":" + port)) {
            throw new InitEnvException("Ip or Port is not valid");
        }
    }

    public MasterServer(@NotNull String host, int port) {
        this(host, port, null);
    }

    public void start() {
        if (started) {
            return;
        }
        started = true;

        LogManager.info("begin to scan rpc request handlers");
        HandlerManager.scanAndCollectHandlers();

        EventLoopGroup boss = new NioEventLoopGroup(10);
        EventLoopGroup worker = new NioEventLoopGroup(10);

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(boss, worker).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(final SocketChannel socketChannel) throws Exception {

                            LogManager.info("server received connection: " + socketChannel.toString());
                            ConnectionManager.recordConnection(socketChannel);

                            List<Class<?>> classList = new ArrayList<Class<?>>();
                            classList.add(RpcRequest.class);
                            classList.add(RpcResponse.class);

                            socketChannel.pipeline()
                                    .addLast(new RpcDecoder(classList))
                                    .addLast(new RpcEncoder(classList))
                                    .addLast(new AllRequestHandler(socketChannel));

                            socketChannel.closeFuture().addListener(new ChannelFutureListener() {
                                @Override
                                public void operationComplete(ChannelFuture channelFuture) throws Exception {

                                    LogManager.info("connection: " + socketChannel.toString() + " is closed");
                                    ConnectionManager.removeConnection(socketChannel);

                                }
                            });
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.AUTO_READ, true)   //If Not Being Set,Can't accpet multi client!!!
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            LogManager.info("server begin to bind port: " + port);
            ChannelFuture future = bootstrap.bind(host, port).sync();
            LogManager.info("bind success");

            if (lifecycle != null) {
                lifecycle.onBindSuccess(host, port);
            }

            serverSocket = future.channel();
            future.channel().closeFuture().sync();

            LogManager.info("server socket closed");
        } catch (InterruptedException e) {

        } finally {
            worker.shutdownGracefully();
            boss.shutdownGracefully();

            if (lifecycle != null) {
                lifecycle.onShutdown();
            }
        }

    }

    Channel serverSocket = null;

    public void forceClose() {

        LogManager.info("MasterServer.forceClose");
        if (serverSocket != null) {
            serverSocket.close().syncUninterruptibly();
        }
        LogManager.info("Server.close success");

    }


    public interface ServerLifecycle {

        void onBindSuccess(String ip, int port);

        void onShutdown();

    }

}
