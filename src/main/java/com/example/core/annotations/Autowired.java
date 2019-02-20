package com.example.core.annotations;

import java.lang.annotation.*;


/**
 * 暂时实现注解到字段上
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Documented
public @interface Autowired {

    boolean required() default true;

}
