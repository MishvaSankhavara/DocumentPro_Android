package docreader.aidoc.pdfreader.ui.customviews.smartrefresh.header;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import docreader.aidoc.pdfreader.R;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.TwoLevelRefreshListener;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.RefreshHeaderComponent;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.RefreshComponent;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.RefreshManager;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.constant.RefreshLayoutState;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.constant.RefreshSpinnerStyle;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.internal.RefreshInternalAbstract;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.listener.StateChangedListener;


@SuppressWarnings({"unused", "UnusedReturnValue"})
public class TwoLevelRefreshHeader extends RefreshInternalAbstract implements RefreshHeaderComponent/*, NestedScrollingParent*/ {

    protected int spinner;
    protected float percent = 0;
    protected float maxRate = 2.5f;
    protected float floorRate = 1.9f;
    protected float refreshRate = 1f;
    protected boolean enableTwoLevel = true;
    protected boolean enablePullToCloseTwoLevel = true;
    protected boolean enableFloorRefresh = true;
    protected int headerHeight;
    protected int floorDuration = 1000;
    protected float floorOpenLayoutRate = 1f;
    protected float floorBottomDragLayoutRate = 1f/6;
    protected RefreshComponent refreshHeader;
    protected RefreshManager refreshKernel;
    protected TwoLevelRefreshListener twoLevelListener;

    public TwoLevelRefreshHeader(Context context) {
        this(context, null);
    }

    public TwoLevelRefreshHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);

        spinnerStyle = RefreshSpinnerStyle.FIXED_BEHIND;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TwoLevelHeader);
        maxRate = ta.getFloat(R.styleable.TwoLevelHeader_srlMaxRage, maxRate);
        floorRate = ta.getFloat(R.styleable.TwoLevelHeader_srlFloorRage, floorRate);
        refreshRate = ta.getFloat(R.styleable.TwoLevelHeader_srlRefreshRage, refreshRate);
        maxRate = ta.getFloat(R.styleable.TwoLevelHeader_srlMaxRate, maxRate);
        floorRate = ta.getFloat(R.styleable.TwoLevelHeader_srlFloorRate, floorRate);
        refreshRate = ta.getFloat(R.styleable.TwoLevelHeader_srlRefreshRate, refreshRate);
        floorDuration = ta.getInt(R.styleable.TwoLevelHeader_srlFloorDuration, floorDuration);
        enableTwoLevel = ta.getBoolean(R.styleable.TwoLevelHeader_srlEnableTwoLevel, enableTwoLevel);
        enableFloorRefresh = ta.getBoolean(R.styleable.TwoLevelHeader_srlEnableFloorRefresh, enableFloorRefresh);
        floorOpenLayoutRate = ta.getFloat(R.styleable.TwoLevelHeader_srlFloorOpenLayoutRate, floorOpenLayoutRate);
        floorBottomDragLayoutRate = ta.getFloat(R.styleable.TwoLevelHeader_srlFloorBottomDragLayoutRate, floorBottomDragLayoutRate);
        enablePullToCloseTwoLevel = ta.getBoolean(R.styleable.TwoLevelHeader_srlEnablePullToCloseTwoLevel, enablePullToCloseTwoLevel);
        ta.recycle();
    }
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        final ViewGroup thisGroup = this;
        for (int i = 0, len = thisGroup.getChildCount(); i < len; i++) {
            View childAt = thisGroup.getChildAt(i);
            if (childAt instanceof RefreshHeaderComponent) {
                refreshHeader = (RefreshHeaderComponent) childAt;
                wrappedInternal = (RefreshComponent) childAt;
                thisGroup.bringChildToFront(childAt);
                break;
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        spinnerStyle = RefreshSpinnerStyle.FIXED_BEHIND;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        spinnerStyle = RefreshSpinnerStyle.MATCH_LAYOUT;
        if (refreshHeader == null) {
            final View thisView = this;
            setHeaderComponent(new ClassicRefreshHeaderView(thisView.getContext()));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final RefreshComponent refreshHeader = this.refreshHeader;
        if (refreshHeader != null) {
            int mode = MeasureSpec.getMode(heightMeasureSpec);
            if (mode == MeasureSpec.AT_MOST) {
                refreshHeader.getComponentView().measure(widthMeasureSpec, heightMeasureSpec);
                int height = refreshHeader.getComponentView().getMeasuredHeight();
                super.setMeasuredDimension(View.resolveSize(super.getSuggestedMinimumWidth(), widthMeasureSpec), height);
            } else {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    public void onComponentInitialized(@NonNull RefreshManager kernel, int height, int maxDragHeight) {
        final View thisView = this;
        final RefreshComponent refreshHeader = this.refreshHeader;
        if (refreshHeader == null) {
            return;
        }
        if (1f * (maxDragHeight + height) / height != maxRate && headerHeight == 0) {
            headerHeight = height;
            this.refreshHeader = null;
            kernel.getLayoutRefresh().setHeaderMaxDragRate(maxRate);
            this.refreshHeader = refreshHeader;
        }
        if (refreshKernel == null //第一次初始化
                && refreshHeader.getSpinnerBehavior() == RefreshSpinnerStyle.TRANSLATE
                && !thisView.isInEditMode()) {
            MarginLayoutParams params = (MarginLayoutParams) refreshHeader.getComponentView().getLayoutParams();
            params.topMargin -= height;
            refreshHeader.getComponentView().setLayoutParams(params);
        }

        headerHeight = height;
        refreshKernel = kernel;
        kernel.setTwoLevelParameters(floorDuration, floorOpenLayoutRate, floorBottomDragLayoutRate);
        kernel.requestNeedTouchEvent(this, !enablePullToCloseTwoLevel);
        refreshHeader.onComponentInitialized(kernel, height, maxDragHeight);

    }

    @Override
    public boolean equals(Object obj) {
        final Object header = refreshHeader;
        return (header != null && header.equals(obj)) || super.equals(obj);
    }

    @Override
    public void stateChanged(@NonNull SmartRefreshLayout refreshLayout, @NonNull RefreshLayoutState oldState, @NonNull RefreshLayoutState newState) {
        final RefreshComponent refreshHeader = this.refreshHeader;
        if (refreshHeader != null) {
            final StateChangedListener listener = this.refreshHeader;
            if (newState == RefreshLayoutState.RELEASE_TO_REFRESH && !enableFloorRefresh) {
                newState = RefreshLayoutState.PULL_DOWN_TO_REFRESH;
            }
            listener.stateChanged(refreshLayout, oldState, newState);
            switch (newState) {
                case TwoLevelReleased:
                    if (refreshHeader.getComponentView() != this) {
                        refreshHeader.getComponentView().animate().alpha(0).setDuration(floorDuration / 2);
                    }
                    final RefreshManager refreshKernel = this.refreshKernel;
                    if (refreshKernel != null) {
                        final TwoLevelRefreshListener twoLevelListener = this.twoLevelListener;
                        refreshKernel.openTwoLevel(twoLevelListener == null || twoLevelListener.onTwoLevelRefresh(refreshLayout));
                    }
                    break;
                case TwoLevel:
                    break;
                case TwoLevelFinish:
                    if (refreshHeader.getComponentView() != this) {
                        refreshHeader.getComponentView().animate().alpha(1).setDuration(floorDuration / 2);
                    }
                    break;
                case PULL_DOWN_TO_REFRESH:
                    if (refreshHeader.getComponentView().getAlpha() == 0 && refreshHeader.getComponentView() != this) {
                        refreshHeader.getComponentView().setAlpha(1);
                    }
                    break;
            }
        }
    }

    @Override
    public void onDragging(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
        updateSpinnerPosition(offset);
        final RefreshComponent refreshHeader = this.refreshHeader;
        final RefreshManager refreshKernel = this.refreshKernel;
        if (refreshHeader != null) {
            refreshHeader.onDragging(isDragging, percent, offset, height, maxDragHeight);
        }
        if (isDragging) {
            if (this.percent < floorRate && percent >= floorRate && enableTwoLevel) {
                refreshKernel.setRefreshState(RefreshLayoutState.RELEASE_TO_TWO_LEVEL);
            } else if (this.percent >= floorRate && percent < refreshRate) {
                refreshKernel.setRefreshState(RefreshLayoutState.PULL_DOWN_TO_REFRESH);
            } else if (this.percent >= floorRate && percent < floorRate && enableFloorRefresh) {
                refreshKernel.setRefreshState(RefreshLayoutState.RELEASE_TO_REFRESH);
            } else if (!enableFloorRefresh && refreshKernel.getLayoutRefresh().getRefreshState() != RefreshLayoutState.RELEASE_TO_TWO_LEVEL) {
                refreshKernel.setRefreshState(RefreshLayoutState.PULL_DOWN_TO_REFRESH);
            }
            this.percent = percent;
        }
    }

    protected void updateSpinnerPosition(int spinner) {
        final RefreshComponent refreshHeader = this.refreshHeader;
        if (this.spinner != spinner && refreshHeader != null) {
            this.spinner = spinner;
            RefreshSpinnerStyle style = refreshHeader.getSpinnerBehavior();
            if (style == RefreshSpinnerStyle.TRANSLATE) {
                refreshHeader.getComponentView().setTranslationY(spinner);
            } else if (style.scale) {
                View view = refreshHeader.getComponentView();
                view.layout(view.getLeft(), view.getTop(), view.getRight(), view.getTop() + Math.max(0, spinner));
            }
        }
    }

    public TwoLevelRefreshHeader setHeaderComponent(RefreshHeaderComponent header) {
        return setRefreshHeader(header, MATCH_PARENT, WRAP_CONTENT);
    }

    public TwoLevelRefreshHeader setRefreshHeader(RefreshHeaderComponent header, int width, int height) {
        final ViewGroup thisGroup = this;
        if (header != null) {
            RefreshComponent refreshHeader = this.refreshHeader;
            if (refreshHeader != null) {
                thisGroup.removeView(refreshHeader.getComponentView());
            }
            refreshHeader = header;
            width = width == 0 ? MATCH_PARENT : width;
            height = height == 0 ? WRAP_CONTENT : height;
            LayoutParams lp = new LayoutParams(width, height);
            Object olp = refreshHeader.getComponentView().getLayoutParams();
            if (olp instanceof LayoutParams) {
                lp = ((LayoutParams) olp);
            }
            if (refreshHeader.getSpinnerBehavior() == RefreshSpinnerStyle.FIXED_BEHIND) {
                thisGroup.addView(refreshHeader.getComponentView(), 0, lp);
            } else {
                thisGroup.addView(refreshHeader.getComponentView(), thisGroup.getChildCount(), lp);
            }
            this.refreshHeader = header;
            this.wrappedInternal = header;
        }
        return this;
    }

    public TwoLevelRefreshHeader setMaxDragRate(float rate) {
        if (this.maxRate != rate) {
            this.maxRate = rate;
            final RefreshManager refreshKernel = this.refreshKernel;
            if (refreshKernel != null) {
                this.headerHeight = 0;
                refreshKernel.getLayoutRefresh().setHeaderMaxDragRate(maxRate);
            }
        }
        return this;
    }

    public TwoLevelRefreshHeader enablePullToCloseTwoLevel(boolean enabled) {
        final RefreshManager refreshKernel = this.refreshKernel;
        this.enablePullToCloseTwoLevel = enabled;
        if (refreshKernel != null) {
            refreshKernel.requestNeedTouchEvent(this, !enabled);
        }
        return this;
    }

    public TwoLevelRefreshHeader setRefreshTriggerRate(float rate) {
        this.refreshRate = rate;
        return this;
    }

    public TwoLevelRefreshHeader setSecondLevelTriggerRate(float rate) {
        this.floorRate = rate;
        return this;
    }

    public TwoLevelRefreshHeader enableTwoLevel(boolean enabled) {
        this.enableTwoLevel = enabled;
        return this;
    }

    public TwoLevelRefreshHeader setTwoLevelListener(TwoLevelRefreshListener listener) {
        this.twoLevelListener = listener;
        return this;
    }

    public TwoLevelRefreshHeader setSecondLevelDuration(int duration) {
        this.floorDuration = duration;
        return this;
    }

    public TwoLevelRefreshHeader openSecondLevel(boolean widthOnTwoLevelListener) {
        final RefreshManager refreshKernel = this.refreshKernel;
        if (refreshKernel != null) {
            final TwoLevelRefreshListener twoLevelListener = this.twoLevelListener;
            refreshKernel.openTwoLevel(!widthOnTwoLevelListener || twoLevelListener == null || twoLevelListener.onTwoLevelRefresh(refreshKernel.getLayoutRefresh()));
        }
        return this;
    }

    public TwoLevelRefreshHeader closeSecondLevel() {
        final RefreshManager refreshKernel = this.refreshKernel;
        if (refreshKernel != null) {
            refreshKernel.closeTwoLevel();
        }
        return this;
    }
}