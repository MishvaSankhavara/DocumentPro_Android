package docreader.aidoc.pdfreader.ui.customviews.smartrefresh.listener;


import androidx.annotation.NonNull;

import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.RefreshFooterComponent;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.RefreshHeaderComponent;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.constant.RefreshLayoutState;

/**
 * 多功能监听器
 * Created by scwang on 2017/5/26.
 */
public class SimpleMultiPurposeListener implements MultiPurposeListener {

    @Override
    public void headerMoving(RefreshHeaderComponent header, boolean isDragging, float percent, int offset, int headerHeight, int maxDragHeight) {

    }

    @Override
    public void headerReleased(RefreshHeaderComponent header, int headerHeight, int maxDragHeight) {

    }

    @Override
    public void headerAnimationStart(RefreshHeaderComponent header, int footerHeight, int maxDragHeight) {

    }

    @Override
    public void headerFinish(RefreshHeaderComponent header, boolean success) {

    }

    @Override
    public void footerMoving(RefreshFooterComponent footer, boolean isDragging, float percent, int offset, int footerHeight, int maxDragHeight) {

    }

    @Override
    public void footerReleased(RefreshFooterComponent footer, int footerHeight, int maxDragHeight) {

    }

    @Override
    public void footerAnimationStart(RefreshFooterComponent footer, int headerHeight, int maxDragHeight) {

    }

    @Override
    public void footerFinish(RefreshFooterComponent footer, boolean success) {

    }

    @Override
    public void refresh(@NonNull SmartRefreshLayout refreshLayout) {

    }

    @Override
    public void onLoadMore(@NonNull SmartRefreshLayout refreshLayout) {

    }

    @Override
    public void stateChanged(@NonNull SmartRefreshLayout refreshLayout, @NonNull RefreshLayoutState oldState, @NonNull RefreshLayoutState newState) {

    }

}
