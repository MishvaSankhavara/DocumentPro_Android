package com.example.documenpro.ui.customviews.smartrefresh.internal;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

public class RefreshArrowDrawable extends BasePaintDrawable {
    private int cachedWidth = 0;
    private int cachedHeight = 0;
    private final Path arrowPath = new Path();

    @Override
    public void draw(@NonNull Canvas canvas) {
        final Drawable drawable = RefreshArrowDrawable.this;
        final Rect bounds = drawable.getBounds();
        final int width = bounds.width();
        final int height = bounds.height();
        if (cachedWidth != width || cachedHeight != height) {
            int lineWidth = width * 30 / 225;
            arrowPath.reset();

            float vector1 = (lineWidth * 0.70710678118654752440084436210485f);
            float vector2 = (lineWidth / 0.70710678118654752440084436210485f);
            arrowPath.moveTo(width / 2f, height);
            arrowPath.lineTo(0, height / 2f);
            arrowPath.lineTo(vector1, height / 2f - vector1);
            arrowPath.lineTo(width / 2f - lineWidth / 2f, height - vector2 - lineWidth / 2f);
            arrowPath.lineTo(width / 2f - lineWidth / 2f, 0);
            arrowPath.lineTo(width / 2f + lineWidth / 2f, 0);
            arrowPath.lineTo(width / 2f + lineWidth / 2f, height - vector2 - lineWidth / 2f);
            arrowPath.lineTo(width - vector1, height / 2f - vector1);
            arrowPath.lineTo(width, height / 2f);
            arrowPath.close();

            cachedWidth = width;
            cachedHeight = height;
        }
        canvas.drawPath(arrowPath, paint);
    }
}
