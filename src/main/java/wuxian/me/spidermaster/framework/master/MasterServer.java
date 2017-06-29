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
import wuxian.me.spidermaster.framework.master.handler.HandlerScanner;
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

    public MasterServer(@NotNull String host, int port) {
        this.host = host;
        this.port = port;

        if (!IpPortUtil.isValidIpPort(host + ":" + port)) {
            throw new InitEnvException("Ip or Port is not valid");
        }
    }

    public void start() {
        if (started) {
            return;
        }
        started = true;
        HandlerScanner.scanAndCollect();

        EventLoopGroup boss = new NioEventLoopGroup(10);
        EventLoopGroup worker = new NioEventLoopGroup(10);

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
                                    .addLast(new ResponseHandler(socketChannel));

                            socketChannel.closeFuture().addListener(new ChannelFutureListener() {
                                @Override
                                public void operationComplete(ChannelFuture channelFuture) throws Exception {

                                    //Todo: 处理该关闭的连接...
                                    LogManager.error("receive channel close message");
                                }
                            });
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.AUTO_READ, true)   //If Not Being Set,Can't accpet multi client!!!
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            LogManager.info("Master Server begin to bind port: " + port);
            ChannelFuture future = bootstrap.bind(host, port).sync();
            LogManager.info("Bind success");

            //AgentRecorder.startPrintAgentThread(); //暂时注释掉 //Todo

            future.channel().closeFuture().sync();

            LogManager.info("MasterServer Socket closed");
        } catch (InterruptedException e) {

        } finally {
            worker.shutdownGracefully();
            boss.shutdownGracefully();
        }

    }


}
