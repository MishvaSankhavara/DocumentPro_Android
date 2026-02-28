package com.example.documenpro.ui.customviews.smartrefresh.impl;

import android.graphics.PointF;
import android.view.View;

import com.example.documenpro.ui.customviews.smartrefresh.api.RefreshScrollBoundaryDecider;
import com.example.documenpro.ui.customviews.smartrefresh.util.SmartUtil;

/**
 * 滚动边界
 * Created by scwang on 2017/7/8.
 */
@SuppressWarnings("WeakerAccess")
public class ScrollBoundaryDeciderAdapter implements RefreshScrollBoundaryDecider {

    //<editor-fold desc="Internal">
    public PointF mActionEvent;
    public RefreshScrollBoundaryDecider boundary;
    public boolean mEnableLoadMoreWhenContentNotFull = true;

//    void setScrollBoundaryDecider(ScrollBoundaryDecider boundary){
//        this.boundary = boundary;
//    }

//    void setActionEvent(MotionEvent event) {
//        //event 在没有必要时候会被设置为 null
//        mActionEvent = event;
//    }
//
//    public void setEnableLoadMoreWhenContentNotFull(boolean enable) {
//        mEnableLoadMoreWhenContentNotFull = enable;
//    }
    //</editor-fold>

    //<editor-fold desc="ScrollBoundaryDecider">
    @Override
    public boolean canTriggerRefresh(View content) {
        if (boundary != null) {
            return boundary.canTriggerRefresh(content);
        }
        //mActionEvent == null 时 canRefresh 不会动态递归搜索
        return SmartUtil.canRefresh(content, mActionEvent);
    }

    @Override
    public boolean canTriggerLoadMore(View content) {
        if (boundary != null) {
            return boundary.canTriggerLoadMore(content);
        }
//        if (mEnableLoadMoreWhenContentNotFull) {
//            //mActionEvent == null 时 canScrollDown 不会动态递归搜索
//            return !ScrollBoundaryUtil.canScrollDown(content, mActionEvent);
//        }
        //mActionEvent == null 时 canLoadMore 不会动态递归搜索
        return SmartUtil.canLoadMore(content, mActionEvent, mEnableLoadMoreWhenContentNotFull);
    }
    //</editor-fold>
}
