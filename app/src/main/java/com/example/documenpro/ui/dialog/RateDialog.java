package com.example.documenpro.ui.dialog;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;

import com.example.documenpro.GlobalConstant;
import com.example.documenpro.R;
import com.example.documenpro.SharedPreferenceUtils;
import com.example.documenpro.utils.Utils;


public class RateDialog extends Dialog {
    Context mContext;

    public RateDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
        setContentView(R.layout.dialog_rate);
        findViewById(R.id.tv_bt_negative).setOnClickListener(v -> {
            dismiss();
            Utils.rateApp(mContext);
            SharedPreferenceUtils.getInstance(mContext).setBoolean(GlobalConstant.RATE_APP, false);
        });
    }
}
