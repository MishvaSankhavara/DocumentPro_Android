package com.example.documenpro.ui.customviews.smartrefresh.impl;

import android.annotation.SuppressLint;
import android.view.View;

import com.example.documenpro.ui.customviews.smartrefresh.api.RefreshFooterComponent;
import com.example.documenpro.ui.customviews.smartrefresh.internal.InternalAbstract;

@SuppressLint("ViewConstructor")
public class FooterComponentWrapper extends InternalAbstract implements RefreshFooterComponent/*, InvocationHandler */{

    public FooterComponentWrapper(View wrapper) {
        super(wrapper);
    }
}
