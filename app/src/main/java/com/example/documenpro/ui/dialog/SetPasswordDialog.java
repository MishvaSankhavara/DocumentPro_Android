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
import com.example.documenpro.listener.PasswordListener;
import com.example.documenpro.utils.ViewUtils;


public class SetPasswordDialog extends Dialog {
    private final AppCompatImageView btnShowPassword;
    private final ConstraintLayout rootLayout;
    private final AppCompatEditText edtPassword;
    public boolean showPass = false;
    public LinearLayout llError;

    public TextView tvTittle;
    public TextView tvError;
    public TextView tvDes;

    private final PasswordListener mListener;


    public SetPasswordDialog(@NonNull Context context, PasswordListener listener) {
        super(context);
        setContentView(R.layout.dialog_enter_password_2);

        this.mListener = listener;
        tvTittle = findViewById(R.id.tv_title);
        tvDes = findViewById(R.id.tv_hint);
        tvError = findViewById(R.id.tvError);
        tvTittle.setText(R.string.str_set_password);
        tvDes.setText(R.string.str_set_password_des);
        tvError.setText(R.string.toast_please_set_password);
        rootLayout = findViewById(R.id.root);
        edtPassword = findViewById(R.id.et_password);
        llError = findViewById(R.id.ll_error_tip);
        this.edtPassword.setInputType(128);
        this.edtPassword.setTransformationMethod(new PasswordTransformationMethod());
        btnShowPassword = findViewById(R.id.iv_visible);
        btnShowPassword.setOnClickListener(view -> {
            int i;
            boolean z = !showPass;
            showPass = z;
            if (z) {
                edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                i = R.drawable.pdf_ic_pwd_show;
            } else {
                edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                i = R.drawable.pdf_ic_pwd_hide;
            }
            btnShowPassword.setImageResource(i);
            Editable text = edtPassword.getText();
            if (text != null && !TextUtils.isEmpty(text.toString())) {
                edtPassword.setSelection(text.toString().length());
            }
        });
        findViewById(R.id.tv_bt_negative).setOnClickListener(view -> dismiss());
        findViewById(R.id.tv_bt_positive).setOnClickListener(view -> {
            if (edtPassword.getText() != null && edtPassword.getText().length() > 0) {
                if (mListener != null) {
                    mListener.onOkClick(edtPassword.getText().toString());
                    dismiss();
                }
            } else {
                ObjectAnimator objectAnimator = ViewUtils.startAnim(rootLayout);
                objectAnimator.start();
                llError.setVisibility(View.VISIBLE);
            }
        });
    }
}
