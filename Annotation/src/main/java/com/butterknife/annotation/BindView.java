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
@Target(ElementType.FIELD) //作用于属性之上
@Retention(RetentionPolicy.CLASS) //编译期
public @interface BindView {
    int value();
}
