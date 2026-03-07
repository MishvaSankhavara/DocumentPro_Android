package com.example.documenpro.ui.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.documenpro.R;

public class BottomButtonView extends LinearLayout {
    public BottomButtonView(Context context) {
        super(context);
    }
    public BottomButtonView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_button_bottom, this, true);
        if (attrs != null) {
            TypedArray obtainStyledArray = null;
            try {
                obtainStyledArray = context.obtainStyledAttributes(attrs, R.styleable.BottomBtn);
                int iconResId = obtainStyledArray.getResourceId(R.styleable.BottomBtn_Image, R.drawable.ic_flip_black);
                if (iconResId > 0) {
                    ((ImageView) findViewById(R.id.imgButton)).setImageResource(iconResId);
                }
                String buttonText = obtainStyledArray.getString(R.styleable.BottomBtn_Name);
                if (buttonText != null) {
                    ((TextView) findViewById(R.id.tvButton)).setText(buttonText);
                }
            } finally {
                if (obtainStyledArray != null) {
                    obtainStyledArray.recycle();
                }
            }
        }
        setClickable(true);
        setFocusable(true);
    }

    public void setEnable(boolean enable) {
        float viewAlpha;
        if (enable) {
            viewAlpha = 1.0F;
        } else {
            viewAlpha = 0.4F;
        }

        BottomButtonView.this.setAlpha(viewAlpha);
        BottomButtonView.this.setClickable(enable);
        BottomButtonView.this.setFocusable(enable);
    }
}
