package com.example.documenpro.ui.customviews.smartrefresh.header;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.documenpro.R;
import com.example.documenpro.ui.customviews.smartrefresh.api.RefreshHeaderComponent;
import com.example.documenpro.ui.customviews.smartrefresh.api.SmartRefreshLayout;
import com.example.documenpro.ui.customviews.smartrefresh.constant.RefreshLayoutState;
import com.example.documenpro.ui.customviews.smartrefresh.constant.RefreshSpinnerStyle;
import com.example.documenpro.ui.customviews.smartrefresh.internal.InternalAbstract;
import com.example.documenpro.ui.customviews.smartrefresh.util.SmartUtil;

public class RadarRefreshHeader extends InternalAbstract implements RefreshHeaderComponent {

    protected int accentColor;
    protected int primaryColor;
    protected boolean isPrimaryColorManual;
    protected boolean isAccentColorManual;
    protected boolean waveDragging;
    protected boolean horizontalDragEnabled = false;

    protected float dotsAlpha;
    protected float dotsFraction;
    protected float dotsRadius;
    protected float rippleRadius;

    protected Path wavePath;
    protected Paint paint;
    protected int waveTop;
    protected int waveHeight;
    protected int waveOffsetX = -1;
    protected int waveOffsetY = 0;


    protected int radarAngle = 0;
    protected float radarRadius = 0;
    protected float radarCircle = 0;
    protected float radarScale = 0;
    protected Animator animatorSet;
    protected RectF radarRect = new RectF(0,0,0,0);

    public RadarRefreshHeader(Context context) {
        this(context,null);
    }

    public RadarRefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs,0);

        mSpinnerStyle = RefreshSpinnerStyle.FIXED_BEHIND;
        final View thisView = this;

        wavePath = new Path();
        paint = new Paint();
        paint.setAntiAlias(true);

        dotsRadius = SmartUtil.dp2px(7);
        radarRadius = SmartUtil.dp2px(20);
        radarCircle = SmartUtil.dp2px(7);
        paint.setStrokeWidth(SmartUtil.dp2px(3));

        thisView.setMinimumHeight(SmartUtil.dp2px(100));

        if (thisView.isInEditMode()) {
            waveTop = 1000;
            radarScale = 1;
            radarAngle = 270;
        } else {
            radarScale = 0;
        }

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BezierRadarHeader);

        horizontalDragEnabled = ta.getBoolean(R.styleable.BezierRadarHeader_srlEnableHorizontalDrag, horizontalDragEnabled);
        setHeaderAccentColor(ta.getColor(R.styleable.BezierRadarHeader_srlAccentColor, 0xFFffffff));
        setHeaderPrimaryColor(ta.getColor(R.styleable.BezierRadarHeader_srlPrimaryColor, 0xFF222222));
        isAccentColorManual = ta.hasValue(R.styleable.BezierRadarHeader_srlAccentColor);
        isPrimaryColorManual = ta.hasValue(R.styleable.BezierRadarHeader_srlPrimaryColor);

        ta.recycle();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animatorSet != null) {
            animatorSet.removeAllListeners();
            animatorSet.end();
            animatorSet = null;
        }
    }

    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        final View thisView = this;
        final int width = thisView.getWidth();
        final int height = thisView.isInEditMode() ? thisView.getHeight() : waveOffsetY;
        drawWaveBackground(canvas, width);
        drawDraggingDots(canvas, width, height);
        drawRadarAnimation(canvas, width, height);
        drawFinishRipple(canvas, width, height);
        super.dispatchDraw(canvas);
    }

    protected void drawWaveBackground(Canvas canvas, int width) {
        wavePath.reset();
        wavePath.lineTo(0, waveTop);
        wavePath.quadTo(waveOffsetX >= 0 ? (waveOffsetX) : width / 2f, waveTop + waveHeight, width, waveTop);
        wavePath.lineTo(width, 0);
        paint.setColor(primaryColor);
        canvas.drawPath(wavePath, paint);
    }

    protected void drawDraggingDots(Canvas canvas, int width, int height) {
        if (dotsAlpha > 0) {
            paint.setColor(accentColor);
            final int num = 7;
            float x = SmartUtil.px2dp(height);
            float wide = (1f * width / num) * dotsFraction -((dotsFraction >1)?((dotsFraction -1)*(1f * width / num)/ dotsFraction):0);//y1 = t*(w/n)-(t>1)*((t-1)*(w/n)/t)
            float high = height - ((dotsFraction > 1) ? ((dotsFraction - 1) * height / 2 / dotsFraction) : 0);//y2 = x - (t>1)*((t-1)*x/t);
            for (int i = 0 ; i < num; i++) {
                int index = 1 + i - (1 + num) / 2;//y3 = (x + 1) - (n + 1)/2; 居中 index 变量：0 1 2 3 4 结果： -2 -1 0 1 2
                float alpha = 255;//y4 = m * ( 1 - 2 * abs(y3) / n); 横向 alpha 差
                paint.setAlpha((int) (dotsAlpha * alpha * (1d - 1d / Math.pow((x / 800d + 1d), 15))));//y5 = y4 * (1-1/((x/800+1)^15));竖直 alpha 差
                float radius = dotsRadius * (1-1/((x/10+1)));//y6 = mDotRadius*(1-1/(x/10+1));半径
                canvas.drawCircle(width / 2f- radius/2 + wide * index , high / 2, radius, paint);
            }
            paint.setAlpha(255);
        }
    }

    protected void drawRadarAnimation(Canvas canvas, int width, int height) {
        final View thisView = this;
        if (/*mRadarAnimator != null*/animatorSet != null || thisView.isInEditMode()) {
            float radius = radarRadius * radarScale;
            float circle = radarCircle * radarScale;

            paint.setColor(accentColor);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(width / 2f, height / 2f, radius, paint);

            paint.setStyle(Paint.Style.STROKE);//设置为空心
            canvas.drawCircle(width / 2f, height / 2f, radius + circle, paint);

            paint.setColor(primaryColor & 0x00ffffff | 0x55000000);
            paint.setStyle(Paint.Style.FILL);
            radarRect.set(width / 2f - radius, height / 2f - radius, width / 2f + radius, height / 2f + radius);
            canvas.drawArc(radarRect, 270, radarAngle, true, paint);

            radius += circle;
            paint.setStyle(Paint.Style.STROKE);
            radarRect.set(width / 2f - radius, height / 2f - radius, width / 2f + radius, height / 2f + radius);
            canvas.drawArc(radarRect, 270, radarAngle, false, paint);

            paint.setStyle(Paint.Style.FILL);
        }
    }

    protected void drawFinishRipple(Canvas canvas, int width, int height) {
        if (rippleRadius > 0) {
            paint.setColor(accentColor);
            canvas.drawCircle(width / 2f, height / 2f, rippleRadius, paint);
        }
    }

    @Override
    public void onDragging(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
        waveOffsetY = offset;
        if (isDragging || waveDragging) {
            waveDragging = true;
            waveTop = Math.min(height, offset);
            waveHeight = (int) (1.9f * Math.max(0, offset - height));
            dotsFraction = percent;

            final View thisView = this;
            thisView.invalidate();
        }
    }

    @Override
    public void onDragReleased(@NonNull final SmartRefreshLayout refreshLayout, int height, int maxDragHeight) {
        waveTop = height - 1;
        waveDragging = false;

        Interpolator interpolatorDecelerate = new SmartUtil(SmartUtil.INTERPOLATOR_DECELERATE);//new DecelerateInterpolator();
        ValueAnimator animatorDotAlpha = ValueAnimator.ofFloat(1, 0);
        animatorDotAlpha.setInterpolator(interpolatorDecelerate);
        animatorDotAlpha.addUpdateListener(new AnimationPropertyUpdater(ANIM_DOT_ALPHA));
        ValueAnimator animatorRadarScale = ValueAnimator.ofFloat(0, 1);
        animatorDotAlpha.setInterpolator(interpolatorDecelerate);
        animatorRadarScale.addUpdateListener(new AnimationPropertyUpdater(ANIM_RADAR_SCALE));
        ValueAnimator mRadarAnimator = ValueAnimator.ofInt(0,360);
        mRadarAnimator.setDuration(720);
        mRadarAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mRadarAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mRadarAnimator.addUpdateListener(new AnimationPropertyUpdater(ANIM_RADAR_ROTATION));
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(animatorDotAlpha, animatorRadarScale, mRadarAnimator);
        animatorSet.start();
        ValueAnimator animatorWave = ValueAnimator.ofInt(
                waveHeight, 0,
                -(int)(waveHeight *0.8f),0,
                -(int)(waveHeight *0.4f),0);
        animatorWave.addUpdateListener(new AnimationPropertyUpdater(ANIM_WAVE_HEIGHT));
        animatorWave.setInterpolator(new SmartUtil(SmartUtil.INTERPOLATOR_DECELERATE));
        animatorWave.setDuration(800);
        animatorWave.start();

        this.animatorSet = animatorSet;
    }

    @Override
    public int onAnimationFinish(@NonNull SmartRefreshLayout layout, boolean success) {
        if (animatorSet != null) {
            animatorSet.removeAllListeners();
            animatorSet.end();
            animatorSet = null;
        }

        final int duration = 400;
        final View thisView = this;
        final int width = thisView.getWidth();
        final int height = waveOffsetY;//thisView.getHeight();
        final float bigRadius = (float) (Math.sqrt(width * width + height * height));
        ValueAnimator animator = ValueAnimator.ofFloat(radarRadius, bigRadius);
        animator.setDuration(duration);
        animator.addUpdateListener(new AnimationPropertyUpdater(ANIM_RIPPLE_RADIUS));
        animator.start();
        return duration;
    }

    @Override@Deprecated
    public void applyPrimaryColors(@ColorInt int ... colors) {
        if (colors.length > 0 && !isPrimaryColorManual) {
            setHeaderPrimaryColor(colors[0]);
            isPrimaryColorManual = false;
        }
        if (colors.length > 1 && !isAccentColorManual) {
            setHeaderAccentColor(colors[1]);
            isAccentColorManual = false;
        }
    }

    @Override
    public void onStateChanged(@NonNull SmartRefreshLayout refreshLayout, @NonNull RefreshLayoutState oldState, @NonNull RefreshLayoutState newState) {
        switch (newState) {
            case IDLE:
            case PULL_DOWN_TO_REFRESH:
                dotsAlpha = 1;
                radarScale = 0;
                rippleRadius = 0;
                break;
        }
    }

    @Override
    public boolean isHorizontalDragSupported() {
        return horizontalDragEnabled;
    }

    @Override
    public void onHorizontalDragging(float percentX, int offsetX, int offsetMax) {
        waveOffsetX = offsetX;
        final View thisView = this;
        thisView.invalidate();
    }

    public RadarRefreshHeader setHeaderAccentColor(@ColorInt int color) {
        accentColor = color;
        isAccentColorManual = true;
        return this;
    }

    public RadarRefreshHeader setHeaderPrimaryColor(@ColorInt int color) {
        primaryColor = color;
        isPrimaryColorManual = true;
        return this;
    }

    public RadarRefreshHeader setHeaderAccentColorRes(@ColorRes int colorId) {
        final View thisView = this;
        setHeaderAccentColor(ContextCompat.getColor(thisView.getContext(), colorId));
        return this;
    }

    public RadarRefreshHeader setHeaderPrimaryColorRes(@ColorRes int colorId) {
        final View thisView = this;
        setHeaderPrimaryColor(ContextCompat.getColor(thisView.getContext(), colorId));
        return this;
    }

    public RadarRefreshHeader enableHorizontalDrag(boolean enable) {
        this.horizontalDragEnabled = enable;
        if (!enable) {
            waveOffsetX = -1;
        }
        return this;
    }

    protected static final byte ANIM_RADAR_SCALE = 0;
    protected static final byte ANIM_WAVE_HEIGHT = 1;
    protected static final byte ANIM_DOT_ALPHA = 2;
    protected static final byte ANIM_RIPPLE_RADIUS = 3;
    protected static final byte ANIM_RADAR_ROTATION = 4;

    protected class AnimationPropertyUpdater implements ValueAnimator.AnimatorUpdateListener {
        byte propertyName;
        AnimationPropertyUpdater(byte name) {
            this.propertyName = name;
        }
        @Override
        public void onAnimationUpdate(@NonNull ValueAnimator animation) {
            if (ANIM_RADAR_SCALE == propertyName) {
                radarScale = (float) animation.getAnimatedValue();
            } else if (ANIM_WAVE_HEIGHT == propertyName) {
                if (waveDragging) {
                    animation.cancel();
                    return;
                }
                waveHeight = (int) animation.getAnimatedValue() / 2;
            } else if (ANIM_DOT_ALPHA == propertyName) {
                dotsAlpha = (float) animation.getAnimatedValue();
            } else if (ANIM_RIPPLE_RADIUS == propertyName) {
                rippleRadius = (float) animation.getAnimatedValue();
            } else if (ANIM_RADAR_ROTATION == propertyName) {
                radarAngle = (int) animation.getAnimatedValue();
            }
            final View thisView = RadarRefreshHeader.this;
            thisView.invalidate();
        }
    }
}
