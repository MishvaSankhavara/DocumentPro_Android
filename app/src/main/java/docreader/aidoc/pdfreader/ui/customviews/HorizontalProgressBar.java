package docreader.aidoc.pdfreader.ui.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

import docreader.aidoc.pdfreader.R;


public class HorizontalProgressBar extends View {

    public int f32689 = 100;
    public int f32690 = 0;
    public int f32692;
    public Paint f32695;
    public int f32693;
    public boolean f32691 = false;
    public Path f32696 = new Path();
    public RectF f32694 = new RectF();

    public HorizontalProgressBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, new int[]{R.attr.hpb_colorBackground, R.attr.hpb_colorProgress, R.attr.hpb_progress, R.attr.hpb_useRoundRect}, 0, 0);
            try {
                this.f32692 = obtainStyledAttributes.getColor(0, 1683075321);
                this.f32693 = obtainStyledAttributes.getColor(1, -12942662);
                this.f32690 = obtainStyledAttributes.getInt(2, 0);
                this.f32691 = obtainStyledAttributes.getBoolean(3, false);
            } finally {
                obtainStyledAttributes.recycle();
            }
        } else {
            this.f32692 = 1683075321;
            this.f32693 = -12942662;
        }
        Paint paint = new Paint(1);
        this.f32695 = paint;
        paint.setStyle(Paint.Style.FILL);
    }

    public int getProgress() {
        return this.f32690;
    }

    public void onDraw(@NonNull Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        float f = (float) width;
        int i = (int) ((((float) this.f32690) / ((float) this.f32689)) * f);
        if (this.f32691) {
            this.f32695.setColor(this.f32692);
            RectF rectF = this.f32694;
            rectF.left = 0.0f;
            rectF.top = 0.0f;
            rectF.right = f;
            float f2 = (float) height;
            rectF.bottom = f2;
            float f3 = (float) (height / 2);
            canvas.drawRoundRect(rectF, f3, f3, this.f32695);
            this.f32696.addRect(0.0f, 0.0f, (float) i, f2, Path.Direction.CW);
            canvas.clipPath(this.f32696);
            this.f32695.setColor(this.f32693);
            canvas.drawRoundRect(this.f32694, f3, f3, this.f32695);
            return;
        }
        this.f32695.setColor(this.f32692);
        float f4 = (float) height;
        canvas.drawRect(0.0f, 0.0f, f, f4, this.f32695);
        this.f32695.setColor(this.f32693);
        canvas.drawRect(0.0f, 0.0f, (((float) this.f32690) / ((float) this.f32689)) * f, f4, this.f32695);
    }

    public void setForegroundColor(int i) {
        this.f32693 = i;
    }

    public void setBackgroundColor(int i) {
        this.f32692 = i;
    }

    public void setProgress(int i) {
        if (this.f32690 != i) {
            this.f32690 = Math.min(Math.max(0, i), this.f32689);
            invalidate();
        }
    }

    public void setMax(int i) {
        if (this.f32689 != i) {
            this.f32689 = Math.max(1, i);
            invalidate();
        }
    }

    public void setUseRoundRect(boolean z) {
        this.f32691 = z;
    }
}
