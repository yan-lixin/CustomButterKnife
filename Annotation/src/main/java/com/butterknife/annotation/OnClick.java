package com.butterknife.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Copyright (c), 2018-2019
 *
 * @author: lixin
 * Date: 2019-04-23
 * Description:
 */
@Target(ElementType.METHOD) //作用于方法之上
@Retention(RetentionPolicy.CLASS) //编译期
public @interface OnClick {
    int value();
}
