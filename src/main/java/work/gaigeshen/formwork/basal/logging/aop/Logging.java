package work.gaigeshen.formwork.basal.logging.aop;

import java.lang.annotation.*;

/**
 *
 * @author gaigeshen
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Logging {

    String value() default "None";

    boolean first() default false;
}
