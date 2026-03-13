package com.example.documenpro.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.documenpro.R;

public class AppLoadingDialog extends Dialog {
    public AppLoadingDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_load);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }

    public AppLoadingDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public void setMessage(CharSequence message) {
        TextView tv = findViewById(R.id.tv_progress);
        if (tv != null) {
            tv.setText(message);
        }
    }
}
