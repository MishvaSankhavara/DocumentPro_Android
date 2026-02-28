package com.example.documenpro.ui.customviews.smartrefresh.footer;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;

import com.example.documenpro.R;
import com.example.documenpro.ui.customviews.smartrefresh.api.RefreshFooterComponent;
import com.example.documenpro.ui.customviews.smartrefresh.api.SmartRefreshLayout;
import com.example.documenpro.ui.customviews.smartrefresh.constant.RefreshSpinnerStyle;
import com.example.documenpro.ui.customviews.smartrefresh.internal.RefreshInternalAbstract;
import com.example.documenpro.ui.customviews.smartrefresh.util.SmartViewUtil;

public class PulseBallRefreshFooter extends RefreshInternalAbstract implements RefreshFooterComponent {

    protected boolean isNormalColorManuallySet;
    protected boolean isAnimationColorManuallySet;

    protected Paint circlePaint;

    protected int normalColor = 0xffeeeeee;
    protected int animationColor = 0xffe75946;

    protected float circleSpacing;

    protected long animationStartTime = 0;
    protected boolean animationRunning = false;
    protected TimeInterpolator animationInterpolator = new AccelerateDecelerateInterpolator();

    public PulseBallRefreshFooter(Context context) {
        this(context, null);
    }

    public PulseBallRefreshFooter(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);

        final View thisView = this;
        thisView.setMinimumHeight(SmartViewUtil.dpToPx(60));

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BallPulseFooter);

        circlePaint = new Paint();
        circlePaint.setColor(Color.WHITE);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setAntiAlias(true);

        spinnerStyle = RefreshSpinnerStyle.TRANSLATE;
        spinnerStyle = RefreshSpinnerStyle.STYLES[ta.getInt(R.styleable.BallPulseFooter_srlClassicsSpinnerStyle, spinnerStyle.ordinal)];

        if (ta.hasValue(R.styleable.BallPulseFooter_srlNormalColor)) {
            setNormalColor(ta.getColor(R.styleable.BallPulseFooter_srlNormalColor, 0));
        }
        if (ta.hasValue(R.styleable.BallPulseFooter_srlAnimatingColor)) {
            setAnimatingColor(ta.getColor(R.styleable.BallPulseFooter_srlAnimatingColor, 0));
        }

        ta.recycle();

        circleSpacing = SmartViewUtil.dpToPx(4);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        final View thisView = this;
        final int width = thisView.getWidth();
        final int height = thisView.getHeight();
        float radius = (Math.min(width, height) - circleSpacing * 2) / 6;
        float x = width / 2f - (radius * 2 + circleSpacing);
        float y = height / 2f;

        final long now = System.currentTimeMillis();

        for (int i = 0; i < 3; i++) {

            long time = now - animationStartTime - 120 * (i + 1);
            float percent = time > 0 ? ((time%750)/750f) : 0;
            percent = animationInterpolator.getInterpolation(percent);

            canvas.save();

            float translateX = x + (radius * 2) * i + circleSpacing * i;
            canvas.translate(translateX, y);

            if (percent < 0.5) {
                float scale = 1 - percent * 2 * 0.7f;
                canvas.scale(scale, scale);
            } else {
                float scale = percent * 2 * 0.7f - 0.4f;
                canvas.scale(scale, scale);
            }

            canvas.drawCircle(0, 0, radius, circlePaint);
            canvas.restore();
        }

        super.dispatchDraw(canvas);

        if (animationRunning) {
            thisView.invalidate();
        }
    }

    @Override
    public void onAnimationStart(@NonNull SmartRefreshLayout layout, int height, int maxDragHeight) {
        if (animationRunning) return;

        final View thisView = this;
        thisView.invalidate();
        animationRunning = true;
        animationStartTime = System.currentTimeMillis();
        circlePaint.setColor(animationColor);
    }

    @Override
    public int onAnimationFinish(@NonNull SmartRefreshLayout layout, boolean success) {
        animationRunning = false;
        animationStartTime = 0;
        circlePaint.setColor(normalColor);
        return 0;
    }

    @Override@Deprecated
    public void applyPrimaryColors(@ColorInt int... colors) {
        if (!isAnimationColorManuallySet && colors.length > 1) {
            setAnimatingColor(colors[0]);
            isAnimationColorManuallySet = false;
        }
        if (!isNormalColorManuallySet) {
            if (colors.length > 1) {
                setNormalColor(colors[1]);
            } else if (colors.length > 0) {
                setNormalColor(ColorUtils.compositeColors(0x99ffffff,colors[0]));
            }
            isNormalColorManuallySet = false;
        }
    }

    public PulseBallRefreshFooter setAnimatingColor(@ColorInt int color) {
        animationColor = color;
        isAnimationColorManuallySet = true;
        if (animationRunning) {
            circlePaint.setColor(color);
        }
        return this;
    }

    public PulseBallRefreshFooter setNormalColor(@ColorInt int color) {
        normalColor = color;
        isNormalColorManuallySet = true;
        if (!animationRunning) {
            circlePaint.setColor(color);
        }
        return this;
    }

    public PulseBallRefreshFooter setSpinnerStyle(RefreshSpinnerStyle mSpinnerStyle) {
        this.spinnerStyle = mSpinnerStyle;
        return this;
    }
}
