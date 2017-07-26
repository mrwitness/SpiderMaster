package wuxian.me.spidermaster.biz.control;

/**
 * Created by wuxian on 26/7/2017.
 */
public interface IHealthChecker<T> {

    boolean isHealthy(T model);
}
