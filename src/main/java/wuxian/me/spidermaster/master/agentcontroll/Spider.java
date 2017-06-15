package wuxian.me.spidermaster.master.agentcontroll;

import sun.security.provider.ConfigFile;
import wuxian.me.spidercommon.model.SpiderFeature;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuxian on 27/5/2017.
 *
 * Spider具有的爬虫的能力
 * 能力应该包含spider的class名字,该class对应的url pattern。
 *
 * Todo:监控成功的任务数,失败的任务数
 *
 */
public class Spider {

    private String name;
    private String pattern;

    private Spider(String name,String pattern) {
        this.name = name;
        this.pattern = pattern;
    }

    public static Spider fromFeature(SpiderFeature feature) {

        if(feature == null) {
            return null;
        }

        return new Spider(feature.className,feature.urlPattern);
    }


}
