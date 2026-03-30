package docreader.aidoc.pdfreader.ui.customviews;

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

import docreader.aidoc.pdfreader.R;

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

            int startColor = typedArray.getColor(
                    R.styleable.FileTypeItem_startColorFileType,
                    0
            );

            int endColor = typedArray.getColor(
                    R.styleable.FileTypeItem_endColorFileType,
                    0
            );

            float radius = typedArray.getDimension(
                    R.styleable.FileTypeItem_cornerRadiusFileType,
                    0f
            );

            if (startColor != 0 && endColor != 0) {
                GradientDrawable gradientDrawable = new GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        new int[]{startColor, endColor}
                );
                gradientDrawable.setCornerRadius(radius);
                findViewById(R.id.cv_icon_bg).setBackground(gradientDrawable);
            } else if (bgColor != 0) {
                ((com.google.android.material.card.MaterialCardView) findViewById(R.id.cv_icon_bg)).setCardBackgroundColor(bgColor);
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