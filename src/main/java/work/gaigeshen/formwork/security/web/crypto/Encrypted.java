package work.gaigeshen.formwork.security.web.crypto;

import java.lang.annotation.*;

/**
 * 标记注解，用于标记哪些控制器方法需要执行解密和加密操作
 *
 * @author gaigeshen
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Encrypted {
}
