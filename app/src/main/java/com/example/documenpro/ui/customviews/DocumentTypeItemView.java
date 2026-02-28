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


public class DocumentTypeItemView extends LinearLayout {
    TextView countTextView;

    public DocumentTypeItemView(Context context) {
        super(context);
    }

    public DocumentTypeItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.item_document_type, this, true);
        countTextView = findViewById(R.id.tv_dialog_description);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FileTypeItem);
            int iconResId = typedArray.getResourceId(R.styleable.FileTypeItem_imgFileType, R.drawable.ic_document_all);
            if (iconResId > 0) {
                ((AppCompatImageView) findViewById(R.id.iv_icon)).setImageResource(iconResId);
            }
            String fileTypeName = typedArray.getString(R.styleable.FileTypeItem_nameFileType);
            if (fileTypeName != null) {
                ((AppCompatTextView) findViewById(R.id.tv_name)).setText(fileTypeName);
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
