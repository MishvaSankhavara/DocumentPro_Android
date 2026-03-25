package com.artifex.sonui.editor;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.documenpro.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SignatureDialogFragment extends DialogFragment {

    public interface OnSignatureListener {
        void onSignatureCaptured(String path);
    }

    private OnSignatureListener listener;
    private SignatureView signatureView;

    public void setOnSignatureListener(OnSignatureListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_signature_capture, container, false);

        signatureView = view.findViewById(R.id.signature_view);
        View btnClear = view.findViewById(R.id.btn_clear);
        View btnCancel = view.findViewById(R.id.btn_cancel);
        View btnDone = view.findViewById(R.id.btn_done);

        btnClear.setOnClickListener(v -> signatureView.clear());
        btnCancel.setOnClickListener(v -> dismiss());
        btnDone.setOnClickListener(v -> {
            Bitmap bitmap = signatureView.getSignatureBitmap();
            if (bitmap != null) {
                String path = saveSignatureTemp(bitmap);
                if (path != null && listener != null) {
                    listener.onSignatureCaptured(path);
                    dismiss();
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    private String saveSignatureTemp(Bitmap bitmap) {
        File folder = new File(getContext().getFilesDir(), "signatures");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(folder, "sig_" + System.currentTimeMillis() + ".png");
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
