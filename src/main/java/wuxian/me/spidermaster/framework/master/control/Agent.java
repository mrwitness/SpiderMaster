package wuxian.me.spidermaster.framework.master.control;

import io.netty.channel.socket.SocketChannel;
import wuxian.me.spidercommon.model.Proxy;
import wuxian.me.spidermaster.framework.common.Spider;
import wuxian.me.spidermaster.framework.common.StatusEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuxian on 26/5/2017.
 */
public class Agent {

    //该agent的状态变化表
    private List<StatusEnum> statusList = new ArrayList<StatusEnum>();

    //当前agent的状态
    private StatusEnum currentState = null;

    //Fixme:目前并没有给赋值
    //当前使用的代理
    private Proxy currentProxy = null;

    //当前运行在的channel
    private SocketChannel channel;

    //当前agent上工作着哪些spider
    private List<Spider> spiderList = new ArrayList<Spider>();

    public List<StatusEnum> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<StatusEnum> statusList) {
        this.statusList = statusList;
    }

    public StatusEnum getCurrentState() {
        return currentState;
    }

    public void setCurrentState(StatusEnum currentState) {
        this.currentState = currentState;

        this.statusList.add(currentState);
    }

    public Proxy getCurrentProxy() {
        return currentProxy;
    }

    public void setCurrentProxy(Proxy currentProxy) {
        this.currentProxy = currentProxy;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public void setChannel(SocketChannel channel) {
        this.channel = channel;
    }

    public List<Spider> getSpiderList() {
        return spiderList;
    }

    public void setSpiderList(List<Spider> spiderList) {
        this.spiderList = spiderList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Agent agent = (Agent) o;

        return channel != null ? channel.equals(agent.channel) : agent.channel == null;

    }

    //Fixme:若断线重连了 那么目前的方案就认为是另一个agent？？？
    //Todo:解决方案是可以给它赋予一个agent id
    @Override
    public int hashCode() {
        return channel != null ? channel.hashCode() : 0;
    }

    public String printAgentString() {

        StringBuilder builder = new StringBuilder("Agent:{");
        builder.append(" socketChannel: local:" + channel.localAddress().toString() + " remote:" + channel.remoteAddress().toString());
        builder.append(" statusList: ");

        for (int i = 0; i < statusList.size(); i++) {

            if (i + 1 == statusList.size()) {
                builder.append(" " + statusList.get(i).toString());
            } else {
                builder.append(" " + statusList.get(i).toString() + "-->");
            }
        }
        builder.append("}");

        return builder.toString();
    }
}
