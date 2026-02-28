package com.example.documenpro.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.documenpro.R;
import com.example.documenpro.model_reader.DocumentModel;
import com.example.documenpro.utils.Utils;

public class DocumentInfoDialog extends Dialog {
    TextView lastModifiedText;
    TextView filePathText;
    TextView fileSizeText;
    TextView fileNameText;

    public DocumentInfoDialog(@NonNull Context context, DocumentModel document) {
        super(context);
        setContentView(R.layout.dialog_information);
        fileNameText = findViewById(R.id.tv_name);
        fileSizeText = findViewById(R.id.tv_file_size);
        lastModifiedText = findViewById(R.id.tv_last_modified_time);
        filePathText = findViewById(R.id.tv_file_path);
        fileNameText.setText(document.getFileName_DocModel());
        filePathText.setText(document.getFileUri_DocModel());
        fileSizeText.setText(Formatter.formatFileSize(context, document.getLength_DocModel()));
        lastModifiedText.setText(Utils.formatDateToHumanReadable(document.getLastModified_DocModel()));
        findViewById(R.id.btn_confirm_sort).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
