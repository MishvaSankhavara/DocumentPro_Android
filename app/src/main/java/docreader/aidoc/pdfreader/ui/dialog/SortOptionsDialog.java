package docreader.aidoc.pdfreader.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;

import docreader.aidoc.pdfreader.R;
import docreader.aidoc.pdfreader.clickListener.SortingListener;

public class SortOptionsDialog extends Dialog {
    private final RadioGroup rgSortOptions;

    public SortOptionsDialog(@NonNull Context context, SortingListener sortingListener) {
        super(context);
        setContentView(R.layout.dialog_sort_by);
        rgSortOptions = findViewById(R.id.rg_sort_options);
        findViewById(R.id.btn_confirm_sort).setOnClickListener(v -> {
            int selectedOptionId = rgSortOptions.getCheckedRadioButtonId();
            if (selectedOptionId == R.id.rb_date_newest) {
                if (sortingListener != null) {
                    sortingListener.onSortingByDateNewest();
                    dismiss();
                }
            } else if (selectedOptionId == R.id.rb_date_oldest) {
                if (sortingListener != null) {
                    sortingListener.onSortingDateOldest();
                    dismiss();
                }
            } else if (selectedOptionId == R.id.rb_name_az) {
                if (sortingListener != null) {
                    sortingListener.onSortingAtoZ();
                    dismiss();
                }
            } else if (selectedOptionId == R.id.rb_name_za) {
                if (sortingListener != null) {
                    sortingListener.onSortingZtoA();
                    dismiss();
                }
            } else if (selectedOptionId == R.id.rb_size_smallest) {
                if (sortingListener != null) {
                    sortingListener.onSortingFileSizeUp();
                    dismiss();
                }
            } else if (selectedOptionId == R.id.rb_size_largest) {
                if (sortingListener != null) {
                    sortingListener.onSortingFileSizeDown();
                    dismiss();
                }
            } else {
                dismiss();
            }
        });
    }
}
