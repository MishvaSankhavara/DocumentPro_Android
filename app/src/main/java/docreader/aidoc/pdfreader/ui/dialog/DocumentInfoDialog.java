package docreader.aidoc.pdfreader.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import docreader.aidoc.pdfreader.R;
import docreader.aidoc.pdfreader.model_reader.DocumentModel;
import docreader.aidoc.pdfreader.utils.Utils;

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
        
        // Dynamic file extension badge
        String fileName = document.getFileName_DocModel();
        String ext = "";
        if (fileName != null && fileName.contains(".")) {
            ext = fileName.substring(fileName.lastIndexOf(".") + 1).toUpperCase();
        }
        if (ext.isEmpty()) {
            ext = "FILE";
        }
        TextView extBadge = findViewById(R.id.tv_extension_badge);
        if (extBadge != null) {
            extBadge.setText(ext);
        }

        // Close button click listener
        View btnClose = findViewById(R.id.btn_close);
        if (btnClose != null) {
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }

        findViewById(R.id.btn_confirm_sort).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
