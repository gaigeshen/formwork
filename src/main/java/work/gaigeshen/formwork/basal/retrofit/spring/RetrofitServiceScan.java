package work.gaigeshen.formwork.basal.retrofit.spring;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 用于设置服务接口所在包
 *
 * @author gaigeshen
 */
@Import({RetrofitServiceScannerRegistrar.class})
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface RetrofitServiceScan {

    Class<?>[] value() default {};
}
