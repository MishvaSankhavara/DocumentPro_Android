package com.example.documenpro.ui.dialog;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.documenpro.R;
import com.example.documenpro.clickListener.PasswordClickListener;
import com.example.documenpro.utils.ViewUtils;


public class PasswordSetupDialog extends Dialog {
    private final AppCompatImageView ivTogglePasswordVisibility;
    private final ConstraintLayout clDialogRoot;
    private final AppCompatEditText etPassword;
    public boolean isPasswordVisible = false;
    public LinearLayout llErrorContainer;

    public TextView tv_dialog_title;
    public TextView tvErrorMessage;
    public TextView tvDescription;

    private final PasswordClickListener passwordClickListener;


    public PasswordSetupDialog(@NonNull Context context, PasswordClickListener listener) {
        super(context);
        setContentView(R.layout.dialog_password_input2);

        this.passwordClickListener = listener;
        tv_dialog_title = findViewById(R.id.tv_title);
        tvDescription = findViewById(R.id.tv_dialog_description);
        tvErrorMessage = findViewById(R.id.tv_error_message);
        tv_dialog_title.setText(R.string.set_password);
        tvDescription.setText(R.string.app_password_des);
        tvErrorMessage.setText(R.string.toast_please_set_password);
        clDialogRoot = findViewById(R.id.cl_dialog_root);
        etPassword = findViewById(R.id.et_password_input);
        llErrorContainer = findViewById(R.id.ll_error_container);
        this.etPassword.setInputType(128);
        this.etPassword.setTransformationMethod(new PasswordTransformationMethod());
        ivTogglePasswordVisibility = findViewById(R.id.iv_toggle_password_visibility);
        ivTogglePasswordVisibility.setOnClickListener(view -> {
            int i;
            boolean z = !isPasswordVisible;
            isPasswordVisible = z;
            if (z) {
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                i = R.drawable.ic_pdf_pwd_show;
            } else {
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                i = R.drawable.ic_pdf_pwd_hide;
            }
            ivTogglePasswordVisibility.setImageResource(i);
            Editable text = etPassword.getText();
            if (text != null && !TextUtils.isEmpty(text.toString())) {
                etPassword.setSelection(text.toString().length());
            }
        });
        findViewById(R.id.btn_cancel).setOnClickListener(view -> dismiss());
        findViewById(R.id.btn_confirm).setOnClickListener(view -> {
            if (etPassword.getText() != null && etPassword.getText().length() > 0) {
                if (passwordClickListener != null) {
                    passwordClickListener.onOkClickListener(etPassword.getText().toString());
                    dismiss();
                }
            } else {
                ObjectAnimator objectAnimator = ViewUtils.shakeAnimation(clDialogRoot);
                objectAnimator.start();
                llErrorContainer.setVisibility(View.VISIBLE);
            }
        });
    }
}
