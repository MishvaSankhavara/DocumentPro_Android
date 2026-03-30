package docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api;


import androidx.annotation.NonNull;

public interface TwoLevelRefreshListener {
    boolean onTwoLevelRefresh(@NonNull SmartRefreshLayout refreshLayout);
}