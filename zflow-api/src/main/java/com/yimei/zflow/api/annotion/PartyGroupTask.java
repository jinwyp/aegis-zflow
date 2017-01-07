package com.yimei.zflow.api.annotion;

import java.lang.annotation.*;

/**
 * Created by hary on 16/12/23.
 */


@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PartyGroupTask {
    String ggidKey();
    String[] tasks();
    String in();
}
