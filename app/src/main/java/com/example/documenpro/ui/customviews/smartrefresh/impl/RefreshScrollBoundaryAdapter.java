package com.example.documenpro.ui.customviews.smartrefresh.impl;

import android.graphics.PointF;
import android.view.View;

import com.example.documenpro.ui.customviews.smartrefresh.api.RefreshScrollBoundaryDecider;
import com.example.documenpro.ui.customviews.smartrefresh.util.SmartUtil;

public class RefreshScrollBoundaryAdapter implements RefreshScrollBoundaryDecider {

    public PointF actionEvent;
    public boolean loadMoreWhenContentNotFullEnabled = true;
    public RefreshScrollBoundaryDecider customBoundaryDecider;

    @Override
    public boolean canTriggerLoadMore(View content) {
        if (customBoundaryDecider != null) {
            return customBoundaryDecider.canTriggerLoadMore(content);
        }
        return SmartUtil.canLoadMore(content, actionEvent, loadMoreWhenContentNotFullEnabled);
    }

    @Override
    public boolean canTriggerRefresh(View content) {
        if (customBoundaryDecider != null) {
            return customBoundaryDecider.canTriggerRefresh(content);
        }
        return SmartUtil.canRefresh(content, actionEvent);
    }
}
