package com.example.documenpro.ui.customviews.smartrefresh.api;

import android.view.View;


public interface ScrollBoundaryDecider {

    boolean canRefresh(View content);

    boolean canLoadMore(View content);
}
