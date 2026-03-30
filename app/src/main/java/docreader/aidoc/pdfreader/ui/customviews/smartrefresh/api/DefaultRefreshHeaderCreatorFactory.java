package docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api;

import android.content.Context;

import androidx.annotation.NonNull;

public interface DefaultRefreshHeaderCreatorFactory {
    @NonNull
    RefreshHeaderComponent createRefreshFooterHeader(@NonNull Context context, @NonNull SmartRefreshLayout layout);
}
