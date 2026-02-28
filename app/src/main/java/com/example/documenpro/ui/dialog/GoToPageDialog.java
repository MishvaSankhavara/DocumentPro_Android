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

public class GoToPageDialog extends Dialog {
    private final GoToPageDialogListener goToPageListener;
    TextView tvRangePage;
    ConstraintLayout dialogRootLayout;
    EditText pageNumberInput;
    TextView confirmButton;
    TextView cancelButton;

    @SuppressLint("SetTextI18n")
    public GoToPageDialog(@NonNull Context context, GoToPageDialogListener mListener, int totalPageCount) {
        super(context);
        setContentView(R.layout.dialog_go_page);
        this.goToPageListener = mListener;
        dialogRootLayout = findViewById(R.id.rootLayout);
        pageNumberInput = findViewById(R.id.et_name);
        confirmButton = findViewById(R.id.tv_bt_positive);
        cancelButton = findViewById(R.id.tv_bt_negative);
        pageNumberInput.setHint("(1-" + totalPageCount + ")");
        cancelButton.setOnClickListener(v -> dismiss());
        confirmButton.setOnClickListener(v -> {
            String enteredPageNumber = pageNumberInput.getText().toString();
            if (isValidPageNumber(enteredPageNumber, totalPageCount)) {
                dismiss();
                if (goToPageListener != null) {
                    goToPageListener.onPageNum(Integer.parseInt(enteredPageNumber) - 1);
                }

            } else {
                pageNumberInput.setError(context.getString(R.string.invalid_page_number));
                ObjectAnimator objectAnimator = Utils.startAnim(dialogRootLayout);
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
