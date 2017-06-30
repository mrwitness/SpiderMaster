package wuxian.me.spidermaster.biz.control;

import io.netty.channel.socket.SocketChannel;
import wuxian.me.spidercommon.log.LogManager;
import wuxian.me.spidermaster.biz.provider.ProviderManager;

import java.util.*;

/**
 * Created by wuxian on 18/5/2017.
 */
public class AgentRecorder {

    //Todo: 这里的set没什么卵用 后面可以去掉
    private static Set<Agent> agentSet = Collections.synchronizedSet(new HashSet<Agent>());

    private static Map<SocketChannel, Agent> map = new HashMap<SocketChannel, Agent>();

    private AgentRecorder() {
    }


    public static void startPrintAgentThread() {

        Thread thread = new Thread() {
            @Override
            public void run() {

                while (true) {
                    try {
                        sleep(10000);
                    } catch (InterruptedException e) {
                        ;
                    }

                    Set<Agent> agents = new HashSet<Agent>(agentSet);
                    if (agents != null && agents.size() != 0) {
                        LogManager.info("begin to print All Agents");
                    }
                    for (Agent agent : agents) {
                        LogManager.info(agent.printAgentString());
                    }
                }

            }
        };

        thread.setName("printAgentThread");
        thread.start();
    }

    public static void recordAgent(Agent agent) {
        if(agent == null) {
            return;
        }
        if(!agentSet.contains(agent)) {
            synchronized (agentSet) {
                if(!agentSet.contains(agent)) {
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
}
