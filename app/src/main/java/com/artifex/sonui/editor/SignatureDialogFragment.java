package com.artifex.sonui.editor;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import docreader.aidoc.pdfreader.R;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Wrap the dialog context in the Material Components dialog theme to prevent card inflation crash
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.SortDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_signature_capture, container, false);

        signatureView = view.findViewById(R.id.signature_view);
        View placeholder = view.findViewById(R.id.ll_signature_placeholder);
        View btnClear = view.findViewById(R.id.btn_clear);
        View btnUndo = view.findViewById(R.id.btn_undo);
        View btnCancel = view.findViewById(R.id.btn_cancel);
        View btnDone = view.findViewById(R.id.btn_done);
        View ivClose = view.findViewById(R.id.iv_close);

        // Hide placeholder when drawing starts
        signatureView.setOnDrawStartListener(() -> placeholder.setVisibility(View.GONE));

        // Clear drawing and show placeholder again
        btnClear.setOnClickListener(v -> {
            signatureView.clear();
            placeholder.setVisibility(View.VISIBLE);
        });

        // Undo last stroke, show placeholder if no strokes are left
        btnUndo.setOnClickListener(v -> {
            signatureView.undo();
            if (signatureView.isSignatureEmpty()) {
                placeholder.setVisibility(View.VISIBLE);
            }
        });

        ivClose.setOnClickListener(v -> dismiss());
        btnCancel.setOnClickListener(v -> dismiss());
        
        btnDone.setOnClickListener(v -> {
            Bitmap bitmap = signatureView.getSignatureBitmap();
            if (bitmap != null && !signatureView.isSignatureEmpty()) {
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
