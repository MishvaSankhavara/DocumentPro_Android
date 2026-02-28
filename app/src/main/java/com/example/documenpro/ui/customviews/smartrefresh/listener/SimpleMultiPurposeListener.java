package com.example.documenpro.ui.customviews.smartrefresh.listener;


import androidx.annotation.NonNull;

import com.example.documenpro.ui.customviews.smartrefresh.api.RefreshFooterComponent;
import com.example.documenpro.ui.customviews.smartrefresh.api.RefreshHeaderComponent;
import com.example.documenpro.ui.customviews.smartrefresh.api.SmartRefreshLayout;
import com.example.documenpro.ui.customviews.smartrefresh.constant.RefreshLayoutState;

/**
 * 多功能监听器
 * Created by scwang on 2017/5/26.
 */
public class SimpleMultiPurposeListener implements OnMultiPurposeListener {

    @Override
    public void onHeaderMoving(RefreshHeaderComponent header, boolean isDragging, float percent, int offset, int headerHeight, int maxDragHeight) {

    }

    @Override
    public void onHeaderReleased(RefreshHeaderComponent header, int headerHeight, int maxDragHeight) {

    }

    @Override
    public void onHeaderStartAnimator(RefreshHeaderComponent header, int footerHeight, int maxDragHeight) {

    }

    @Override
    public void onHeaderFinish(RefreshHeaderComponent header, boolean success) {

    }

    @Override
    public void onFooterMoving(RefreshFooterComponent footer, boolean isDragging, float percent, int offset, int footerHeight, int maxDragHeight) {

    }

    @Override
    public void onFooterReleased(RefreshFooterComponent footer, int footerHeight, int maxDragHeight) {

    }

    @Override
    public void onFooterStartAnimator(RefreshFooterComponent footer, int headerHeight, int maxDragHeight) {

    }

    @Override
    public void onFooterFinish(RefreshFooterComponent footer, boolean success) {

    }

    @Override
    public void onRefresh(@NonNull SmartRefreshLayout refreshLayout) {

    }

    @Override
    public void onLoadMore(@NonNull SmartRefreshLayout refreshLayout) {

    }

    @Override
    public void onStateChanged(@NonNull SmartRefreshLayout refreshLayout, @NonNull RefreshLayoutState oldState, @NonNull RefreshLayoutState newState) {

    }

}
