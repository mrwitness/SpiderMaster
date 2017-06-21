package wuxian.me.spidermaster.framework.master.provider;

import com.sun.istack.internal.Nullable;
import io.netty.channel.socket.SocketChannel;
import wuxian.me.spidermaster.framework.master.control.Agent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wuxian on 21/6/2017.
 */
public class ProviderManager {

    private static Map<String, Agent> providerMap = new ConcurrentHashMap<String, Agent>();

    private ProviderManager() {
    }

    //Todo:be called on @RegisterHandler
    public static void registerProvider(String provider, Agent agent) {
        if (provider == null || provider.length() == 0 || agent == null) {
            return;
        }

        providerMap.put(provider, agent);
    }

    @Nullable
    public static Agent findProvider(String provider) {
        if (provider == null || provider.length() == 0) {
            return null;
        }

        return providerMap.get(provider);
    }

    @Nullable
    public static SocketChannel findProviderChannle(String provider) {
        Agent agent = findProvider(provider);
        if (agent == null) {
            return null;
        }

        return agent.getChannel();
    }
}
