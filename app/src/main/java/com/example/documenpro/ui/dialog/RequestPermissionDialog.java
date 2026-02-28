package com.example.documenpro.ui.dialog;

import android.app.Activity;
import android.app.Dialog;

import androidx.annotation.NonNull;

import com.example.documenpro.R;
import com.example.documenpro.utils.Utils;

public class RequestPermissionDialog extends Dialog {
    Activity context;

    public RequestPermissionDialog(@NonNull Activity context) {
        super(context);
        this.context = context;
        setContentView(R.layout.dialog_request_permission);

        findViewById(R.id.tv_ok).setOnClickListener(v -> {
            Utils.askPermission(this.context);
            dismiss();
        });
        findViewById(R.id.ivCancel).setOnClickListener(v -> dismiss());
    }
}
