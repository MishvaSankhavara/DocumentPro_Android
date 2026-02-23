package com.example.documenpro.ui.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.example.documenpro.R;


public class FileTypeItem extends LinearLayout {
    TextView tvCount;

    public FileTypeItem(Context context) {
        super(context);
    }

    public FileTypeItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.item_document_type, this, true);
        tvCount = findViewById(R.id.tv_hint);
        if (attrs != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attrs, R.styleable.FileTypeItem);
            int resourceId = obtainStyledAttributes.getResourceId(R.styleable.FileTypeItem_imgFileType, R.drawable.ic_document_all);
            if (resourceId > 0) {
                ((AppCompatImageView) findViewById(R.id.iv_icon)).setImageResource(resourceId);
            }
            String nameFileType = obtainStyledAttributes.getString(R.styleable.FileTypeItem_nameFileType);
            if (nameFileType != null) {
                ((AppCompatTextView) findViewById(R.id.tv_name)).setText(nameFileType);
            }
            obtainStyledAttributes.recycle();
        }
        setClickable(true);
        setFocusable(true);
    }

    public void setTvCount(String str) {
        this.tvCount.setText(str);
    }
}
