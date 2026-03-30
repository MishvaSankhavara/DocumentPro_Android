package docreader.aidoc.pdfreader.ui.customviews.smartrefresh.impl;

import android.graphics.PointF;
import android.view.View;

import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.RefreshScrollBoundaryDecider;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.util.SmartViewUtil;

public class RefreshScrollBoundaryAdapter implements RefreshScrollBoundaryDecider {

    public PointF actionEvent;
    public boolean loadMoreWhenContentNotFullEnabled = true;
    public RefreshScrollBoundaryDecider customBoundaryDecider;

    @Override
    public boolean canTriggerLoadMore(View content) {
        if (customBoundaryDecider != null) {
            return customBoundaryDecider.canTriggerLoadMore(content);
        }
        return SmartViewUtil.canTriggerLoadMore(content, actionEvent, loadMoreWhenContentNotFullEnabled);
    }

    @Override
    public boolean canTriggerRefresh(View content) {
        if (customBoundaryDecider != null) {
            return customBoundaryDecider.canTriggerRefresh(content);
        }
        return SmartViewUtil.canTriggerRefresh(content, actionEvent);
    }
}
