package wuxian.me.spidermaster.framework.master;

import com.sun.istack.internal.NotNull;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidercommon.util.IpPortUtil;
import wuxian.me.spidermaster.framework.common.IpPortNotValidException;
import wuxian.me.spidermaster.framework.master.control.AgentRecorder;
import wuxian.me.spidermaster.framework.master.control.ConnectionManager;
import wuxian.me.spidermaster.framework.master.handler.HandlerScanner;
import wuxian.me.spidermaster.framework.rpc.RpcDecoder;
import wuxian.me.spidermaster.framework.rpc.RpcEncoder;
import wuxian.me.spidermaster.framework.rpc.RpcRequest;
import wuxian.me.spidermaster.framework.rpc.RpcResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuxian on 18/5/2017.
 */
public class MasterServer extends ChannelInboundHandlerAdapter {

    private boolean started = false;
    private String host = null;
    private int port;

    public MasterServer(@NotNull String host, int port) {
        this.host = host;
        this.port = port;

        if (!IpPortUtil.isValidIpPort(host + ":" + port)) {
            throw new IpPortNotValidException();
        }
    }

    public void start() {
        if (started) {
            return;
        }
        started = true;
        HandlerScanner.scanAndCollect();

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
                                    .addLast(new AllRequestHandler(socketChannel))
                                    .addLast(MasterServer.this)
                            ;
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.AUTO_READ, false)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            LogManager.info("Master Server begin to bind port: " + port);
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

    //Todo: handle exception
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
