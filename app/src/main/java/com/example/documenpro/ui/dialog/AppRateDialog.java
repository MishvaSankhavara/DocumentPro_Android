package com.example.documenpro.ui.dialog;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;

import com.example.documenpro.GlobalConstant;
import com.example.documenpro.R;
import com.example.documenpro.SharedPreferenceUtils;
import com.example.documenpro.utils.Utils;


public class AppRateDialog extends Dialog {
    Context context;

    public AppRateDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        setContentView(R.layout.dialog_rate);
        findViewById(R.id.btn_cancel).setOnClickListener(v -> {
            dismiss();
            Utils.rateApp(this.context);
            SharedPreferenceUtils.getInstance(this.context).setBoolean(GlobalConstant.RATE_APP, false);
        });
    }
}
