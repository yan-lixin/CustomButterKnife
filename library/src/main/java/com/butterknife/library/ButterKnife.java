package com.butterknife.library;

import android.app.Activity;

/**
 * Copyright (c), 2018-2019
 *
 * @author: lixin
 * Date: 2019-04-23
 * Description:
 */
public class ButterKnife {

    public static void bind(Activity activity) {
        String className = activity.getClass().getName() + "$ViewBinder";
        try {
            Class<?> viewBinderClass = Class.forName(className);
            //实例化对象
            ViewBinder viewBinder = (ViewBinder) viewBinderClass.newInstance();
            viewBinder.bind(activity);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

}
