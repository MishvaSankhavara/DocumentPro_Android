package com.example.documenpro.ui.customviews.smartrefresh.impl;

import android.annotation.SuppressLint;
import android.view.View;

import com.example.documenpro.ui.customviews.smartrefresh.api.RefreshFooterComponent;
import com.example.documenpro.ui.customviews.smartrefresh.internal.RefreshInternalAbstract;

@SuppressLint("ViewConstructor")
public class FooterComponentWrapper extends RefreshInternalAbstract implements RefreshFooterComponent/*, InvocationHandler */{

    public FooterComponentWrapper(View wrapper) {
        super(wrapper);
    }
}
