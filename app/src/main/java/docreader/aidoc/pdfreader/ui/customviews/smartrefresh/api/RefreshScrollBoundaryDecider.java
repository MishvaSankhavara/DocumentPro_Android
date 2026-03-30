package docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api;

import android.view.View;

public interface RefreshScrollBoundaryDecider {

    boolean canTriggerRefresh(View content);

    boolean canTriggerLoadMore(View content);
}
