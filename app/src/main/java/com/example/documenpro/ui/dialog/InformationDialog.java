package com.example.documenpro.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.documenpro.R;
import com.example.documenpro.model.Document;
import com.example.documenpro.utils.Utils;

public class InformationDialog extends Dialog {
    TextView tvName;
    TextView tvFileSize;
    TextView tvLastModified;
    TextView tvPath;


    public InformationDialog(@NonNull Context context, Document document) {
        super(context);
        setContentView(R.layout.dialog_information);
        tvName = findViewById(R.id.tv_name);
        tvFileSize = findViewById(R.id.tv_file_size);
        tvLastModified = findViewById(R.id.tv_last_modified_time);
        tvPath = findViewById(R.id.tv_file_path);
        tvName.setText(document.getFileName());
        tvPath.setText(document.getFileUri());
        tvFileSize.setText(Formatter.formatFileSize(context, document.getLength()));
        tvLastModified.setText(Utils.formatDateToHumanReadable(document.getLastModified()));
        findViewById(R.id.tv_bt_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
