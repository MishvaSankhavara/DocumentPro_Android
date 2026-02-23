package com.example.documenpro.ui.dialog;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.documenpro.R;
import com.example.documenpro.clickListener.GoToPageDialogListener;
import com.example.documenpro.utils.Utils;

public class GoPageDialog extends Dialog {
    private final GoToPageDialogListener listener;
    TextView tvRangePage;
    ConstraintLayout rootView;
    EditText edtPage;
    TextView tvOk;
    TextView tvCancel;

    @SuppressLint("SetTextI18n")
    public GoPageDialog(@NonNull Context context, GoToPageDialogListener mListener, int pageCount) {
        super(context);
        setContentView(R.layout.dialog_go_page);
        this.listener = mListener;
        rootView = findViewById(R.id.rootLayout);
//        tvRangePage = findViewById(R.id.tvPageSize);
        edtPage = findViewById(R.id.et_name);
        tvOk = findViewById(R.id.tv_bt_positive);
        tvCancel = findViewById(R.id.tv_bt_negative);
        edtPage.setHint("(1-" + pageCount + ")");
        tvCancel.setOnClickListener(v -> dismiss());
        tvOk.setOnClickListener(v -> {
            String obj = edtPage.getText().toString();
            if (isValidPageNumber(obj, pageCount)) {
                dismiss();
                if (listener != null) {
                    listener.onPageNum(Integer.parseInt(obj) - 1);
                }

            } else {
                edtPage.setError(context.getString(R.string.invalid_page_number));
                ObjectAnimator objectAnimator = Utils.startAnim(rootView);
                objectAnimator.start();
            }
        });
    }

    public boolean isValidPageNumber(String str, int pageCount) {
        boolean z = false;
        if (TextUtils.isEmpty(str) || !TextUtils.isDigitsOnly(str)) {
            return false;
        }
        try {
            int intValue = Integer.parseInt(str);
            if (intValue > 0 && intValue <= pageCount) {
                z = true;
            }
            return z;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
