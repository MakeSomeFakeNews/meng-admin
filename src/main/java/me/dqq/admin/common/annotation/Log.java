package me.dqq.admin.common.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解，标注在 Controller 方法上
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    /** 模块名 */
    String module() default "";

    /** 操作名 */
    String action() default "";
}
