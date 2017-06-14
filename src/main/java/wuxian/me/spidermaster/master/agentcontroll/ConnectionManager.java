package wuxian.me.spidermaster.master.agentcontroll;

import io.netty.channel.socket.SocketChannel;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by wuxian on 18/5/2017.
 * <p>
 * 处理所有连接
 *
 * 定时清理中断心跳的connection
 */
public class ConnectionManager {

    private static Set<SocketChannel> channelSet = Collections.synchronizedSet(new HashSet<SocketChannel>());

    private ConnectionManager() {
        ;
    }

    public static void recordConnection(SocketChannel channel) {

        channelSet.add(channel);
    }

}
