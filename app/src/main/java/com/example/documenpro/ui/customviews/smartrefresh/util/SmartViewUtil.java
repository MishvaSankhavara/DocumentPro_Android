package com.example.documenpro.ui.customviews.smartrefresh.util;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.content.res.Resources;
import android.graphics.PointF;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.ScrollingView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;


public class SmartViewUtil implements Interpolator {

    public static int INTERPOLATOR_FLUID = 0;
    public static int INTERPOLATOR_DINTERPOLATOR_SLOW_DOWNCELERATE = 1;

    private final int interpolatorType;

    public SmartViewUtil(int type) {
        this.interpolatorType = type;
    }

    public static int getViewHeight(View view) {
        ViewGroup.LayoutParams p = view.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(MATCH_PARENT,WRAP_CONTENT);
        }
        int childHeightSpec;
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        if (p.height > 0) {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(p.height, View.MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        }
        view.measure(childWidthSpec, childHeightSpec);
        return view.getMeasuredHeight();
    }

    public static void scrollList(@NonNull AbsListView listView, int y) {
        listView.scrollListBy(y);
    }

    public static boolean isScrollable(View view) {
        return view instanceof AbsListView
                || view instanceof ScrollView
                || view instanceof ScrollingView
                || view instanceof WebView
                || view instanceof NestedScrollingChild;
    }

    public static boolean isContent(View view) {
        return isScrollable(view)
                || view instanceof ViewPager
                || view instanceof NestedScrollingParent;
    }

    public static void flingView(View scrollableView, int velocity) {
        if (scrollableView instanceof ScrollView) {
            ((ScrollView) scrollableView).fling(velocity);
        } else if (scrollableView instanceof AbsListView) {
            ((AbsListView) scrollableView).fling(velocity);
        } else if (scrollableView instanceof WebView) {
            ((WebView) scrollableView).flingScroll(0, velocity);
        } else if (scrollableView instanceof NestedScrollView) {
            ((NestedScrollView) scrollableView).fling(velocity);
        } else if (scrollableView instanceof RecyclerView) {
            ((RecyclerView) scrollableView).fling(0, velocity);
        }
    }

    public static boolean canTriggerRefresh(@NonNull View targetView, PointF touch) {
        if (checkVerticalScroll(targetView, -1) && targetView.getVisibility() == View.VISIBLE) {
            return false;
        }
        if (targetView instanceof ViewGroup && touch != null) {
            ViewGroup viewGroup = (ViewGroup) targetView;
            final int childCount = viewGroup.getChildCount();
            PointF point = new PointF();
            for (int i = childCount; i > 0; i--) {
                View child = viewGroup.getChildAt(i - 1);
                if (isTouchInsideChild(viewGroup, child, touch.x, touch.y, point)) {
                    if ("fixed".equals(child.getTag()) || "fixed-bottom".equals(child.getTag())) {
                        return false;
                    }
                    touch.offset(point.x, point.y);
                    boolean can = canTriggerRefresh(child, touch);
                    touch.offset(-point.x, -point.y);
                    return can;
                }
            }
        }
        return true;
    }

    public static boolean canTriggerLoadMore(@NonNull View targetView, PointF touch, boolean contentFull) {
        if (checkVerticalScroll(targetView, 1) && targetView.getVisibility() == View.VISIBLE) {
            return false;
        }
        if (targetView instanceof ViewGroup && touch != null && !SmartViewUtil.isScrollable(targetView)) {
            ViewGroup viewGroup = (ViewGroup) targetView;
            final int childCount = viewGroup.getChildCount();
            PointF point = new PointF();
            for (int i = childCount; i > 0; i--) {
                View child = viewGroup.getChildAt(i - 1);
                if (isTouchInsideChild(viewGroup, child, touch.x, touch.y, point)) {
                    if ("fixed".equals(child.getTag()) || "fixed-top".equals(child.getTag())) {
                        return false;
                    }
                    touch.offset(point.x, point.y);
                    boolean can = canTriggerLoadMore(child, touch, contentFull);
                    touch.offset(-point.x, -point.y);
                    return can;
                }
            }
        }
        return (contentFull || checkVerticalScroll(targetView, -1));
    }

    public static boolean checkVerticalScroll(@NonNull View targetView, int direction) {
        return targetView.canScrollVertically(direction);
    }

    public static boolean isTouchInsideChild(@NonNull View group, @NonNull View child, float x, float y, PointF outLocalPoint) {
        if (child.getVisibility() != View.VISIBLE) {
            return false;
        }
        final float[] point = new float[2];
        point[0] = x;
        point[1] = y;
        point[0] += group.getScrollX() - child.getLeft();
        point[1] += group.getScrollY() - child.getTop();
        final boolean isInView = point[0] >= 0 && point[1] >= 0
                && point[0] < (child.getWidth())
                && point[1] < ((child.getHeight()));
        if (isInView && outLocalPoint != null) {
            outLocalPoint.set(point[0]-x, point[1]-y);
        }
        return isInView;
    }
    private static final float screenDensity = Resources.getSystem().getDisplayMetrics().density;
    public static int dpToPx(float dpValue) {
        return (int) (0.5f + dpValue * screenDensity);
    }

    public static float pxToDp(int pxValue) {
        return (pxValue / screenDensity);
    }

    private static final float VISCOUS_FLUID_SCALE = 8.0f;

    private static final float VISCOUS_FLUID_NORMALIZE;
    private static final float VISCOUS_FLUID_OFFSET;

    static {
        VISCOUS_FLUID_NORMALIZE = 1.0f / fluidEffect(1.0f);
        VISCOUS_FLUID_OFFSET = 1.0f - VISCOUS_FLUID_NORMALIZE * fluidEffect(1.0f);
    }

    private static float fluidEffect(float x) {
        x *= VISCOUS_FLUID_SCALE;
        if (x < 1.0f) {
            x -= (1.0f - (float)Math.exp(-x));
        } else {
            float start = 0.36787944117f;   // 1/e == exp(-1)
            x = 1.0f - (float)Math.exp(1.0f - x);
            x = start + x * (1.0f - start);
        }
        return x;
    }

    @Override
    public float getInterpolation(float input) {
        if (interpolatorType == INTERPOLATOR_DINTERPOLATOR_SLOW_DOWNCELERATE) {
            return (1.0f - (1.0f - input) * (1.0f - input));
        }
        final float interpolated = VISCOUS_FLUID_NORMALIZE * fluidEffect(input);
        if (interpolated > 0) {
            return interpolated + VISCOUS_FLUID_OFFSET;
        }
        return interpolated;
    }
}
