package com.example.documenpro.ui.customviews.smartrefresh.internal;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.documenpro.ui.customviews.smartrefresh.api.RefreshFooterComponent;
import com.example.documenpro.ui.customviews.smartrefresh.api.RefreshHeaderComponent;
import com.example.documenpro.ui.customviews.smartrefresh.api.RefreshComponent;
import com.example.documenpro.ui.customviews.smartrefresh.api.RefreshManager;
import com.example.documenpro.ui.customviews.smartrefresh.api.SmartRefreshLayout;
import com.example.documenpro.ui.customviews.smartrefresh.constant.RefreshLayoutState;
import com.example.documenpro.ui.customviews.smartrefresh.constant.RefreshSpinnerStyle;
import com.example.documenpro.ui.customviews.smartrefresh.impl.FooterComponentWrapper;
import com.example.documenpro.ui.customviews.smartrefresh.impl.HeaderComponentWrapper;
import com.example.documenpro.ui.customviews.smartrefresh.listener.StateChangedListener;

public abstract class RefreshInternalAbstract extends RelativeLayout implements RefreshComponent {

    protected View wrappedView;
    protected RefreshComponent wrappedInternal;
    protected RefreshSpinnerStyle spinnerStyle;

    protected RefreshInternalAbstract(@NonNull View wrappedView, @Nullable RefreshComponent wrappedInternal) {
        super(wrappedView.getContext(), null, 0);
        this.wrappedView = wrappedView;
        this.wrappedInternal = wrappedInternal;
        if (this instanceof FooterComponentWrapper && this.wrappedInternal instanceof RefreshHeaderComponent && this.wrappedInternal.getSpinnerBehavior() == RefreshSpinnerStyle.MATCH_LAYOUT) {
            wrappedInternal.getComponentView().setScaleY(-1);
        } else if (this instanceof HeaderComponentWrapper && this.wrappedInternal instanceof RefreshFooterComponent && this.wrappedInternal.getSpinnerBehavior() == RefreshSpinnerStyle.MATCH_LAYOUT) {
            wrappedInternal.getComponentView().setScaleY(-1);
        }
    }

    protected RefreshInternalAbstract(@NonNull View wrapped) {
        this(wrapped, wrapped instanceof RefreshComponent ? (RefreshComponent) wrapped : null);
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            if (obj instanceof RefreshComponent) {
                final RefreshComponent thisView = this;
                return thisView.getComponentView() == ((RefreshComponent)obj).getComponentView();
            }
            return false;
        }
        return true;
    }

    protected RefreshInternalAbstract(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @NonNull
    public View getComponentView() {
        return wrappedView == null ? this : wrappedView;
    }

    @Override
    public void applyPrimaryColors(@ColorInt int ... colors) {
        if (wrappedInternal != null && wrappedInternal != this) {
            wrappedInternal.applyPrimaryColors(colors);
        }
    }

    @Override
    public int onAnimationFinish(@NonNull SmartRefreshLayout refreshLayout, boolean success) {
        if (wrappedInternal != null && wrappedInternal != this) {
            return wrappedInternal.onAnimationFinish(refreshLayout, success);
        }
        return 0;
    }

    @NonNull
    @Override
    public RefreshSpinnerStyle getSpinnerBehavior() {
        if (spinnerStyle != null) {
            return spinnerStyle;
        }
        if (wrappedInternal != null && wrappedInternal != this) {
            return wrappedInternal.getSpinnerBehavior();
        }
        if (wrappedView != null) {
            ViewGroup.LayoutParams params = wrappedView.getLayoutParams();
            if (params instanceof com.example.documenpro.ui.customviews.smartrefresh.SmartRefreshLayout.LayoutParams) {
                spinnerStyle = ((com.example.documenpro.ui.customviews.smartrefresh.SmartRefreshLayout.LayoutParams) params).spinnerStyle;
                if (spinnerStyle != null) {
                    return spinnerStyle;
                }
            }
            if (params != null) {
                if (params.height == 0 || params.height == MATCH_PARENT) {
                    for (RefreshSpinnerStyle style : RefreshSpinnerStyle.STYLES) {
                        if (style.scale) {
                            return spinnerStyle = style;
                        }
                    }
                }
            }
        }
        return spinnerStyle = RefreshSpinnerStyle.TRANSLATE;
    }

    @Override
    public boolean isHorizontalDragSupported() {
        return wrappedInternal != null && wrappedInternal != this && wrappedInternal.isHorizontalDragSupported();
    }

    @Override
    public void onComponentInitialized(@NonNull RefreshManager kernel, int height, int maxDragHeight) {
        if (wrappedInternal != null && wrappedInternal != this) {
            wrappedInternal.onComponentInitialized(kernel, height, maxDragHeight);
        } else if (wrappedView != null) {
            ViewGroup.LayoutParams params = wrappedView.getLayoutParams();
            if (params instanceof com.example.documenpro.ui.customviews.smartrefresh.SmartRefreshLayout.LayoutParams) {
                kernel.requestDrawBackground(this, ((com.example.documenpro.ui.customviews.smartrefresh.SmartRefreshLayout.LayoutParams) params).backgroundColor);
            }
        }
    }

    @Override
    public void onDragging(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
        if (wrappedInternal != null && wrappedInternal != this) {
            wrappedInternal.onDragging(isDragging, percent, offset, height, maxDragHeight);
        }
    }

    @Override
    public void onHorizontalDragging(float percentX, int offsetX, int offsetMax) {
        if (wrappedInternal != null && wrappedInternal != this) {
            wrappedInternal.onHorizontalDragging(percentX, offsetX, offsetMax);
        }
    }

    @Override
    public void onAnimationStart(@NonNull SmartRefreshLayout refreshLayout, int height, int maxDragHeight) {
        if (wrappedInternal != null && wrappedInternal != this) {
            wrappedInternal.onAnimationStart(refreshLayout, height, maxDragHeight);
        }
    }

    @Override
    public void onDragReleased(@NonNull SmartRefreshLayout refreshLayout, int height, int maxDragHeight) {
        if (wrappedInternal != null && wrappedInternal != this) {
            wrappedInternal.onDragReleased(refreshLayout, height, maxDragHeight);
        }
    }

    @Override
    public void stateChanged(@NonNull SmartRefreshLayout refreshLayout, @NonNull RefreshLayoutState oldState, @NonNull RefreshLayoutState newState) {
        if (wrappedInternal != null && wrappedInternal != this) {
            if (this instanceof FooterComponentWrapper && wrappedInternal instanceof RefreshHeaderComponent) {
                if (oldState.isFooterState) {
                    oldState = oldState.convertToHeaderState();
                }
                if (newState.isFooterState) {
                    newState = newState.convertToHeaderState();
                }
            } else if (this instanceof HeaderComponentWrapper && wrappedInternal instanceof RefreshFooterComponent) {
                if (oldState.isHeaderState) {
                    oldState = oldState.convertToFooterState();
                }
                if (newState.isHeaderState) {
                    newState = newState.convertToFooterState();
                }
            }
            final StateChangedListener listener = wrappedInternal;
            if (listener != null) {
                listener.stateChanged(refreshLayout, oldState, newState);
            }
        }
    }

    @SuppressLint("RestrictedApi")
    public boolean setNoMoreDataAvailable(boolean noMoreData) {
        return wrappedInternal instanceof RefreshFooterComponent && ((RefreshFooterComponent) wrappedInternal).setNoMoreDataAvailable(noMoreData);
    }
}
