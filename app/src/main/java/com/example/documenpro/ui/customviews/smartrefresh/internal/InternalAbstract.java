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
import com.example.documenpro.ui.customviews.smartrefresh.listener.OnStateChangedListener;

public abstract class InternalAbstract extends RelativeLayout implements RefreshComponent {

    protected View mWrappedView;
    protected RefreshSpinnerStyle mSpinnerStyle;
    protected RefreshComponent mWrappedInternal;

    protected InternalAbstract(@NonNull View wrapped) {
        this(wrapped, wrapped instanceof RefreshComponent ? (RefreshComponent) wrapped : null);
    }

    protected InternalAbstract(@NonNull View wrappedView, @Nullable RefreshComponent wrappedInternal) {
        super(wrappedView.getContext(), null, 0);
        this.mWrappedView = wrappedView;
        this.mWrappedInternal = wrappedInternal;
        if (this instanceof FooterComponentWrapper && mWrappedInternal instanceof RefreshHeaderComponent && mWrappedInternal.getSpinnerBehavior() == RefreshSpinnerStyle.MATCH_LAYOUT) {
            wrappedInternal.getComponentView().setScaleY(-1);
        } else if (this instanceof HeaderComponentWrapper && mWrappedInternal instanceof RefreshFooterComponent && mWrappedInternal.getSpinnerBehavior() == RefreshSpinnerStyle.MATCH_LAYOUT) {
            wrappedInternal.getComponentView().setScaleY(-1);
        }
    }

    protected InternalAbstract(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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

    @NonNull
    public View getComponentView() {
        return mWrappedView == null ? this : mWrappedView;
    }

    @Override
    public int onAnimationFinish(@NonNull SmartRefreshLayout refreshLayout, boolean success) {
        if (mWrappedInternal != null && mWrappedInternal != this) {
            return mWrappedInternal.onAnimationFinish(refreshLayout, success);
        }
        return 0;
    }

    @Override
    public void applyPrimaryColors(@ColorInt int ... colors) {
        if (mWrappedInternal != null && mWrappedInternal != this) {
            mWrappedInternal.applyPrimaryColors(colors);
        }
    }

    @NonNull
    @Override
    public RefreshSpinnerStyle getSpinnerBehavior() {
        if (mSpinnerStyle != null) {
            return mSpinnerStyle;
        }
        if (mWrappedInternal != null && mWrappedInternal != this) {
            return mWrappedInternal.getSpinnerBehavior();
        }
        if (mWrappedView != null) {
            ViewGroup.LayoutParams params = mWrappedView.getLayoutParams();
            if (params instanceof com.example.documenpro.ui.customviews.smartrefresh.SmartRefreshLayout.LayoutParams) {
                mSpinnerStyle = ((com.example.documenpro.ui.customviews.smartrefresh.SmartRefreshLayout.LayoutParams) params).spinnerStyle;
                if (mSpinnerStyle != null) {
                    return mSpinnerStyle;
                }
            }
            if (params != null) {
                if (params.height == 0 || params.height == MATCH_PARENT) {
                    for (RefreshSpinnerStyle style : RefreshSpinnerStyle.STYLES) {
                        if (style.scale) {
                            return mSpinnerStyle = style;
                        }
                    }
                }
            }
        }
        return mSpinnerStyle = RefreshSpinnerStyle.TRANSLATE;
    }

    @Override
    public void onComponentInitialized(@NonNull RefreshManager kernel, int height, int maxDragHeight) {
        if (mWrappedInternal != null && mWrappedInternal != this) {
            mWrappedInternal.onComponentInitialized(kernel, height, maxDragHeight);
        } else if (mWrappedView != null) {
            ViewGroup.LayoutParams params = mWrappedView.getLayoutParams();
            if (params instanceof com.example.documenpro.ui.customviews.smartrefresh.SmartRefreshLayout.LayoutParams) {
                kernel.requestDrawBackground(this, ((com.example.documenpro.ui.customviews.smartrefresh.SmartRefreshLayout.LayoutParams) params).backgroundColor);
            }
        }
    }

    @Override
    public boolean isHorizontalDragSupported() {
        return mWrappedInternal != null && mWrappedInternal != this && mWrappedInternal.isHorizontalDragSupported();
    }

    @Override
    public void onHorizontalDragging(float percentX, int offsetX, int offsetMax) {
        if (mWrappedInternal != null && mWrappedInternal != this) {
            mWrappedInternal.onHorizontalDragging(percentX, offsetX, offsetMax);
        }
    }

    @Override
    public void onDragging(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
        if (mWrappedInternal != null && mWrappedInternal != this) {
            mWrappedInternal.onDragging(isDragging, percent, offset, height, maxDragHeight);
        }
    }

    @Override
    public void onDragReleased(@NonNull SmartRefreshLayout refreshLayout, int height, int maxDragHeight) {
        if (mWrappedInternal != null && mWrappedInternal != this) {
            mWrappedInternal.onDragReleased(refreshLayout, height, maxDragHeight);
        }
    }

    @Override
    public void onAnimationStart(@NonNull SmartRefreshLayout refreshLayout, int height, int maxDragHeight) {
        if (mWrappedInternal != null && mWrappedInternal != this) {
            mWrappedInternal.onAnimationStart(refreshLayout, height, maxDragHeight);
        }
    }

    @Override
    public void onStateChanged(@NonNull SmartRefreshLayout refreshLayout, @NonNull RefreshLayoutState oldState, @NonNull RefreshLayoutState newState) {
        if (mWrappedInternal != null && mWrappedInternal != this) {
            if (this instanceof FooterComponentWrapper && mWrappedInternal instanceof RefreshHeaderComponent) {
                if (oldState.isFooterState) {
                    oldState = oldState.convertToHeaderState();
                }
                if (newState.isFooterState) {
                    newState = newState.convertToHeaderState();
                }
            } else if (this instanceof HeaderComponentWrapper && mWrappedInternal instanceof RefreshFooterComponent) {
                if (oldState.isHeaderState) {
                    oldState = oldState.convertToFooterState();
                }
                if (newState.isHeaderState) {
                    newState = newState.convertToFooterState();
                }
            }
            final OnStateChangedListener listener = mWrappedInternal;
            if (listener != null) {
                listener.onStateChanged(refreshLayout, oldState, newState);
            }
        }
    }

    @SuppressLint("RestrictedApi")
    public boolean setNoMoreDataAvailable(boolean noMoreData) {
        return mWrappedInternal instanceof RefreshFooterComponent && ((RefreshFooterComponent) mWrappedInternal).setNoMoreDataAvailable(noMoreData);
    }
}
