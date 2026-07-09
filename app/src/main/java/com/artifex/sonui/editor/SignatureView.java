package com.artifex.sonui.editor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class SignatureView extends View {
    private Paint paint = new Paint();
    private List<Path> paths = new ArrayList<>();
    private Path currentPath;

    public interface OnDrawStartListener {
        void onDrawStart();
    }
    
    private OnDrawStartListener onDrawStartListener;

    public void setOnDrawStartListener(OnDrawStartListener listener) {
        this.onDrawStartListener = listener;
    }

    public SignatureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(8f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Path p : paths) {
            canvas.drawPath(p, paint);
        }
        if (currentPath != null) {
            canvas.drawPath(currentPath, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (onDrawStartListener != null) {
                    onDrawStartListener.onDrawStart();
                }
                currentPath = new Path();
                currentPath.moveTo(x, y);
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                if (currentPath != null) {
                    currentPath.lineTo(x, y);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (currentPath != null) {
                    currentPath.lineTo(x, y);
                    paths.add(currentPath);
                    currentPath = null;
                }
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

    public void clear() {
        paths.clear();
        currentPath = null;
        invalidate();
    }

    public void undo() {
        if (!paths.isEmpty()) {
            paths.remove(paths.size() - 1);
            invalidate();
        }
    }

    public Bitmap getSignatureBitmap() {
        if (getWidth() <= 0 || getHeight() <= 0) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        bitmap.eraseColor(Color.TRANSPARENT);
        for (Path p : paths) {
            canvas.drawPath(p, paint);
        }
        return bitmap;
    }

    public boolean isSignatureEmpty() {
        return paths.isEmpty();
    }
}
