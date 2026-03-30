package docreader.aidoc.pdfreader.ui.customviews.smartrefresh.util;

import android.view.View;
import android.view.ViewGroup;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.RefreshManager;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.listener.CoordinatorLayoutListener;

public class CoordinatorLayoutUtil {

    public static void handleCoordinatorLayout(View content, RefreshManager kernel, final CoordinatorLayoutListener listener) {
        try {
            if (content instanceof CoordinatorLayout) {
                kernel.getLayoutRefresh().setNestedScrollEnable(false);
                ViewGroup layout = (ViewGroup) content;
                for (int i = layout.getChildCount() - 1; i >= 0; i--) {
                    View view = layout.getChildAt(i);
                    if (view instanceof AppBarLayout) {
                        ((AppBarLayout) view).addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                            @Override
                            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                                listener.coordinatorUpdate(
                                        verticalOffset >= 0,
                                        (appBarLayout.getTotalScrollRange() + verticalOffset) <= 0);
                            }
                        });
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
