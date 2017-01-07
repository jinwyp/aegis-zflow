package com.yimei.zflow.api.annotation;

import java.lang.annotation.*;

/**
 * Created by hary on 16/12/23.
 */

@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UserTask {
    String description() default "";
    String[] points();
    String in();
}
