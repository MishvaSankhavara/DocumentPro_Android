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

public class RemovePdfPasswordDialog extends Dialog {


    private final AppCompatImageView togglePasswordVisibilityButton;
    private final ConstraintLayout dialogRootLayout;
    private final AppCompatEditText passwordInputField;
    public boolean isPasswordVisible = false;
    public LinearLayout errorContainer;

    public TextView titleTextView;
    public TextView errorTextView;
    public TextView descriptionTextView;

    private final PasswordClickListener passwordClickListener;
    private final PDFReaderModel pdfReaderModel;


    public RemovePdfPasswordDialog(@NonNull Context context, PDFReaderModel pdfModel, PasswordClickListener listener) {
        super(context);
        setContentView(R.layout.dialog_enter_password_2);
        this.passwordClickListener = listener;
        this.pdfReaderModel = pdfModel;
        titleTextView = findViewById(R.id.tv_title);
        descriptionTextView = findViewById(R.id.tv_hint);
        errorTextView = findViewById(R.id.tvError);
        titleTextView.setText(R.string.dialog_remove_password_tittle);
        descriptionTextView.setText(R.string.dialog_remove_password_des);
        errorTextView.setText(R.string.dialog_password_invalid);
        dialogRootLayout = findViewById(R.id.root);
        passwordInputField = findViewById(R.id.et_password);
        errorContainer = findViewById(R.id.ll_error_tip);
        this.passwordInputField.setInputType(128);
        this.passwordInputField.setTransformationMethod(new PasswordTransformationMethod());
        togglePasswordVisibilityButton = findViewById(R.id.iv_visible);
        togglePasswordVisibilityButton.setOnClickListener(view -> {
            int i;
            boolean z = !isPasswordVisible;
            isPasswordVisible = z;
            if (z) {
                passwordInputField.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                i = R.drawable.pdf_ic_pwd_show;
            } else {
                passwordInputField.setTransformationMethod(PasswordTransformationMethod.getInstance());
                i = R.drawable.pdf_ic_pwd_hide;
            }
            togglePasswordVisibilityButton.setImageResource(i);
            Editable text = passwordInputField.getText();
            if (text != null && !TextUtils.isEmpty(text.toString())) {
                passwordInputField.setSelection(text.toString().length());
            }
        });
        findViewById(R.id.tv_bt_negative).setOnClickListener(view -> dismiss());
        findViewById(R.id.tv_bt_positive).setOnClickListener(view -> {
            PDDocument pdDocument;
            if (passwordInputField.getText() != null && passwordInputField.getText().length() > 0) {
                try {
                    pdDocument = PDDocument.load(new File(pdfReaderModel.getAbsolutePath_PDFModel()), passwordInputField.getText().toString());
                    pdDocument.close();
                    if (passwordClickListener != null) {
                        passwordClickListener.onOkClickListener(passwordInputField.getText().toString());
                        dismiss();
                    }
                } catch (Exception e) {
                    ObjectAnimator objectAnimator = ViewUtils.startAnim(dialogRootLayout);
                    objectAnimator.start();
                    errorContainer.setVisibility(View.VISIBLE);
                }
            } else {
                ObjectAnimator objectAnimator = ViewUtils.startAnim(dialogRootLayout);
                objectAnimator.start();
                errorTextView.setText(R.string.toast_please_set_password);
            }
        });
    }

}
