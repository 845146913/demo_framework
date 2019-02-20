package com.example.core.annotations;

import java.lang.annotation.*;

/**
 * 参数注解
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {

    /**
     * 必填项
     * @return
     */
    String value();
}
