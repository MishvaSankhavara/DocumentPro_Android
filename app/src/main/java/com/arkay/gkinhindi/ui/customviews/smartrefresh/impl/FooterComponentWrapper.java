package com.arkay.gkinhindi.ui.customviews.smartrefresh.impl;

import android.annotation.SuppressLint;
import android.view.View;

import com.arkay.gkinhindi.ui.customviews.smartrefresh.api.RefreshFooterComponent;
import com.arkay.gkinhindi.ui.customviews.smartrefresh.internal.RefreshInternalAbstract;

@SuppressLint("ViewConstructor")
public class FooterComponentWrapper extends RefreshInternalAbstract implements RefreshFooterComponent/*, InvocationHandler */{

    public FooterComponentWrapper(View wrapper) {
        super(wrapper);
    }
}
