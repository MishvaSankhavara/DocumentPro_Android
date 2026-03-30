package docreader.aidoc.pdfreader.ui.customviews.seekbar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

public class SeekBarUtils {

    private static final String LOG_TAG = "RangeSeekBar";

    public static void print(String log) {
        Log.d(LOG_TAG, log);
    }

    public static void print(Object... logs) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Object log : logs) {
            stringBuilder.append(log);
        }
        Log.d(LOG_TAG, stringBuilder.toString());
    }

    public static Bitmap convertDrawableResToBitmap(Context context, int width, int height, int drawableId) {
        if (context == null || width <= 0 || height <= 0 || drawableId == 0) return null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return SeekBarUtils.convertDrawableToBitmap(width, height, context.getResources().getDrawable(drawableId, null));
        } else {
            return SeekBarUtils.convertDrawableToBitmap(width, height, context.getResources().getDrawable(drawableId));
        }
    }

    public static Bitmap convertDrawableToBitmap(int width, int height, Drawable drawable) {
        Bitmap bitmap = null;
        try {
            if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                bitmap = bitmapDrawable.getBitmap();
                if (bitmap != null && bitmap.getHeight() > 0) {
                    Matrix matrix = new Matrix();
                    float scaleWidth = width * 1.0f / bitmap.getWidth();
                    float scaleHeight = height * 1.0f / bitmap.getHeight();
                    matrix.postScale(scaleWidth, scaleHeight);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    return bitmap;
                }
            }
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static void drawNinePatchBitmap(Canvas canvas, Bitmap bmp, Rect rect) {
        NinePatch.isNinePatchChunk(bmp.getNinePatchChunk());
        NinePatch patch = new NinePatch(bmp, bmp.getNinePatchChunk(), null);
        patch.draw(canvas, rect);
    }

    public static void drawBitmapSafely(Canvas canvas, Paint paint, Bitmap bmp, Rect rect) {
        try {
            if (NinePatch.isNinePatchChunk(bmp.getNinePatchChunk())) {
                drawNinePatchBitmap(canvas, bmp, rect);
                return;
            }
        } catch (Exception e) {
        }
        canvas.drawBitmap(bmp, rect.left, rect.top, paint);
    }

    public static int dpToPx(Context context, float dpValue) {
        if (context == null || compareFloatPrecision(0f, dpValue) == 0) return 0;
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int compareFloatPrecision(float a, float b) {
        int ta = Math.round(a * 1000000);
        int tb = Math.round(b * 1000000);
        if (ta > tb) {
            return 1;
        } else if (ta < tb) {
            return -1;
        } else {
            return 0;
        }
    }

    public static int compareFloatWithTolerance(float a, float b, int degree) {
        if (Math.abs(a-b) < Math.pow(0.1, degree)) {
            return 0;
        } else {
            if (a < b) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    public static float safeParseFloat(String s) {
        try {
            return Float.parseFloat(s);
        } catch (NumberFormatException e) {
            return 0f;
        }
    }

    public static Rect measureTextBounds(String text, float textSize) {
        Paint paint = new Paint();
        Rect textRect = new Rect();
        paint.setTextSize(textSize);
        paint.getTextBounds(text, 0, text.length(), textRect);
        paint.reset();
        return textRect;
    }

    public static boolean isBitmapValid(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled() || bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0) {
            return false;
        }
        return true;
    }

    public static int getColorCompat(Context context, @ColorRes int colorId) {
        if (context != null) {
            return ContextCompat.getColor(context.getApplicationContext(), colorId);
        }
        return Color.WHITE;
    }
}