package com.arkay.gkinhindi.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.arkay.gkinhindi.R;

public class UnsavedChangesDialog extends Dialog {

    public interface OnOptionSelectedListener {
        void onSave();
        void onDiscard();
        void onContinue();
    }

    public UnsavedChangesDialog(@NonNull Activity activity, int saveResId, @NonNull OnOptionSelectedListener listener) {
        super(activity);
        
        // Transparent window styling
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_unsaved_changes, null);
        setContentView(view);
        
        if (window != null) {
            // Calculate 85% of screen width to provide horizontal margin/space
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = (int) (displayMetrics.widthPixels * 0.85);
            
            window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        View btnSave = findViewById(R.id.btn_save);
        View btnDiscard = findViewById(R.id.btn_discard);
        View btnClose = findViewById(R.id.btn_close);
        AppCompatTextView tvSaveText = findViewById(R.id.tv_btn_save_text);

        if (tvSaveText != null) {
            tvSaveText.setText(saveResId);
        }

        btnSave.setOnClickListener(v -> {
            dismiss();
            listener.onSave();
        });

        btnDiscard.setOnClickListener(v -> {
            dismiss();
            listener.onDiscard();
        });

        if (btnClose != null) {
            btnClose.setOnClickListener(v -> {
                dismiss();
                listener.onContinue();
            });
        }
    }
}
