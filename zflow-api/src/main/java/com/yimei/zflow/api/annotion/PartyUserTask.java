package com.yimei.zflow.api.annotion;

import java.lang.annotation.*;

/**
 * Created by hary on 16/12/23.
 */

@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PartyUserTask {
    String description() default "";
    String guidKey();
    String[] tasks();
    String in();
}

