package com.example.documenpro.ui.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.documenpro.R;

public class EditBtn extends ConstraintLayout {

    View backgroundView;

    public EditBtn(@NonNull Context context) {
        super(context);
    }

    public EditBtn(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_edit_button, this, true);
        if (attrs != null) {
            backgroundView = findViewById(R.id.viewBg);
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.EditBtn);
            int iconResId = typedArray.getResourceId(R.styleable.EditBtn_ImageEdt, R.drawable.ic_preview_highlight);
            if (iconResId > 0) {
                ((ImageView) findViewById(R.id.imgEdit)).setImageResource(iconResId);
            }

            typedArray.recycle();
        }
        setClickable(true);
        setFocusable(true);
    }

    public EditBtn(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }
    public void setChoose(boolean isChoose){
        if (isChoose){
            backgroundView.setVisibility(VISIBLE);
        }else {
            backgroundView.setVisibility(INVISIBLE);
        }
    }

    public void setEnable(boolean enable) {
        float viewAlpha;
        if (enable) {
            viewAlpha = 1.0F;
        } else {
            viewAlpha = 0.4F;
        }

        EditBtn.this.setAlpha(viewAlpha);
        EditBtn.this.setClickable(enable);
        EditBtn.this.setFocusable(enable);
    }

}
