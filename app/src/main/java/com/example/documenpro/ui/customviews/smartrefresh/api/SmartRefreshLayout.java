package com.example.documenpro.ui.customviews.smartrefresh.api;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.documenpro.ui.customviews.smartrefresh.constant.RefreshLayoutState;
import com.example.documenpro.ui.customviews.smartrefresh.listener.OnLoadMoreListener;
import com.example.documenpro.ui.customviews.smartrefresh.listener.OnMultiPurposeListener;
import com.example.documenpro.ui.customviews.smartrefresh.listener.OnRefreshListener;
import com.example.documenpro.ui.customviews.smartrefresh.listener.OnRefreshLoadMoreListener;

public interface SmartRefreshLayout {

    SmartRefreshLayout setFooterHeightDp(float dp);

    SmartRefreshLayout setHeaderHeightDp(float dp);

    SmartRefreshLayout setHeaderInsetStartDp(float dp);

    SmartRefreshLayout setFooterInsetStartDp(float dp);

    SmartRefreshLayout setDragSensitivity(@FloatRange(from = 0,to = 1) float rate);

    SmartRefreshLayout setHeaderMaxDragRate(@FloatRange(from = 1,to = 10) float rate);

    SmartRefreshLayout setFooterMaxDragRate(@FloatRange(from = 1,to = 10) float rate);

    SmartRefreshLayout setFooterTriggerThreshold(@FloatRange(from = 0,to = 1.0) float rate);

    SmartRefreshLayout setFooterTriggerRate(@FloatRange(from = 0,to = 1.0) float rate);

    SmartRefreshLayout setBounceInterpolator(@NonNull Interpolator interpolator);

    SmartRefreshLayout setBounceDuration(int duration);
    SmartRefreshLayout setFooterRefresh(@NonNull RefreshFooterComponent footer);
    SmartRefreshLayout setFooterRefresh(@NonNull RefreshFooterComponent footer, int width, int height);
    SmartRefreshLayout setHeaderRefresh(@NonNull RefreshHeaderComponent header);
    SmartRefreshLayout setHeaderRefresh(@NonNull RefreshHeaderComponent header, int width, int height);
    SmartRefreshLayout setContentRefresh(@NonNull View content);
    SmartRefreshLayout setContentRefresh(@NonNull View content, int width, int height);
    SmartRefreshLayout setRefreshEnable(boolean enabled);
    SmartRefreshLayout setLoadMoreEnable(boolean enabled);
    SmartRefreshLayout setAutoLoadMoreEnable(boolean enabled);
    SmartRefreshLayout enableHeaderContentTranslation(boolean enabled);
    SmartRefreshLayout enableFooterContentTranslation(boolean enabled);
    SmartRefreshLayout enableOverScrollBounce(boolean enabled);
    SmartRefreshLayout enablePureScrollMode(boolean enabled);
    SmartRefreshLayout enableScrollAfterLoad(boolean enabled);
    SmartRefreshLayout enableScrollAfterRefresh(boolean enabled);
    SmartRefreshLayout enableLoadMoreWhenNotFull(boolean enabled);
    SmartRefreshLayout enableOverScrollDrag(boolean enabled);

    @Deprecated
    SmartRefreshLayout enableFooterFollowWhenFinished(boolean enabled);
    SmartRefreshLayout enableFooterFollowWhenNoMoreData(boolean enabled);
    SmartRefreshLayout setHeaderWhenFixedBehindEnableClip(boolean enabled);
    SmartRefreshLayout setFooterWhenFixedBehindEnableClip(boolean enabled);
    SmartRefreshLayout setNestedScrollEnable(boolean enabled);
    SmartRefreshLayout setContentWhenRefreshDisable(boolean disable);
    SmartRefreshLayout setContentWhenLoadingDisable(boolean disable);
    SmartRefreshLayout setRefreshListener(OnRefreshListener listener);
    SmartRefreshLayout setLoadMoreListener(OnLoadMoreListener listener);
    SmartRefreshLayout setRefreshLoadMoreListener(OnRefreshLoadMoreListener listener);
    SmartRefreshLayout setMultiPurposeListener(OnMultiPurposeListener listener);
    SmartRefreshLayout setScrollBoundary(RefreshScrollBoundaryDecider boundary);
    SmartRefreshLayout setPrimaryColors(@ColorInt int... primaryColors);
    SmartRefreshLayout setThemeColorResources(@ColorRes int... primaryColorId);
    SmartRefreshLayout completeRefresh();
    SmartRefreshLayout completeRefresh(int delayed);
    SmartRefreshLayout completeRefresh(boolean success);
    SmartRefreshLayout completeRefresh(int delayed, boolean success, Boolean noMoreData);
    SmartRefreshLayout completeRefreshWithNoMoreData();
    SmartRefreshLayout completeLoadMore();
    SmartRefreshLayout completeLoadMore(int delayed);
    SmartRefreshLayout completeLoadMore(boolean success);
    SmartRefreshLayout completeLoadMore(int delayed, boolean success, boolean noMoreData);
    SmartRefreshLayout finishLoadMoreWithNoMoreData();
    SmartRefreshLayout closeHeaderFooter();
    SmartRefreshLayout setNoMoreDataAvailable(boolean noMoreData);
    SmartRefreshLayout resetNoMoreDataState();
    @Nullable
    RefreshHeaderComponent getHeader();
    @Nullable
    RefreshFooterComponent getFooter();
    @NonNull
    RefreshLayoutState getRefreshState();
    @NonNull
    ViewGroup getLayoutView();
    boolean autoRefresh();
    boolean autoRefresh(int delayed);
    boolean triggerRefreshAnimation();
    boolean autoRefresh(int delayed, int duration, float dragRate, boolean animationOnly);
    boolean triggerAutoLoadMore();
    boolean triggerLoadMoreAnimation();
    boolean triggerAutoLoadMore(int delayed, int duration, float dragRate, boolean animationOnly);
}
