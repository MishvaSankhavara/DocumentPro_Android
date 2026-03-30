package docreader.aidoc.pdfreader.ui.customviews.smartrefresh.listener;


import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.RefreshFooterComponent;
import docreader.aidoc.pdfreader.ui.customviews.smartrefresh.api.RefreshHeaderComponent;


public interface MultiPurposeListener extends RefreshLoadListener, StateChangedListener {
    void headerMoving(RefreshHeaderComponent header, boolean isDragging, float percent, int offset, int headerHeight, int maxDragHeight);
    void headerReleased(RefreshHeaderComponent header, int headerHeight, int maxDragHeight);
    void headerAnimationStart(RefreshHeaderComponent header, int headerHeight, int maxDragHeight);
    void headerFinish(RefreshHeaderComponent header, boolean success);
    void footerMoving(RefreshFooterComponent footer, boolean isDragging, float percent, int offset, int footerHeight, int maxDragHeight);
    void footerReleased(RefreshFooterComponent footer, int footerHeight, int maxDragHeight);
    void footerAnimationStart(RefreshFooterComponent footer, int footerHeight, int maxDragHeight);
    void footerFinish(RefreshFooterComponent footer, boolean success);
}
