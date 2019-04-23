package com.butterknife.library;

import android.view.View;

/**
 * Copyright (c), 2018-2019
 *
 * @author: lixin
 * Date: 2019-04-23
 * Description:
 */
public abstract class DebouncingOnClickListener implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        doClick(v);
    }

    public abstract void doClick(View view);
}
