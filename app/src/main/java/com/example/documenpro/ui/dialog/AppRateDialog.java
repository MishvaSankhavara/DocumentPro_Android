package com.example.documenpro.ui.dialog;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;

import com.example.documenpro.AppGlobalConstants;
import com.example.documenpro.R;
import com.example.documenpro.PreferenceUtils;
import com.example.documenpro.utils.Utils;


public class AppRateDialog extends Dialog {
    Context context;

    public AppRateDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        setContentView(R.layout.dialog_rate_app);
        findViewById(R.id.btn_cancel).setOnClickListener(v -> {
            dismiss();
            Utils.rateApp(this.context);
            PreferenceUtils.getInstance(this.context).setBoolean(AppGlobalConstants.ACTION_RATE_APP, false);
        });
    }
}
