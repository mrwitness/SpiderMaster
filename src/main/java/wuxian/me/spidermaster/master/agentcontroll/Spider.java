package wuxian.me.spidermaster.master.agentcontroll;

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

    private List<SpiderFeature> featureList = new ArrayList<SpiderFeature>();

    public void addFeature(SpiderFeature feature) {
        if (feature == null) {
            return;
        }

        featureList.add(feature);
    }
}
