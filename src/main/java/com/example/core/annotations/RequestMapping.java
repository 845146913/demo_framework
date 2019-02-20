package com.example.core.annotations;

import java.lang.annotation.*;

/**
 * url映射
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RequestMapping {

    String value() default "";
}
