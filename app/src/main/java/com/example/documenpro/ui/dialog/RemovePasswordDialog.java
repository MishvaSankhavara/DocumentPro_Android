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
import com.example.documenpro.model_reader.PDFReaderModel;
import com.example.documenpro.utils.ViewUtils;
import com.tom_roush.pdfbox.pdmodel.PDDocument;

import java.io.File;

public class RemovePasswordDialog extends Dialog {


    private final AppCompatImageView btnShowPassword;
    private final ConstraintLayout rootLayout;
    private final AppCompatEditText edtPassword;
    public boolean showPass = false;
    public LinearLayout llError;

    public TextView tvTittle;
    public TextView tvError;
    public TextView tvDes;

    private final PasswordClickListener mListener;
    private final PDFReaderModel mPdfMode;


    public RemovePasswordDialog(@NonNull Context context, PDFReaderModel pdfModel, PasswordClickListener listener) {
        super(context);
        setContentView(R.layout.dialog_enter_password_2);
        this.mListener = listener;
        this.mPdfMode = pdfModel;
        tvTittle = findViewById(R.id.tv_title);
        tvDes = findViewById(R.id.tv_hint);
        tvError = findViewById(R.id.tvError);
        tvTittle.setText(R.string.dialog_remove_password_tittle);
        tvDes.setText(R.string.dialog_remove_password_des);
        tvError.setText(R.string.dialog_password_invalid);
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
            PDDocument pdDocument;
            if (edtPassword.getText() != null && edtPassword.getText().length() > 0) {
                try {
                    pdDocument = PDDocument.load(new File(mPdfMode.getAbsolutePath_PDFModel()), edtPassword.getText().toString());
                    pdDocument.close();
                    if (mListener != null) {
                        mListener.onOkClickListener(edtPassword.getText().toString());
                        dismiss();
                    }
                } catch (Exception e) {
                    ObjectAnimator objectAnimator = ViewUtils.startAnim(rootLayout);
                    objectAnimator.start();
                    llError.setVisibility(View.VISIBLE);
                }
            } else {
                ObjectAnimator objectAnimator = ViewUtils.startAnim(rootLayout);
                objectAnimator.start();
                tvError.setText(R.string.toast_please_set_password);
            }
        });
    }

}
