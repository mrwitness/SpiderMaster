package wuxian.me.spidermaster.biz.master.provider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by wuxian on 21/6/2017.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Requestor {

    String REQUEST_RESROURCE = "requestResource";

    String request() default "";
}
