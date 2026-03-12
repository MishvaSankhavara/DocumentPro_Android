package com.example.documenpro.ui.customviews.switchdaynight;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;

import com.example.documenpro.R;

public class ThemeToggleSwitch extends View implements Animator.AnimatorListener {

    private boolean animating = false;

    private final GradientDrawable dayBackgroundDrawable;
    private final BitmapDrawable nightBackgroundBitmap;
    private final BitmapDrawable sunBitmap;
    private final BitmapDrawable moonBitmap;
    private final BitmapDrawable cloudsBitmap;

    private float animationValue;
    private boolean nightMode;
    private int animationDuration;
    private ThemeToggleSwitchListener switchListener;
    private ThemeToggleAnimationListener animationListener;

    public ThemeToggleSwitch(Context context) {
        this(context, null, 0);
    }

    public ThemeToggleSwitch(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThemeToggleSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);

        animationValue = 0;
        nightMode = false;
        animationDuration = 500;

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSwitch();
            }
        });
        dayBackgroundDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                new int[] { Color.parseColor("#21b5e7"), Color.parseColor("#59ccda") });
        dayBackgroundDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        nightBackgroundBitmap = (BitmapDrawable) getContext().getResources().getDrawable(R.drawable.dark_background);
        sunBitmap = (BitmapDrawable) getContext().getResources().getDrawable(R.drawable.img_sun);
        moonBitmap = (BitmapDrawable) getContext().getResources().getDrawable(R.drawable.img_moon);
        cloudsBitmap = (BitmapDrawable) getContext().getResources().getDrawable(R.drawable.img_clouds);
    }

    public void toggleSwitch() {
        if (!animating) {
            animating = true;
            nightMode = !nightMode;
            if (switchListener != null)
                switchListener.onThemeChanged(nightMode);
            startToggleAnimation();
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        int space = getWidth() - getHeight();

        nightBackgroundBitmap.setBounds(0, 0, getWidth(), getHeight());
        nightBackgroundBitmap.setAlpha((int) (animationValue * 255));
        nightBackgroundBitmap.draw(canvas);

        dayBackgroundDrawable.setCornerRadius(getHeight() / 2);
        dayBackgroundDrawable.setBounds(0, 0, getWidth(), getHeight());
        dayBackgroundDrawable.setAlpha(255 - ((int) (animationValue * 255)));
        dayBackgroundDrawable.draw(canvas);

        moonBitmap.setBounds((int) (animationValue * space), 0, (int) (animationValue * space) + getHeight(),
                getHeight());
        moonBitmap.setAlpha((int) (animationValue * 255));
        moonBitmap.getBitmap();

        sunBitmap.setBounds((int) (animationValue * space), 0, (int) (animationValue * space) + getHeight(),
                getHeight());
        sunBitmap.setAlpha(255 - ((int) (animationValue * 255)));

        moonBitmap.draw(canvas);
        sunBitmap.draw(canvas);

        moonBitmap.setAlpha((int) (animationValue * 255));

        int clouds_bitmap_alpha = animationValue <= 0.5 ? (int) ((1.0f - animationValue * 2.0f) * 255) : 0;
        cloudsBitmap.setAlpha(clouds_bitmap_alpha);

        int clouds_bitmap_left = (int) (animationValue * (getHeight() / 2));
        cloudsBitmap.setBounds(clouds_bitmap_left, 0, clouds_bitmap_left + getHeight(), getHeight());
        cloudsBitmap.draw(canvas);

    }

    private void startToggleAnimation() {
        final ValueAnimator va = ValueAnimator.ofFloat(0, 1);
        if (animationValue == 1)
            va.setFloatValues(1, 0);

        va.setDuration(animationDuration);
        va.addListener(this);
        va.setInterpolator(new DecelerateInterpolator());
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                animationValue = (float) animation.getAnimatedValue();
                if (animationListener != null)
                    animationListener.onAnimValueChanged(animationValue);
                invalidate();
            }
        });
        va.start();
    }

    @Override
    public void onAnimationStart(@NonNull Animator animation) {
        if (animationListener != null)
            animationListener.onAnimStart();
    }

    @Override
    public void onAnimationEnd(@NonNull Animator animation) {
        animating = false;
        if (animationListener != null)
            animationListener.onAnimEnd();
    }

    @Override
    public void onAnimationCancel(@NonNull Animator animation) {

    }

    @Override
    public void onAnimationRepeat(@NonNull Animator animation) {

    }

    public void setNightMode(boolean is_night) {
        this.nightMode = is_night;
        animationValue = is_night ? 1 : 0;
        invalidate();
        if (switchListener != null)
            switchListener.onThemeChanged(is_night);
    }

    public boolean isNightMode() {
        return nightMode;
    }

    public void setSwitchListener(ThemeToggleSwitchListener listener) {
        this.switchListener = listener;
    }

    public void setAnimationListener(ThemeToggleAnimationListener animListener) {
        this.animationListener = animListener;
    }

    public void setAnimationDuration(int duration) {
        this.animationDuration = duration;
    }
}
