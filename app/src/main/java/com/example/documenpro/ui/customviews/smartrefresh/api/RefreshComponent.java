package com.example.documenpro.ui.customviews.smartrefresh.api;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;
import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP;
import static androidx.annotation.RestrictTo.Scope.SUBCLASSES;

import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import com.example.documenpro.ui.customviews.smartrefresh.constant.RefreshSpinnerStyle;
import com.example.documenpro.ui.customviews.smartrefresh.listener.StateChangedListener;

public interface RefreshComponent extends StateChangedListener {

    @NonNull
    View getComponentView();
    @NonNull
    RefreshSpinnerStyle getSpinnerBehavior();
    @RestrictTo({LIBRARY, LIBRARY_GROUP, SUBCLASSES})
    void applyPrimaryColors(@ColorInt int... colors);
    @RestrictTo({LIBRARY, LIBRARY_GROUP, SUBCLASSES})
    void onComponentInitialized(@NonNull RefreshManager kernel, int height, int maxDragHeight);
    @RestrictTo({LIBRARY, LIBRARY_GROUP, SUBCLASSES})
    void onDragging(boolean isDragging, float percent, int offset, int height, int maxDragHeight);
    @RestrictTo({LIBRARY, LIBRARY_GROUP, SUBCLASSES})
    void onHorizontalDragging(float percentX, int offsetX, int offsetMax);
    @RestrictTo({LIBRARY, LIBRARY_GROUP, SUBCLASSES})
    void onDragReleased(@NonNull SmartRefreshLayout refreshLayout, int height, int maxDragHeight);
    @RestrictTo({LIBRARY, LIBRARY_GROUP, SUBCLASSES})
    void onAnimationStart(@NonNull SmartRefreshLayout refreshLayout, int height, int maxDragHeight);

    @RestrictTo({LIBRARY, LIBRARY_GROUP, SUBCLASSES})
    int onAnimationFinish(@NonNull SmartRefreshLayout refreshLayout, boolean success);
    boolean isHorizontalDragSupported();
}
