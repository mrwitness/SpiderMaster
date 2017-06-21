package wuxian.me.spidermaster.framework.master.provider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by wuxian on 21/6/2017.
 */
@Role(role = Roles.ROLE_PROVIDER)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Provider {

    String provide() default "";

}
