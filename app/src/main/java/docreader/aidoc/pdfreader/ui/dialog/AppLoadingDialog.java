package docreader.aidoc.pdfreader.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.widget.TextView;

import androidx.annotation.NonNull;

import docreader.aidoc.pdfreader.R;

public class AppLoadingDialog extends Dialog {
    public AppLoadingDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_load);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }

    public AppLoadingDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public void setMessage(CharSequence message) {
        TextView tv = findViewById(R.id.tv_progress);
        if (tv != null) {
            tv.setText(message);
        }
    }
}
