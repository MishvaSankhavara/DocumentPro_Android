package com.example.documenpro.ui.customviews.smartrefresh.impl;

import android.annotation.SuppressLint;
import android.view.View;

import com.example.documenpro.ui.customviews.smartrefresh.api.RefreshHeaderComponent;
import com.example.documenpro.ui.customviews.smartrefresh.internal.RefreshInternalAbstract;

@SuppressLint("ViewConstructor")
public class HeaderComponentWrapper extends RefreshInternalAbstract implements RefreshHeaderComponent/*, InvocationHandler*/ {
    public HeaderComponentWrapper(View wrapper) {
        super(wrapper);
    }
}
