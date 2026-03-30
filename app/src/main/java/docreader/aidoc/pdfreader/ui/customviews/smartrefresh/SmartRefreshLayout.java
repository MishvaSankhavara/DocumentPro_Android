package docreader.aidoc.pdfreader.ui.customviews.smartrefresh;

import static android.view.MotionEvent.obtain;
import static android.view.View.MeasureSpec.AT_MOST;
import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.getSize;
import static android.view.View.MeasureSpec.makeMeasureSpec;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static docreader.aidoc.pdfreader.ui.customviews.smartrefresh.util.SmartViewUtil.dpToPx;
import static docreader.aidoc.pdfreader.ui.customviews.smartrefresh.util.SmartViewUtil.flingView;
import static docreader.aidoc.pdfreader.ui.customviews.smartrefresh.util.SmartViewUtil.isContent;
import static java.lang.System.currentTimeMillis;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;

import docreader.aidoc.pdfreader.R;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.RefreshFooterCreatorFactory;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.DefaultRefreshHeaderCreatorFactory;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.RefreshLayoutInitializer;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.RefreshContentHandler;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.RefreshFooterComponent;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.RefreshHeaderComponent;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.RefreshComponent;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.RefreshManager;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.RefreshScrollBoundaryDecider;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.constant.RefreshDimensionStatus;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.constant.RefreshLayoutState;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.constant.RefreshSpinnerStyle;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.footer.PulseBallRefreshFooter;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.header.RadarRefreshHeader;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.impl.RefreshContentContainer;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.impl.FooterComponentWrapper;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.impl.HeaderComponentWrapper;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.listener.LoadMoreListener;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.listener.MultiPurposeListener;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.listener.RefreshListener;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.listener.RefreshLoadListener;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.listener.StateChangedListener;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.util.SmartViewUtil;


@SuppressLint("RestrictedApi")
public class SmartRefreshLayout extends ViewGroup implements docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout, NestedScrollingParent/*, NestedScrollingChild*/ {

    protected int touchSlop;
    protected float touchX;
    protected float touchY;
    protected float lastTouchX;
    protected float lastTouchY;
    protected int touchStartSpinner;
    protected char dragDirection = 'n';
    protected boolean isBeingDragged;
    protected boolean superDispatchTouchEvent;
    protected boolean enableDisallowIntercept;
    protected float dragRate = .5f;
    protected int spinnerOffset;
    protected int previousSpinnerOffset;
    protected int floorDuration = 300;
    protected int reboundDuration = 300;
    protected float floorOpenLayoutRate = 1f;
    protected float floorBottomDragLayoutRate = 1f/6;
    protected int screenHeightPixels;
    protected int headerHeight;
    protected int footerHeight;
    protected int headerInsetStart;
    protected int footerInsetStart;
    protected float headerMaxDragRatio = 2.5f;
    protected float footerMaxDragRatio = 2.5f;
    protected float headerTriggerRatio = 1.0f;
    protected float footerTriggerRatio = 1.0f;
    protected int fixedHeaderViewId = View.NO_ID;
    protected int fixedFooterViewId = View.NO_ID;
    protected int headerTranslationViewId = View.NO_ID;
    protected int footerTranslationViewId = View.NO_ID;
    protected int minFlingVelocity;
    protected int maxFlingVelocity;
    protected int currentFlingVelocity;
    protected Scroller scrollAnimator;
    protected VelocityTracker velocityTracker;
    protected Interpolator reboundInterpolator;
    protected int[] primaryColors;
    protected boolean refreshEnabled = true;
    protected boolean loadMoreEnabled = false;
    protected boolean enableClipHeaderWhenFixedBehind = true;
    protected boolean enableClipFooterWhenFixedBehind = true;
    protected boolean enableHeaderTranslationContent = true;
    protected boolean enableFooterTranslationContent = true;
    protected boolean enableFooterFollowWhenNoMoreData = false;
    protected boolean enablePreviewInEditMode = true;
    protected boolean overScrollBounceEnabled = true;
    protected boolean overScrollDragEnabled = false;
    protected boolean autoLoadMoreEnabled = true;
    protected boolean pureScrollModeEnabled = false;
    protected boolean enableScrollContentWhenLoaded = true;
    protected boolean enableScrollContentWhenRefreshed = true;
    protected boolean enableLoadMoreWhenContentNotFull = true;
    protected boolean nestedScrollingEnabled = true;
    protected boolean disableContentWhenRefresh = false;
    protected boolean disableContentWhenLoading = false;
    protected boolean footerNoMoreData = false;
    protected boolean footerNoMoreDataEffective = false;
    protected boolean manualLoadMore = false;
    protected boolean manualHeaderTranslationContent = false;
    protected boolean manualFooterTranslationContent = false;
    protected RefreshListener refreshCallback;
    protected LoadMoreListener loadMoreCallback;
    protected MultiPurposeListener multiPurposeCallback;
    protected RefreshScrollBoundaryDecider scrollBoundaryChecker;
    protected int nestedUnconsumed;
    protected boolean nestedScrollingActive;
    protected int[] parentOffset = new int[2];
    protected NestedScrollingChildHelper nestedChildHelper = new NestedScrollingChildHelper(this);
    protected NestedScrollingParentHelper nestedParentHelper = new NestedScrollingParentHelper(this);
    protected RefreshDimensionStatus headerHeightStatus = RefreshDimensionStatus.DEFAULT_UNNOTIFIED;
    protected RefreshDimensionStatus footerHeightStatus = RefreshDimensionStatus.DEFAULT_UNNOTIFIED;
    protected RefreshComponent headerComponent;
    protected RefreshComponent footerComponent;
    protected RefreshContentHandler contentHandler;
    protected Paint mPaint;
    protected Handler mHandler;
    protected RefreshManager refreshKernel = new RefreshKernelManager();
    protected RefreshLayoutState layoutState = RefreshLayoutState.IDLE;
    protected RefreshLayoutState secondaryState = RefreshLayoutState.IDLE;
    protected long lastActionTime = 0;
    protected int headerBackgroundColor = 0;
    protected int footerBackgroundColor = 0;
    protected boolean headerNeedTouchEventWhenRefreshing;
    protected boolean footerNeedTouchEventWhenLoading;
    protected boolean attachedToWindow;
    protected boolean footerScrollLocked = false;
    protected static RefreshFooterCreatorFactory sFooterCreator = null;
    protected static DefaultRefreshHeaderCreatorFactory sHeaderCreator = null;
    protected static RefreshLayoutInitializer sRefreshInitializer = null;
    protected static MarginLayoutParams sDefaultMarginLP = new MarginLayoutParams(-1,-1);

    public SmartRefreshLayout(Context context) {
        this(context, null);
    }

    public SmartRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        ViewConfiguration configuration = ViewConfiguration.get(context);

        mHandler = new Handler();
        scrollAnimator = new Scroller(context);
        velocityTracker = VelocityTracker.obtain();
        screenHeightPixels = context.getResources().getDisplayMetrics().heightPixels;
        reboundInterpolator = new SmartViewUtil(SmartViewUtil.INTERPOLATOR_FLUID);
        touchSlop = configuration.getScaledTouchSlop();
        minFlingVelocity = configuration.getScaledMinimumFlingVelocity();
        maxFlingVelocity = configuration.getScaledMaximumFlingVelocity();

        footerHeight = dpToPx(60);
        headerHeight = dpToPx(100);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SmartRefreshLayout);

        if (!ta.hasValue(R.styleable.SmartRefreshLayout_android_clipToPadding)) {
            super.setClipToPadding(false);
        }
        if (!ta.hasValue(R.styleable.SmartRefreshLayout_android_clipChildren)) {
            super.setClipChildren(false);
        }

        if (sRefreshInitializer != null) {
            sRefreshInitializer.initializeRefreshLayout(context, this);//调用全局初始化
        }

        dragRate = ta.getFloat(R.styleable.SmartRefreshLayout_srlDragRate, dragRate);
        headerMaxDragRatio = ta.getFloat(R.styleable.SmartRefreshLayout_srlHeaderMaxDragRate, headerMaxDragRatio);
        footerMaxDragRatio = ta.getFloat(R.styleable.SmartRefreshLayout_srlFooterMaxDragRate, footerMaxDragRatio);
        headerTriggerRatio = ta.getFloat(R.styleable.SmartRefreshLayout_srlHeaderTriggerRate, headerTriggerRatio);
        footerTriggerRatio = ta.getFloat(R.styleable.SmartRefreshLayout_srlFooterTriggerRate, footerTriggerRatio);
        refreshEnabled = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableRefresh, refreshEnabled);
        reboundDuration = ta.getInt(R.styleable.SmartRefreshLayout_srlReboundDuration, reboundDuration);
        loadMoreEnabled = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableLoadMore, loadMoreEnabled);
        headerHeight = ta.getDimensionPixelOffset(R.styleable.SmartRefreshLayout_srlHeaderHeight, headerHeight);
        footerHeight = ta.getDimensionPixelOffset(R.styleable.SmartRefreshLayout_srlFooterHeight, footerHeight);
        headerInsetStart = ta.getDimensionPixelOffset(R.styleable.SmartRefreshLayout_srlHeaderInsetStart, headerInsetStart);
        footerInsetStart = ta.getDimensionPixelOffset(R.styleable.SmartRefreshLayout_srlFooterInsetStart, footerInsetStart);
        disableContentWhenRefresh = ta.getBoolean(R.styleable.SmartRefreshLayout_srlDisableContentWhenRefresh, disableContentWhenRefresh);
        disableContentWhenLoading = ta.getBoolean(R.styleable.SmartRefreshLayout_srlDisableContentWhenLoading, disableContentWhenLoading);
        enableHeaderTranslationContent = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableHeaderTranslationContent, enableHeaderTranslationContent);
        enableFooterTranslationContent = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableFooterTranslationContent, enableFooterTranslationContent);
        enablePreviewInEditMode = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnablePreviewInEditMode, enablePreviewInEditMode);
        autoLoadMoreEnabled = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableAutoLoadMore, autoLoadMoreEnabled);
        overScrollBounceEnabled = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableOverScrollBounce, overScrollBounceEnabled);
        pureScrollModeEnabled = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnablePureScrollMode, pureScrollModeEnabled);
        enableScrollContentWhenLoaded = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableScrollContentWhenLoaded, enableScrollContentWhenLoaded);
        enableScrollContentWhenRefreshed = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableScrollContentWhenRefreshed, enableScrollContentWhenRefreshed);
        enableLoadMoreWhenContentNotFull = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableLoadMoreWhenContentNotFull, enableLoadMoreWhenContentNotFull);
        enableFooterFollowWhenNoMoreData = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableFooterFollowWhenLoadFinished, enableFooterFollowWhenNoMoreData);
        enableFooterFollowWhenNoMoreData = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableFooterFollowWhenNoMoreData, enableFooterFollowWhenNoMoreData);
        enableClipHeaderWhenFixedBehind = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableClipHeaderWhenFixedBehind, enableClipHeaderWhenFixedBehind);
        enableClipFooterWhenFixedBehind = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableClipFooterWhenFixedBehind, enableClipFooterWhenFixedBehind);
        overScrollDragEnabled = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableOverScrollDrag, overScrollDragEnabled);
        fixedHeaderViewId = ta.getResourceId(R.styleable.SmartRefreshLayout_srlFixedHeaderViewId, fixedHeaderViewId);
        fixedFooterViewId = ta.getResourceId(R.styleable.SmartRefreshLayout_srlFixedFooterViewId, fixedFooterViewId);
        headerTranslationViewId = ta.getResourceId(R.styleable.SmartRefreshLayout_srlHeaderTranslationViewId, headerTranslationViewId);
        footerTranslationViewId = ta.getResourceId(R.styleable.SmartRefreshLayout_srlFooterTranslationViewId, footerTranslationViewId);
        nestedScrollingEnabled = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableNestedScrolling, nestedScrollingEnabled);
        nestedChildHelper.setNestedScrollingEnabled(nestedScrollingEnabled);

        manualLoadMore = manualLoadMore || ta.hasValue(R.styleable.SmartRefreshLayout_srlEnableLoadMore);
        manualHeaderTranslationContent = manualHeaderTranslationContent || ta.hasValue(R.styleable.SmartRefreshLayout_srlEnableHeaderTranslationContent);
        manualFooterTranslationContent = manualFooterTranslationContent || ta.hasValue(R.styleable.SmartRefreshLayout_srlEnableFooterTranslationContent);
        headerHeightStatus = ta.hasValue(R.styleable.SmartRefreshLayout_srlHeaderHeight) ? RefreshDimensionStatus.XML_LAYOUT_UNNOTIFIED : headerHeightStatus;
        footerHeightStatus = ta.hasValue(R.styleable.SmartRefreshLayout_srlFooterHeight) ? RefreshDimensionStatus.XML_LAYOUT_UNNOTIFIED : footerHeightStatus;

        int accentColor = ta.getColor(R.styleable.SmartRefreshLayout_srlAccentColor, 0);
        int primaryColor = ta.getColor(R.styleable.SmartRefreshLayout_srlPrimaryColor, 0);
        if (primaryColor != 0) {
            if (accentColor != 0) {
                primaryColors = new int[]{primaryColor, accentColor};
            } else {
                primaryColors = new int[]{primaryColor};
            }
        } else if (accentColor != 0) {
            primaryColors = new int[]{0, accentColor};
        }
//        }
        if (pureScrollModeEnabled && !manualLoadMore && !loadMoreEnabled) {
            loadMoreEnabled = true;
        }

        ta.recycle();
    }
    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        final int count = super.getChildCount();
        if (count > 3) {
            throw new RuntimeException("最多只支持3个子View，Most only support three sub view");
        }

        int contentLevel = 0;
        int indexContent = -1;
        for (int i = 0; i < count; i++) {
            View view = super.getChildAt(i);
            if (isContent(view) && (contentLevel < 2 || i == 1)) {
                indexContent = i;
                contentLevel = 2;
            } else if (!(view instanceof RefreshComponent) && contentLevel < 1) {
                indexContent = i;
                contentLevel = i > 0 ? 1 : 0;
            }
        }

        int indexHeader = -1;
        int indexFooter = -1;
        if (indexContent >= 0) {
            contentHandler = new RefreshContentContainer(super.getChildAt(indexContent));
            if (indexContent == 1) {
                indexHeader = 0;
                if (count == 3) {
                    indexFooter = 2;
                }
            } else if (count == 2) {
                indexFooter = 1;
            }
        }

        for (int i = 0; i < count; i++) {
            View view = super.getChildAt(i);
            if (i == indexHeader || (i != indexFooter && indexHeader == -1 && headerComponent == null && view instanceof RefreshHeaderComponent)) {
                headerComponent = (view instanceof RefreshHeaderComponent) ? (RefreshHeaderComponent) view : new HeaderComponentWrapper(view);
            } else if (i == indexFooter || (indexFooter == -1 && view instanceof RefreshFooterComponent)) {
                loadMoreEnabled = (loadMoreEnabled || !manualLoadMore);
                footerComponent = (view instanceof RefreshFooterComponent) ? (RefreshFooterComponent) view : new FooterComponentWrapper(view);
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        attachedToWindow = true;

        final View thisView = this;
        if (!thisView.isInEditMode()) {

            if (headerComponent == null) {
                if (sHeaderCreator != null) {
                    RefreshHeaderComponent header = sHeaderCreator.createRefreshFooterHeader(thisView.getContext(), this);
                    if (header == null) {
                        throw new RuntimeException("DefaultRefreshHeaderCreator can not return null");
                    }
                    setHeaderRefresh(header);
                } else {
                    setHeaderRefresh(new RadarRefreshHeader(thisView.getContext()));
                }
            }
            if (footerComponent == null) {
                if (sFooterCreator != null) {
                    RefreshFooterComponent footer = sFooterCreator.createDefaultRefreshFooter(thisView.getContext(), this);
                    if (footer == null) {
                        throw new RuntimeException("DefaultRefreshFooterCreator can not return null");
                    }
                    setFooterRefresh(footer);
                } else {
                    boolean old = loadMoreEnabled;
                    setFooterRefresh(new PulseBallRefreshFooter(thisView.getContext()));
                    loadMoreEnabled = old;
                }
            } else {
                loadMoreEnabled = loadMoreEnabled || !manualLoadMore;
            }

            if (contentHandler == null) {
                for (int i = 0, len = getChildCount(); i < len; i++) {
                    View view = getChildAt(i);
                    if ((headerComponent == null || view != headerComponent.getComponentView())&&
                            (footerComponent == null || view != footerComponent.getComponentView())) {
                        contentHandler = new RefreshContentContainer(view);
                    }
                }
            }
            if (contentHandler == null) {
                final int padding = dpToPx(20);
                final TextView errorView = new TextView(thisView.getContext());
                errorView.setTextColor(0xffff6600);
                errorView.setGravity(Gravity.CENTER);
                errorView.setTextSize(20);
                errorView.setText(R.string.content_empty);
                super.addView(errorView, 0, new LayoutParams(MATCH_PARENT, MATCH_PARENT));
                contentHandler = new RefreshContentContainer(errorView);
                contentHandler.getContentView().setPadding(padding, padding, padding, padding);
            }

            View fixedHeaderView = thisView.findViewById(fixedHeaderViewId);
            View fixedFooterView = thisView.findViewById(fixedFooterViewId);

            contentHandler.setScrollBoundaryChecker(scrollBoundaryChecker);
            contentHandler.setLoadMoreWhenContentNotFullEnabled(enableLoadMoreWhenContentNotFull);
            contentHandler.setupComponent(refreshKernel, fixedHeaderView, fixedFooterView);

            if (spinnerOffset != 0) {
                dispatchStateChange(RefreshLayoutState.IDLE);
                contentHandler.updateSpinnerPosition(spinnerOffset = 0, headerTranslationViewId, footerTranslationViewId);
            }
        }

        if (primaryColors != null) {
            if (headerComponent != null) {
                headerComponent.applyPrimaryColors(primaryColors);
            }
            if (footerComponent != null) {
                footerComponent.applyPrimaryColors(primaryColors);
            }
        }

        //重新排序
        if (contentHandler != null) {
            super.bringChildToFront(contentHandler.getContentView());
        }
        if (headerComponent != null && headerComponent.getSpinnerBehavior().front) {
            super.bringChildToFront(headerComponent.getComponentView());
        }
        if (footerComponent != null && footerComponent.getSpinnerBehavior().front) {
            super.bringChildToFront(footerComponent.getComponentView());
        }

    }

    @Override
    protected void onMeasure(final int widthMeasureSpec,final int heightMeasureSpec) {
        int minimumHeight = 0;
        final View thisView = this;
        final boolean needPreview = thisView.isInEditMode() && enablePreviewInEditMode;

        for (int i = 0, len = super.getChildCount(); i < len; i++) {
            View child = super.getChildAt(i);

            if (child.getVisibility() == GONE || child.getTag(R.string.component_falsify) == child) {
                continue;
            }

            if (headerComponent != null && headerComponent.getComponentView() == child) {
                final View headerView = headerComponent.getComponentView();
                final ViewGroup.LayoutParams lp = headerView.getLayoutParams();
                final MarginLayoutParams mlp = lp instanceof MarginLayoutParams ? (MarginLayoutParams)lp : sDefaultMarginLP;
                final int widthSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec, mlp.leftMargin + mlp.rightMargin, lp.width);
                int height = headerHeight;

                if (headerHeightStatus.ordinal < RefreshDimensionStatus.XML_LAYOUT_UNNOTIFIED.ordinal) {
                    if (lp.height > 0) {
                        height =  lp.height + mlp.bottomMargin + mlp.topMargin;
                        if (headerHeightStatus.canBeReplacedBy(RefreshDimensionStatus.XML_EXACT_UNNOTIFIED)) {
                            headerHeight = lp.height + mlp.bottomMargin + mlp.topMargin;
                            headerHeightStatus = RefreshDimensionStatus.XML_EXACT_UNNOTIFIED;
                        }
                    } else if (lp.height == WRAP_CONTENT && (headerComponent.getSpinnerBehavior() != RefreshSpinnerStyle.MATCH_LAYOUT || !headerHeightStatus.isNotified)) {
                        final int maxHeight = Math.max(getSize(heightMeasureSpec) - mlp.bottomMargin - mlp.topMargin, 0);
                        headerView.measure(widthSpec, makeMeasureSpec(maxHeight, AT_MOST));
                        final int measuredHeight = headerView.getMeasuredHeight();
                        if (measuredHeight > 0) {
                            height = -1;
                            if (measuredHeight != (maxHeight) && headerHeightStatus.canBeReplacedBy(RefreshDimensionStatus.XML_WRAP_UNNOTIFIED)) {
                                headerHeight = measuredHeight + mlp.bottomMargin + mlp.topMargin;
                                headerHeightStatus = RefreshDimensionStatus.XML_WRAP_UNNOTIFIED;
                            }
                        }
                    }
                }

                if (headerComponent.getSpinnerBehavior() == RefreshSpinnerStyle.MATCH_LAYOUT) {
                    height = getSize(heightMeasureSpec);
                } else if (headerComponent.getSpinnerBehavior().scale && !needPreview) {
                    height = Math.max(0, isEnableRefreshOrLoadMore(refreshEnabled) ? spinnerOffset : 0);
                }

                if (height != -1) {
                    headerView.measure(widthSpec, makeMeasureSpec(Math.max(height - mlp.bottomMargin - mlp.topMargin, 0), EXACTLY));
                }

                if (!headerHeightStatus.isNotified) {
                    headerHeightStatus = headerHeightStatus.notified();
                    headerComponent.onComponentInitialized(refreshKernel, headerHeight, (int) (headerMaxDragRatio * headerHeight));
                }

                if (needPreview && isEnableRefreshOrLoadMore(refreshEnabled)) {
                    minimumHeight += headerView.getMeasuredHeight();
                }
            }

            if (footerComponent != null && footerComponent.getComponentView() == child) {
                final View footerView = footerComponent.getComponentView();
                final ViewGroup.LayoutParams lp = footerView.getLayoutParams();
                final MarginLayoutParams mlp = lp instanceof MarginLayoutParams ? (MarginLayoutParams)lp : sDefaultMarginLP;
                final int widthSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec, mlp.leftMargin + mlp.rightMargin, lp.width);
                int height = footerHeight;

                if (footerHeightStatus.ordinal < RefreshDimensionStatus.XML_LAYOUT_UNNOTIFIED.ordinal) {
                    if (lp.height > 0) {
                        height = lp.height + mlp.topMargin + mlp.bottomMargin;
                        if (footerHeightStatus.canBeReplacedBy(RefreshDimensionStatus.XML_EXACT_UNNOTIFIED)) {
                            footerHeight = lp.height + mlp.topMargin + mlp.bottomMargin;
                            footerHeightStatus = RefreshDimensionStatus.XML_EXACT_UNNOTIFIED;
                        }
                    } else if (lp.height == WRAP_CONTENT && (footerComponent.getSpinnerBehavior() != RefreshSpinnerStyle.MATCH_LAYOUT || !footerHeightStatus.isNotified)) {
                        int maxHeight = Math.max(getSize(heightMeasureSpec) - mlp.bottomMargin - mlp.topMargin, 0);
                        footerView.measure(widthSpec, makeMeasureSpec(maxHeight, AT_MOST));
                        int measuredHeight = footerView.getMeasuredHeight();
                        if (measuredHeight > 0) {
                            height = -1;
                            if (measuredHeight != (maxHeight) && footerHeightStatus.canBeReplacedBy(RefreshDimensionStatus.XML_WRAP_UNNOTIFIED)) {
                                footerHeight = measuredHeight + mlp.topMargin + mlp.bottomMargin;
                                footerHeightStatus = RefreshDimensionStatus.XML_WRAP_UNNOTIFIED;
                            }
                        }
                    }
                }

                if (footerComponent.getSpinnerBehavior() == RefreshSpinnerStyle.MATCH_LAYOUT) {
                    height = getSize(heightMeasureSpec);
                } else if (footerComponent.getSpinnerBehavior().scale && !needPreview) {
                    height = Math.max(0, isEnableRefreshOrLoadMore(loadMoreEnabled) ? -spinnerOffset : 0);
                }

                if (height != -1) {
                    footerView.measure(widthSpec, makeMeasureSpec(Math.max(height - mlp.bottomMargin - mlp.topMargin, 0), EXACTLY));
                }

                if (!footerHeightStatus.isNotified) {
                    footerHeightStatus = footerHeightStatus.notified();
                    footerComponent.onComponentInitialized(refreshKernel, footerHeight, (int) (footerMaxDragRatio * footerHeight));
                }

                if (needPreview && isEnableRefreshOrLoadMore(loadMoreEnabled)) {
                    minimumHeight += footerView.getMeasuredHeight();
                }
            }

            if (contentHandler != null && contentHandler.getContentView() == child) {
                final View contentView = contentHandler.getContentView();
                final ViewGroup.LayoutParams lp = contentView.getLayoutParams();
                final MarginLayoutParams mlp = lp instanceof MarginLayoutParams ? (MarginLayoutParams)lp : sDefaultMarginLP;
                final boolean showHeader = (headerComponent != null && isEnableRefreshOrLoadMore(refreshEnabled) && isEnableTranslationContent(enableHeaderTranslationContent, headerComponent));
                final boolean showFooter = (footerComponent != null && isEnableRefreshOrLoadMore(loadMoreEnabled) && isEnableTranslationContent(enableFooterTranslationContent, footerComponent));
                final int widthSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec,
                        thisView.getPaddingLeft() + thisView.getPaddingRight() +  mlp.leftMargin + mlp.rightMargin, lp.width);
                final int heightSpec = ViewGroup.getChildMeasureSpec(heightMeasureSpec,
                        thisView.getPaddingTop() + thisView.getPaddingBottom() + mlp.topMargin + mlp.bottomMargin +
                                ((needPreview && showHeader) ? headerHeight : 0) +
                                ((needPreview && showFooter) ? footerHeight : 0), lp.height);
                contentView.measure(widthSpec, heightSpec);
                minimumHeight += contentView.getMeasuredHeight();
            }
        }

        super.setMeasuredDimension(
                View.resolveSize(super.getSuggestedMinimumWidth(), widthMeasureSpec),
                View.resolveSize(minimumHeight, heightMeasureSpec));

        lastTouchX = thisView.getMeasuredWidth() / 2f;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final View thisView = this;
        final int paddingLeft = thisView.getPaddingLeft();
        final int paddingTop = thisView.getPaddingTop();
        final int paddingBottom = thisView.getPaddingBottom();

        for (int i = 0, len = super.getChildCount(); i < len; i++) {
            View child = super.getChildAt(i);

            if (child.getVisibility() == GONE || child.getTag(R.string.component_falsify) == child) {
                continue;
            }

            if (contentHandler != null && contentHandler.getContentView() == child) {
                boolean isPreviewMode = thisView.isInEditMode() && enablePreviewInEditMode && isEnableRefreshOrLoadMore(refreshEnabled) && headerComponent != null;
                final View contentView = contentHandler.getContentView();
                final ViewGroup.LayoutParams lp = contentView.getLayoutParams();
                final MarginLayoutParams mlp = lp instanceof MarginLayoutParams ? (MarginLayoutParams)lp : sDefaultMarginLP;
                int left = paddingLeft + mlp.leftMargin;
                int top = paddingTop + mlp.topMargin;
                int right = left + contentView.getMeasuredWidth();
                int bottom = top + contentView.getMeasuredHeight();
                if (isPreviewMode && (isEnableTranslationContent(enableHeaderTranslationContent, headerComponent))) {
                    top = top + headerHeight;
                    bottom = bottom + headerHeight;
                }

                contentView.layout(left, top, right, bottom);
            }
            if (headerComponent != null && headerComponent.getComponentView() == child) {
                boolean isPreviewMode = thisView.isInEditMode() && enablePreviewInEditMode && isEnableRefreshOrLoadMore(refreshEnabled);
                final View headerView = headerComponent.getComponentView();
                final ViewGroup.LayoutParams lp = headerView.getLayoutParams();
                final MarginLayoutParams mlp = lp instanceof MarginLayoutParams ? (MarginLayoutParams)lp : sDefaultMarginLP;
                int left = mlp.leftMargin;
                int top = mlp.topMargin + headerInsetStart;
                int right = left + headerView.getMeasuredWidth();
                int bottom = top + headerView.getMeasuredHeight();
                if (!isPreviewMode) {
                    if (headerComponent.getSpinnerBehavior() == RefreshSpinnerStyle.TRANSLATE) {
                        top = top - headerHeight;
                        bottom = bottom - headerHeight;
                    }
                }
                headerView.layout(left, top, right, bottom);
            }
            if (footerComponent != null && footerComponent.getComponentView() == child) {
                final boolean isPreviewMode = thisView.isInEditMode() && enablePreviewInEditMode && isEnableRefreshOrLoadMore(loadMoreEnabled);
                final View footerView = footerComponent.getComponentView();
                final ViewGroup.LayoutParams lp = footerView.getLayoutParams();
                final MarginLayoutParams mlp = lp instanceof MarginLayoutParams ? (MarginLayoutParams)lp : sDefaultMarginLP;
                final RefreshSpinnerStyle style = footerComponent.getSpinnerBehavior();
                int left = mlp.leftMargin;
                int top = mlp.topMargin + thisView.getMeasuredHeight() - footerInsetStart;
                if (footerNoMoreData && footerNoMoreDataEffective && enableFooterFollowWhenNoMoreData && contentHandler != null
                        && footerComponent.getSpinnerBehavior() == RefreshSpinnerStyle.TRANSLATE
                        && isEnableRefreshOrLoadMore(loadMoreEnabled)) {
                    final View contentView = contentHandler.getContentView();
                    final ViewGroup.LayoutParams clp = contentView.getLayoutParams();
                    final int topMargin = clp instanceof MarginLayoutParams ? ((MarginLayoutParams)clp).topMargin : 0;
                    top = paddingTop + paddingTop + topMargin + contentView.getMeasuredHeight();
                }

                if (style == RefreshSpinnerStyle.MATCH_LAYOUT) {
                    top = mlp.topMargin - footerInsetStart;
                } else if (isPreviewMode
                        || style == RefreshSpinnerStyle.FIXED_FRONT
                        || style == RefreshSpinnerStyle.FIXED_BEHIND) {
                    top = top - footerHeight;
                } else if (style.scale && spinnerOffset < 0) {
                    top = top - Math.max(isEnableRefreshOrLoadMore(loadMoreEnabled) ? -spinnerOffset : 0, 0);
                }

                int right = left + footerView.getMeasuredWidth();
                int bottom = top + footerView.getMeasuredHeight();
                footerView.layout(left, top, right, bottom);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        attachedToWindow = false;
        manualLoadMore = true;
        activeAnimationTask = null;
        if (spinnerAnimator != null) {
            Animator animator = spinnerAnimator;
            animator.removeAllListeners();
            spinnerAnimator.removeAllUpdateListeners();
            spinnerAnimator.setDuration(0);
            spinnerAnimator.cancel();
            spinnerAnimator = null;
        }

        if (headerComponent != null && layoutState == RefreshLayoutState.REFRESHING) {
            headerComponent.onAnimationFinish(this, false);
        }
        if (footerComponent != null && layoutState == RefreshLayoutState.Loading) {
            footerComponent.onAnimationFinish(this, false);
        }
        if (spinnerOffset != 0) {
            refreshKernel.updateSpinnerPosition(0, true);
        }
        if (layoutState != RefreshLayoutState.IDLE) {
            dispatchStateChange(RefreshLayoutState.IDLE);
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        footerScrollLocked = false;
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        final View thisView = this;
        final View contentView = contentHandler != null ? contentHandler.getContentView() : null;
        if (headerComponent != null && headerComponent.getComponentView() == child) {
            if (!isEnableRefreshOrLoadMore(refreshEnabled) || (!enablePreviewInEditMode && thisView.isInEditMode())) {
                return true;
            }
            if (contentView != null) {
                int bottom = Math.max(contentView.getTop() + contentView.getPaddingTop() + spinnerOffset, child.getTop());
                if (headerBackgroundColor != 0 && mPaint != null) {
                    mPaint.setColor(headerBackgroundColor);
                    if (headerComponent.getSpinnerBehavior().scale) {
                        bottom = child.getBottom();
                    } else if (headerComponent.getSpinnerBehavior() == RefreshSpinnerStyle.TRANSLATE) {
                        bottom = child.getBottom() + spinnerOffset;
                    }
                    canvas.drawRect(0, child.getTop(), thisView.getWidth(), bottom, mPaint);
                }
                if ((enableClipHeaderWhenFixedBehind && headerComponent.getSpinnerBehavior() == RefreshSpinnerStyle.FIXED_BEHIND) || headerComponent.getSpinnerBehavior().scale) {
                    canvas.save();
                    canvas.clipRect(child.getLeft(), child.getTop(), child.getRight(), bottom);
                    boolean ret = super.drawChild(canvas, child, drawingTime);
                    canvas.restore();
                    return ret;
                }
            }
        }
        if (footerComponent != null && footerComponent.getComponentView() == child) {
            if (!isEnableRefreshOrLoadMore(loadMoreEnabled) || (!enablePreviewInEditMode && thisView.isInEditMode())) {
                return true;
            }
            if (contentView != null) {
                int top = Math.min(contentView.getBottom() - contentView.getPaddingBottom() + spinnerOffset, child.getBottom());
                if (footerBackgroundColor != 0 && mPaint != null) {
                    mPaint.setColor(footerBackgroundColor);
                    if (footerComponent.getSpinnerBehavior().scale) {
                        top = child.getTop();
                    } else if (footerComponent.getSpinnerBehavior() == RefreshSpinnerStyle.TRANSLATE) {
                        top = child.getTop() + spinnerOffset;
                    }
                    canvas.drawRect(0, top, thisView.getWidth(), child.getBottom(), mPaint);
                }

                if ((enableClipFooterWhenFixedBehind && footerComponent.getSpinnerBehavior() == RefreshSpinnerStyle.FIXED_BEHIND) || footerComponent.getSpinnerBehavior().scale) {
                    canvas.save();
                    canvas.clipRect(child.getLeft(), top, child.getRight(), child.getBottom());
                    boolean ret = super.drawChild(canvas, child, drawingTime);
                    canvas.restore();
                    return ret;
                }
            }

        }
        return super.drawChild(canvas, child, drawingTime);
    }

    protected boolean mVerticalPermit = false;

    @Override
    public void computeScroll() {
        int lastCurY = scrollAnimator.getCurrY();
        if (scrollAnimator.computeScrollOffset()) {
            int finalY = scrollAnimator.getFinalY();
            if ((finalY < 0 && (refreshEnabled || overScrollDragEnabled) && contentHandler.isRefreshPossible())
                    || (finalY > 0 && (loadMoreEnabled || overScrollDragEnabled) && contentHandler.isLoadMorePossible())) {
                if(mVerticalPermit) {
                    float velocity;
                    if (Build.VERSION.SDK_INT >= 14) {
                        velocity = finalY > 0 ? -scrollAnimator.getCurrVelocity() : scrollAnimator.getCurrVelocity();
                    } else {
                        velocity = 1f * (scrollAnimator.getCurrY() - finalY) / Math.max((scrollAnimator.getDuration() - scrollAnimator.timePassed()), 1);
                    }
                    startBounceAnimation(velocity);
                }
                scrollAnimator.forceFinished(true);
            } else {
                mVerticalPermit = true;
                final View thisView = this;
                thisView.invalidate();
            }
        }
    }

    protected MotionEvent mFalsifyEvent = null;

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {

        final int action = e.getActionMasked();
        final boolean pointerUp = action == MotionEvent.ACTION_POINTER_UP;
        final int skipIndex = pointerUp ? e.getActionIndex() : -1;

        float sumX = 0, sumY = 0;
        final int count = e.getPointerCount();
        for (int i = 0; i < count; i++) {
            if (skipIndex == i) continue;
            sumX += e.getX(i);
            sumY += e.getY(i);
        }
        final int div = pointerUp ? count - 1 : count;
        final float touchX = sumX / div;
        final float touchY = sumY / div;
        if ((action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_POINTER_DOWN)
                && isBeingDragged) {
            this.touchY += touchY - lastTouchY;
        }
        lastTouchX = touchX;
        lastTouchY = touchY;

        final View thisView = this;
        if (nestedScrollingActive) {
            int totalUnconsumed = nestedUnconsumed;
            boolean ret = super.dispatchTouchEvent(e);
            if (action == MotionEvent.ACTION_MOVE) {
                if (totalUnconsumed == nestedUnconsumed) {
                    final int offsetX = (int) lastTouchX;
                    final int offsetMax = thisView.getWidth();
                    final float percentX = lastTouchX / (offsetMax == 0 ? 1 : offsetMax);
                    if (isEnableRefreshOrLoadMore(refreshEnabled) && spinnerOffset > 0 && headerComponent != null && headerComponent.isHorizontalDragSupported()) {
                        headerComponent.onHorizontalDragging(percentX, offsetX, offsetMax);
                    } else if (isEnableRefreshOrLoadMore(loadMoreEnabled) && spinnerOffset < 0 && footerComponent != null && footerComponent.isHorizontalDragSupported()) {
                        footerComponent.onHorizontalDragging(percentX, offsetX, offsetMax);
                    }
                }
            }
            return ret;
        } else if (!thisView.isEnabled()
                || (!refreshEnabled && !loadMoreEnabled && !overScrollDragEnabled)
                || (headerNeedTouchEventWhenRefreshing && ((layoutState.isOpeningState || layoutState.isFinishingState) && layoutState.isHeaderState))
                || (footerNeedTouchEventWhenLoading && ((layoutState.isOpeningState || layoutState.isFinishingState) && layoutState.isFooterState))) {
            return super.dispatchTouchEvent(e);
        }

        if (cancelAnimatorIfNeeded(action) || layoutState.isFinishingState
                || (layoutState == RefreshLayoutState.Loading && disableContentWhenLoading)
                || (layoutState == RefreshLayoutState.REFRESHING && disableContentWhenRefresh)) {
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                currentFlingVelocity = 0;
                velocityTracker.addMovement(e);
                scrollAnimator.forceFinished(true);
                this.touchX = touchX;
                this.touchY = touchY;
                previousSpinnerOffset = 0;
                touchStartSpinner = spinnerOffset;
                isBeingDragged = false;
                enableDisallowIntercept = false;
                superDispatchTouchEvent = super.dispatchTouchEvent(e);
                if (layoutState == RefreshLayoutState.TwoLevel) {
                    final int height = thisView.getMeasuredHeight();
                    if (floorBottomDragLayoutRate <= 1 && this.touchY < height * (1- floorBottomDragLayoutRate)) {
                        dragDirection = 'h';
                        return superDispatchTouchEvent;
                    } else if (floorBottomDragLayoutRate > 1 && this.touchY < (height - floorBottomDragLayoutRate)) {
                        dragDirection = 'h';
                        return superDispatchTouchEvent;
                    }
                }
                if (contentHandler != null) {
                    contentHandler.handleActionDown(e);
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                float dx = touchX - this.touchX;
                float dy = touchY - this.touchY;
                velocityTracker.addMovement(e);
                if (!isBeingDragged && !enableDisallowIntercept && dragDirection != 'h' && contentHandler != null) {//没有拖动之前，检测  canRefresh canLoadMore 来开启拖动
                    if (dragDirection == 'v' || (Math.abs(dy) >= touchSlop && Math.abs(dx) < Math.abs(dy))) {//滑动允许最大角度为45度
                        dragDirection = 'v';
                        if (dy > 0 && (spinnerOffset < 0 || ((overScrollDragEnabled || refreshEnabled) && contentHandler.isRefreshPossible()))) {
                            isBeingDragged = true;
                            this.touchY = touchY - touchSlop;
                        } else if (dy < 0 && (spinnerOffset > 0 || ((overScrollDragEnabled || loadMoreEnabled) && ((layoutState == RefreshLayoutState.Loading&& footerScrollLocked)|| contentHandler.isLoadMorePossible())))) {
                            isBeingDragged = true;
                            this.touchY = touchY + touchSlop;
                        }
                        if (isBeingDragged) {
                            dy = touchY - this.touchY;
                            if (superDispatchTouchEvent) {
                                e.setAction(MotionEvent.ACTION_CANCEL);
                                super.dispatchTouchEvent(e);
                            }
                            refreshKernel.setRefreshState((spinnerOffset > 0 || (spinnerOffset == 0 && dy > 0)) ? RefreshLayoutState.PULL_DOWN_TO_REFRESH : RefreshLayoutState.PullUpToLoad);
                            final ViewParent parent = thisView.getParent();
                            if (parent instanceof ViewGroup) {
                                ((ViewGroup)parent).requestDisallowInterceptTouchEvent(true);//通知父控件不要拦截事件
                            }
                        }
                    } else if (Math.abs(dx) >= touchSlop && Math.abs(dx) > Math.abs(dy) && dragDirection != 'v') {
                        dragDirection = 'h';
                    }
                }
                if (isBeingDragged) {
                    int spinner = (int) dy + touchStartSpinner;
                    if ((secondaryState.isHeaderState && (spinner < 0 || previousSpinnerOffset < 0)) || (secondaryState.isFooterState && (spinner > 0 || previousSpinnerOffset > 0))) {
                        previousSpinnerOffset = spinner;
                        long time = e.getEventTime();
                        if (mFalsifyEvent == null) {
                            mFalsifyEvent = obtain(time, time, MotionEvent.ACTION_DOWN, this.touchX + dx, this.touchY, 0);
                            super.dispatchTouchEvent(mFalsifyEvent);
                        }
                        MotionEvent em = obtain(time, time, MotionEvent.ACTION_MOVE, this.touchX + dx, this.touchY + spinner, 0);
                        super.dispatchTouchEvent(em);
                        if (footerScrollLocked && dy > touchSlop && spinnerOffset < 0) {
                            footerScrollLocked = false;//内容向下滚动时 解锁Footer 的锁定
                        }
                        if (spinner > 0 && ((overScrollDragEnabled || refreshEnabled) && contentHandler.isRefreshPossible())) {
                            this.touchY = lastTouchY = touchY;
                            touchStartSpinner = spinner = 0;
                            refreshKernel.setRefreshState(RefreshLayoutState.PULL_DOWN_TO_REFRESH);
                        } else if (spinner < 0 && ((overScrollDragEnabled || loadMoreEnabled) && contentHandler.isLoadMorePossible())) {
                            this.touchY = lastTouchY = touchY;
                            touchStartSpinner = spinner = 0;
                            refreshKernel.setRefreshState(RefreshLayoutState.PullUpToLoad);
                        }
                        if ((secondaryState.isHeaderState && spinner < 0) || (secondaryState.isFooterState && spinner > 0)) {
                            if (spinnerOffset != 0) {
                                updateSpinnerWithResistance(0);
                            }
                            return true;
                        } else if (mFalsifyEvent != null) {
                            mFalsifyEvent = null;
                            em.setAction(MotionEvent.ACTION_CANCEL);
                            super.dispatchTouchEvent(em);
                        }
                        em.recycle();
                    }
                    updateSpinnerWithResistance(spinner);
                    return true;
                } else if (footerScrollLocked && dy > touchSlop && spinnerOffset < 0) {
                    footerScrollLocked = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                velocityTracker.addMovement(e);
                velocityTracker.computeCurrentVelocity(1000, maxFlingVelocity);
                currentFlingVelocity = (int) velocityTracker.getYVelocity();
                handleFlingIfNeeded(0);
            case MotionEvent.ACTION_CANCEL:
                velocityTracker.clear();
                dragDirection = 'n';
                if (mFalsifyEvent != null) {
                    mFalsifyEvent.recycle();
                    mFalsifyEvent = null;
                    long time = e.getEventTime();
                    MotionEvent ec = obtain(time, time, action, this.touchX, touchY, 0);
                    super.dispatchTouchEvent(ec);
                    ec.recycle();
                }
                handleSpinnerRelease();
                if (isBeingDragged) {
                    isBeingDragged = false;
                    return true;
                }
                break;
        }
        return super.dispatchTouchEvent(e);
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        View target = contentHandler.getScrollableContentView();
        if (ViewCompat.isNestedScrollingEnabled(target)) {
            enableDisallowIntercept = disallowIntercept;
            super.requestDisallowInterceptTouchEvent(disallowIntercept);
        }
    }

    protected boolean handleFlingIfNeeded(float flingVelocity) {
        float velocity = flingVelocity == 0 ? currentFlingVelocity : flingVelocity;
        if (Build.VERSION.SDK_INT > 27 && contentHandler != null) {
            float scaleY = getScaleY();
            final View thisView = this;
            final View contentView = contentHandler.getContentView();
            if (thisView.getScaleY() == -1 && contentView.getScaleY() == -1) {
                velocity = -velocity;
            }
        }
        if (Math.abs(velocity) > minFlingVelocity) {
            if (velocity * spinnerOffset < 0) {
                if (layoutState == RefreshLayoutState.REFRESHING || layoutState == RefreshLayoutState.Loading || (spinnerOffset < 0 && footerNoMoreData)) {
                    activeAnimationTask = new SpinnerFlingRunnable(velocity).start();
                    return true;
                } else if (layoutState.isReleaseToOpeningState) {
                    return true;
                }
            }
            if ((velocity < 0 && ((overScrollBounceEnabled && (loadMoreEnabled || overScrollDragEnabled)) || (layoutState == RefreshLayoutState.Loading && spinnerOffset >= 0) || (autoLoadMoreEnabled &&isEnableRefreshOrLoadMore(loadMoreEnabled))))
                    || (velocity > 0 && ((overScrollBounceEnabled && refreshEnabled || overScrollDragEnabled) || (layoutState == RefreshLayoutState.REFRESHING && spinnerOffset <= 0)))) {
                mVerticalPermit = false;
                scrollAnimator.fling(0, 0, 0, (int) -velocity, 0, 0, -Integer.MAX_VALUE, Integer.MAX_VALUE);
                scrollAnimator.computeScrollOffset();
                final View thisView = this;
                thisView.invalidate();
            }
        }
        return false;
    }
    protected boolean cancelAnimatorIfNeeded(int action) {
        if (action == MotionEvent.ACTION_DOWN) {
            if (spinnerAnimator != null) {
                if (layoutState.isFinishingState || layoutState == RefreshLayoutState.TwoLevelReleased || layoutState == RefreshLayoutState.REFRESH_RELEASED || layoutState == RefreshLayoutState.LoadReleased) {
                    return true;
                }
                if (layoutState == RefreshLayoutState.PULL_DOWN_CANCELLED) {
                    refreshKernel.setRefreshState(RefreshLayoutState.PULL_DOWN_TO_REFRESH);
                } else if (layoutState == RefreshLayoutState.PullUpCanceled) {
                    refreshKernel.setRefreshState(RefreshLayoutState.PullUpToLoad);
                }
                spinnerAnimator.setDuration(0);
                spinnerAnimator.cancel();
                spinnerAnimator = null;
            }
            activeAnimationTask = null;
        }
        return spinnerAnimator != null;
    }

    protected void dispatchStateChange(RefreshLayoutState state) {
        final RefreshLayoutState oldState = layoutState;
        if (oldState != state) {
            layoutState = state;
            secondaryState = state;
            final StateChangedListener refreshHeader = headerComponent;
            final StateChangedListener refreshFooter = footerComponent;
            final StateChangedListener refreshListener = multiPurposeCallback;
            if (refreshHeader != null) {
                refreshHeader.stateChanged(this, oldState, state);
            }
            if (refreshFooter != null) {
                refreshFooter.stateChanged(this, oldState, state);
            }
            if (refreshListener != null) {
                refreshListener.stateChanged(this, oldState, state);
            }
            if (state == RefreshLayoutState.LoadFinish) {
                footerScrollLocked = false;
            }
        } else if (secondaryState != layoutState) {
            secondaryState = layoutState;
        }
    }

    protected void enterLoadingState(boolean triggerLoadMoreEvent) {
        if (layoutState != RefreshLayoutState.Loading) {
            lastActionTime = currentTimeMillis();
            footerScrollLocked = true;
            dispatchStateChange(RefreshLayoutState.Loading);
            if (loadMoreCallback != null) {
                if (triggerLoadMoreEvent) {
                    loadMoreCallback.onLoadMore(this);
                }
            } else if (multiPurposeCallback == null) {
                completeLoadMore(2000);
            }
            if (footerComponent != null) {
                footerComponent.onAnimationStart(this, footerHeight, (int) (footerMaxDragRatio * footerHeight));
            }
            if (multiPurposeCallback != null && footerComponent instanceof RefreshFooterComponent) {
                final LoadMoreListener listener = multiPurposeCallback;
                if (triggerLoadMoreEvent) {
                    listener.onLoadMore(this);
                }
                multiPurposeCallback.footerAnimationStart((RefreshFooterComponent) footerComponent, footerHeight, (int) (footerMaxDragRatio * footerHeight));
            }
        }
    }

    protected void startLoadingState(final boolean notify) {
        AnimatorListenerAdapter listener = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (animation != null && animation.getDuration() == 0) {
                    return;//0 表示被取消
                }
                enterLoadingState(notify);
            }
        };
        dispatchStateChange(RefreshLayoutState.LoadReleased);
        ValueAnimator animator = refreshKernel.animateSpinnerTo(-footerHeight);
        if (animator != null) {
            animator.addListener(listener);
        }
        if (footerComponent != null) {
            footerComponent.onDragReleased(this, footerHeight, (int) (footerMaxDragRatio * footerHeight));
        }
        if (multiPurposeCallback != null && footerComponent instanceof RefreshFooterComponent) {
            multiPurposeCallback.footerReleased((RefreshFooterComponent) footerComponent, footerHeight, (int) (footerMaxDragRatio * footerHeight));
        }
        if (animator == null) {
            listener.onAnimationEnd(null);
        }
    }

    protected void startRefreshingState(final boolean notify) {
        AnimatorListenerAdapter listener = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (animation != null && animation.getDuration() == 0) {
                    return;//0 表示被取消
                }
                lastActionTime = currentTimeMillis();
                dispatchStateChange(RefreshLayoutState.REFRESHING);
                if (refreshCallback != null) {
                    if(notify) {
                        refreshCallback.refresh(SmartRefreshLayout.this);
                    }
                } else if (multiPurposeCallback == null) {
                    completeRefresh(3000);
                }
                if (headerComponent != null) {
                    headerComponent.onAnimationStart(SmartRefreshLayout.this, headerHeight,  (int) (headerMaxDragRatio * headerHeight));
                }
                if (multiPurposeCallback != null && headerComponent instanceof RefreshHeaderComponent) {
                    if (notify) {
                        multiPurposeCallback.refresh(SmartRefreshLayout.this);
                    }
                    multiPurposeCallback.headerAnimationStart((RefreshHeaderComponent) headerComponent, headerHeight,  (int) (headerMaxDragRatio * headerHeight));
                }
            }
        };
        dispatchStateChange(RefreshLayoutState.REFRESH_RELEASED);
        ValueAnimator animator = refreshKernel.animateSpinnerTo(headerHeight);
        if (animator != null) {
            animator.addListener(listener);
        }
        if (headerComponent != null) {
            headerComponent.onDragReleased(this, headerHeight,  (int) (headerMaxDragRatio * headerHeight));
        }
        if (multiPurposeCallback != null && headerComponent instanceof RefreshHeaderComponent) {
            multiPurposeCallback.headerReleased((RefreshHeaderComponent) headerComponent, headerHeight,  (int) (headerMaxDragRatio * headerHeight));
        }
        if (animator == null) {
            listener.onAnimationEnd(null);
        }
    }

    protected void updateSecondaryState(RefreshLayoutState state) {
        if (layoutState.isDraggingState && layoutState.isHeaderState != state.isHeaderState) {
            dispatchStateChange(RefreshLayoutState.IDLE);
        }
        if (secondaryState != state) {
            secondaryState = state;
        }
    }

    protected boolean isEnableTranslationContent(boolean enable, RefreshComponent internal) {
        return enable || pureScrollModeEnabled || internal == null || internal.getSpinnerBehavior() == RefreshSpinnerStyle.FIXED_BEHIND;
    }

    protected boolean isEnableRefreshOrLoadMore(boolean enable) {
        return enable && !pureScrollModeEnabled;
    }
    protected Runnable activeAnimationTask;
    protected ValueAnimator spinnerAnimator;
    protected class SpinnerFlingRunnable implements Runnable {
        int mOffset;
        int mFrame = 0;
        int mFrameDelay = 10;
        float mVelocity;
        float mDamping = 0.98f;
        long mStartTime = 0;
        long mLastTime = AnimationUtils.currentAnimationTimeMillis();

        SpinnerFlingRunnable(float velocity) {
            mVelocity = velocity;
            mOffset = spinnerOffset;
        }

        public Runnable start() {
            if (layoutState.isFinishingState) {
                return null;
            }
            if (spinnerOffset != 0 && (!(layoutState.isOpeningState || (footerNoMoreData && enableFooterFollowWhenNoMoreData && footerNoMoreDataEffective && isEnableRefreshOrLoadMore(loadMoreEnabled)))
                    || ((layoutState == RefreshLayoutState.Loading || (footerNoMoreData && enableFooterFollowWhenNoMoreData && footerNoMoreDataEffective && isEnableRefreshOrLoadMore(loadMoreEnabled))) && spinnerOffset < -footerHeight)
                    || (layoutState == RefreshLayoutState.REFRESHING && spinnerOffset > headerHeight))) {
                int frame = 0;
                int offset = spinnerOffset;
                int spinner = spinnerOffset;
                float velocity = mVelocity;
                while (spinner * offset > 0) {
                    velocity *= Math.pow(mDamping, (++frame) * mFrameDelay / 10f);
                    float velocityFrame = (velocity * (1f * mFrameDelay / 1000));
                    if (Math.abs(velocityFrame) < 1) {
                        if (!layoutState.isOpeningState
                                || (layoutState == RefreshLayoutState.REFRESHING && offset > headerHeight)
                                || (layoutState != RefreshLayoutState.REFRESHING && offset < -footerHeight)) {
                            return null;
                        }
                        break;
                    }
                    offset += velocityFrame;
                }
            }
            mStartTime = AnimationUtils.currentAnimationTimeMillis();
            mHandler.postDelayed(this, mFrameDelay);
            return this;
        }

        @Override
        public void run() {
            if (activeAnimationTask == this && !layoutState.isFinishingState) {
//                mVelocity *= Math.pow(mDamping, ++mFrame);
                long now = AnimationUtils.currentAnimationTimeMillis();
                long span = now - mLastTime;
                mVelocity *= Math.pow(mDamping, (now - mStartTime) / (1000f / mFrameDelay));
                float velocity = (mVelocity * (1f * span / 1000));
                if (Math.abs(velocity) > 1) {
                    mLastTime = now;
                    mOffset += velocity;
                    if (spinnerOffset * mOffset > 0) {
                        refreshKernel.updateSpinnerPosition(mOffset, true);
                        mHandler.postDelayed(this, mFrameDelay);
                    } else {
                        activeAnimationTask = null;
                        refreshKernel.updateSpinnerPosition(0, true);
                        flingView(contentHandler.getScrollableContentView(), (int) -mVelocity);
                        if (footerScrollLocked && velocity > 0) {
                            footerScrollLocked = false;
                        }
                    }
                } else {
                    activeAnimationTask = null;
                }
            }
        }
    }
    protected class SpinnerBounceRunnable implements Runnable {
        int mFrame = 0;
        int mFrameDelay = 10;
        int mSmoothDistance;
        long mLastTime;
        float mOffset = 0;
        float mVelocity;
        SpinnerBounceRunnable(float velocity, int smoothDistance){
            mVelocity = velocity;
            mSmoothDistance = smoothDistance;
            mLastTime = AnimationUtils.currentAnimationTimeMillis();
            mHandler.postDelayed(this, mFrameDelay);
            if (velocity > 0) {
                refreshKernel.setRefreshState(RefreshLayoutState.PULL_DOWN_TO_REFRESH);
            } else {
                refreshKernel.setRefreshState(RefreshLayoutState.PullUpToLoad);
            }
        }
        @Override
        public void run() {
            if (activeAnimationTask == this && !layoutState.isFinishingState) {
                if (Math.abs(spinnerOffset) >= Math.abs(mSmoothDistance)) {
                    if (mSmoothDistance != 0) {
                        mVelocity *= Math.pow(0.45f, ++mFrame * 2);//刷新、加载时回弹滚动数度衰减
                    } else {
                        mVelocity *= Math.pow(0.85f, ++mFrame * 2);//回弹滚动数度衰减
                    }
                } else {
                    mVelocity *= Math.pow(0.95f, ++mFrame * 2);//平滑滚动数度衰减
                }
                long now = AnimationUtils.currentAnimationTimeMillis();
                float t = 1f * (now - mLastTime) / 1000;
                float velocity = mVelocity * t;
                if (Math.abs(velocity) >= 1) {
                    mLastTime = now;
                    mOffset += velocity;
                    updateSpinnerWithResistance(mOffset);
                    mHandler.postDelayed(this, mFrameDelay);
                } else {
                    if (secondaryState.isDraggingState && secondaryState.isHeaderState) {
                        refreshKernel.setRefreshState(RefreshLayoutState.PULL_DOWN_CANCELLED);
                    } else if (secondaryState.isDraggingState && secondaryState.isFooterState) {
                        refreshKernel.setRefreshState(RefreshLayoutState.PullUpCanceled);
                    }
                    activeAnimationTask = null;
                    if (Math.abs(spinnerOffset) >= Math.abs(mSmoothDistance)) {
                        int duration = 10 * Math.min(Math.max((int) SmartViewUtil.pxToDp(Math.abs(spinnerOffset -mSmoothDistance)), 30), 100);
                        animateSpinner(mSmoothDistance, 0, reboundInterpolator, duration);
                    }
                }
            }
        }
    }

    protected ValueAnimator animateSpinner(int endSpinner, int startDelay, Interpolator interpolator, int duration) {
        if (spinnerOffset != endSpinner) {
            if (spinnerAnimator != null) {
                spinnerAnimator.setDuration(0);
                spinnerAnimator.cancel();
                spinnerAnimator = null;
            }
            activeAnimationTask = null;
            spinnerAnimator = ValueAnimator.ofInt(spinnerOffset, endSpinner);
            spinnerAnimator.setDuration(duration);
            spinnerAnimator.setInterpolator(interpolator);
            spinnerAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (animation != null && animation.getDuration() == 0) {
                        return;
                    }
                    spinnerAnimator = null;
                    if (spinnerOffset == 0 && layoutState != RefreshLayoutState.IDLE && !layoutState.isOpeningState && !layoutState.isDraggingState) {
                        dispatchStateChange(RefreshLayoutState.IDLE);
                    } else if (layoutState != secondaryState) {
                        updateSecondaryState(layoutState);
                    }
                }
            });
            spinnerAnimator.addUpdateListener(new AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (spinnerAnimator != null) {
                        refreshKernel.updateSpinnerPosition((int) animation.getAnimatedValue(), false);
                    }
                }
            });
            spinnerAnimator.setStartDelay(startDelay);
            spinnerAnimator.start();
            return spinnerAnimator;
        }
        return null;
    }

    protected void startBounceAnimation(final float velocity) {
        if (spinnerAnimator == null) {
            if (velocity > 0 && (layoutState == RefreshLayoutState.REFRESHING || layoutState == RefreshLayoutState.TwoLevel)) {
                activeAnimationTask = new SpinnerBounceRunnable(velocity, headerHeight);
            } else if (velocity < 0 && (layoutState == RefreshLayoutState.Loading
                    || (enableFooterFollowWhenNoMoreData && footerNoMoreData && footerNoMoreDataEffective && isEnableRefreshOrLoadMore(loadMoreEnabled))
                    || (autoLoadMoreEnabled && !footerNoMoreData && isEnableRefreshOrLoadMore(loadMoreEnabled) && layoutState != RefreshLayoutState.REFRESHING))) {
                activeAnimationTask = new SpinnerBounceRunnable(velocity, -footerHeight);
            } else if (spinnerOffset == 0 && overScrollBounceEnabled) {
                activeAnimationTask = new SpinnerBounceRunnable(velocity, 0);
            }
        }
    }

    protected void handleSpinnerRelease() {
        if (layoutState == RefreshLayoutState.TwoLevel) {
            final View thisView = this;
            final int height = thisView.getMeasuredHeight();
            final int floorHeight = floorOpenLayoutRate > 1 ? (int) floorOpenLayoutRate : (int)(height* floorOpenLayoutRate);
            if (currentFlingVelocity > -1000 && spinnerOffset > floorHeight / 2) {
                ValueAnimator animator = refreshKernel.animateSpinnerTo(floorHeight);
                if (animator != null) {
                    animator.setDuration(floorDuration);
                }
            } else if (isBeingDragged) {
                refreshKernel.closeTwoLevel();
            }
        } else if (layoutState == RefreshLayoutState.Loading
                || (enableFooterFollowWhenNoMoreData && footerNoMoreData && footerNoMoreDataEffective && spinnerOffset < 0 && isEnableRefreshOrLoadMore(loadMoreEnabled))) {
            if (spinnerOffset < -footerHeight) {
                refreshKernel.animateSpinnerTo(-footerHeight);
            } else if (spinnerOffset > 0) {
                refreshKernel.animateSpinnerTo(0);
            }
        } else if (layoutState == RefreshLayoutState.REFRESHING) {
            if (spinnerOffset > headerHeight) {
                refreshKernel.animateSpinnerTo(headerHeight);
            } else if (spinnerOffset < 0) {
                refreshKernel.animateSpinnerTo(0);
            }
        } else if (layoutState == RefreshLayoutState.PULL_DOWN_TO_REFRESH) {
            refreshKernel.setRefreshState(RefreshLayoutState.PULL_DOWN_CANCELLED);
        } else if (layoutState == RefreshLayoutState.PullUpToLoad) {
            refreshKernel.setRefreshState(RefreshLayoutState.PullUpCanceled);
        } else if (layoutState == RefreshLayoutState.RELEASE_TO_REFRESH) {
            refreshKernel.setRefreshState(RefreshLayoutState.REFRESHING);
        } else if (layoutState == RefreshLayoutState.ReleaseToLoad) {
            refreshKernel.setRefreshState(RefreshLayoutState.Loading);
        } else if (layoutState == RefreshLayoutState.RELEASE_TO_TWO_LEVEL) {
            refreshKernel.setRefreshState(RefreshLayoutState.TwoLevelReleased);
        } else if (layoutState == RefreshLayoutState.REFRESH_RELEASED) {
            if (spinnerAnimator == null) {
                refreshKernel.animateSpinnerTo(headerHeight);
            }
        } else if (layoutState == RefreshLayoutState.LoadReleased) {
            if (spinnerAnimator == null) {
                refreshKernel.animateSpinnerTo(-footerHeight);
            }
        } else if (layoutState == RefreshLayoutState.LoadFinish) {
        } else if (spinnerOffset != 0) {
            refreshKernel.animateSpinnerTo(0);
        }
    }

    protected void updateSpinnerWithResistance(float spinner) {
        final View thisView = this;
        if (nestedScrollingActive && !enableLoadMoreWhenContentNotFull && spinner < 0) {
            if (!contentHandler.isLoadMorePossible()) {
                spinner = 0;
            }
        }
        if (spinner > screenHeightPixels * 5 && thisView.getTag() == null && lastTouchY < screenHeightPixels / 6f && lastTouchX < screenHeightPixels / 16f) {
            String egg = "你这么死拉，臣妾做不到啊！";
            Toast.makeText(thisView.getContext(), egg, Toast.LENGTH_SHORT).show();
            thisView.setTag(egg);
        }
        if (layoutState == RefreshLayoutState.TwoLevel && spinner > 0 && contentHandler != null) {
            final int height = thisView.getMeasuredHeight();
            final int floorHeight = floorOpenLayoutRate > 1 ? (int) floorOpenLayoutRate : (int)(height* floorOpenLayoutRate);
            refreshKernel.updateSpinnerPosition(Math.min((int) spinner, floorHeight), true);
        } else if (layoutState == RefreshLayoutState.REFRESHING && spinner >= 0) {
            if (spinner < headerHeight) {
                refreshKernel.updateSpinnerPosition((int) spinner, true);
            } else {
                final float M = (headerMaxDragRatio - 1) * headerHeight;
                final float H = Math.max(screenHeightPixels * 4 / 3, thisView.getHeight()) - headerHeight;
                final float x = Math.max(0, (spinner - headerHeight) * dragRate);
                final float y = Math.min(M * (1 - (float)Math.pow(100, -x / (H == 0 ? 1 : H))), x);// 公式 y = M(1-100^(-x/H))
                refreshKernel.updateSpinnerPosition((int) y + headerHeight, true);
            }
        } else if (spinner < 0 && (layoutState == RefreshLayoutState.Loading
                || (enableFooterFollowWhenNoMoreData && footerNoMoreData && footerNoMoreDataEffective && isEnableRefreshOrLoadMore(loadMoreEnabled))
                || (autoLoadMoreEnabled && !footerNoMoreData && isEnableRefreshOrLoadMore(loadMoreEnabled)))) {
            if (spinner > -footerHeight) {
                refreshKernel.updateSpinnerPosition((int) spinner, true);
            } else {
                final float M = (footerMaxDragRatio - 1) * footerHeight;
                final float H = Math.max(screenHeightPixels * 4 / 3, thisView.getHeight()) - footerHeight;
                final float x = -Math.min(0, (spinner + footerHeight) * dragRate);
                final float y = -Math.min(M * (1 - (float)Math.pow(100, -x / (H == 0 ? 1 : H))), x);// 公式 y = M(1-100^(-x/H))
                refreshKernel.updateSpinnerPosition((int) y - footerHeight, true);
            }
        } else if (spinner >= 0) {
            final float M = headerMaxDragRatio * headerHeight;
            final float H = Math.max(screenHeightPixels / 2, thisView.getHeight());
            final float x = Math.max(0, spinner * dragRate);
            final float y = Math.min(M * (1 - (float)Math.pow(100, -x / (H == 0 ? 1 : H))), x);// 公式 y = M(1-100^(-x/H))
            refreshKernel.updateSpinnerPosition((int) y, true);
        } else {
            final float M = footerMaxDragRatio * footerHeight;
            final float H = Math.max(screenHeightPixels / 2, thisView.getHeight());
            final float x = -Math.min(0, spinner * dragRate);
            final float y = -Math.min(M * (1 - (float)Math.pow(100, -x / (H == 0 ? 1 : H))), x);// 公式 y = M(1-100^(-x/H))
            refreshKernel.updateSpinnerPosition((int) y, true);
        }
        if (autoLoadMoreEnabled && !footerNoMoreData && isEnableRefreshOrLoadMore(loadMoreEnabled) && spinner < 0
                && layoutState != RefreshLayoutState.REFRESHING
                && layoutState != RefreshLayoutState.Loading
                && layoutState != RefreshLayoutState.LoadFinish) {
            if (disableContentWhenLoading) {
                activeAnimationTask = null;
                refreshKernel.animateSpinnerTo(-footerHeight);
            }
            enterLoadingState(false);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (loadMoreCallback != null) {
                        loadMoreCallback.onLoadMore(SmartRefreshLayout.this);
                    } else if (multiPurposeCallback == null) {
                        completeLoadMore(2000);//如果没有任何加载监听器，两秒之后自动关闭
                    }
                    final LoadMoreListener listener = multiPurposeCallback;
                    if (listener != null) {
                        listener.onLoadMore(SmartRefreshLayout.this);
                    }
                }
            }, reboundDuration);
        }
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        final View thisView = this;
        return new LayoutParams(thisView.getContext(), attrs);
    }

    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SmartRefreshLayout_Layout);
            backgroundColor = ta.getColor(R.styleable.SmartRefreshLayout_Layout_layout_srlBackgroundColor, backgroundColor);
            if (ta.hasValue(R.styleable.SmartRefreshLayout_Layout_layout_srlSpinnerStyle)) {
                spinnerStyle = RefreshSpinnerStyle.STYLES[ta.getInt(R.styleable.SmartRefreshLayout_Layout_layout_srlSpinnerStyle, RefreshSpinnerStyle.TRANSLATE.ordinal)];
            }
            ta.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public int backgroundColor = 0;
        public RefreshSpinnerStyle spinnerStyle = null;
    }
    @Override
    public int getNestedScrollAxes() {
        return nestedParentHelper.getNestedScrollAxes();
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int nestedScrollAxes) {
        final View thisView = this;
        boolean accepted = thisView.isEnabled() && isNestedScrollingEnabled() && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
        accepted = accepted && (overScrollDragEnabled || refreshEnabled || loadMoreEnabled);
        return accepted;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes) {
        nestedParentHelper.onNestedScrollAccepted(child, target, axes);
        nestedChildHelper.startNestedScroll(axes & ViewCompat.SCROLL_AXIS_VERTICAL);

        nestedUnconsumed = spinnerOffset;//0;
        nestedScrollingActive = true;

        cancelAnimatorIfNeeded(MotionEvent.ACTION_DOWN);
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed) {
        int consumedY = 0;

        if (dy * nestedUnconsumed > 0) {
            if (Math.abs(dy) > Math.abs(nestedUnconsumed)) {
                consumedY = nestedUnconsumed;
                nestedUnconsumed = 0;
            } else {
                consumedY = dy;
                nestedUnconsumed -= dy;
            }
            updateSpinnerWithResistance(nestedUnconsumed);
        } else if (dy > 0 && footerScrollLocked) {
            consumedY = dy;
            nestedUnconsumed -= dy;
            updateSpinnerWithResistance(nestedUnconsumed);
        }

        nestedChildHelper.dispatchNestedPreScroll(dx, dy - consumedY, consumed, null);
        consumed[1] += consumedY;

    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        boolean scrolled = nestedChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, parentOffset);
        final int dy = dyUnconsumed + parentOffset[1];
        if ((dy < 0 && (refreshEnabled || overScrollDragEnabled) && (nestedUnconsumed != 0 || scrollBoundaryChecker == null || scrollBoundaryChecker.canTriggerRefresh(contentHandler.getContentView())))
                || (dy > 0 && (loadMoreEnabled || overScrollDragEnabled) && (nestedUnconsumed != 0 || scrollBoundaryChecker == null || scrollBoundaryChecker.canTriggerLoadMore(contentHandler.getContentView())))) {
            if (secondaryState == RefreshLayoutState.IDLE || secondaryState.isOpeningState) {
                refreshKernel.setRefreshState(dy > 0 ? RefreshLayoutState.PullUpToLoad : RefreshLayoutState.PULL_DOWN_TO_REFRESH);
                if (!scrolled) {
                    final View thisView = this;
                    final ViewParent parent = thisView.getParent();
                    if (parent instanceof ViewGroup) {
                        ((ViewGroup)parent).requestDisallowInterceptTouchEvent(true);//通知父控件不要拦截事件
                    }
                }
            }
            updateSpinnerWithResistance(nestedUnconsumed -= dy);
        }

        if (footerScrollLocked && dyConsumed < 0) {
            footerScrollLocked = false;
        }

    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        return (footerScrollLocked && velocityY > 0) || handleFlingIfNeeded(-velocityY) || nestedChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
        return nestedChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public void onStopNestedScroll(@NonNull View target) {
        nestedParentHelper.onStopNestedScroll(target);
        nestedScrollingActive = false;
        nestedUnconsumed = 0;
        handleSpinnerRelease();
        nestedChildHelper.stopNestedScroll();
    }
    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        nestedScrollingEnabled = enabled;
        nestedChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return nestedScrollingEnabled && (overScrollDragEnabled || refreshEnabled || loadMoreEnabled);
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout setHeaderHeightDp(float heightDp) {
        int height = dpToPx(heightDp);
        if (height == headerHeight) {
            return this;
        }
        if (headerHeightStatus.canBeReplacedBy(RefreshDimensionStatus.CODE_EXACT)) {
            headerHeight = height;
            if (headerComponent != null && attachedToWindow && headerHeightStatus.isNotified) {
                RefreshSpinnerStyle style = headerComponent.getSpinnerBehavior();
                if (style != RefreshSpinnerStyle.MATCH_LAYOUT && !style.scale) {
                    View headerView = headerComponent.getComponentView();
                    final ViewGroup.LayoutParams lp = headerView.getLayoutParams();
                    final MarginLayoutParams mlp = lp instanceof MarginLayoutParams ? (MarginLayoutParams) lp : sDefaultMarginLP;
                    final int widthSpec = makeMeasureSpec(headerView.getMeasuredWidth(), EXACTLY);
                    headerView.measure(widthSpec, makeMeasureSpec(Math.max(headerHeight - mlp.bottomMargin - mlp.topMargin, 0), EXACTLY));
                    final int left = mlp.leftMargin;
                    int top = mlp.topMargin + headerInsetStart - ((style == RefreshSpinnerStyle.TRANSLATE) ? headerHeight : 0);
                    headerView.layout(left, top, left + headerView.getMeasuredWidth(), top + headerView.getMeasuredHeight());
                }
                headerHeightStatus = RefreshDimensionStatus.CODE_EXACT;
                headerComponent.onComponentInitialized(refreshKernel, headerHeight, (int) (headerMaxDragRatio * headerHeight));
            } else {
                headerHeightStatus = RefreshDimensionStatus.CODE_EXACT_UNNOTIFIED;
            }
        }
        return this;
    }
    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout setFooterHeightDp(float heightDp) {
        int height = dpToPx(heightDp);
        if (height == footerHeight) {
            return this;
        }
        if (footerHeightStatus.canBeReplacedBy(RefreshDimensionStatus.CODE_EXACT)) {
            footerHeight = height;
            if (footerComponent != null && attachedToWindow && footerHeightStatus.isNotified) {
                RefreshSpinnerStyle style = footerComponent.getSpinnerBehavior();
                if (style != RefreshSpinnerStyle.MATCH_LAYOUT && !style.scale) {
                    View thisView = this;
                    View footerView = footerComponent.getComponentView();
                    final ViewGroup.LayoutParams lp = footerView.getLayoutParams();
                    final MarginLayoutParams mlp = lp instanceof MarginLayoutParams ? (MarginLayoutParams)lp : sDefaultMarginLP;
                    final int widthSpec = makeMeasureSpec(footerView.getMeasuredWidth(), EXACTLY);
                    footerView.measure(widthSpec, makeMeasureSpec(Math.max(footerHeight - mlp.bottomMargin - mlp.topMargin, 0), EXACTLY));
                    final int left = mlp.leftMargin;
                    final int top = mlp.topMargin + thisView.getMeasuredHeight() - footerInsetStart - ((style != RefreshSpinnerStyle.TRANSLATE) ? footerHeight : 0);
                    footerView.layout(left, top, left + footerView.getMeasuredWidth(), top + footerView.getMeasuredHeight());
                }
                footerHeightStatus = RefreshDimensionStatus.CODE_EXACT;
                footerComponent.onComponentInitialized(refreshKernel, footerHeight, (int) (footerMaxDragRatio * footerHeight));
            } else {
                footerHeightStatus = RefreshDimensionStatus.CODE_EXACT_UNNOTIFIED;
            }
        }
        return this;
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout setHeaderInsetStartDp(float insetDp) {
        headerInsetStart = dpToPx(insetDp);
        return this;
    }
    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout setFooterInsetStartDp(float insetDp) {
        footerInsetStart = dpToPx(insetDp);
        return this;
    }
    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout setDragSensitivity(float rate) {
        this.dragRate = rate;
        return this;
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout setHeaderMaxDragRate(float rate) {
        this.headerMaxDragRatio = rate;
        if (headerComponent != null && attachedToWindow) {
            headerComponent.onComponentInitialized(refreshKernel, headerHeight,  (int) (headerMaxDragRatio * headerHeight));
        } else {
            headerHeightStatus = headerHeightStatus.toUnnotified();
        }
        return this;
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout setFooterMaxDragRate(float rate) {
        this.footerMaxDragRatio = rate;
        if (footerComponent != null && attachedToWindow) {
            footerComponent.onComponentInitialized(refreshKernel, footerHeight, (int)(footerHeight * footerMaxDragRatio));
        } else {
            footerHeightStatus = footerHeightStatus.toUnnotified();
        }
        return this;
    }
    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout setFooterTriggerThreshold(float rate) {
        this.headerTriggerRatio = rate;
        return this;
    }
    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout setFooterTriggerRate(float rate) {
        this.footerTriggerRatio = rate;
        return this;
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout setBounceInterpolator(@NonNull Interpolator interpolator) {
        this.reboundInterpolator = interpolator;
        return this;
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout setBounceDuration(int duration) {
        this.reboundDuration = duration;
        return this;
    }
    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout setLoadMoreEnable(boolean enabled) {
        this.manualLoadMore = true;
        this.loadMoreEnabled = enabled;
        return this;
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout setRefreshEnable(boolean enabled) {
        this.refreshEnabled = enabled;
        return this;
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout enableHeaderContentTranslation(boolean enabled) {
        this.enableHeaderTranslationContent = enabled;
        this.manualHeaderTranslationContent = true;
        return this;
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout enableFooterContentTranslation(boolean enabled) {
        this.enableFooterTranslationContent = enabled;
        this.manualFooterTranslationContent = true;
        return this;
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout setAutoLoadMoreEnable(boolean enabled) {
        this.autoLoadMoreEnabled = enabled;
        return this;
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout enableOverScrollBounce(boolean enabled) {
        this.overScrollBounceEnabled = enabled;
        return this;
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout enablePureScrollMode(boolean enabled) {
        this.pureScrollModeEnabled = enabled;
        return this;
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout enableScrollAfterLoad(boolean enabled) {
        this.enableScrollContentWhenLoaded = enabled;
        return this;
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout enableScrollAfterRefresh(boolean enabled) {
        this.enableScrollContentWhenRefreshed = enabled;
        return this;
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout enableLoadMoreWhenNotFull(boolean enabled) {
        this.enableLoadMoreWhenContentNotFull = enabled;
        if (contentHandler != null) {
            contentHandler.setLoadMoreWhenContentNotFullEnabled(enabled);
        }
        return this;
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout enableOverScrollDrag(boolean enabled) {
        this.overScrollDragEnabled = enabled;
        return this;
    }

    @Override
    @Deprecated
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout enableFooterFollowWhenFinished(boolean enabled) {
        this.enableFooterFollowWhenNoMoreData = enabled;
        return this;
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout enableFooterFollowWhenNoMoreData(boolean enabled) {
        this.enableFooterFollowWhenNoMoreData = enabled;
        return this;
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout setHeaderWhenFixedBehindEnableClip(boolean enabled) {
        this.enableClipHeaderWhenFixedBehind = enabled;
        return this;
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout setFooterWhenFixedBehindEnableClip(boolean enabled) {
        this.enableClipFooterWhenFixedBehind = enabled;
        return this;
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout setNestedScrollEnable(boolean enabled) {
        setNestedScrollingEnabled(enabled);
        return this;
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout setContentWhenRefreshDisable(boolean disable) {
        this.disableContentWhenRefresh = disable;
        return this;
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout setContentWhenLoadingDisable(boolean disable) {
        this.disableContentWhenLoading = disable;
        return this;
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout setHeaderRefresh(@NonNull RefreshHeaderComponent header) {
        return setHeaderRefresh(header, 0, 0);
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout setHeaderRefresh(@NonNull RefreshHeaderComponent header, int width, int height) {
        if (headerComponent != null) {
            super.removeView(headerComponent.getComponentView());
        }
        this.headerComponent = header;
        this.headerBackgroundColor = 0;
        this.headerNeedTouchEventWhenRefreshing = false;
        this.headerHeightStatus = RefreshDimensionStatus.DEFAULT_UNNOTIFIED;
        width = width == 0 ? MATCH_PARENT : width;
        height = height == 0 ? WRAP_CONTENT : height;
        LayoutParams lp = new LayoutParams(width, height);
        Object olp = headerComponent.getComponentView().getLayoutParams();
        if (olp instanceof LayoutParams) {
            lp = ((LayoutParams) olp);
        }
        if (headerComponent.getSpinnerBehavior().front) {
            final ViewGroup thisGroup = this;
            super.addView(headerComponent.getComponentView(), thisGroup.getChildCount(), lp);
        } else {
            super.addView(headerComponent.getComponentView(), 0, lp);
        }
        if (primaryColors != null && headerComponent != null) {
            headerComponent.applyPrimaryColors(primaryColors);
        }
        return this;
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout setFooterRefresh(@NonNull RefreshFooterComponent footer) {
        return setFooterRefresh(footer, 0, 0);
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout setFooterRefresh(@NonNull RefreshFooterComponent footer, int width, int height) {
        if (footerComponent != null) {
            super.removeView(footerComponent.getComponentView());
        }
        this.footerComponent = footer;
        this.footerScrollLocked = false;
        this.footerBackgroundColor = 0;
        this.footerNoMoreDataEffective = false;
        this.footerNeedTouchEventWhenLoading = false;
        this.footerHeightStatus = RefreshDimensionStatus.DEFAULT_UNNOTIFIED;//2020-5-23 修复动态切换时，不能及时测量新的高度
        this.loadMoreEnabled = !manualLoadMore || loadMoreEnabled;
        /*
         * 2020-3-16 修复 header 中自带 LayoutParams 丢失问题
         */
        width = width == 0 ? MATCH_PARENT : width;
        height = height == 0 ? WRAP_CONTENT : height;
        LayoutParams lp = new LayoutParams(width, height);
        Object olp = footerComponent.getComponentView().getLayoutParams();
        if (olp instanceof LayoutParams) {
            lp = ((LayoutParams) olp);
        }
        if (footerComponent.getSpinnerBehavior().front) {
            final ViewGroup thisGroup = this;
            super.addView(footerComponent.getComponentView(), thisGroup.getChildCount(), lp);
        } else {
            super.addView(footerComponent.getComponentView(), 0, lp);
        }
        if (primaryColors != null && footerComponent != null) {
            footerComponent.applyPrimaryColors(primaryColors);
        }
        return this;
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout setContentRefresh(@NonNull View content) {
        return setContentRefresh(content, 0, 0);
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout setContentRefresh(@NonNull View content, int width, int height) {
        final View thisView = this;
        if (contentHandler != null) {
            super.removeView(contentHandler.getContentView());
        }
        final ViewGroup thisGroup = this;

        width = width == 0 ? MATCH_PARENT : width;
        height = height == 0 ? MATCH_PARENT : height;
        LayoutParams lp = new LayoutParams(width, height);
        Object olp = content.getLayoutParams();
        if (olp instanceof LayoutParams) {
            lp = ((LayoutParams) olp);
        }

        super.addView(content, thisGroup.getChildCount(), lp);

        contentHandler = new RefreshContentContainer(content);
        if (attachedToWindow) {
            View fixedHeaderView = thisView.findViewById(fixedHeaderViewId);
            View fixedFooterView = thisView.findViewById(fixedFooterViewId);

            contentHandler.setScrollBoundaryChecker(scrollBoundaryChecker);
            contentHandler.setLoadMoreWhenContentNotFullEnabled(enableLoadMoreWhenContentNotFull);
            contentHandler.setupComponent(refreshKernel, fixedHeaderView, fixedFooterView);
        }

        if (headerComponent != null && headerComponent.getSpinnerBehavior().front) {
            super.bringChildToFront(headerComponent.getComponentView());
        }
        if (footerComponent != null && footerComponent.getSpinnerBehavior().front) {
            super.bringChildToFront(footerComponent.getComponentView());
        }
        return this;
    }

    @Nullable
    @Override
    public RefreshFooterComponent getFooter() {
        return footerComponent instanceof RefreshFooterComponent ? (RefreshFooterComponent) footerComponent : null;
    }

    @Nullable
    @Override
    public RefreshHeaderComponent getHeader() {
        return headerComponent instanceof RefreshHeaderComponent ? (RefreshHeaderComponent) headerComponent : null;
    }

    @NonNull
    @Override
    public RefreshLayoutState getRefreshState() {
        return layoutState;
    }

    @NonNull
    @Override
    public ViewGroup getLayoutView() {
        return this;
    }


    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout setRefreshListener(RefreshListener listener) {
        this.refreshCallback = listener;
        return this;
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout setLoadMoreListener(LoadMoreListener listener) {
        this.loadMoreCallback = listener;
        this.loadMoreEnabled = loadMoreEnabled || (!manualLoadMore && listener != null);
        return this;
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout setRefreshLoadMoreListener(RefreshLoadListener listener) {
        this.refreshCallback = listener;
        this.loadMoreCallback = listener;
        this.loadMoreEnabled = loadMoreEnabled || (!manualLoadMore && listener != null);
        return this;
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout setMultiPurposeListener(MultiPurposeListener listener) {
        this.multiPurposeCallback = listener;
        return this;
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout setPrimaryColors(@ColorInt int... primaryColors) {
        if (headerComponent != null) {
            headerComponent.applyPrimaryColors(primaryColors);
        }
        if (footerComponent != null) {
            footerComponent.applyPrimaryColors(primaryColors);
        }
        this.primaryColors = primaryColors;
        return this;
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout setThemeColorResources(@ColorRes int... primaryColorId) {
        final View thisView = this;
        final int[] colors = new int[primaryColorId.length];
        for (int i = 0; i < primaryColorId.length; i++) {
            colors[i] = ContextCompat.getColor(thisView.getContext(), primaryColorId[i]);
        }
        setPrimaryColors(colors);
        return this;
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout setScrollBoundary(RefreshScrollBoundaryDecider boundary) {
        scrollBoundaryChecker = boundary;
        if (contentHandler != null) {
            contentHandler.setScrollBoundaryChecker(boundary);
        }
        return this;
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout setNoMoreDataAvailable(boolean noMoreData) {
        if (layoutState == RefreshLayoutState.REFRESHING && noMoreData) {
            completeRefreshWithNoMoreData();
        } else if (layoutState == RefreshLayoutState.Loading && noMoreData) {
            finishLoadMoreWithNoMoreData();
        } else if (footerNoMoreData != noMoreData) {
            footerNoMoreData = noMoreData;
            if (footerComponent instanceof RefreshFooterComponent) {
                if (((RefreshFooterComponent) footerComponent).setNoMoreDataAvailable(noMoreData)) {
                    footerNoMoreDataEffective = true;
                    if (footerNoMoreData && enableFooterFollowWhenNoMoreData && spinnerOffset > 0
                            && footerComponent.getSpinnerBehavior() == RefreshSpinnerStyle.TRANSLATE
                            && isEnableRefreshOrLoadMore(loadMoreEnabled)
                            && isEnableTranslationContent(refreshEnabled, headerComponent)) {
                        footerComponent.getComponentView().setTranslationY(spinnerOffset);
                    }
                } else {
                    footerNoMoreDataEffective = false;
                    String msg = "Footer:" + footerComponent + " NoMoreData is not supported.(不支持NoMoreData，请使用[ClassicsFooter]或者[自定义Footer并实现setNoMoreData方法且返回true])";
                    Throwable e = new RuntimeException(msg);
                    e.printStackTrace();
                }
            }

        }
        return this;
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout resetNoMoreDataState() {
        return setNoMoreDataAvailable(false);
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout completeRefresh() {
        return completeRefresh(true);
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout completeLoadMore() {
        return completeLoadMore(true);
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout completeRefresh(int delayed) {
        return completeRefresh(delayed, true, Boolean.FALSE);
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout completeRefresh(boolean success) {
        if (success) {
            long passTime = System.currentTimeMillis() - lastActionTime;
            int delayed = (Math.min(Math.max(0, 300 - (int) passTime), 300) << 16);//保证加载动画有300毫秒的时间
            return completeRefresh(delayed, true, Boolean.FALSE);
        } else {
            return completeRefresh(0, false, null);
        }
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout completeRefresh(final int delayed, final boolean success, final Boolean noMoreData) {
        final int more = delayed >> 16;
        int delay = delayed << 16 >> 16;
        Runnable runnable = new Runnable() {
            int count = 0;
            @Override
            public void run() {
                if (count == 0) {
                    if (layoutState == RefreshLayoutState.IDLE && secondaryState == RefreshLayoutState.REFRESHING) {
                        secondaryState = RefreshLayoutState.IDLE;
                    } else if (spinnerAnimator != null && layoutState.isHeaderState && (layoutState.isDraggingState || layoutState == RefreshLayoutState.REFRESH_RELEASED)) {
                        spinnerAnimator.setDuration(0);
                        spinnerAnimator.cancel();
                        spinnerAnimator = null;
                        if (refreshKernel.animateSpinnerTo(0) == null) {
                            dispatchStateChange(RefreshLayoutState.IDLE);
                        } else {
                            dispatchStateChange(RefreshLayoutState.PULL_DOWN_CANCELLED);
                        }
                    } else if (layoutState == RefreshLayoutState.REFRESHING && headerComponent != null && contentHandler != null) {
                        count++;
                        mHandler.postDelayed(this, more);
                        dispatchStateChange(RefreshLayoutState.REFRESH_FINISHED);
                        if (noMoreData == Boolean.FALSE) {
                            setNoMoreDataAvailable(false);
                        }
                    }
                    if (noMoreData == Boolean.TRUE) {
                        setNoMoreDataAvailable(true);
                    }
                } else {
                    int startDelay = headerComponent.onAnimationFinish(SmartRefreshLayout.this, success);
                    if (multiPurposeCallback != null && headerComponent instanceof RefreshHeaderComponent) {
                        multiPurposeCallback.headerFinish((RefreshHeaderComponent) headerComponent, success);
                    }
                    if (startDelay < Integer.MAX_VALUE) {
                        if (isBeingDragged || nestedScrollingActive) {
                            long time = System.currentTimeMillis();
                            if (isBeingDragged) {
                                touchY = lastTouchY;
                                touchStartSpinner = 0;
                                isBeingDragged = false;
                                SmartRefreshLayout.super.dispatchTouchEvent(obtain(time, time, MotionEvent.ACTION_DOWN, lastTouchX, lastTouchY + spinnerOffset - touchSlop * 2, 0));
                                SmartRefreshLayout.super.dispatchTouchEvent(obtain(time, time, MotionEvent.ACTION_MOVE, lastTouchX, lastTouchY + spinnerOffset, 0));
                            }
                            if (nestedScrollingActive) {
                                nestedUnconsumed = 0;
                                SmartRefreshLayout.super.dispatchTouchEvent(obtain(time, time, MotionEvent.ACTION_UP, lastTouchX, lastTouchY, 0));
                                nestedScrollingActive = false;
                                touchStartSpinner = 0;
                            }
                        }
                        if (spinnerOffset > 0) {
                            AnimatorUpdateListener updateListener = null;
                            ValueAnimator valueAnimator = animateSpinner(0, startDelay, reboundInterpolator, reboundDuration);
                            if (enableScrollContentWhenRefreshed) {
                                updateListener = contentHandler.createScrollAnimatorOnFinish(spinnerOffset);
                            }
                            if (valueAnimator != null && updateListener != null) {
                                valueAnimator.addUpdateListener(updateListener);
                            }
                        } else if (spinnerOffset < 0) {
                            animateSpinner(0, startDelay, reboundInterpolator, reboundDuration);
                        } else {
                            refreshKernel.updateSpinnerPosition(0, false);
//                            resetStatus();
                            refreshKernel.setRefreshState(RefreshLayoutState.IDLE);
                        }
                    }
                }
            }
        };
        if (delay > 0) {
            mHandler.postDelayed(runnable, delay);
        } else {
            runnable.run();
        }
        return this;
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout completeRefreshWithNoMoreData() {
        long passTime = System.currentTimeMillis() - lastActionTime;
        return completeRefresh((Math.min(Math.max(0, 300 - (int) passTime), 300) << 16), true, Boolean.TRUE);
    }
    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout completeLoadMore(int delayed) {
        return completeLoadMore(delayed, true, false);
    }
    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout completeLoadMore(boolean success) {
        long passTime = System.currentTimeMillis() - lastActionTime;
        return completeLoadMore(success ? (Math.min(Math.max(0, 300 - (int) passTime), 300) << 16) : 0, success, false);
    }
    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout completeLoadMore(final int delayed, final boolean success, final boolean noMoreData) {
        final int more = delayed >> 16;
        int delay = delayed << 16 >> 16;
        Runnable runnable = new Runnable() {
            int count = 0;
            @Override
            public void run() {
                if (count == 0) {
                    if (layoutState == RefreshLayoutState.IDLE && secondaryState == RefreshLayoutState.Loading) {
                        secondaryState = RefreshLayoutState.IDLE;
                    } else if (spinnerAnimator != null && (layoutState.isDraggingState || layoutState == RefreshLayoutState.LoadReleased) && layoutState.isFooterState) {
                        spinnerAnimator.setDuration(0);
                        spinnerAnimator.cancel();
                        spinnerAnimator = null;
                        if (refreshKernel.animateSpinnerTo(0) == null) {
                            dispatchStateChange(RefreshLayoutState.IDLE);
                        } else {
                            dispatchStateChange(RefreshLayoutState.PullUpCanceled);
                        }
                    } else if (layoutState == RefreshLayoutState.Loading && footerComponent != null && contentHandler != null) {
                        count++;
                        mHandler.postDelayed(this, more);
                        dispatchStateChange(RefreshLayoutState.LoadFinish);
                        return;
                    }
                    if (noMoreData) {
                        setNoMoreDataAvailable(true);
                    }
                } else {
                    final int startDelay = footerComponent.onAnimationFinish(SmartRefreshLayout.this, success);
                    if (multiPurposeCallback != null && footerComponent instanceof RefreshFooterComponent) {
                        multiPurposeCallback.footerFinish((RefreshFooterComponent) footerComponent, success);
                    }
                    if (startDelay < Integer.MAX_VALUE) {
                        final boolean needHoldFooter = noMoreData && enableFooterFollowWhenNoMoreData && spinnerOffset < 0 && contentHandler.isLoadMorePossible();
                        final int offset = spinnerOffset - (needHoldFooter ? Math.max(spinnerOffset,-footerHeight) : 0);
                        if (isBeingDragged || nestedScrollingActive) {
                            final long time = System.currentTimeMillis();
                            if (isBeingDragged) {
                                touchY = lastTouchY;
                                touchStartSpinner = spinnerOffset - offset;
                                isBeingDragged = false;
                                int offsetY = enableFooterTranslationContent ? offset : 0;
                                SmartRefreshLayout.super.dispatchTouchEvent(obtain(time, time, MotionEvent.ACTION_DOWN, lastTouchX, lastTouchY + offsetY + touchSlop * 2, 0));
                                SmartRefreshLayout.super.dispatchTouchEvent(obtain(time, time, MotionEvent.ACTION_MOVE, lastTouchX, lastTouchY + offsetY, 0));
                            }
                            if (nestedScrollingActive) {
                                nestedUnconsumed = 0;
                                SmartRefreshLayout.super.dispatchTouchEvent(obtain(time, time, MotionEvent.ACTION_UP, lastTouchX, lastTouchY, 0));
                                nestedScrollingActive = false;
                                touchStartSpinner = 0;
                            }
                        }

                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AnimatorUpdateListener updateListener = null;
                                if (enableScrollContentWhenLoaded && offset < 0) {
                                    updateListener = contentHandler.createScrollAnimatorOnFinish(spinnerOffset);
                                    if (updateListener != null) {
                                        updateListener.onAnimationUpdate(ValueAnimator.ofInt(0, 0));
                                    }
                                }
                                ValueAnimator animator = null;
                                AnimatorListenerAdapter listenerAdapter = new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        if (animation != null && animation.getDuration() == 0) {
                                            return;
                                        }
                                        footerScrollLocked = false;
                                        if (noMoreData) {
                                            setNoMoreDataAvailable(true);
                                        }
                                        if (layoutState == RefreshLayoutState.LoadFinish) {
                                            dispatchStateChange(RefreshLayoutState.IDLE);
                                        }
                                    }
                                };
                                if (spinnerOffset > 0) {
                                    animator = refreshKernel.animateSpinnerTo(0);
                                } else if (updateListener != null || spinnerOffset == 0) {
                                    if (spinnerAnimator != null) {
                                        spinnerAnimator.setDuration(0);
                                        spinnerAnimator.cancel();
                                        spinnerAnimator = null;
                                    }
                                    refreshKernel.updateSpinnerPosition(0, false);
                                    refreshKernel.setRefreshState(RefreshLayoutState.IDLE);
                                } else {
                                    if (noMoreData && enableFooterFollowWhenNoMoreData) {
                                        if (spinnerOffset >= -footerHeight) {
                                            dispatchStateChange(RefreshLayoutState.IDLE);
                                        } else {
                                            animator = refreshKernel.animateSpinnerTo(-footerHeight);
                                        }
                                    } else {
                                        animator = refreshKernel.animateSpinnerTo(0);
                                    }
                                }
                                if (animator != null) {
                                    animator.addListener(listenerAdapter);
                                } else {
                                    listenerAdapter.onAnimationEnd(null);
                                }
                            }
                        }, spinnerOffset < 0 ? startDelay : 0);
                    }
                }
            }
        };
        if (delay > 0) {
            mHandler.postDelayed(runnable, delay);
        } else {
            runnable.run();
        }
        return this;
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout finishLoadMoreWithNoMoreData() {
        long passTime = System.currentTimeMillis() - lastActionTime;
        return completeLoadMore((Math.min(Math.max(0, 300 - (int) passTime), 300) << 16), true, true);
    }

    @Override
    public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout closeHeaderFooter() {
        if (layoutState == RefreshLayoutState.IDLE && (secondaryState == RefreshLayoutState.REFRESHING || secondaryState == RefreshLayoutState.Loading)) {
            secondaryState = RefreshLayoutState.IDLE;
        }
        if (layoutState == RefreshLayoutState.REFRESHING) {
            completeRefresh();
        } else if (layoutState == RefreshLayoutState.Loading) {
            completeLoadMore();
        } else {

            if (refreshKernel.animateSpinnerTo(0) == null) {
                dispatchStateChange(RefreshLayoutState.IDLE);
            } else {
                if (layoutState.isHeaderState) {
                    dispatchStateChange(RefreshLayoutState.PULL_DOWN_CANCELLED);
                } else {
                    dispatchStateChange(RefreshLayoutState.PullUpCanceled);
                }
            }
        }
        return this;
    }

    @Override
    public boolean autoRefresh() {
        return autoRefresh(attachedToWindow ? 0 : 400, reboundDuration, 1f * ((headerMaxDragRatio /2 + 0.5f) * headerHeight) / (headerHeight == 0 ? 1 : headerHeight), false);
    }

    @Override
    @Deprecated
    public boolean autoRefresh(int delayed) {
        return autoRefresh(delayed, reboundDuration, ((headerMaxDragRatio / 2 + 0.5f) * headerHeight) / (headerHeight == 0 ? 1 : headerHeight), false);
    }

    @Override
    public boolean triggerRefreshAnimation() {
        return autoRefresh(attachedToWindow ? 0 : 400, reboundDuration, ((headerMaxDragRatio / 2 + 0.5f) * headerHeight) / (headerHeight == 0 ? 1 : headerHeight), true);
    }

    @Override
    public boolean autoRefresh(int delayed, final int duration, final float dragRate,final boolean animationOnly) {
        if (layoutState == RefreshLayoutState.IDLE && isEnableRefreshOrLoadMore(refreshEnabled)) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (secondaryState != RefreshLayoutState.REFRESHING) return;
                    if (spinnerAnimator != null) {
                        spinnerAnimator.setDuration(0);
                        spinnerAnimator.cancel();
                        spinnerAnimator = null;
                    }

                    final View thisView = SmartRefreshLayout.this;
                    lastTouchX = thisView.getMeasuredWidth() / 2f;
                    refreshKernel.setRefreshState(RefreshLayoutState.PULL_DOWN_TO_REFRESH);

                    spinnerAnimator = ValueAnimator.ofInt(spinnerOffset, (int) (headerHeight * dragRate));
                    spinnerAnimator.setDuration(duration);
                    spinnerAnimator.setInterpolator(new SmartViewUtil(SmartViewUtil.INTERPOLATOR_FLUID));
                    spinnerAnimator.addUpdateListener(new AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            if (spinnerAnimator != null && headerComponent != null) {
                                refreshKernel.updateSpinnerPosition((int) animation.getAnimatedValue(), true);
                            }
                        }
                    });
                    spinnerAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (animation != null && animation.getDuration() == 0) {
                                return;
                            }
                            spinnerAnimator = null;
                            if (headerComponent != null) {
                                if (layoutState != RefreshLayoutState.RELEASE_TO_REFRESH) {
                                    refreshKernel.setRefreshState(RefreshLayoutState.RELEASE_TO_REFRESH);
                                }
                                startRefreshingState(!animationOnly);
                            } else {
                                refreshKernel.setRefreshState(RefreshLayoutState.IDLE);
                            }
                        }
                    });
                    spinnerAnimator.start();
                }
            };
            updateSecondaryState(RefreshLayoutState.REFRESHING);
            if (delayed > 0) {
                mHandler.postDelayed(runnable, delayed);
            } else {
                runnable.run();
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean triggerAutoLoadMore() {
        return triggerAutoLoadMore(0, reboundDuration, 1f * (footerHeight * (footerMaxDragRatio / 2 + 0.5f)) / (footerHeight == 0 ? 1 : footerHeight), false);
    }

    @Override
    public boolean triggerLoadMoreAnimation() {
        return triggerAutoLoadMore(0, reboundDuration, 1f * (footerHeight * (footerMaxDragRatio / 2 + 0.5f)) / (footerHeight == 0 ? 1 : footerHeight), true);
    }

    @Override
    public boolean triggerAutoLoadMore(int delayed, final int duration, final float dragRate, final boolean animationOnly) {
        if (layoutState == RefreshLayoutState.IDLE && (isEnableRefreshOrLoadMore(loadMoreEnabled) && !footerNoMoreData)) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (secondaryState != RefreshLayoutState.Loading)return;
                    if (spinnerAnimator != null) {
                        spinnerAnimator.setDuration(0);
                        spinnerAnimator.cancel();
                        spinnerAnimator = null;
                    }

                    final View thisView = SmartRefreshLayout.this;
                    lastTouchX = thisView.getMeasuredWidth() / 2f;
                    refreshKernel.setRefreshState(RefreshLayoutState.PullUpToLoad);

                    spinnerAnimator = ValueAnimator.ofInt(spinnerOffset, -(int) (footerHeight * dragRate));
                    spinnerAnimator.setDuration(duration);
                    spinnerAnimator.setInterpolator(new SmartViewUtil(SmartViewUtil.INTERPOLATOR_FLUID));
                    spinnerAnimator.addUpdateListener(new AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            if (spinnerAnimator != null && footerComponent != null) {
                                refreshKernel.updateSpinnerPosition((int) animation.getAnimatedValue(), true);
                            }
                        }
                    });
                    spinnerAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (animation != null && animation.getDuration() == 0) {
                                return;
                            }
                            spinnerAnimator = null;
                            if (footerComponent != null) {
                                if (layoutState != RefreshLayoutState.ReleaseToLoad) {
                                    refreshKernel.setRefreshState(RefreshLayoutState.ReleaseToLoad);
                                }
                                startLoadingState(!animationOnly);
                            } else {
                                refreshKernel.setRefreshState(RefreshLayoutState.IDLE);
                            }
                        }
                    });
                    spinnerAnimator.start();
                }
            };
            updateSecondaryState(RefreshLayoutState.Loading);
            if (delayed > 0) {
                mHandler.postDelayed(runnable, delayed);
            } else {
                runnable.run();
            }
            return true;
        } else {
            return false;
        }
    }

    public static void setDefaultRefreshHeaderCreator(@NonNull DefaultRefreshHeaderCreatorFactory creator) {
        sHeaderCreator = creator;
    }

    public static void setDefaultRefreshFooterCreator(@NonNull RefreshFooterCreatorFactory creator) {
        sFooterCreator = creator;
    }

    public static void setDefaultRefreshInitializer(@NonNull RefreshLayoutInitializer initializer) {
        sRefreshInitializer = initializer;
    }

    public class RefreshKernelManager implements RefreshManager {

        @NonNull
        @Override
        public docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout getLayoutRefresh() {
            return SmartRefreshLayout.this;
        }

        @NonNull
        @Override
        public RefreshContentHandler getContentRefresh() {
            return contentHandler;
        }
        @Override
        public RefreshManager setRefreshState(@NonNull RefreshLayoutState state) {
            switch (state) {
                case IDLE:
                    if (layoutState != RefreshLayoutState.IDLE && spinnerOffset == 0) {
                        dispatchStateChange(RefreshLayoutState.IDLE);
                    } else if (spinnerOffset != 0) {
                        animateSpinnerTo(0);
                    }
                    break;
                case PULL_DOWN_TO_REFRESH:
                    if (!layoutState.isOpeningState && isEnableRefreshOrLoadMore(refreshEnabled)) {
                        dispatchStateChange(RefreshLayoutState.PULL_DOWN_TO_REFRESH);
                    } else {
                        updateSecondaryState(RefreshLayoutState.PULL_DOWN_TO_REFRESH);
                    }
                    break;
                case PullUpToLoad:
                    if (isEnableRefreshOrLoadMore(loadMoreEnabled) && !layoutState.isOpeningState && !layoutState.isFinishingState && !(footerNoMoreData && enableFooterFollowWhenNoMoreData && footerNoMoreDataEffective)) {
                        dispatchStateChange(RefreshLayoutState.PullUpToLoad);
                    } else {
                        updateSecondaryState(RefreshLayoutState.PullUpToLoad);
                    }
                    break;
                case PULL_DOWN_CANCELLED:
                    if (!layoutState.isOpeningState && isEnableRefreshOrLoadMore(refreshEnabled)) {
                        dispatchStateChange(RefreshLayoutState.PULL_DOWN_CANCELLED);
                        setRefreshState(RefreshLayoutState.IDLE);
                    } else {
                        updateSecondaryState(RefreshLayoutState.PULL_DOWN_CANCELLED);
                    }
                    break;
                case PullUpCanceled:
                    if (isEnableRefreshOrLoadMore(loadMoreEnabled) && !layoutState.isOpeningState && !(footerNoMoreData && enableFooterFollowWhenNoMoreData && footerNoMoreDataEffective)) {
                        dispatchStateChange(RefreshLayoutState.PullUpCanceled);
                        setRefreshState(RefreshLayoutState.IDLE);
                    } else {
                        updateSecondaryState(RefreshLayoutState.PullUpCanceled);
                    }
                    break;
                case RELEASE_TO_REFRESH:
                    if (!layoutState.isOpeningState && isEnableRefreshOrLoadMore(refreshEnabled)) {
                        dispatchStateChange(RefreshLayoutState.RELEASE_TO_REFRESH);
                    } else {
                        updateSecondaryState(RefreshLayoutState.RELEASE_TO_REFRESH);
                    }
                    break;
                case ReleaseToLoad:
                    if (isEnableRefreshOrLoadMore(loadMoreEnabled) && !layoutState.isOpeningState && !layoutState.isFinishingState && !(footerNoMoreData && enableFooterFollowWhenNoMoreData && footerNoMoreDataEffective)) {
                        dispatchStateChange(RefreshLayoutState.ReleaseToLoad);
                    } else {
                        updateSecondaryState(RefreshLayoutState.ReleaseToLoad);
                    }
                    break;
                case RELEASE_TO_TWO_LEVEL: {
                    if (!layoutState.isOpeningState && isEnableRefreshOrLoadMore(refreshEnabled)) {
                        dispatchStateChange(RefreshLayoutState.RELEASE_TO_TWO_LEVEL);
                    } else {
                        updateSecondaryState(RefreshLayoutState.RELEASE_TO_TWO_LEVEL);
                    }
                    break;
                }
                case REFRESH_RELEASED: {
                    if (!layoutState.isOpeningState && isEnableRefreshOrLoadMore(refreshEnabled)) {
                        dispatchStateChange(RefreshLayoutState.REFRESH_RELEASED);
                    } else {
                        updateSecondaryState(RefreshLayoutState.REFRESH_RELEASED);
                    }
                    break;
                }
                case LoadReleased: {
                    if (!layoutState.isOpeningState && isEnableRefreshOrLoadMore(loadMoreEnabled)) {
                        dispatchStateChange(RefreshLayoutState.LoadReleased);
                    } else {
                        updateSecondaryState(RefreshLayoutState.LoadReleased);
                    }
                    break;
                }
                case REFRESHING:
                    startRefreshingState(true);
                    break;
                case Loading:
                    startLoadingState(true);
                    break;
                default:
                    dispatchStateChange(state);
                    break;
            }
            return null;
        }

        @Override
        public RefreshManager openTwoLevel(boolean open) {
            if (open) {
                AnimatorListenerAdapter listener = new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (animation != null && animation.getDuration() == 0) {
                            return;
                        }
                        refreshKernel.setRefreshState(RefreshLayoutState.TwoLevel);
                    }
                };
                final View thisView = SmartRefreshLayout.this;
                final int height = thisView.getMeasuredHeight();
                final int floorHeight = floorOpenLayoutRate > 1 ? (int) floorOpenLayoutRate : (int)(height* floorOpenLayoutRate);
                ValueAnimator animator = animateSpinnerTo(floorHeight);
                if (animator != null && animator == spinnerAnimator) {
                    animator.setDuration(floorDuration);
                    animator.addListener(listener);
                } else {
                    listener.onAnimationEnd(null);
                }
            } else {
                if (animateSpinnerTo(0) == null) {
                    dispatchStateChange(RefreshLayoutState.IDLE);
                }
            }
            return this;
        }

        @Override
        public RefreshManager closeTwoLevel() {
            if (layoutState == RefreshLayoutState.TwoLevel) {
                refreshKernel.setRefreshState(RefreshLayoutState.TwoLevelFinish);
                if (spinnerOffset == 0) {
                    updateSpinnerPosition(0, false);
                    dispatchStateChange(RefreshLayoutState.IDLE);
                } else {
                    animateSpinnerTo(0).setDuration(floorDuration);
                }
            }
            return this;
        }

        public RefreshManager updateSpinnerPosition(final int spinner, final boolean isDragging) {
            if (spinnerOffset == spinner
                    && (headerComponent == null || !headerComponent.isHorizontalDragSupported())
                    && (footerComponent == null || !footerComponent.isHorizontalDragSupported())) {
                return this;
            }
            final View thisView = SmartRefreshLayout.this;
            final int oldSpinner = spinnerOffset;
            spinnerOffset = spinner;
            // 附加 mViceState.isDragging 的判断，是因为 isDragging 有时候时动画模拟的，如 autoRefresh 动画
            //
            if (isDragging && (secondaryState.isDraggingState || secondaryState.isOpeningState)) {
                if (spinnerOffset > headerHeight * headerTriggerRatio) {
                    if (layoutState != RefreshLayoutState.RELEASE_TO_TWO_LEVEL) {
                        refreshKernel.setRefreshState(RefreshLayoutState.RELEASE_TO_REFRESH);
                    }
                } else if (-spinnerOffset > footerHeight * footerTriggerRatio && !footerNoMoreData) {
                    refreshKernel.setRefreshState(RefreshLayoutState.ReleaseToLoad);
                } else if (spinnerOffset < 0 && !footerNoMoreData) {
                    refreshKernel.setRefreshState(RefreshLayoutState.PullUpToLoad);
                } else if (spinnerOffset > 0) {
                    refreshKernel.setRefreshState(RefreshLayoutState.PULL_DOWN_TO_REFRESH);
                }
            }
            if (contentHandler != null) {
                int tSpinner = 0;
                boolean changed = false;
                if (spinner >= 0 && headerComponent != null) {
                    if (isEnableTranslationContent(enableHeaderTranslationContent, headerComponent)) {
                        changed = true;
                        tSpinner = spinner;
                    } else if (oldSpinner < 0) {
                        changed = true;
                        tSpinner = 0;
                    }
                }
                if (spinner <= 0 && footerComponent != null) {
                    if (isEnableTranslationContent(enableFooterTranslationContent, footerComponent)) {
                        changed = true;
                        tSpinner = spinner;
                    } else if (oldSpinner > 0) {
                        changed = true;
                        tSpinner = 0;
                    }
                }
                if (changed) {
                    contentHandler.updateSpinnerPosition(tSpinner, headerTranslationViewId, footerTranslationViewId);
                    if (footerNoMoreData && footerNoMoreDataEffective && enableFooterFollowWhenNoMoreData
                            && footerComponent instanceof RefreshFooterComponent && footerComponent.getSpinnerBehavior() == RefreshSpinnerStyle.TRANSLATE
                            && isEnableRefreshOrLoadMore(loadMoreEnabled)) {
                        footerComponent.getComponentView().setTranslationY(Math.max(0, tSpinner));
                    }
                    boolean header = enableClipHeaderWhenFixedBehind && headerComponent != null && headerComponent.getSpinnerBehavior() == RefreshSpinnerStyle.FIXED_BEHIND;
                    header = header || headerBackgroundColor != 0;
                    boolean footer = enableClipFooterWhenFixedBehind && footerComponent != null && footerComponent.getSpinnerBehavior() == RefreshSpinnerStyle.FIXED_BEHIND;
                    footer = footer || footerBackgroundColor != 0;
                    if ((header && (tSpinner >= 0 || oldSpinner > 0)) || (footer && (tSpinner <= 0 || oldSpinner < 0))) {
                        thisView.invalidate();
                    }
                }
            }
            if ((spinner >= 0 || oldSpinner > 0) && headerComponent != null) {

                final int offset = Math.max(spinner, 0);
                final int headerHeight = SmartRefreshLayout.this.headerHeight;
                final int maxDragHeight = (int) (SmartRefreshLayout.this.headerHeight * headerMaxDragRatio);
                final float percent = 1f * offset / (SmartRefreshLayout.this.headerHeight == 0 ? 1 : SmartRefreshLayout.this.headerHeight);
                if (isEnableRefreshOrLoadMore(refreshEnabled) || (layoutState == RefreshLayoutState.REFRESH_FINISHED && !isDragging)) {
                    if (oldSpinner != spinnerOffset) {
                        if (headerComponent.getSpinnerBehavior() == RefreshSpinnerStyle.TRANSLATE) {
                            headerComponent.getComponentView().setTranslationY(spinnerOffset);
                            if (headerBackgroundColor != 0 && mPaint != null && !isEnableTranslationContent(enableHeaderTranslationContent, headerComponent)) {
                                thisView.invalidate();
                            }
                        } else if (headerComponent.getSpinnerBehavior().scale){
                            View headerView = headerComponent.getComponentView();
                            final ViewGroup.LayoutParams lp = headerView.getLayoutParams();
                            final MarginLayoutParams mlp = lp instanceof MarginLayoutParams ? (MarginLayoutParams)lp : sDefaultMarginLP;
                            final int widthSpec = makeMeasureSpec(headerView.getMeasuredWidth(), EXACTLY);
                            headerView.measure(widthSpec, makeMeasureSpec(Math.max(spinnerOffset - mlp.bottomMargin - mlp.topMargin, 0), EXACTLY));
                            final int left = mlp.leftMargin;
                            final int top = mlp.topMargin + headerInsetStart;
                            headerView.layout(left, top, left + headerView.getMeasuredWidth(), top + headerView.getMeasuredHeight());
                        }
                        headerComponent.onDragging(isDragging, percent, offset, headerHeight, maxDragHeight);
                    }
                    if (isDragging && headerComponent.isHorizontalDragSupported()) {
                        final int offsetX = (int) lastTouchX;
                        final int offsetMax = thisView.getWidth();
                        final float percentX = lastTouchX / (offsetMax == 0 ? 1 : offsetMax);
                        headerComponent.onHorizontalDragging(percentX, offsetX, offsetMax);
                    }
                }

                if (oldSpinner != spinnerOffset && multiPurposeCallback != null && headerComponent instanceof RefreshHeaderComponent) {
                    multiPurposeCallback.headerMoving((RefreshHeaderComponent) headerComponent, isDragging, percent, offset, headerHeight, maxDragHeight);
                }

            }
            if ((spinner <= 0 || oldSpinner < 0) && footerComponent != null) {

                final int offset = -Math.min(spinner, 0);
                final int footerHeight = SmartRefreshLayout.this.footerHeight;
                final int maxDragHeight = (int) (SmartRefreshLayout.this.footerHeight * footerMaxDragRatio);
                final float percent = offset * 1f / (SmartRefreshLayout.this.footerHeight == 0 ? 1 : SmartRefreshLayout.this.footerHeight);

                if (isEnableRefreshOrLoadMore(loadMoreEnabled) || (layoutState == RefreshLayoutState.LoadFinish && !isDragging)) {
                    if (oldSpinner != spinnerOffset) {
                        if (footerComponent.getSpinnerBehavior() == RefreshSpinnerStyle.TRANSLATE) {
                            footerComponent.getComponentView().setTranslationY(spinnerOffset);
                            if (footerBackgroundColor != 0 && mPaint != null && !isEnableTranslationContent(enableFooterTranslationContent, footerComponent)) {
                                thisView.invalidate();
                            }
                        } else if (footerComponent.getSpinnerBehavior().scale){
                            View footerView = footerComponent.getComponentView();
                            final ViewGroup.LayoutParams lp = footerView.getLayoutParams();
                            final MarginLayoutParams mlp = lp instanceof MarginLayoutParams ? (MarginLayoutParams)lp : sDefaultMarginLP;
                            final int widthSpec = makeMeasureSpec(footerView.getMeasuredWidth(), EXACTLY);
                            footerView.measure(widthSpec, makeMeasureSpec(Math.max(-spinnerOffset - mlp.bottomMargin - mlp.topMargin, 0), EXACTLY));
                            final int left = mlp.leftMargin;
                            final int bottom = mlp.topMargin + thisView.getMeasuredHeight() - footerInsetStart;
                            footerView.layout(left, bottom - footerView.getMeasuredHeight(), left + footerView.getMeasuredWidth(), bottom);
                        }
                        footerComponent.onDragging(isDragging, percent, offset, footerHeight, maxDragHeight);
                    }
                    if (isDragging && footerComponent.isHorizontalDragSupported()) {
                        final int offsetX = (int) lastTouchX;
                        final int offsetMax = thisView.getWidth();
                        final float percentX = lastTouchX / (offsetMax == 0 ? 1 : offsetMax);
                        footerComponent.onHorizontalDragging(percentX, offsetX, offsetMax);
                    }
                }

                if (oldSpinner != spinnerOffset && multiPurposeCallback != null && footerComponent instanceof RefreshFooterComponent) {
                    multiPurposeCallback.footerMoving((RefreshFooterComponent) footerComponent, isDragging, percent, offset, footerHeight, maxDragHeight);
                }
            }
            return this;
        }

        public ValueAnimator animateSpinnerTo(int endSpinner) {
            return SmartRefreshLayout.this.animateSpinner(endSpinner, 0, reboundInterpolator, reboundDuration);
        }

        @Override
        public RefreshManager requestDrawBackground(@NonNull RefreshComponent internal, int backgroundColor) {
            if (mPaint == null && backgroundColor != 0) {
                mPaint = new Paint();
            }
            if (internal.equals(headerComponent)) {
                headerBackgroundColor = backgroundColor;
            } else if (internal.equals(footerComponent)) {
                footerBackgroundColor = backgroundColor;
            }
            return this;
        }

        @Override
        public RefreshManager requestNeedTouchEvent(@NonNull RefreshComponent internal, boolean request) {
            if (internal.equals(headerComponent)) {
                headerNeedTouchEventWhenRefreshing = request;
            } else if (internal.equals(footerComponent)) {
                footerNeedTouchEventWhenLoading = request;
            }
            return this;
        }

        @Override
        public RefreshManager requestDefaultTranslationContent(@NonNull RefreshComponent internal, boolean translation) {
            if (internal.equals(headerComponent)) {
                if (!manualHeaderTranslationContent) {
                    manualHeaderTranslationContent = true;
                    enableHeaderTranslationContent = translation;
                }
            } else if (internal.equals(footerComponent)) {
                if (!manualFooterTranslationContent) {
                    manualFooterTranslationContent = true;
                    enableFooterTranslationContent = translation;
                }
            }
            return this;
        }
        @Override
        public RefreshManager requestRemeasureHeight(@NonNull RefreshComponent internal) {
            if (internal.equals(headerComponent)) {
                if (headerHeightStatus.isNotified) {
                    headerHeightStatus = headerHeightStatus.toUnnotified();
                }
            } else if (internal.equals(footerComponent)) {
                if (footerHeightStatus.isNotified) {
                    footerHeightStatus = footerHeightStatus.toUnnotified();
                }
            }
            return this;
        }
        @Override
        public RefreshManager setTwoLevelParameters(int duration, float openLayoutRate, float dragLayoutRate) {
            floorDuration = duration;
            floorOpenLayoutRate = openLayoutRate;
            floorBottomDragLayoutRate = dragLayoutRate;
            return this;
        }
    }
}
