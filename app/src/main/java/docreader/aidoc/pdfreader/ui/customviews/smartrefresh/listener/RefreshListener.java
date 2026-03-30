package docreader.aidoc.pdfreader.ui.customviews.smartrefresh.listener;

import androidx.annotation.NonNull;

import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout;

public interface RefreshListener {
    void refresh(@NonNull SmartRefreshLayout refreshLayout);
}
