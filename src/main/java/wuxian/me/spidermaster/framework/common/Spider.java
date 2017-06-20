package wuxian.me.spidermaster.framework.common;

import wuxian.me.spidercommon.model.SpiderFeature;

/**
 * Created by wuxian on 27/5/2017.
 *
 * Spider具有的爬虫的能力
 * 能力应该包含spider的class名字,该class对应的url pattern
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
