package com.example.documenpro.ui.customviews.smartrefresh.header;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.documenpro.R;
import com.example.documenpro.ui.customviews.smartrefresh.api.RefreshHeaderComponent;
import com.example.documenpro.ui.customviews.smartrefresh.api.SmartRefreshLayout;
import com.example.documenpro.ui.customviews.smartrefresh.constant.RefreshLayoutState;
import com.example.documenpro.ui.customviews.smartrefresh.constant.RefreshSpinnerStyle;
import com.example.documenpro.ui.customviews.smartrefresh.internal.RefreshArrowDrawable;
import com.example.documenpro.ui.customviews.smartrefresh.internal.RefreshClassicComponent;
import com.example.documenpro.ui.customviews.smartrefresh.internal.RefreshProgressDrawable;
import com.example.documenpro.ui.customviews.smartrefresh.util.SmartViewUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClassicRefreshHeaderView extends RefreshClassicComponent<ClassicRefreshHeaderView> implements RefreshHeaderComponent {

    public static final int VIEW_ID_LAST_UPDATE_TEXT = R.id.srl_classics_update;

    public static String DEFAULT_TEXT_PULLING = null;
    public static String DEFAULT_TEXT_REFRESHING = null;
    public static String DEFAULT_TEXT_LOADING = null;
    public static String DEFAULT_TEXT_RELEASE = null;
    public static String DEFAULT_TEXT_FINISH = null;
    public static String DEFAULT_TEXT_FAILED = null;
    public static String DEFAULT_TEXT_UPDATE = null;
    public static String DEFAULT_TEXT_SECONDARY = null;

    protected String KEY_LAST_UPDATE_TIME = "LAST_UPDATE_TIME";

    protected Date lastTime;
    protected TextView lastUpdateText;
    protected SharedPreferences shared;
    protected DateFormat lastUpdateFormat;
    protected boolean enableLastTime = true;

    protected String textPulling;
    protected String textRefreshing;
    protected String textLoading;
    protected String textRelease;
    protected String textFinish;
    protected String textFailed;
    protected String textUpdate;
    protected String textSecondary;

    public ClassicRefreshHeaderView(Context context) {
        this(context, null);
    }

    public ClassicRefreshHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);

        View.inflate(context, R.layout.srl_classics_header, this);

        final View thisView = this;
        final View arrowView = this.arrowView = thisView.findViewById(R.id.srl_classics_arrow);
        final View updateView = lastUpdateText = thisView.findViewById(R.id.srl_classics_update);
        final View progressView = this.progressView = thisView.findViewById(R.id.srl_classics_progress);

        titleTextView = thisView.findViewById(R.id.srl_classics_title);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ClassicsHeader);

        LayoutParams lpArrow = (LayoutParams) arrowView.getLayoutParams();
        LayoutParams lpProgress = (LayoutParams) progressView.getLayoutParams();
        LinearLayout.LayoutParams lpUpdateText = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lpUpdateText.topMargin = ta.getDimensionPixelSize(R.styleable.ClassicsHeader_srlTextTimeMarginTop, SmartViewUtil.dpToPx(0));
        lpProgress.rightMargin = ta.getDimensionPixelSize(R.styleable.ClassicsFooter_srlDrawableMarginRight, SmartViewUtil.dpToPx(20));
        lpArrow.rightMargin = lpProgress.rightMargin;

        lpArrow.width = ta.getLayoutDimension(R.styleable.ClassicsHeader_srlDrawableArrowSize, lpArrow.width);
        lpArrow.height = ta.getLayoutDimension(R.styleable.ClassicsHeader_srlDrawableArrowSize, lpArrow.height);
        lpProgress.width = ta.getLayoutDimension(R.styleable.ClassicsHeader_srlDrawableProgressSize, lpProgress.width);
        lpProgress.height = ta.getLayoutDimension(R.styleable.ClassicsHeader_srlDrawableProgressSize, lpProgress.height);

        lpArrow.width = ta.getLayoutDimension(R.styleable.ClassicsHeader_srlDrawableSize, lpArrow.width);
        lpArrow.height = ta.getLayoutDimension(R.styleable.ClassicsHeader_srlDrawableSize, lpArrow.height);
        lpProgress.width = ta.getLayoutDimension(R.styleable.ClassicsHeader_srlDrawableSize, lpProgress.width);
        lpProgress.height = ta.getLayoutDimension(R.styleable.ClassicsHeader_srlDrawableSize, lpProgress.height);

        finishAnimationDuration = ta.getInt(R.styleable.ClassicsHeader_srlFinishDuration, finishAnimationDuration);
        enableLastTime = ta.getBoolean(R.styleable.ClassicsHeader_srlEnableLastTime, enableLastTime);
        spinnerStyle = RefreshSpinnerStyle.STYLES[ta.getInt(R.styleable.ClassicsHeader_srlClassicsSpinnerStyle, spinnerStyle.ordinal)];

        if (ta.hasValue(R.styleable.ClassicsHeader_srlDrawableArrow)) {
            this.arrowView.setImageDrawable(ta.getDrawable(R.styleable.ClassicsHeader_srlDrawableArrow));
        } else if (this.arrowView.getDrawable() == null) {
            arrowDrawable = new RefreshArrowDrawable();
            arrowDrawable.setPaintColor(getResources().getColor(R.color.colorSolidText));
            this.arrowView.setImageDrawable(arrowDrawable);
        }

        if (ta.hasValue(R.styleable.ClassicsHeader_srlDrawableProgress)) {
            this.progressView.setImageDrawable(ta.getDrawable(R.styleable.ClassicsHeader_srlDrawableProgress));
        } else if (this.progressView.getDrawable() == null) {
            progressDrawable = new RefreshProgressDrawable();
            progressDrawable.setPaintColor(getResources().getColor(R.color.colorSolidText));
            this.progressView.setImageDrawable(progressDrawable);
        }

        if (ta.hasValue(R.styleable.ClassicsHeader_srlTextSizeTitle)) {
            titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ta.getDimensionPixelSize(R.styleable.ClassicsHeader_srlTextSizeTitle, SmartViewUtil.dpToPx(16)));
        }

        if (ta.hasValue(R.styleable.ClassicsHeader_srlTextSizeTime)) {
            lastUpdateText.setTextSize(TypedValue.COMPLEX_UNIT_PX, ta.getDimensionPixelSize(R.styleable.ClassicsHeader_srlTextSizeTime, SmartViewUtil.dpToPx(12)));
        }

        if (ta.hasValue(R.styleable.ClassicsHeader_srlPrimaryColor)) {
            super.applyPrimaryColor(ta.getColor(R.styleable.ClassicsHeader_srlPrimaryColor, 0));
        }
        if (ta.hasValue(R.styleable.ClassicsHeader_srlAccentColor)) {
            applyAccentColor(ta.getColor(R.styleable.ClassicsHeader_srlAccentColor, 0));
        }

        if (ta.hasValue(R.styleable.ClassicsHeader_srlTextPulling)) {
            textPulling = ta.getString(R.styleable.ClassicsHeader_srlTextPulling);
        } else if (DEFAULT_TEXT_PULLING != null) {
            textPulling = DEFAULT_TEXT_PULLING;
        } else {
            textPulling = context.getString(R.string.srl_header_pulling);
        }
        if (ta.hasValue(R.styleable.ClassicsHeader_srlTextLoading)) {
            textLoading = ta.getString(R.styleable.ClassicsHeader_srlTextLoading);
        } else if (DEFAULT_TEXT_LOADING != null) {
            textLoading = DEFAULT_TEXT_LOADING;
        } else {
            textLoading = context.getString(R.string.srl_header_loading);
        }
        if (ta.hasValue(R.styleable.ClassicsHeader_srlTextRelease)) {
            textRelease = ta.getString(R.styleable.ClassicsHeader_srlTextRelease);
        } else if (DEFAULT_TEXT_RELEASE != null) {
            textRelease = DEFAULT_TEXT_RELEASE;
        } else {
            textRelease = context.getString(R.string.srl_header_release);
        }
        if (ta.hasValue(R.styleable.ClassicsHeader_srlTextFinish)) {
            textFinish = ta.getString(R.styleable.ClassicsHeader_srlTextFinish);
        } else if (DEFAULT_TEXT_FINISH != null) {
            textFinish = DEFAULT_TEXT_FINISH;
        } else {
            textFinish = context.getString(R.string.srl_header_finish);
        }
        if (ta.hasValue(R.styleable.ClassicsHeader_srlTextFailed)) {
            textFailed = ta.getString(R.styleable.ClassicsHeader_srlTextFailed);
        } else if (DEFAULT_TEXT_FAILED != null) {
            textFailed = DEFAULT_TEXT_FAILED;
        } else {
            textFailed = context.getString(R.string.srl_header_failed);
        }
        if (ta.hasValue(R.styleable.ClassicsHeader_srlTextSecondary)) {
            textSecondary = ta.getString(R.styleable.ClassicsHeader_srlTextSecondary);
        } else if (DEFAULT_TEXT_SECONDARY != null) {
            textSecondary = DEFAULT_TEXT_SECONDARY;
        } else {
            textSecondary = context.getString(R.string.srl_header_secondary);
        }
        if (ta.hasValue(R.styleable.ClassicsHeader_srlTextRefreshing)) {
            textRefreshing = ta.getString(R.styleable.ClassicsHeader_srlTextRefreshing);
        } else if (DEFAULT_TEXT_REFRESHING != null) {
            textRefreshing = DEFAULT_TEXT_REFRESHING;
        } else {
            textRefreshing = context.getString(R.string.srl_header_refreshing);
        }
        if (ta.hasValue(R.styleable.ClassicsHeader_srlTextUpdate)) {
            textUpdate = ta.getString(R.styleable.ClassicsHeader_srlTextUpdate);
        } else if (DEFAULT_TEXT_UPDATE != null) {
            textUpdate = DEFAULT_TEXT_UPDATE;
        } else {
            textUpdate = context.getString(R.string.srl_header_update);
        }
        lastUpdateFormat = new SimpleDateFormat(textUpdate, Locale.getDefault());

        ta.recycle();

        progressView.animate().setInterpolator(null);
        updateView.setVisibility(enableLastTime ? VISIBLE : GONE);
        titleTextView.setText(thisView.isInEditMode() ? textRefreshing : textPulling);

        if (thisView.isInEditMode()) {
            arrowView.setVisibility(GONE);
        } else {
            progressView.setVisibility(GONE);
        }

        try {
            if (context instanceof FragmentActivity) {
                FragmentManager manager = ((FragmentActivity) context).getSupportFragmentManager();
                @SuppressLint("RestrictedApi")
                List<Fragment> fragments = manager.getFragments();
                if (fragments.size() > 0) {
                    updateLastRefreshTime(new Date());
                    return;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        KEY_LAST_UPDATE_TIME += context.getClass().getName();
        shared = context.getSharedPreferences("ClassicsHeader", Context.MODE_PRIVATE);
        updateLastRefreshTime(new Date(shared.getLong(KEY_LAST_UPDATE_TIME, System.currentTimeMillis())));
    }

    @Override
    public int onAnimationFinish(@NonNull SmartRefreshLayout layout, boolean success) {
        if (success) {
            titleTextView.setText(textFinish);
            if (lastTime != null) {
                updateLastRefreshTime(new Date());
            }
        } else {
            titleTextView.setText(textFailed);
        }
        return super.onAnimationFinish(layout, success);
    }

    @Override
    public void stateChanged(@NonNull SmartRefreshLayout refreshLayout, @NonNull RefreshLayoutState oldState, @NonNull RefreshLayoutState newState) {
        final View arrowView = this.arrowView;
        final View updateView = lastUpdateText;
        switch (newState) {
            case IDLE:
                updateView.setVisibility(enableLastTime ? VISIBLE : GONE);
            case PULL_DOWN_TO_REFRESH:
                titleTextView.setText(textPulling);
                arrowView.setVisibility(VISIBLE);
                arrowView.animate().rotation(0);
                break;
            case REFRESHING:
            case REFRESH_RELEASED:
                titleTextView.setText(textRefreshing);
                arrowView.setVisibility(GONE);
                break;
            case RELEASE_TO_REFRESH:
                titleTextView.setText(textRelease);
                arrowView.animate().rotation(180);
                break;
            case RELEASE_TO_TWO_LEVEL:
                titleTextView.setText(textSecondary);
                arrowView.animate().rotation(0);
                break;
            case Loading:
                arrowView.setVisibility(GONE);
                updateView.setVisibility(enableLastTime ? INVISIBLE : GONE);
                titleTextView.setText(textLoading);
                break;
        }
    }
    public ClassicRefreshHeaderView updateLastRefreshTime(Date time) {
        final View thisView = this;
        lastTime = time;
        lastUpdateText.setText(lastUpdateFormat.format(time));
        if (shared != null && !thisView.isInEditMode()) {
            shared.edit().putLong(KEY_LAST_UPDATE_TIME, time.getTime()).apply();
        }
        return this;
    }

    public ClassicRefreshHeaderView setLastUpdateLabel(CharSequence text) {
        lastTime = null;
        lastUpdateText.setText(text);
        return this;
    }

    public ClassicRefreshHeaderView setLastUpdateFormat(DateFormat format) {
        lastUpdateFormat = format;
        if (lastTime != null) {
            lastUpdateText.setText(lastUpdateFormat.format(lastTime));
        }
        return this;
    }

    public ClassicRefreshHeaderView enableLastUpdateDisplay(boolean enable) {
        final View updateView = lastUpdateText;
        enableLastTime = enable;
        updateView.setVisibility(enable ? VISIBLE : GONE);
        if (refreshKernel != null) {
            refreshKernel.requestRemeasureHeight(this);
        }
        return this;
    }

    public ClassicRefreshHeaderView applyAccentColor(@ColorInt int accentColor) {
        lastUpdateText.setTextColor(accentColor & 0x00ffffff | 0xcc000000);
        return super.applyAccentColor(accentColor);
    }

    public ClassicRefreshHeaderView setLastUpdateMarginTop(float dp) {
        final View updateView = lastUpdateText;
        MarginLayoutParams lp = (MarginLayoutParams) updateView.getLayoutParams();
        lp.topMargin = SmartViewUtil.dpToPx(dp);
        updateView.setLayoutParams(lp);
        return this;
    }

    public ClassicRefreshHeaderView setLastUpdateTextSize(float size) {
        lastUpdateText.setTextSize(size);
        if (refreshKernel != null) {
            refreshKernel.requestRemeasureHeight(this);
        }
        return this;
    }
}
