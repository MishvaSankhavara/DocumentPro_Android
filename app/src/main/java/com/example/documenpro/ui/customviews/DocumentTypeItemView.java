package com.example.documenpro.ui.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.example.documenpro.R;

public class DocumentTypeItemView extends LinearLayout {

    TextView countTextView;

    public DocumentTypeItemView(Context context) {
        super(context);
    }

    public DocumentTypeItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.item_type_document, this, true);

        countTextView = findViewById(R.id.tv_dialog_description);

        if (attrs != null) {

            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FileTypeItem);

            int iconResId = typedArray.getResourceId(
                    R.styleable.FileTypeItem_imgFileType,
                    R.drawable.ic_document
            );

            if (iconResId > 0) {
                ((AppCompatImageView) findViewById(R.id.iv_icon)).setImageResource(iconResId);
            }

            String fileTypeName = typedArray.getString(
                    R.styleable.FileTypeItem_nameFileType
            );

            if (fileTypeName != null) {
                ((AppCompatTextView) findViewById(R.id.tv_name)).setText(fileTypeName);
            }

            int bgColor = typedArray.getColor(
                    R.styleable.FileTypeItem_backgroundColorFileType,
                    0
            );

            float radius = typedArray.getDimension(
                    R.styleable.FileTypeItem_cornerRadiusFileType,
                    0f
            );

            if (bgColor != 0) {

                GradientDrawable drawable = new GradientDrawable();
                drawable.setColor(bgColor);
                drawable.setCornerRadius(radius);

                findViewById(R.id.cv_icon_bg).setBackground(drawable);
            }

            typedArray.recycle();
        }

        setClickable(true);
        setFocusable(true);
    }

    public void setTvCount(String str) {
        this.countTextView.setText(str);
    }
}