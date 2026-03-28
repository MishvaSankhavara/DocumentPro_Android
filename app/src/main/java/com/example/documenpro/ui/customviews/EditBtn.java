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
    ImageView imgEdit;

    public EditBtn(@NonNull Context context) {
        super(context);
    }

    public EditBtn(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_edit_button, this, true);
        if (attrs != null) {
            backgroundView = findViewById(R.id.viewBg);
            imgEdit = findViewById(R.id.imgEdit);
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.EditBtn);
            int iconResId = typedArray.getResourceId(R.styleable.EditBtn_ImageEdt, R.drawable.ic_highlight_preview);
            if (iconResId > 0) {
                imgEdit.setImageResource(iconResId);
            }

            typedArray.recycle();
        }
        setClickable(true);
        setFocusable(true);
    }

    public EditBtn(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }
    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        setChoose(selected);
    }

    public void setChoose(boolean isChoose){
        if (isChoose){
            if (backgroundView != null) {
                backgroundView.setVisibility(VISIBLE);
            }
            if (imgEdit != null) {
                imgEdit.setColorFilter(getContext().getResources().getColor(R.color.app_blue));
            }
        }else {
            if (backgroundView != null) {
                backgroundView.setVisibility(INVISIBLE);
            }
            if (imgEdit != null) {
                imgEdit.clearColorFilter();
            }
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
