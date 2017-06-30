package wuxian.me.spidermaster;

import wuxian.me.spidercommon.model.Proxy;
import wuxian.me.spidermaster.biz.provider.IProvider;
import wuxian.me.spidermaster.biz.provider.Provider;

/**
 * Created by wuxian on 21/6/2017.
 */

@Provider(provide = "proxy")
public class FakeProxyProvider implements IProvider<Proxy> {
    @Override
    public Proxy provide() {
        Proxy proxy = new Proxy("12.32.33.12", 3444);
        return proxy;
    }
}
