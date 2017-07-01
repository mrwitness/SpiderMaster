package wuxian.me.spidermaster.biz.control;

import io.netty.channel.socket.SocketChannel;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidermaster.biz.provider.ProviderManager;
import wuxian.me.spidermaster.framework.master.ConnectionManager;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Thread.sleep;

/**
 * Created by wuxian on 18/5/2017.
 */
public class AgentRecorder implements Runnable {

    private static long PRINT_INTERVAL = 1000 * 20;  //20s

    private static Set<Agent> agentSet = new HashSet<Agent>();

    private static Map<SocketChannel, Agent> map = new HashMap<SocketChannel, Agent>();

    private static Thread printThread = null;
    private static AtomicBoolean started = new AtomicBoolean(false);
    private static AtomicBoolean isStarting = new AtomicBoolean(false);

    public static void recordAgent(Agent agent) {
        if (agent == null) {
            return;
        }
        if (!agentSet.contains(agent)) {
            synchronized (agentSet) {
                if (!agentSet.contains(agent)) {
                    agentSet.add(agent);
                }
                if (agent.getChannel() == null) {
                    return;
                }

                map.put(agent.getChannel(), agent);

                List<String> providerList = agent.getProviderList();
                if (providerList != null && providerList.size() != 0) {
                    for (String s : providerList) {
                        ProviderManager.registerProvider(s, agent);
                    }
                }
            }
        } else {  //这种情况一般是被reportStatus调用
            synchronized (agentSet) {
                agentSet.remove(agent);
                agentSet.add(agent);
                map.put(agent.getChannel(), agent);
            }

        }
    }

    public static Agent findByChannel(SocketChannel socketChannel) {
        if (socketChannel == null) {
            return null;
        }

        if (!map.containsKey(socketChannel)) {
            return null;
        }

        return map.get(socketChannel);
    }


    private AgentRecorder() {
    }

    public static void init() {

        ConnectionManager.addCallback(new ConnectionManager.ConnectionsCallback() {
            @Override
            public void onConnAdd(SocketChannel channel) {

            }

            @Override
            public void onConnRemove(SocketChannel channel) {

                LogManager.info("channel:" + channel.toString() + " disconnected!");

                if (map.keySet().contains(channel)) {
                    map.get(channel).setCurrentState(StatusEnum.DISCONNECTED);
                }
            }
        });
    }

    public static void stopPrintThread() {
        if (started.get() && printThread != null) {
            printThread.interrupt();
        }
    }

    public static void startPrintThread() {

        if (isStarting.get() || started.get()) {  //already connectiong
            return;
        }
        isStarting.set(true);

        printThread = new Thread(new AgentRecorder());
        printThread.setName("printAgentThread");
        printThread.start();
    }

    @Override
    public void run() {

        isStarting.set(false);
        started.set(true);
        while (true) {
            try {
                sleep(PRINT_INTERVAL);
            } catch (InterruptedException e) {
                break;
            }

            Set<Agent> agents = new HashSet<Agent>(agentSet);
            if (agents != null && agents.size() != 0) {
                LogManager.info("begin to print All Agents");
            }
            for (Agent agent : agents) {
                if (agent.getCurrentState() == StatusEnum.DISCONNECTED) {  //don't print those agent who is disconnected
                    continue;
                }
                LogManager.info(agent.printAgentString());
            }
        }

        started.set(false);
    }
}
