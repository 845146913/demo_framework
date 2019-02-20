package com.example.core.annotations;

import java.lang.annotation.*;

/**
 * Service,Controller的注解一致
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {

    String value() default "";
}
