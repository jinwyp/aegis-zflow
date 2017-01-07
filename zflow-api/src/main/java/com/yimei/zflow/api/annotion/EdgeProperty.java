package com.yimei.zflow.api.annotion;

import java.lang.annotation.*;

/**
 * Created by hary on 16/12/23.
 */

@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EdgeProperty {
    String begin();
    String end();
}

