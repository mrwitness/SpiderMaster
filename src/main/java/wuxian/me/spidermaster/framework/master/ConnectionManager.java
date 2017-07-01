package wuxian.me.spidermaster.framework.master;

import io.netty.channel.socket.SocketChannel;
import wuxian.me.spidercommon.log.LogManager;

import java.util.*;

/**
 * Created by wuxian on 18/5/2017.
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

            for (ConnectionsCallback cb : callbackList) {
                cb.onConnAdd(channel);
            }

            return;
        }
    }

    public static boolean removeConnection(SocketChannel channel) {

        if (!containsChannel(channel)) {
            return true;
        }

        if (channelSet.contains(channel)) {
            channelSet.remove(channel);

            for (ConnectionsCallback cb : callbackList) {
                cb.onConnRemove(channel);
            }

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

    private static List<ConnectionsCallback> callbackList = new ArrayList<ConnectionsCallback>();

    public static void addCallback(ConnectionsCallback cb) {

        if (cb != null && !callbackList.contains(cb)) {
            callbackList.add(cb);
        }
    }

    public static void removeCallback(ConnectionsCallback cb) {
        if (cb != null && callbackList.contains(cb)) {
            callbackList.remove(cb);
        }
    }

    public interface ConnectionsCallback {

        void onConnAdd(SocketChannel channel);

        void onConnRemove(SocketChannel channel);
    }
}
