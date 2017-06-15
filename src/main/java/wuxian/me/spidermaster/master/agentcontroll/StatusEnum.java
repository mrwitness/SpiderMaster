package wuxian.me.spidermaster.master.agentcontroll;

/**
 * Created by wuxian on 11/6/2017.
 */
public enum StatusEnum {

    REGISTERED,

    BLOCKED,

    PAUSED,

    WORKING,

    SWITCH_PROXY,  //switch失败的时候会进入paused状态

    EXIT,          //可能会被master命令进入停止状态

    DISCONNECTED,
}
