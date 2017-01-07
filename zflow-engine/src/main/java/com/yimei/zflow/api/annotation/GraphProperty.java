package com.yimei.zflow.api.annotation;

import java.lang.annotation.*;

/**
 * Created by hary on 16/12/23.
 */

@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GraphProperty {

    String graphType();

    boolean persistent() default true;

    int timeout() default 200;

    String initial();
}
