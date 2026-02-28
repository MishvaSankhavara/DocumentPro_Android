package com.example.documenpro.ui.customviews.smartrefresh.header;

import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.makeMeasureSpec;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.documenpro.R;
import com.example.documenpro.ui.customviews.smartrefresh.api.RefreshHeaderComponent;
import com.example.documenpro.ui.customviews.smartrefresh.api.RefreshManager;
import com.example.documenpro.ui.customviews.smartrefresh.api.SmartRefreshLayout;
import com.example.documenpro.ui.customviews.smartrefresh.internal.RefreshInternalAbstract;
import com.example.documenpro.ui.customviews.smartrefresh.util.SmartUtil;


public class PlaceholderRefreshHeader extends RefreshInternalAbstract implements RefreshHeaderComponent {

    protected RefreshManager refreshKernel;

    public PlaceholderRefreshHeader(Context context) {
        this(context, null);
    }

    public PlaceholderRefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        super.dispatchDraw(canvas);
        final View thisView = this;
        if (thisView.isInEditMode()) {
            final int d = SmartUtil.dp2px(5);
            final Context context = thisView.getContext();

            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(0xcccccccc);
            paint.setStrokeWidth(SmartUtil.dp2px(1));
            paint.setPathEffect(new DashPathEffect(new float[]{d, d, d, d}, 1));
            canvas.drawRect(d, d, thisView.getWidth() - d, thisView.getBottom() - d, paint);

            TextView textView = new TextView(context);
            textView.setText(context.getString(R.string.srl_component_falsify, getClass().getSimpleName(), SmartUtil.px2dp(thisView.getHeight())));
            textView.setTextColor(0xcccccccc);
            textView.setGravity(Gravity.CENTER);
            //noinspection UnnecessaryLocalVariable
            View view = textView;
            view.measure(makeMeasureSpec(thisView.getWidth(), EXACTLY), makeMeasureSpec(thisView.getHeight(), EXACTLY));
            view.layout(0, 0, thisView.getWidth(), thisView.getHeight());
            view.draw(canvas);
        }
    }

    @Override
    public void onDragReleased(@NonNull SmartRefreshLayout layout, int height, int maxDragHeight) {
        if (refreshKernel != null) {
            layout.closeHeaderFooter();
        }
    }
    @Override
    public void onComponentInitialized(@NonNull RefreshManager kernel, int height, int maxDragHeight) {
        refreshKernel = kernel;
    }
}
