package com.example.documenpro.ui.customviews.smartrefresh.impl;

import android.annotation.SuppressLint;
import android.view.View;

import com.example.documenpro.ui.customviews.smartrefresh.api.RefreshHeaderComponent;
import com.example.documenpro.ui.customviews.smartrefresh.internal.InternalAbstract;


@SuppressLint("ViewConstructor")
public class RefreshHeaderWrapper extends InternalAbstract implements RefreshHeaderComponent/*, InvocationHandler*/ {

    public RefreshHeaderWrapper(View wrapper) {
        super(wrapper);
    }

}
