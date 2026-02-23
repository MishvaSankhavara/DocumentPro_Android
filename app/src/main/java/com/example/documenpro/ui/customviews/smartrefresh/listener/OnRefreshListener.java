package com.example.documenpro.ui.customviews.smartrefresh.listener;


import androidx.annotation.NonNull;

import com.example.documenpro.ui.customviews.smartrefresh.api.RefreshLayout;


/**
 * 刷新监听器
 * Created by scwang on 2017/5/26.
 */
public interface OnRefreshListener {
    void onRefresh(@NonNull RefreshLayout refreshLayout);
}
