package docreader.aidoc.pdfreader.ui.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import docreader.aidoc.pdfreader.R;

public class EditBtn extends ConstraintLayout {

    View backgroundView;
    ImageView imgEdit;
    TextView tvEdit;

    public EditBtn(@NonNull Context context) {
        super(context);
    }

    public EditBtn(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_edit_button, this, true);
        if (attrs != null) {
            backgroundView = findViewById(R.id.fl_circle_bg);
            imgEdit = findViewById(R.id.imgEdit);
            tvEdit = findViewById(R.id.tvEdit);
            
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.EditBtn);
            int iconResId = typedArray.getResourceId(R.styleable.EditBtn_ImageEdt, R.drawable.ic_highlight_preview);
            if (iconResId > 0 && imgEdit != null) {
                imgEdit.setImageResource(iconResId);
            }
            
            String text = typedArray.getString(R.styleable.EditBtn_TextEdt);
            if (text != null && tvEdit != null) {
                tvEdit.setText(text);
                tvEdit.setVisibility(VISIBLE);
            } else {
                if (tvEdit != null) {
                    tvEdit.setVisibility(GONE);
                }
                // If there's no text, remove top padding to prevent circle clipping and center the icon
                View rootLayout = getChildAt(0);
                if (rootLayout != null) {
                    rootLayout.setPadding(0, 0, 0, 0);
                }
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
        if (backgroundView != null) {
            backgroundView.setSelected(isChoose);
        }
        if (imgEdit != null) {
            imgEdit.setSelected(isChoose);
        }
        if (tvEdit != null) {
            tvEdit.setSelected(isChoose);
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
