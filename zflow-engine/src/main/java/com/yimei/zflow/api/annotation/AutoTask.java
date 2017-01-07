package com.yimei.zflow.api.annotation;

import java.lang.annotation.*;

/**
 * Created by hary on 16/12/23.
 */

@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoTask {
    String description() default "";   // 描述
    String[] points(); // 负责哪些数据点
    String in();  // 在哪个edge上
}

