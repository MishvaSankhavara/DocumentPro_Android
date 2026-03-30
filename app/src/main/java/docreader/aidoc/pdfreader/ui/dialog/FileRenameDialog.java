package docreader.aidoc.pdfreader.ui.dialog;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import docreader.aidoc.pdfreader.R;
import docreader.aidoc.pdfreader.clickListener.RenameDialogClickListener;
import docreader.aidoc.pdfreader.utils.Utils;


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
        findViewById(R.id.btn_cancel).setOnClickListener(view -> dismiss());
        findViewById(R.id.btn_confirm).setOnClickListener(view -> {
            if (TextUtils.equals(oldName, etFileName.getText().toString())) {
                layoutErrorContainer.setVisibility(View.VISIBLE);
                tvErrorMessage.setText(context.getString(R.string.dialog_rename_same_name_error));
                ObjectAnimator objectAnimator = Utils.startAnim(dialogRootLayout);
                objectAnimator.start();
            } else if (Utils.isFileNameValid(etFileName.getText().toString())) {
                if (renameListener != null) {
                    renameListener.onRenameDialogListener(etFileName.getText().toString());
                    dismiss();
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
