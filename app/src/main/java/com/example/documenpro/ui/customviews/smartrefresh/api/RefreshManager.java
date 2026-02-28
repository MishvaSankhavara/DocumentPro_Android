package com.example.documenpro.ui.customviews.smartrefresh.api;

import android.animation.ValueAnimator;

import androidx.annotation.NonNull;

import com.example.documenpro.ui.customviews.smartrefresh.constant.RefreshState;

public interface RefreshManager {

    @NonNull
    SmartRefreshLayout getLayoutRefresh();

    @NonNull
    RefreshContentHandler getContentRefresh();

    RefreshManager setRefreshState(@NonNull RefreshState state);

    RefreshManager openTwoLevel(boolean open);

    RefreshManager closeTwoLevel();

    RefreshManager updateSpinnerPosition(int spinner, boolean isDragging);

    ValueAnimator animateSpinnerTo(int endSpinner);

    RefreshManager requestDrawBackground(@NonNull RefreshComponent internal, int backgroundColor);

    RefreshManager requestNeedTouchEvent(@NonNull RefreshComponent internal, boolean request);

    RefreshManager requestDefaultTranslationContent(@NonNull RefreshComponent internal, boolean translation);

    RefreshManager requestRemeasureHeight(@NonNull RefreshComponent internal);

    RefreshManager setTwoLevelParameters(int duration, float openLayoutRate, float dragLayoutRate);
}
