
package docreader.aidoc.pdfreader.photopick.internal.entity;

import android.content.Context;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import docreader.aidoc.pdfreader.photopick.internal.ui.widget.IncapableDialog;


public class IncapableCause {
    public static final int TOAST = 0x00;
    public static final int DIALOG = 0x01;
    public static final int NONE = 0x02;

    private final int mForm = TOAST;
    private String mTitle;
    private final String mMessage;

    public IncapableCause(String message) {
        mMessage = message;
    }

    public static void handleCause(Context context, IncapableCause cause) {
        if (cause == null)
            return;

        switch (cause.mForm) {
            case NONE:
                // do nothing.
                break;
            case DIALOG:
                IncapableDialog incapableDialog = IncapableDialog.newInstance(cause.mTitle, cause.mMessage);
                incapableDialog.show(((FragmentActivity) context).getSupportFragmentManager(),
                        IncapableDialog.class.getName());
                break;
            case TOAST:
            default:
                Toast.makeText(context, cause.mMessage, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
