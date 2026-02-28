package com.example.documenpro.ui.customviews.smartrefresh.listener;


import androidx.annotation.NonNull;

import com.example.documenpro.ui.customviews.smartrefresh.api.SmartRefreshLayout;

public interface LoadMoreListener {
    void onLoadMore(@NonNull SmartRefreshLayout refreshLayout);
}
