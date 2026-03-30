package docreader.aidoc.pdfreader.ui.customviews.smartrefresh.listener;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;
import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP;
import static androidx.annotation.RestrictTo.Scope.SUBCLASSES;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.SmartRefreshLayout;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.constant.RefreshLayoutState;

public interface StateChangedListener {
    @RestrictTo({LIBRARY,LIBRARY_GROUP,SUBCLASSES})
    void stateChanged(@NonNull SmartRefreshLayout refreshLayout, @NonNull RefreshLayoutState oldState, @NonNull RefreshLayoutState newState);
}
