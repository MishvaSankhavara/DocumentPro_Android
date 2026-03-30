package docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api;

import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

public interface RefreshContentHandler {
    @NonNull
    View getContentView();
    @NonNull
    View getScrollableContentView();
    void setupComponent(RefreshManager kernel, View fixedHeader, View fixedFooter);
    void setScrollBoundaryChecker(RefreshScrollBoundaryDecider boundary);
    void setLoadMoreWhenContentNotFullEnabled(boolean enable);
    void handleActionDown(MotionEvent e);
    void updateSpinnerPosition(int spinner, int headerTranslationViewId, int footerTranslationViewId);
    boolean isRefreshPossible();
    boolean isLoadMorePossible();
    AnimatorUpdateListener createScrollAnimatorOnFinish(int spinner);
}
