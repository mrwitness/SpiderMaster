package wuxian.me.spidermaster.biz.control;

/**
 * Created by wuxian on 26/7/2017.
 */
public class AgentHealthChecker implements IHealthChecker<Agent> {

    private static AgentHealthChecker ins = new AgentHealthChecker();

    public static AgentHealthChecker getDefault() {
        return ins;
    }

    private AgentHealthChecker() {
    }

    //Todo:检查Agent.statusList
    //若出现太多次切换proxy,那么认为对方服务器已经识别出本爬虫并且切换代理也无法破解屏蔽,此时应该通知关闭agent。
    @Override
    public boolean isHealthy(Agent model) {
        return true;
    }
}
