package wuxian.me.spidermaster.agent.biz;

import com.google.gson.Gson;

/**
 * Created by wuxian on 12/6/2017.
 */
public class GsonProvider {

    private static Gson gson;

    private GsonProvider() {
    }

    public static Gson gson() {
        if (gson == null) {
            synchronized (GsonProvider.class) {
                if (gson == null) {
                    gson = new Gson();
                }
            }
        }
        return gson;
    }
}
