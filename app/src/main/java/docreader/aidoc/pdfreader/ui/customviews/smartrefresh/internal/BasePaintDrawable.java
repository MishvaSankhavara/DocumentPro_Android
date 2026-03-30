package docreader.aidoc.pdfreader.ui.customviews.smartrefresh.internal;

import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;


public abstract class BasePaintDrawable extends Drawable {

    protected Paint paint = new Paint();

    protected BasePaintDrawable() {
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setColor(0xffaaaaaa);
    }

    public void setPaintColor(int color) {
        paint.setColor(color);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
