package wuxian.me.spidermaster.master.agentcontroll;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by wuxian on 18/5/2017.
 * <p>
 * 记录所有agent
 * <p>
 * Todo
 */
public class AgentRecorder {

    private Set<Agent> agentSet = Collections.synchronizedSet(new HashSet<Agent>());

    private AgentRecorder() {
    }


}
