package docreader.aidoc.pdfreader.ui.customviews.flashbutton;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.appcompat.widget.AppCompatButton;

public class AdsFlashEffectButton extends AppCompatButton {

    public final adC2434p af1267a;

    public AdsFlashEffectButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        adC2434p controller = new adC2434p();
        this.af1267a = controller;
        controller.af4946h = this;
        int i = 1;
        Paint paint = new Paint(1);
        controller.af4939a = paint;
        paint.setStyle(Paint.Style.STROKE);
        controller.af4939a.setColor(-1);
        controller.af4939a.setStrokeWidth(100.0f);
        controller.af4940b = new Path();
        int i2 = (int) (context.getResources().getDisplayMetrics().density * 8.0f);
        controller.af4941c = i2 != 0 ? i2 : i;
    }

    @ColorInt
    public int getFlashEffectColor() {
        return this.af1267a.af4939a.getColor();
    }

    public void onDetachedFromWindow() {
        adC2434p pVar = this.af1267a;
        View view = pVar.af4946h;
        if (view != null) {
            view.removeCallbacks(pVar.af4947i);
        }
        super.onDetachedFromWindow();
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        adC2434p pVar = this.af1267a;
        if (pVar.af4946h.isEnabled() && pVar.af4945g && !pVar.af4943e) {
            int viewWidth = pVar.af4946h.getWidth();
            int viewHeight = pVar.af4946h.getHeight();
            if (pVar.af4944f) {
                pVar.af4944f = false;
                pVar.af4942d = -viewHeight;
                pVar.af4943e = true;
                pVar.af4946h.postDelayed(pVar.af4947i, 2000);
                return;
            }
            pVar.af4940b.reset();
            pVar.af4940b.moveTo((float) (pVar.af4942d - 50), (float) (viewHeight + 50));
            pVar.af4940b.lineTo((float) (pVar.af4942d + viewHeight + 50), -50.0f);
            pVar.af4940b.close();
            double d2 = (((double) ((viewHeight * 2) + viewWidth)) * 0.3d) - (double) viewHeight;
            int i = pVar.af4942d;
            pVar.af4939a.setAlpha((int) (((double) i < d2 ? ((((double) (i + viewHeight)) / (d2 + (double) viewHeight)) * 0.19999999999999998d) + 0.1d : 0.3d - ((((double) i - d2) / ((((double) viewWidth) - d2) + (double) viewHeight)) * 0.19999999999999998d)) * 255.0d));
            canvas.drawPath(pVar.af4940b, pVar.af4939a);
            int i2 = pVar.af4942d + pVar.af4941c;
            pVar.af4942d = i2;
            if (i2 >= viewWidth + viewHeight + 50) {
                pVar.af4942d = -viewHeight;
                pVar.af4943e = true;
                pVar.af4946h.postDelayed(pVar.af4947i, 2000);
                return;
            }
            pVar.af4946h.postInvalidate();
        }
    }

    public void setFlashAnimationEnabled(boolean z) {
        adC2434p pVar = this.af1267a;
        pVar.af4945g = z;
        View view = pVar.af4946h;
        if (view != null) {
            view.invalidate();
        }
    }

    public void setFlashEffectColor(@ColorInt int i) {
        this.af1267a.af4939a.setColor(i);
    }
}
