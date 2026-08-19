package com.arkay.gkinhindi.ui.dialog;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.arkay.gkinhindi.BuildConfig;
import com.arkay.gkinhindi.Constants;
import com.arkay.gkinhindi.R;
import com.arkay.gkinhindi.clickListener.RenameDialogClickListener;
import com.arkay.gkinhindi.utils.AdsUtils;
import com.arkay.gkinhindi.utils.Utils;


public class FileRenameDialog extends Dialog {
    private final EditText etFileName;
    private final RenameDialogClickListener renameListener;
    private final ConstraintLayout dialogRootLayout;
    private final LinearLayout layoutErrorContainer;
    private final TextView tvErrorMessage;
    public FileRenameDialog(@NonNull Context context, String oldName, RenameDialogClickListener mListener) {
        super(context);
        setContentView(R.layout.dialog_file_name_input);
        this.renameListener = mListener;
        dialogRootLayout = findViewById(R.id.cl_dialog_root);
        layoutErrorContainer = findViewById(R.id.ll_error_container);
        etFileName = findViewById(R.id.et_name);
        tvErrorMessage = findViewById(R.id.tv_error_message);
        etFileName.setText(oldName);
        etFileName.setSelectAllOnFocus(true);
        findViewById(R.id.iv_clear).setOnClickListener(view -> etFileName.setText(""));
        findViewById(R.id.btn_cancel).setOnClickListener(view -> dismiss());
        findViewById(R.id.btn_confirm).setOnClickListener(view -> {
            if (TextUtils.equals(oldName, etFileName.getText().toString())) {
                layoutErrorContainer.setVisibility(View.VISIBLE);
                tvErrorMessage.setText(context.getString(R.string.dialog_rename_same_name_error));
                ObjectAnimator objectAnimator = Utils.startAnim(dialogRootLayout);
                objectAnimator.start();
            } else if (Utils.isFileNameValid(etFileName.getText().toString())) {
                if (renameListener != null) {
                    final String newName = etFileName.getText().toString();
                    dismiss();
                    if (context instanceof Activity) {
                        AdsUtils.showRewardedAdSave(
                                (Activity) context,
                                BuildConfig.reward_save,
                                BuildConfig.reward_save_2ID,
                                Constants.reward_save,
                                Constants.reward_save_2ID,
                                () -> renameListener.onRenameDialogListener(newName)
                        );
                    } else {
                        renameListener.onRenameDialogListener(newName);
                    }
                }
            } else {
                ObjectAnimator objectAnimator = Utils.startAnim(dialogRootLayout);
                objectAnimator.start();
                layoutErrorContainer.setVisibility(View.VISIBLE);
                tvErrorMessage.setText(context.getString(R.string.dialog_rename_invalid_characters));
            }
        });
    }
}
