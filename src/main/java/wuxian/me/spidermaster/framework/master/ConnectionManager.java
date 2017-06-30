package wuxian.me.spidermaster.framework.master;

import io.netty.channel.socket.SocketChannel;
import wuxian.me.spidercommon.log.LogManager;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by wuxian on 18/5/2017.
 * <p>
 * 记录所有连接
 */
public class ConnectionManager {

    private static Set<SocketChannel> channelSet = Collections.synchronizedSet(new HashSet<SocketChannel>());

    private ConnectionManager() {
        ;
    }

    public static void recordConnection(SocketChannel channel) {
        if (channel == null) {
            return;
        }

        LogManager.info("ConnectionManager.recordConnection, channel: " + channel.toString());
        if (!channelSet.contains(channel)) {
            channelSet.add(channel);
            return;
        }
    }

    public static boolean removeConnection(SocketChannel channel) {

        if (!containsChannel(channel)) {
            return true;
        }

        if (channelSet.contains(channel)) {
            channelSet.remove(channel);
            return true;
        }

        return false;
    }

    public static boolean containsChannel(SocketChannel channel) {
        if (channel == null) {
            return false;
        }

        return channelSet.contains(channel);
    }

}
