package com.example.documenpro.ui.dialog;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.documenpro.R;
import com.example.documenpro.clickListener.RenameDialogClickListener;
import com.example.documenpro.utils.Utils;


public class RenameDialog extends Dialog {
    private final EditText edtRename;
    private final RenameDialogClickListener listener;
    private final ConstraintLayout rootLayout;
    private final LinearLayout llError;
    private final TextView tvError;
    public RenameDialog(@NonNull Context context, String oldName, RenameDialogClickListener mListener) {
        super(context);
        setContentView(R.layout.dialog_file_name);
        this.listener = mListener;
        rootLayout = findViewById(R.id.root);
        llError = findViewById(R.id.ll_error_tip);
        edtRename = findViewById(R.id.et_name);
        tvError = findViewById(R.id.tvError);
        edtRename.setText(oldName);
        edtRename.setSelectAllOnFocus(true);
        findViewById(R.id.tv_bt_negative).setOnClickListener(view -> dismiss());
        findViewById(R.id.tv_bt_positive).setOnClickListener(view -> {
            if (TextUtils.equals(oldName, edtRename.getText().toString())) {
                llError.setVisibility(View.VISIBLE);
                tvError.setText(context.getString(R.string.dialog_rename_error));
                ObjectAnimator objectAnimator = Utils.startAnim(rootLayout);
                objectAnimator.start();
            } else if (Utils.isFileNameValid(edtRename.getText().toString())) {
                if (listener != null) {
                    listener.onRenameDialogListener(edtRename.getText().toString());
                    dismiss();
                }
            } else {
                ObjectAnimator objectAnimator = Utils.startAnim(rootLayout);
                objectAnimator.start();
                llError.setVisibility(View.VISIBLE);
                tvError.setText(context.getString(R.string.dialog_rename_error_2));
            }
        });
    }
}
