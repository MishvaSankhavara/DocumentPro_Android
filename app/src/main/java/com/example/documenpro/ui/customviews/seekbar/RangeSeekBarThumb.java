package com.example.documenpro.ui.customviews.seekbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.core.content.ContextCompat;

import com.example.documenpro.R;

import java.text.DecimalFormat;

public class RangeSeekBarThumb {
    // Indicator mode constants
    public static final int INDICATOR_SHOW_WHEN_TOUCH = 0;
    public static final int INDICATOR_ALWAYS_HIDE = 1;
    public static final int INDICATOR_ALWAYS_SHOW_AFTER_TOUCH = 2;
    public static final int INDICATOR_ALWAYS_SHOW = 3;

    @IntDef({
            INDICATOR_SHOW_WHEN_TOUCH,
            INDICATOR_ALWAYS_HIDE,
            INDICATOR_ALWAYS_SHOW_AFTER_TOUCH,
            INDICATOR_ALWAYS_SHOW
    })
    public @interface IndicatorModeDef {}

    public static final int WRAP_CONTENT = -1;
    public static final int MATCH_PARENT = -2;
    RangeSeekBar parentSeekBar;
    private int indicatorShowMode;
    private int indicatorHeight;
    private int indicatorWidth;
    private int indicatorMargin;
    private int indicatorDrawableId;
    private int indicatorArrowSize;
    private int indicatorTextSize;
    private int indicatorTextColor;
    private float indicatorRadius;
    private int indicatorBackgroundColor;
    private int indicatorPaddingLeft;
    private int indicatorPaddingRight;
    private int indicatorPaddingTop;
    private int indicatorPaddingBottom;
    String indicatorTextStringFormat;
    DecimalFormat indicatorTextDecimalFormat;
    private int thumbDrawableId;
    private int thumbInactivatedDrawableId;
    private int thumbWidth;
    private int thumbHeight;
    float thumbScaleRatio;
    int thumbLeft;
    int thumbRight;
    int thumbTop;
    int thumbBottom;
    boolean isLeftThumb;
    boolean isActive = false;
    boolean isThumbVisible = true;
    private boolean isShowIndicator;
    float currentPercent;
    float rippleAnimationValue = 0;
    ValueAnimator restoreAnimator;
    Paint indicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Path indicatorArrowShape = new Path();
    Rect indicatorTextBounds = new Rect();
    Rect indicatorBounds = new Rect();
    Bitmap activeThumbBitmap;
    Bitmap inactiveThumbBitmap;
    Bitmap indicatorBitmap;

    String customIndicatorText;
    int scaledThumbWidth;
    int scaledThumbHeight;

    public RangeSeekBarThumb(RangeSeekBar rangeSeekBar, AttributeSet attrs, boolean isLeft) {
        this.parentSeekBar = rangeSeekBar;
        this.isLeftThumb = isLeft;
        initializeAttributes(attrs);
        initializeBitmaps();
        initializeVariables();
    }

    private void initializeAttributes(AttributeSet attrs) {
        TypedArray t = getContext().obtainStyledAttributes(attrs, R.styleable.RangeSeekBar);
        if (t == null) return;
        indicatorMargin = (int) t.getDimension(R.styleable.RangeSeekBar_rsb_indicator_margin, 0);
        indicatorDrawableId = t.getResourceId(R.styleable.RangeSeekBar_rsb_indicator_drawable, 0);
        indicatorShowMode = t.getInt(R.styleable.RangeSeekBar_rsb_indicator_show_mode, INDICATOR_ALWAYS_HIDE);
        indicatorHeight = t.getLayoutDimension(R.styleable.RangeSeekBar_rsb_indicator_height, WRAP_CONTENT);
        indicatorWidth = t.getLayoutDimension(R.styleable.RangeSeekBar_rsb_indicator_width, WRAP_CONTENT);
        indicatorTextSize = (int) t.getDimension(R.styleable.RangeSeekBar_rsb_indicator_text_size, SeekBarUtils.dpToPx(getContext(), 14));
        indicatorTextColor = t.getColor(R.styleable.RangeSeekBar_rsb_indicator_text_color, Color.WHITE);
        indicatorBackgroundColor = t.getColor(R.styleable.RangeSeekBar_rsb_indicator_background_color, ContextCompat.getColor(getContext(), R.color.colorAccent));
        indicatorPaddingLeft = (int) t.getDimension(R.styleable.RangeSeekBar_rsb_indicator_padding_left, 0);
        indicatorPaddingRight = (int) t.getDimension(R.styleable.RangeSeekBar_rsb_indicator_padding_right, 0);
        indicatorPaddingTop = (int) t.getDimension(R.styleable.RangeSeekBar_rsb_indicator_padding_top, 0);
        indicatorPaddingBottom = (int) t.getDimension(R.styleable.RangeSeekBar_rsb_indicator_padding_bottom, 0);
        indicatorArrowSize = (int) t.getDimension(R.styleable.RangeSeekBar_rsb_indicator_arrow_size, 0);
        thumbDrawableId = t.getResourceId(R.styleable.RangeSeekBar_rsb_thumb_drawable, R.drawable.rsb_default_thumb);
        thumbInactivatedDrawableId = t.getResourceId(R.styleable.RangeSeekBar_rsb_thumb_inactivated_drawable, 0);
        thumbWidth = (int) t.getDimension(R.styleable.RangeSeekBar_rsb_thumb_width, SeekBarUtils.dpToPx(getContext(), 26));
        thumbHeight = (int) t.getDimension(R.styleable.RangeSeekBar_rsb_thumb_height, SeekBarUtils.dpToPx(getContext(), 26));
        thumbScaleRatio = t.getFloat(R.styleable.RangeSeekBar_rsb_thumb_scale_ratio, 1f);
        indicatorRadius = t.getDimension(R.styleable.RangeSeekBar_rsb_indicator_radius, 0f);
        t.recycle();
    }

    protected void initializeVariables() {
        scaledThumbWidth = thumbWidth;
        scaledThumbHeight = thumbHeight;
        if (indicatorHeight == WRAP_CONTENT) {
            indicatorHeight = SeekBarUtils.measureTextBounds("8", indicatorTextSize).height() + indicatorPaddingTop + indicatorPaddingBottom;
        }
        if (indicatorArrowSize <= 0) {
            indicatorArrowSize = (int) (thumbWidth / 4);
        }
    }

    public Context getContext() {
        return parentSeekBar.getContext();
    }

    public Resources getResources() {
        if (getContext() != null) return getContext().getResources();
        return null;
    }

    private void initializeBitmaps() {
        setIndicatorDrawableId(indicatorDrawableId);
        setThumbDrawableId(thumbDrawableId, thumbWidth, thumbHeight);
        setThumbInactivatedDrawableId(thumbInactivatedDrawableId, thumbWidth, thumbHeight);
    }

    protected void onSizeChanged(int x, int y) {
        initializeVariables();
        initializeBitmaps();
        thumbLeft = (int) (x - getThumbScaleWidth() / 2);
        thumbRight = (int) (x + getThumbScaleWidth() / 2);
        thumbTop = y - getThumbHeight() / 2;
        thumbBottom = y + getThumbHeight() / 2;
    }

    public void scaleThumb() {
        scaledThumbWidth = (int) getThumbScaleWidth();
        scaledThumbHeight = (int) getThumbScaleHeight();
        int y = parentSeekBar.getProgressBottom();
        thumbTop = y - scaledThumbHeight / 2;
        thumbBottom = y + scaledThumbHeight / 2;
        setThumbDrawableId(thumbDrawableId, scaledThumbWidth, scaledThumbHeight);
    }

    public void resetThumb() {
        scaledThumbWidth = getThumbWidth();
        scaledThumbHeight = getThumbHeight();
        int y = parentSeekBar.getProgressBottom();
        thumbTop = y - scaledThumbHeight / 2;
        thumbBottom = y + scaledThumbHeight / 2;
        setThumbDrawableId(thumbDrawableId, scaledThumbWidth, scaledThumbHeight);
    }

    public float getRawHeight() {
        return getIndicatorHeight() + getIndicatorArrowSize() + getIndicatorMargin() + getThumbScaleHeight();
    }

    protected void drawThumb(Canvas canvas) {
        if (!isThumbVisible) {
            return;
        }
        int offset = (int) (parentSeekBar.getProgressWidth() * currentPercent);
        canvas.save();
        canvas.translate(offset, 0);
        // translate canvas, then don't care left
        canvas.translate(thumbLeft, 0);
        if (isShowIndicator) {
            onDrawIndicator(canvas, indicatorPaint, formatIndicatorText(customIndicatorText));
        }
        onDrawThumb(canvas);
        canvas.restore();
    }

    protected void onDrawThumb(Canvas canvas) {
        if (inactiveThumbBitmap != null && !isActive) {
            canvas.drawBitmap(inactiveThumbBitmap, 0, parentSeekBar.getProgressTop() + (parentSeekBar.getProgressHeight() - scaledThumbHeight) / 2f, null);
        } else if (activeThumbBitmap != null) {
            canvas.drawBitmap(activeThumbBitmap, 0, parentSeekBar.getProgressTop() + (parentSeekBar.getProgressHeight() - scaledThumbHeight) / 2f, null);
        }
    }

    protected String formatIndicatorText(String text2Draw) {
        SeekBarState[] states = parentSeekBar.getRangeSeekBarState();
        if (TextUtils.isEmpty(text2Draw)) {
            if (isLeftThumb) {
                if (indicatorTextDecimalFormat != null) {
                    text2Draw = indicatorTextDecimalFormat.format(states[0].progressValue);
                } else {
                    text2Draw = states[0].indicatorDisplayText;
                }
            } else {
                if (indicatorTextDecimalFormat != null) {
                    text2Draw = indicatorTextDecimalFormat.format(states[1].progressValue);
                } else {
                    text2Draw = states[1].indicatorDisplayText;
                }
            }
        }
        if (indicatorTextStringFormat != null) {
            text2Draw = String.format(indicatorTextStringFormat, text2Draw);
        }
        return text2Draw;
    }

    protected void onDrawIndicator(Canvas canvas, Paint paint, String text2Draw) {
        if (text2Draw == null) return;
        paint.setTextSize(indicatorTextSize);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(indicatorBackgroundColor);
        paint.getTextBounds(text2Draw, 0, text2Draw.length(), indicatorTextBounds);
        int realIndicatorWidth = indicatorTextBounds.width() + indicatorPaddingLeft + indicatorPaddingRight;
        if (indicatorWidth > realIndicatorWidth) {
            realIndicatorWidth = indicatorWidth;
        }

        int realIndicatorHeight = indicatorTextBounds.height() + indicatorPaddingTop + indicatorPaddingBottom;
        if (indicatorHeight > realIndicatorHeight) {
            realIndicatorHeight = indicatorHeight;
        }

        indicatorBounds.left = (int) (scaledThumbWidth / 2f - realIndicatorWidth / 2f);
        indicatorBounds.top = thumbBottom - realIndicatorHeight - scaledThumbHeight - indicatorMargin;
        indicatorBounds.right = indicatorBounds.left + realIndicatorWidth;
        indicatorBounds.bottom = indicatorBounds.top + realIndicatorHeight;

        if (indicatorBitmap == null) {
            int ax = scaledThumbWidth / 2;
            int ay = indicatorBounds.bottom;
            int bx = ax - indicatorArrowSize;
            int by = ay - indicatorArrowSize;
            int cx = ax + indicatorArrowSize;
            indicatorArrowShape.reset();
            indicatorArrowShape.moveTo(ax, ay);
            indicatorArrowShape.lineTo(bx, by);
            indicatorArrowShape.lineTo(cx, by);
            indicatorArrowShape.close();
            canvas.drawPath(indicatorArrowShape, paint);
            indicatorBounds.bottom -= indicatorArrowSize;
            indicatorBounds.top -= indicatorArrowSize;
        }

        int defaultPaddingOffset = SeekBarUtils.dpToPx(getContext(), 1);
        int leftOffset = indicatorBounds.width() / 2 - (int) (parentSeekBar.getProgressWidth() * currentPercent) - parentSeekBar.getProgressLeft() + defaultPaddingOffset;
        int rightOffset = indicatorBounds.width() / 2 - (int) (parentSeekBar.getProgressWidth() * (1 - currentPercent)) - parentSeekBar.getProgressPaddingRight() + defaultPaddingOffset;

        if (leftOffset > 0) {
            indicatorBounds.left += leftOffset;
            indicatorBounds.right += leftOffset;
        } else if (rightOffset > 0) {
            indicatorBounds.left -= rightOffset;
            indicatorBounds.right -= rightOffset;
        }

        if (indicatorBitmap != null) {
            SeekBarUtils.drawBitmapSafely(canvas, paint, indicatorBitmap, indicatorBounds);
        } else if (indicatorRadius > 0f) {
            canvas.drawRoundRect(new RectF(indicatorBounds), indicatorRadius, indicatorRadius, paint);
        } else {
            canvas.drawRect(indicatorBounds, paint);
        }

        int tx, ty;
        if (indicatorPaddingLeft > 0) {
            tx = indicatorBounds.left + indicatorPaddingLeft;
        } else if (indicatorPaddingRight > 0) {
            tx = indicatorBounds.right - indicatorPaddingRight - indicatorTextBounds.width();
        } else {
            tx = indicatorBounds.left + (realIndicatorWidth - indicatorTextBounds.width()) / 2;
        }

        if (indicatorPaddingTop > 0) {
            ty = indicatorBounds.top + indicatorTextBounds.height() + indicatorPaddingTop;
        } else if (indicatorPaddingBottom > 0) {
            ty = indicatorBounds.bottom - indicatorTextBounds.height() - indicatorPaddingBottom;
        } else {
            ty = indicatorBounds.bottom - (realIndicatorHeight - indicatorTextBounds.height()) / 2 + 1;
        }

        paint.setColor(indicatorTextColor);
        canvas.drawText(text2Draw, tx, ty, paint);
    }

    protected boolean isTouchInsideThumb(float x, float y) {
        int offset = (int) (parentSeekBar.getProgressWidth() * currentPercent);
        return x > thumbLeft + offset && x < thumbRight + offset && y > thumbTop && y < thumbBottom;
    }

    protected void updatePercent(float percent) {
        if (percent < 0) percent = 0;
        else if (percent > 1) percent = 1;
        currentPercent = percent;
    }

    protected void updateIndicatorVisibility(boolean isEnable) {
        switch (indicatorShowMode) {
            case INDICATOR_SHOW_WHEN_TOUCH:
                isShowIndicator = isEnable;
                break;
            case INDICATOR_ALWAYS_SHOW:
            case INDICATOR_ALWAYS_SHOW_AFTER_TOUCH:
                isShowIndicator = true;
                break;
            case INDICATOR_ALWAYS_HIDE:
                isShowIndicator = false;
                break;
        }
    }

    public void restoreMaterialAnimation() {
        if (restoreAnimator != null) restoreAnimator.cancel();
        restoreAnimator = ValueAnimator.ofFloat(rippleAnimationValue, 0);
        restoreAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                rippleAnimationValue = (float) animation.getAnimatedValue();
                if (parentSeekBar != null) parentSeekBar.invalidate();
            }
        });
        restoreAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                rippleAnimationValue = 0;
                if (parentSeekBar != null) parentSeekBar.invalidate();
            }
        });
        restoreAnimator.start();
    }

    public void setIndicatorText(String text) {
        customIndicatorText = text;
    }

    public void setIndicatorTextDecimalFormat(String formatPattern) {
        indicatorTextDecimalFormat = new DecimalFormat(formatPattern);
    }

    public DecimalFormat getIndicatorTextDecimalFormat() {
        return indicatorTextDecimalFormat;
    }

    public void setIndicatorTextStringFormat(String formatPattern) {
        indicatorTextStringFormat = formatPattern;
    }

    public int getIndicatorDrawableId() {
        return indicatorDrawableId;
    }

    public void setIndicatorDrawableId(@DrawableRes int indicatorDrawableId) {
        if (indicatorDrawableId != 0) {
            this.indicatorDrawableId = indicatorDrawableId;
            indicatorBitmap = BitmapFactory.decodeResource(getResources(), indicatorDrawableId);
        }
    }

    public int getIndicatorArrowSize() {
        return indicatorArrowSize;
    }

    public void setIndicatorArrowSize(int indicatorArrowSize) {
        this.indicatorArrowSize = indicatorArrowSize;
    }

    public int getIndicatorPaddingLeft() {
        return indicatorPaddingLeft;
    }

    public void setIndicatorPaddingLeft(int indicatorPaddingLeft) {
        this.indicatorPaddingLeft = indicatorPaddingLeft;
    }

    public int getIndicatorPaddingRight() {
        return indicatorPaddingRight;
    }

    public void setIndicatorPaddingRight(int indicatorPaddingRight) {
        this.indicatorPaddingRight = indicatorPaddingRight;
    }

    public int getIndicatorPaddingTop() {
        return indicatorPaddingTop;
    }

    public void setIndicatorPaddingTop(int indicatorPaddingTop) {
        this.indicatorPaddingTop = indicatorPaddingTop;
    }

    public int getIndicatorPaddingBottom() {
        return indicatorPaddingBottom;
    }

    public void setIndicatorPaddingBottom(int indicatorPaddingBottom) {
        this.indicatorPaddingBottom = indicatorPaddingBottom;
    }

    public int getIndicatorMargin() {
        return indicatorMargin;
    }

    public void setIndicatorMargin(int indicatorMargin) {
        this.indicatorMargin = indicatorMargin;
    }

    public int getIndicatorShowMode() {
        return indicatorShowMode;
    }

    public void setIndicatorShowMode(@IndicatorModeDef int indicatorShowMode) {
        this.indicatorShowMode = indicatorShowMode;
    }

    public void showIndicator(boolean isShown) {
        isShowIndicator = isShown;
    }

    public boolean isShowIndicator() {
        return isShowIndicator;
    }

    public int getIndicatorRawHeight() {
        if (indicatorHeight > 0) {
            if (indicatorBitmap != null) {
                return indicatorHeight + indicatorMargin;
            } else {
                return indicatorHeight + indicatorArrowSize + indicatorMargin;
            }
        } else {
            if (indicatorBitmap != null) {
                return SeekBarUtils.measureTextBounds("8", indicatorTextSize).height() + indicatorPaddingTop + indicatorPaddingBottom + indicatorMargin;
            } else {
                return SeekBarUtils.measureTextBounds("8", indicatorTextSize).height() + indicatorPaddingTop + indicatorPaddingBottom + indicatorMargin + indicatorArrowSize;
            }
        }
    }

    public int getIndicatorHeight() {
        return indicatorHeight;
    }

    public void setIndicatorHeight(int indicatorHeight) {
        this.indicatorHeight = indicatorHeight;
    }

    public int getIndicatorWidth() {
        return indicatorWidth;
    }

    public void setIndicatorWidth(int indicatorWidth) {
        this.indicatorWidth = indicatorWidth;
    }

    public int getIndicatorTextSize() {
        return indicatorTextSize;
    }

    public void setIndicatorTextSize(int indicatorTextSize) {
        this.indicatorTextSize = indicatorTextSize;
    }

    public int getIndicatorTextColor() {
        return indicatorTextColor;
    }

    public void setIndicatorTextColor(@ColorInt int indicatorTextColor) {
        this.indicatorTextColor = indicatorTextColor;
    }

    public int getIndicatorBackgroundColor() {
        return indicatorBackgroundColor;
    }

    public void setIndicatorBackgroundColor(@ColorInt int indicatorBackgroundColor) {
        this.indicatorBackgroundColor = indicatorBackgroundColor;
    }

    public int getThumbInactivatedDrawableId() {
        return thumbInactivatedDrawableId;
    }

    public void setThumbInactivatedDrawableId(@DrawableRes int thumbInactivatedDrawableId, int width, int height) {
        if (thumbInactivatedDrawableId != 0 && getResources() != null) {
            this.thumbInactivatedDrawableId = thumbInactivatedDrawableId;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                inactiveThumbBitmap = SeekBarUtils.convertDrawableToBitmap(width, height, getResources().getDrawable(thumbInactivatedDrawableId, null));
            } else {
                inactiveThumbBitmap = SeekBarUtils.convertDrawableToBitmap(width, height, getResources().getDrawable(thumbInactivatedDrawableId));
            }
        }
    }

    public int getThumbDrawableId() {
        return thumbDrawableId;
    }

    public void setThumbDrawableId(@DrawableRes int thumbDrawableId, int width, int height) {
        if (thumbDrawableId != 0 && getResources() != null && width > 0 && height > 0) {
            this.thumbDrawableId = thumbDrawableId;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activeThumbBitmap = SeekBarUtils.convertDrawableToBitmap(width, height, getResources().getDrawable(thumbDrawableId, null));
            } else {
                activeThumbBitmap = SeekBarUtils.convertDrawableToBitmap(width, height, getResources().getDrawable(thumbDrawableId));
            }
        }
    }

    public void setThumbDrawableId(@DrawableRes int thumbDrawableId) {
        if (thumbWidth <= 0 || thumbHeight <= 0){
            throw new IllegalArgumentException("please set thumbWidth and thumbHeight first!");
        }
        if (thumbDrawableId != 0 && getResources() != null) {
            this.thumbDrawableId = thumbDrawableId;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activeThumbBitmap = SeekBarUtils.convertDrawableToBitmap(thumbWidth, thumbHeight, getResources().getDrawable(thumbDrawableId, null));
            } else {
                activeThumbBitmap = SeekBarUtils.convertDrawableToBitmap(thumbWidth, thumbHeight, getResources().getDrawable(thumbDrawableId));
            }
        }
    }

    public int getThumbWidth() {
        return thumbWidth;
    }

    public void setThumbWidth(int thumbWidth) {
        this.thumbWidth = thumbWidth;
    }

    public float getThumbScaleHeight() {
        return thumbHeight * thumbScaleRatio;
    }

    public float getThumbScaleWidth() {
        return thumbWidth * thumbScaleRatio;
    }

    public int getThumbHeight() {
        return thumbHeight;
    }

    public void setThumbHeight(int thumbHeight) {
        this.thumbHeight = thumbHeight;
    }

    public float getIndicatorRadius() {
        return indicatorRadius;
    }

    public void setIndicatorRadius(float indicatorRadius) {
        this.indicatorRadius = indicatorRadius;
    }

    protected boolean getActivate() {
        return isActive;
    }

    protected void setActivate(boolean activate) {
        isActive = activate;
    }

    public void setTypeface(Typeface typeFace) {
        indicatorPaint.setTypeface(typeFace);
    }

    public float getThumbScaleRatio() {
        return thumbScaleRatio;
    }

    public boolean isVisible() {
        return isThumbVisible;
    }

    public void setVisible(boolean visible) {
        isThumbVisible = visible;
    }

    public float getProgress() {
        float range = parentSeekBar.getMaxProgress() - parentSeekBar.getMinProgress();
        return parentSeekBar.getMinProgress() + range * currentPercent;
    }
}