package com.example.documenpro.ui.customviews.smartrefresh.impl;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.example.documenpro.ui.customviews.smartrefresh.util.SmartViewUtil.checkVerticalScroll;
import static com.example.documenpro.ui.customviews.smartrefresh.util.SmartViewUtil.isContent;
import static com.example.documenpro.ui.customviews.smartrefresh.util.SmartViewUtil.isTouchInsideChild;
import static com.example.documenpro.ui.customviews.smartrefresh.util.SmartViewUtil.getViewHeight;
import static com.example.documenpro.ui.customviews.smartrefresh.util.SmartViewUtil.scrollList;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.PointF;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.Space;

import androidx.annotation.NonNull;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingParent;
import androidx.viewpager.widget.ViewPager;

import com.example.documenpro.ui.customviews.smartrefresh.api.RefreshContentHandler;
import com.example.documenpro.ui.customviews.smartrefresh.api.RefreshManager;
import com.example.documenpro.ui.customviews.smartrefresh.api.RefreshScrollBoundaryDecider;
import com.example.documenpro.ui.customviews.smartrefresh.listener.CoordinatorLayoutListener;
import com.example.documenpro.ui.customviews.smartrefresh.util.CoordinatorLayoutUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class RefreshContentContainer implements RefreshContentHandler, CoordinatorLayoutListener, AnimatorUpdateListener {

    protected View contentView;
    protected View originalContentView;
    protected View scrollableView;

    protected View fixedHeader;
    protected View fixedFooter;

    protected RefreshScrollBoundaryAdapter boundaryAdapter = new RefreshScrollBoundaryAdapter();

    protected boolean enableRefresh = true;
    protected boolean enableLoadMore = true;

    protected int lastSpinner = 0;

    public RefreshContentContainer(@NonNull View view) {
        this.contentView = originalContentView = scrollableView = view;
    }

    protected void locateScrollableView(View content, RefreshManager kernel) {
        View scrollableView = null;
        boolean isInEditMode = contentView.isInEditMode();
        while (scrollableView == null || (scrollableView instanceof NestedScrollingParent
                && !(scrollableView instanceof NestedScrollingChild))) {
            content = locateScrollableViewInternal(content, scrollableView == null);
            if (content == scrollableView) {
                break;
            }
            if (!isInEditMode) {
                CoordinatorLayoutUtil.handleCoordinatorLayout(content, kernel, this);
            }
            scrollableView = content;
        }
        if (scrollableView != null) {
            this.scrollableView = scrollableView;
        }
    }

    protected View locateScrollableViewInternal(View content, boolean selfAble) {
        View scrollableView = null;
        final Queue<View> views = new LinkedList<>();
        //noinspection unchecked
        final List<View> list = (List<View>)views;
        list.add(content);
        while (list.size() > 0 && scrollableView == null) {
            View view = views.poll();
            if (view != null) {
                if ((selfAble || view != content) && isContent(view)) {
                    scrollableView = view;
                } else if (view instanceof ViewGroup) {
                    ViewGroup group = (ViewGroup) view;
                    for (int j = 0; j < group.getChildCount(); j++) {
                        list.add(group.getChildAt(j));
                    }
                }
            }
        }
        return scrollableView == null ? content : scrollableView;
    }

    @Override
    public void coordinatorUpdate(boolean enableRefresh, boolean enableLoadMore) {
        this.enableRefresh = enableRefresh;
        this.enableLoadMore = enableLoadMore;
    }

    protected View locateScrollableViewByTouchPoint(View content, PointF event, View orgScrollableView) {
        if (content instanceof ViewGroup && event != null) {
            ViewGroup viewGroup = (ViewGroup) content;
            final int childCount = viewGroup.getChildCount();
            PointF point = new PointF();
            for (int i = childCount; i > 0; i--) {
                View child = viewGroup.getChildAt(i - 1);
                if (isTouchInsideChild(viewGroup, child, event.x, event.y, point)) {
                    if (child instanceof ViewPager || !isContent(child)) {
                        event.offset(point.x, point.y);
                        child = locateScrollableViewByTouchPoint(child, event, orgScrollableView);
                        event.offset(-point.x, -point.y);
                    }
                    return child;
                }
            }
        }
        return orgScrollableView;
    }

    @Override
    @NonNull
    public View getScrollableContentView() {
        return scrollableView;
    }

    @NonNull
    public View getContentView() {
        return contentView;
    }

    @Override
    public void updateSpinnerPosition(int spinner, int headerTranslationViewId, int footerTranslationViewId) {
        boolean translated = false;
        if (headerTranslationViewId != View.NO_ID) {
            View headerTranslationView = originalContentView.findViewById(headerTranslationViewId);
            if (headerTranslationView != null) {
                if (spinner > 0) {
                    translated = true;
                    headerTranslationView.setTranslationY(spinner);
                } else if (headerTranslationView.getTranslationY() > 0) {
                    headerTranslationView.setTranslationY(0);
                }
            }
        }
        if (footerTranslationViewId != View.NO_ID) {
            View footerTranslationView = originalContentView.findViewById(footerTranslationViewId);
            if (footerTranslationView != null) {
                if (spinner < 0) {
                    translated = true;
                    footerTranslationView.setTranslationY(spinner);
                } else if (footerTranslationView.getTranslationY() < 0) {
                    footerTranslationView.setTranslationY(0);
                }
            }
        }
        if (!translated) {
            originalContentView.setTranslationY(spinner);
        } else {
            originalContentView.setTranslationY(0);
        }
        if (fixedHeader != null) {
            fixedHeader.setTranslationY(Math.max(0, spinner));
        }
        if (fixedFooter != null) {
            fixedFooter.setTranslationY(Math.min(0, spinner));
        }
    }

    @Override
    public boolean isLoadMorePossible() {
        return enableLoadMore && boundaryAdapter.canTriggerLoadMore(contentView);
    }

    @Override
    public boolean isRefreshPossible() {
        return enableRefresh && boundaryAdapter.canTriggerRefresh(contentView);
    }

    @Override
    public void handleActionDown(MotionEvent e) {
        PointF point = new PointF(e.getX(), e.getY());
        point.offset(-contentView.getLeft(), -contentView.getTop());
        if (scrollableView != contentView) {
            scrollableView = locateScrollableViewByTouchPoint(contentView, point, scrollableView);
        }
        if (scrollableView == contentView) {
            boundaryAdapter.actionEvent = null;
        } else {
            boundaryAdapter.actionEvent = point;
        }
    }

    @Override
    public void setupComponent(RefreshManager kernel, View fixedHeader, View fixedFooter) {
        locateScrollableView(contentView, kernel);

        if (fixedHeader != null || fixedFooter != null) {
            this.fixedHeader = fixedHeader;
            this.fixedFooter = fixedFooter;
            ViewGroup frameLayout = new FrameLayout(contentView.getContext());
            int index = kernel.getLayoutRefresh().getLayoutView().indexOfChild(contentView);
            kernel.getLayoutRefresh().getLayoutView().removeView(contentView);
            frameLayout.addView(contentView, 0, new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
            ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
            kernel.getLayoutRefresh().getLayoutView().addView(frameLayout, index, layoutParams);
            contentView = frameLayout;
            if (fixedHeader != null) {
                fixedHeader.setTag("fixed-top");
                ViewGroup.LayoutParams lp = fixedHeader.getLayoutParams();
                ViewGroup parent = (ViewGroup) fixedHeader.getParent();
                index = parent.indexOfChild(fixedHeader);
                parent.removeView(fixedHeader);
                lp.height = getViewHeight(fixedHeader);
                parent.addView(new Space(contentView.getContext()), index, lp);
                frameLayout.addView(fixedHeader, 1, lp);
            }
            if (fixedFooter != null) {
                fixedFooter.setTag("fixed-bottom");
                ViewGroup.LayoutParams lp = fixedFooter.getLayoutParams();
                ViewGroup parent = (ViewGroup) fixedFooter.getParent();
                index = parent.indexOfChild(fixedFooter);
                parent.removeView(fixedFooter);
                FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(lp);
                lp.height = getViewHeight(fixedFooter);
                parent.addView(new Space(contentView.getContext()), index, lp);
                flp.gravity = Gravity.BOTTOM;
                frameLayout.addView(fixedFooter, 1, flp);
            }
        }
    }

    @Override
    public void setLoadMoreWhenContentNotFullEnabled(boolean enable) {
        boundaryAdapter.loadMoreWhenContentNotFullEnabled = enable;
    }

    @Override
    public void setScrollBoundaryChecker(RefreshScrollBoundaryDecider boundary) {
        if (boundary instanceof RefreshScrollBoundaryAdapter) {
            boundaryAdapter = ((RefreshScrollBoundaryAdapter) boundary);
        } else {
            boundaryAdapter.customBoundaryDecider = (boundary);
        }
    }

    @Override
    public AnimatorUpdateListener createScrollAnimatorOnFinish(final int spinner) {
        if (scrollableView != null && spinner != 0) {
            if ((spinner < 0 && checkVerticalScroll(scrollableView, 1)) || (spinner > 0 && checkVerticalScroll(scrollableView, -1))) {
                lastSpinner = spinner;
                return this;
            }
        }
        return null;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        int value = (int) animation.getAnimatedValue();
        try {
            float dy = (value - lastSpinner) * scrollableView.getScaleY();
            if (scrollableView instanceof AbsListView) {
                scrollList((AbsListView) scrollableView, (int)dy);
            } else {
                scrollableView.scrollBy(0, (int)dy);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        lastSpinner = value;
    }
}
