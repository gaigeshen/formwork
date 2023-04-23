package work.gaigeshen.formwork.controller;

import java.lang.annotation.*;

/**
 * 标记接口，用于标记哪些控制器方法需要执行解密操作
 *
 * @author gaigeshen
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Decrytion {
}
