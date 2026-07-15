package com.arkay.gkinhindi.ui.dialog;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;

import com.arkay.gkinhindi.Constants;
import com.arkay.gkinhindi.R;
import com.arkay.gkinhindi.PreferenceUtils;
import com.arkay.gkinhindi.utils.Utils;


public class AppRateDialog extends Dialog {
    Context context;

    public AppRateDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        setContentView(R.layout.dialog_rate_app);
        findViewById(R.id.btn_cancel).setOnClickListener(v -> {
            dismiss();
            Utils.rateApp(this.context);
            PreferenceUtils.getInstance(this.context).setBoolean(Constants.ACTION_RATE_APP, false);
        });
    }
}
