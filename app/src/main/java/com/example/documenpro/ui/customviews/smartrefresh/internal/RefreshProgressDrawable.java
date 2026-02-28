package com.example.documenpro.ui.customviews.smartrefresh.internal;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

public class RefreshProgressDrawable extends BasePaintDrawable implements Animatable , ValueAnimator.AnimatorUpdateListener{

    protected ValueAnimator rotationAnimator;
    protected int cachedWidth = 0;
    protected int cachedHeight = 0;
    protected int rotationDegree = 0;
    protected Path progressPath = new Path();

    public RefreshProgressDrawable() {
        rotationAnimator = ValueAnimator.ofInt(30, 3600);
        rotationAnimator.setDuration(10000);
        rotationAnimator.setInterpolator(null);
        rotationAnimator.setRepeatCount(ValueAnimator.INFINITE);
        rotationAnimator.setRepeatMode(ValueAnimator.RESTART);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        final Drawable drawable = RefreshProgressDrawable.this;
        final Rect bounds = drawable.getBounds();
        final int width = bounds.width();
        final int height = bounds.height();
        final float r = Math.max(1f, width / 22f);

        if (cachedWidth != width || cachedHeight != height) {
            progressPath.reset();
            progressPath.addCircle(width - r, height / 2f, r, Path.Direction.CW);
            progressPath.addRect(width - 5 * r, height / 2f - r, width - r, height / 2f + r, Path.Direction.CW);
            progressPath.addCircle(width - 5 * r, height / 2f, r, Path.Direction.CW);
            cachedWidth = width;
            cachedHeight = height;
        }

        canvas.save();
        canvas.rotate(rotationDegree, (width) / 2f, (height) / 2f);
        for (int i = 0; i < 12; i++) {
            paint.setAlpha((i+5) * 0x11);
            canvas.rotate(30, (width) / 2f, (height) / 2f);
            canvas.drawPath(progressPath, paint);
        }
        canvas.restore();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        int value = (int) animation.getAnimatedValue();
        rotationDegree = 30 * (value / 30);
        final Drawable drawable = RefreshProgressDrawable.this;
        drawable.invalidateSelf();
    }

    @Override
    public void stop() {
        if (rotationAnimator.isRunning()) {
            Animator animator = rotationAnimator;
            animator.removeAllListeners();
            rotationAnimator.removeAllUpdateListeners();
            rotationAnimator.cancel();
        }
    }

    @Override
    public void start() {
        if (!rotationAnimator.isRunning()) {
            rotationAnimator.addUpdateListener(this);
            rotationAnimator.start();
        }
    }

    @Override
    public boolean isRunning() {
        return rotationAnimator.isRunning();
    }
}
