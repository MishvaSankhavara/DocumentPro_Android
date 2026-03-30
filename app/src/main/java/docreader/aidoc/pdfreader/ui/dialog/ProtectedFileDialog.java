package docreader.aidoc.pdfreader.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import docreader.aidoc.pdfreader.R;
public class ProtectedFileDialog extends Dialog {
    public ProtectedFileDialog(@NonNull Context context, int titleTextResId) {
        super(context);
        setContentView(R.layout.dialog_not_print_error);
        AppCompatTextView tvDescription = findViewById(R.id.tv_content);
        tvDescription.setText(titleTextResId);
        findViewById(R.id.btn_confirm_sort).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
