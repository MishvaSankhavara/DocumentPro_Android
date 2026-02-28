package com.example.documenpro.ui.customviews.smartrefresh.internal;

import static android.view.View.MeasureSpec.EXACTLY;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.documenpro.R;
import com.example.documenpro.ui.customviews.smartrefresh.api.RefreshComponent;
import com.example.documenpro.ui.customviews.smartrefresh.api.RefreshManager;
import com.example.documenpro.ui.customviews.smartrefresh.api.SmartRefreshLayout;
import com.example.documenpro.ui.customviews.smartrefresh.constant.RefreshSpinnerStyle;
import com.example.documenpro.ui.customviews.smartrefresh.util.SmartUtil;

public abstract class RefreshClassicComponent<T extends RefreshClassicComponent> extends RefreshInternalAbstract implements RefreshComponent {

    public static final int ID_TEXT_TITLE = R.id.srl_classics_title;
    public static final int ID_IMAGE_ARROW = R.id.srl_classics_arrow;
    public static final int ID_IMAGE_PROGRESS = R.id.srl_classics_progress;

    protected TextView titleTextView;
    protected ImageView arrowView;
    protected ImageView progressView;

    protected RefreshManager refreshKernel;
    protected BasePaintDrawable arrowDrawable;
    protected BasePaintDrawable progressDrawable;

    protected boolean setAccentColor;
    protected boolean setPrimaryColor;
    protected int backgroundColor;
    protected int finishAnimationDuration = 500;
    protected int contentPaddingTop = 20;
    protected int contentPaddingBottom = 20;
    protected int minContentHeight = 0;

    public RefreshClassicComponent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        spinnerStyle = RefreshSpinnerStyle.TRANSLATE;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final View thisView = this;
        if (minContentHeight == 0) {
            contentPaddingTop = thisView.getPaddingTop();
            contentPaddingBottom = thisView.getPaddingBottom();
            if (contentPaddingTop == 0 || contentPaddingBottom == 0) {
                int paddingLeft = thisView.getPaddingLeft();
                int paddingRight = thisView.getPaddingRight();
                contentPaddingTop = contentPaddingTop == 0 ? SmartUtil.dp2px(20) : contentPaddingTop;
                contentPaddingBottom = contentPaddingBottom == 0 ? SmartUtil.dp2px(20) : contentPaddingBottom;
                thisView.setPadding(paddingLeft, contentPaddingTop, paddingRight, contentPaddingBottom);
            }
            final ViewGroup thisGroup = this;
            thisGroup.setClipToPadding(false);
        }
        if (MeasureSpec.getMode(heightMeasureSpec) == EXACTLY) {
            final int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
            if (parentHeight < minContentHeight) {
                final int padding = (parentHeight - minContentHeight) / 2;
                thisView.setPadding(thisView.getPaddingLeft(), padding, thisView.getPaddingRight(), padding);
            } else {
                thisView.setPadding(thisView.getPaddingLeft(), 0, thisView.getPaddingRight(), 0);
            }
        } else {
            thisView.setPadding(thisView.getPaddingLeft(), contentPaddingTop, thisView.getPaddingRight(), contentPaddingBottom);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (minContentHeight == 0) {
            final ViewGroup thisGroup = this;
            for (int i = 0; i < thisGroup.getChildCount(); i++) {
                final int height = thisGroup.getChildAt(i).getMeasuredHeight();
                if (minContentHeight < height) {
                    minContentHeight = height;
                }
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        final View arrowView = this.arrowView;
        final View progressView = this.progressView;
        arrowView.animate().cancel();
        progressView.animate().cancel();
        final Drawable drawable = this.progressView.getDrawable();
        if (drawable instanceof Animatable) {
            if (((Animatable) drawable).isRunning()) {
                ((Animatable) drawable).stop();
            }
        }
    }

    protected T getSelf() {
        return (T) this;
    }

    @Override
    public void onComponentInitialized(@NonNull RefreshManager kernel, int height, int maxDragHeight) {
        refreshKernel = kernel;
        refreshKernel.requestDrawBackground(this, backgroundColor);
    }

    @Override
    public void onAnimationStart(@NonNull SmartRefreshLayout refreshLayout, int height, int maxDragHeight) {
        final View progressView = this.progressView;
        if (progressView.getVisibility() != VISIBLE) {
            progressView.setVisibility(VISIBLE);
            Drawable drawable = this.progressView.getDrawable();
            if (drawable instanceof Animatable) {
                ((Animatable) drawable).start();
            } else {
                progressView.animate().rotation(36000).setDuration(100000);
            }
        }
    }

    @Override
    public void onDragReleased(@NonNull SmartRefreshLayout refreshLayout, int height, int maxDragHeight) {
        onAnimationStart(refreshLayout, height, maxDragHeight);
    }

    @Override
    public int onAnimationFinish(@NonNull SmartRefreshLayout refreshLayout, boolean success) {
        final View progressView = this.progressView;
        Drawable drawable = this.progressView.getDrawable();
        if (drawable instanceof Animatable) {
            if (((Animatable) drawable).isRunning()) {
                ((Animatable) drawable).stop();
            }
        } else {
            progressView.animate().rotation(0).setDuration(0);
        }
        progressView.setVisibility(GONE);
        return finishAnimationDuration;
    }

    @Override
    public void applyPrimaryColors(@ColorInt int ... colors) {
        if (colors.length > 0) {
            final View thisView = this;
            if (!(thisView.getBackground() instanceof BitmapDrawable) && !setPrimaryColor) {
                applyPrimaryColor(colors[0]);
                setPrimaryColor = false;
            }
            if (!setAccentColor) {
                if (colors.length > 1) {
                    applyAccentColor(colors[1]);
                } else {
                    applyAccentColor(colors[0] == 0xffffffff ? 0xff666666 : 0xffffffff);
                }
                setAccentColor = false;
            }
        }
    }

    public T setProgressDrawable(Drawable drawable) {
        progressDrawable = null;
        progressView.setImageDrawable(drawable);
        return getSelf();
    }
    public T setProgressResource(@DrawableRes int resId) {
        progressDrawable = null;
        progressView.setImageResource(resId);
        return getSelf();
    }

    public T setArrowResource(@DrawableRes int resId) {
        arrowDrawable = null;
        arrowView.setImageResource(resId);
        return getSelf();
    }

    public T setArrowDrawable(Drawable drawable) {
        arrowDrawable = null;
        arrowView.setImageDrawable(drawable);
        return getSelf();
    }

    public T applyPrimaryColor(@ColorInt int primaryColor) {
        setPrimaryColor = true;
        backgroundColor = primaryColor;
        if (refreshKernel != null) {
            refreshKernel.requestDrawBackground(this, primaryColor);
        }
        return getSelf();
    }

    public T setSpinnerStyle(RefreshSpinnerStyle style) {
        this.spinnerStyle = style;
        return getSelf();
    }

    public T applyAccentColor(@ColorInt int accentColor) {
        setAccentColor = true;
        titleTextView.setTextColor(accentColor);
        if (arrowDrawable != null) {
            arrowDrawable.setPaintColor(accentColor);
            arrowView.invalidateDrawable(arrowDrawable);
        }
        if (progressDrawable != null) {
            progressDrawable.setPaintColor(accentColor);
            progressView.invalidateDrawable(progressDrawable);
        }
        return getSelf();
    }

    public T setAccentColorId(@ColorRes int colorId) {
        final View thisView = this;
        applyAccentColor(ContextCompat.getColor(thisView.getContext(), colorId));
        return getSelf();
    }

    public T setPrimaryColorId(@ColorRes int colorId) {
        final View thisView = this;
        applyPrimaryColor(ContextCompat.getColor(thisView.getContext(), colorId));
        return getSelf();
    }

    public T setTextSizeTitle(float size) {
        titleTextView.setTextSize(size);
        if (refreshKernel != null) {
            refreshKernel.requestRemeasureHeight(this);
        }
        return getSelf();
    }

    public T setFinishDuration(int delay) {
        finishAnimationDuration = delay;
        return getSelf();
    }

    public T setDrawableMarginEnd(float dp) {
        final View arrowView = this.arrowView;
        final View progressView = this.progressView;
        MarginLayoutParams lpArrow = (MarginLayoutParams)arrowView.getLayoutParams();
        MarginLayoutParams lpProgress = (MarginLayoutParams)progressView.getLayoutParams();
        lpArrow.rightMargin = lpProgress.rightMargin = SmartUtil.dp2px(dp);
        arrowView.setLayoutParams(lpArrow);
        progressView.setLayoutParams(lpProgress);
        return getSelf();
    }

    public T setDrawableDimension(float dp) {
        final View arrowView = this.arrowView;
        final View progressView = this.progressView;
        ViewGroup.LayoutParams lpArrow = arrowView.getLayoutParams();
        ViewGroup.LayoutParams lpProgress = progressView.getLayoutParams();
        lpArrow.width = lpProgress.width = SmartUtil.dp2px(dp);
        lpArrow.height = lpProgress.height = SmartUtil.dp2px(dp);
        arrowView.setLayoutParams(lpArrow);
        progressView.setLayoutParams(lpProgress);
        return getSelf();
    }

    public T setArrowSize(float dp) {
        final View arrowView = this.arrowView;
        ViewGroup.LayoutParams lpArrow = arrowView.getLayoutParams();
        lpArrow.height = lpArrow.width = SmartUtil.dp2px(dp);
        arrowView.setLayoutParams(lpArrow);
        return getSelf();
    }

    public T setProgressSize(float dp) {
        final View progressView = this.progressView;
        ViewGroup.LayoutParams lpProgress = progressView.getLayoutParams();
        lpProgress.height = lpProgress.width = SmartUtil.dp2px(dp);
        progressView.setLayoutParams(lpProgress);
        return getSelf();
    }
}
