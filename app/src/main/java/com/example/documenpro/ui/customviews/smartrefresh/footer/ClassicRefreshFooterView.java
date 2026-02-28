package com.example.documenpro.ui.customviews.smartrefresh.footer;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.example.documenpro.R;
import com.example.documenpro.ui.customviews.smartrefresh.api.RefreshFooterComponent;
import com.example.documenpro.ui.customviews.smartrefresh.api.SmartRefreshLayout;
import com.example.documenpro.ui.customviews.smartrefresh.constant.RefreshLayoutState;
import com.example.documenpro.ui.customviews.smartrefresh.constant.RefreshSpinnerStyle;
import com.example.documenpro.ui.customviews.smartrefresh.internal.ArrowDrawable;
import com.example.documenpro.ui.customviews.smartrefresh.internal.InternalClassics;
import com.example.documenpro.ui.customviews.smartrefresh.internal.ProgressDrawable;
import com.example.documenpro.ui.customviews.smartrefresh.util.SmartUtil;


public class ClassicRefreshFooterView extends InternalClassics<ClassicRefreshFooterView> implements RefreshFooterComponent {

    public static String REFRESH_FOOTER_TEXT_PULLING = null;
    public static String REFRESH_FOOTER_TEXT_RELEASE = null;
    public static String REFRESH_FOOTER_TEXT_LOADING = null;
    public static String REFRESH_FOOTER_TEXT_REFRESHING = null;
    public static String REFRESH_FOOTER_TEXT_FINISH = null;
    public static String REFRESH_FOOTER_TEXT_FAILED = null;
    public static String REFRESH_FOOTER_TEXT_NOTHING = null;

    protected String textPulling;
    protected String textRelease;
    protected String textLoading;
    protected String textRefreshing;
    protected String textFinish;
    protected String textFailed;
    protected String textNoMoreData;
    protected boolean noMoreDataAvailable = false;

    public ClassicRefreshFooterView(Context context) {
        this(context, null);
    }

    public ClassicRefreshFooterView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);

        View.inflate(context, R.layout.srl_classics_footer, this);

        final View thisView = this;
        final View arrowView = mArrowView = thisView.findViewById(R.id.srl_classics_arrow);
        final View progressView = mProgressView = thisView.findViewById(R.id.srl_classics_progress);

        mTitleText = thisView.findViewById(R.id.srl_classics_title);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ClassicsFooter);

        LayoutParams lpArrow = (LayoutParams) arrowView.getLayoutParams();
        LayoutParams lpProgress = (LayoutParams) progressView.getLayoutParams();
        lpProgress.rightMargin = ta.getDimensionPixelSize(R.styleable.ClassicsFooter_srlDrawableMarginRight, SmartUtil.dp2px(20));
        lpArrow.rightMargin = lpProgress.rightMargin;

        lpArrow.width = ta.getLayoutDimension(R.styleable.ClassicsFooter_srlDrawableArrowSize, lpArrow.width);
        lpArrow.height = ta.getLayoutDimension(R.styleable.ClassicsFooter_srlDrawableArrowSize, lpArrow.height);
        lpProgress.width = ta.getLayoutDimension(R.styleable.ClassicsFooter_srlDrawableProgressSize, lpProgress.width);
        lpProgress.height = ta.getLayoutDimension(R.styleable.ClassicsFooter_srlDrawableProgressSize, lpProgress.height);

        lpArrow.width = ta.getLayoutDimension(R.styleable.ClassicsFooter_srlDrawableSize, lpArrow.width);
        lpArrow.height = ta.getLayoutDimension(R.styleable.ClassicsFooter_srlDrawableSize, lpArrow.height);
        lpProgress.width = ta.getLayoutDimension(R.styleable.ClassicsFooter_srlDrawableSize, lpProgress.width);
        lpProgress.height = ta.getLayoutDimension(R.styleable.ClassicsFooter_srlDrawableSize, lpProgress.height);

        mFinishDuration = ta.getInt(R.styleable.ClassicsFooter_srlFinishDuration, mFinishDuration);
        mSpinnerStyle = RefreshSpinnerStyle.STYLES[ta.getInt(R.styleable.ClassicsFooter_srlClassicsSpinnerStyle, mSpinnerStyle.ordinal)];

        if (ta.hasValue(R.styleable.ClassicsFooter_srlDrawableArrow)) {
            mArrowView.setImageDrawable(ta.getDrawable(R.styleable.ClassicsFooter_srlDrawableArrow));
        } else if (mArrowView.getDrawable() == null) {
            mArrowDrawable = new ArrowDrawable();
            mArrowDrawable.setColor(0xff666666);
            mArrowView.setImageDrawable(mArrowDrawable);
        }

        if (ta.hasValue(R.styleable.ClassicsFooter_srlDrawableProgress)) {
            mProgressView.setImageDrawable(ta.getDrawable(R.styleable.ClassicsFooter_srlDrawableProgress));
        } else if (mProgressView.getDrawable() == null) {
            mProgressDrawable = new ProgressDrawable();
            mProgressDrawable.setColor(0xff666666);
            mProgressView.setImageDrawable(mProgressDrawable);
        }

        if (ta.hasValue(R.styleable.ClassicsFooter_srlTextSizeTitle)) {
            mTitleText.setTextSize(TypedValue.COMPLEX_UNIT_PX, ta.getDimensionPixelSize(R.styleable.ClassicsFooter_srlTextSizeTitle, SmartUtil.dp2px(16)));
        }

        if (ta.hasValue(R.styleable.ClassicsFooter_srlPrimaryColor)) {
            super.setPrimaryColor(ta.getColor(R.styleable.ClassicsFooter_srlPrimaryColor, 0));
        }
        if (ta.hasValue(R.styleable.ClassicsFooter_srlAccentColor)) {
            super.setAccentColor(ta.getColor(R.styleable.ClassicsFooter_srlAccentColor, 0));
        }

        if(ta.hasValue(R.styleable.ClassicsFooter_srlTextPulling)){
            textPulling = ta.getString(R.styleable.ClassicsFooter_srlTextPulling);
        } else if(REFRESH_FOOTER_TEXT_PULLING != null) {
            textPulling = REFRESH_FOOTER_TEXT_PULLING;
        } else {
            textPulling = context.getString(R.string.srl_footer_pulling);
        }
        if(ta.hasValue(R.styleable.ClassicsFooter_srlTextRelease)){
            textRelease = ta.getString(R.styleable.ClassicsFooter_srlTextRelease);
        } else if(REFRESH_FOOTER_TEXT_RELEASE != null) {
            textRelease = REFRESH_FOOTER_TEXT_RELEASE;
        } else {
            textRelease = context.getString(R.string.srl_footer_release);
        }
        if(ta.hasValue(R.styleable.ClassicsFooter_srlTextLoading)){
            textLoading = ta.getString(R.styleable.ClassicsFooter_srlTextLoading);
        } else if(REFRESH_FOOTER_TEXT_LOADING != null) {
            textLoading = REFRESH_FOOTER_TEXT_LOADING;
        } else {
            textLoading = context.getString(R.string.srl_footer_loading);
        }
        if(ta.hasValue(R.styleable.ClassicsFooter_srlTextRefreshing)){
            textRefreshing = ta.getString(R.styleable.ClassicsFooter_srlTextRefreshing);
        } else if(REFRESH_FOOTER_TEXT_REFRESHING != null) {
            textRefreshing = REFRESH_FOOTER_TEXT_REFRESHING;
        } else {
            textRefreshing = context.getString(R.string.srl_footer_refreshing);
        }
        if(ta.hasValue(R.styleable.ClassicsFooter_srlTextFinish)){
            textFinish = ta.getString(R.styleable.ClassicsFooter_srlTextFinish);
        } else if(REFRESH_FOOTER_TEXT_FINISH != null) {
            textFinish = REFRESH_FOOTER_TEXT_FINISH;
        } else {
            textFinish = context.getString(R.string.srl_footer_finish);
        }
        if(ta.hasValue(R.styleable.ClassicsFooter_srlTextFailed)){
            textFailed = ta.getString(R.styleable.ClassicsFooter_srlTextFailed);
        } else if(REFRESH_FOOTER_TEXT_FAILED != null) {
            textFailed = REFRESH_FOOTER_TEXT_FAILED;
        } else {
            textFailed = context.getString(R.string.srl_footer_failed);
        }
        if(ta.hasValue(R.styleable.ClassicsFooter_srlTextNothing)){
            textNoMoreData = ta.getString(R.styleable.ClassicsFooter_srlTextNothing);
        } else if(REFRESH_FOOTER_TEXT_NOTHING != null) {
            textNoMoreData = REFRESH_FOOTER_TEXT_NOTHING;
        } else {
            textNoMoreData = context.getString(R.string.srl_footer_nothing);
        }

        ta.recycle();

        progressView.animate().setInterpolator(null);
        mTitleText.setText(thisView.isInEditMode() ? textLoading : textPulling);

        if (thisView.isInEditMode()) {
            arrowView.setVisibility(GONE);
        } else {
            progressView.setVisibility(GONE);
        }
    }

    @Override
    public int onAnimationFinish(@NonNull SmartRefreshLayout layout, boolean success) {

        super.onAnimationFinish(layout, success);
        if (!noMoreDataAvailable) {
            mTitleText.setText(success ? textFinish : textFailed);
            return mFinishDuration;
        }
        return 0;
    }

    @Override
    public boolean setNoMoreDataAvailable(boolean noMoreData) {
        if (noMoreDataAvailable != noMoreData) {
            noMoreDataAvailable = noMoreData;
            final View arrowView = mArrowView;
            if (noMoreData) {
                mTitleText.setText(textNoMoreData);
                arrowView.setVisibility(GONE);
            } else {
                mTitleText.setText(textPulling);
                arrowView.setVisibility(VISIBLE);
            }
        }
        return true;
    }

    @Override@Deprecated
    public void applyPrimaryColors(@ColorInt int ... colors) {
        if (mSpinnerStyle == RefreshSpinnerStyle.FIXED_BEHIND) {
            super.applyPrimaryColors(colors);
        }
    }

    @Override
    public void onStateChanged(@NonNull SmartRefreshLayout refreshLayout, @NonNull RefreshLayoutState oldState, @NonNull RefreshLayoutState newState) {
        final View arrowView = mArrowView;
        if (!noMoreDataAvailable) {
            switch (newState) {
                case IDLE:
                    arrowView.setVisibility(VISIBLE);
                case PullUpToLoad:
                    mTitleText.setText(textPulling);
                    arrowView.animate().rotation(180);
                    break;
                case Loading:
                case LoadReleased:
                    arrowView.setVisibility(GONE);
                    mTitleText.setText(textLoading);
                    break;
                case ReleaseToLoad:
                    mTitleText.setText(textRelease);
                    arrowView.animate().rotation(0);
                    break;
                case REFRESHING:
                    mTitleText.setText(textRefreshing);
                    arrowView.setVisibility(GONE);
                    break;
            }
        }
    }
}
