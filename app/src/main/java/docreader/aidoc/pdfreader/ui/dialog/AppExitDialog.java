package docreader.aidoc.pdfreader.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import docreader.aidoc.pdfreader.R;
public class AppExitDialog extends Dialog {

    public AppExitDialog(@NonNull Activity context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_exit, null);
        setContentView(view);
        findViewById(R.id.btn_confirm).setOnClickListener(v -> context.finish());
        findViewById(R.id.btn_cancel).setOnClickListener(v -> dismiss());
    }
}
