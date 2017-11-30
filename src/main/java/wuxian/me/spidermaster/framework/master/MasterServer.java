package wuxian.me.spidermaster.framework.master;

import com.sun.istack.internal.NotNull;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.log4j.Logger;
import wuxian.me.spidercommon.util.IpPortUtil;
import wuxian.me.spidermaster.biz.control.AgentRecorder;
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

    static Logger logger = Logger.getLogger("server");

    private static int DEFAULT_THREAD_NUM = 10;
    private boolean started = false;
    private String host = null;
    private int port;

    private int threadNum = DEFAULT_THREAD_NUM;

    private List<ServerLifecycle> lifecycles = new ArrayList<ServerLifecycle>();

    public void setThreadNum(int num) {
        this.threadNum = num;
    }

    public void addServerLifecyle(ServerLifecycle lifecycle) {
        if (lifecycle != null && !lifecycles.contains(lifecycle)) {
            lifecycles.add(lifecycle);
        }
    }

    public MasterServer(@NotNull String host, int port) {
        this.host = host;
        this.port = port;

        if (!IpPortUtil.isValidIpPort(host + ":" + port)) {
            throw new InitEnvException("Ip or Port is not valid");
        }

        addServerLifecyle(new ServerLifecycle() {
            @Override
            public void onBindSuccess(String ip, int port) {
                logger.info("bind success");

                AgentRecorder.init();
                AgentRecorder.startPrintThread();
            }

            @Override
            public void onShutdown() {
                logger.info("server socket closed");
            }

            @Override
            public void onReceiveConnection(SocketChannel socketChannel) {
                logger.info("server received connection: " + socketChannel.toString());
                ConnectionManager.recordConnection(socketChannel);
            }

            @Override
            public void onConnectionClosed(SocketChannel socketChannel) {
                logger.info("connection: " + socketChannel.toString() + " is closed");
                ConnectionManager.removeConnection(socketChannel);
            }
        });
    }

    public void start() {
        if (started) {
            return;
        }
        started = true;

        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup(threadNum);
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(boss, worker).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(final SocketChannel socketChannel) throws Exception {

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
                                    for (ServerLifecycle lifecycle : lifecycles) {
                                        lifecycle.onConnectionClosed(socketChannel);
                                    }
                                }
                            });
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.AUTO_READ, true)   //If Not Being Set,Can't accpet multi client!!!
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            logger.info("server begin to bind port: " + port);
            ChannelFuture future = bootstrap.bind(host, port).sync();


            for (ServerLifecycle lifecycle : lifecycles) {
                lifecycle.onBindSuccess(host, port);
            }

            serverSocket = future.channel();
            future.channel().closeFuture().sync();


        } catch (InterruptedException e) {

        } finally {
            worker.shutdownGracefully();
            boss.shutdownGracefully();

            for (ServerLifecycle lifecycle : lifecycles) {
                lifecycle.onShutdown();
            }
        }

    }

    Channel serverSocket = null;

    public void forceClose() {

        logger.warn("MasterServer.forceClose");
        if (serverSocket != null) {
            serverSocket.close().syncUninterruptibly();
        }
    }


    public interface ServerLifecycle {

        void onBindSuccess(String ip, int port);

        void onShutdown();

        void onReceiveConnection(SocketChannel socketChannel);

        void onConnectionClosed(SocketChannel socketChannel);

    }

}
