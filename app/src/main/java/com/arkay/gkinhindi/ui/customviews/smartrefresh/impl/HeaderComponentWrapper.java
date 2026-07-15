package com.arkay.gkinhindi.ui.customviews.smartrefresh.impl;

import android.annotation.SuppressLint;
import android.view.View;

import com.arkay.gkinhindi.ui.customviews.smartrefresh.api.RefreshHeaderComponent;
import com.arkay.gkinhindi.ui.customviews.smartrefresh.internal.RefreshInternalAbstract;

@SuppressLint("ViewConstructor")
public class HeaderComponentWrapper extends RefreshInternalAbstract implements RefreshHeaderComponent/*, InvocationHandler*/ {
    public HeaderComponentWrapper(View wrapper) {
        super(wrapper);
    }
}
