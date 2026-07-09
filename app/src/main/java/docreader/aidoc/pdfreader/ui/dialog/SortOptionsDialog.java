package docreader.aidoc.pdfreader.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.widget.RadioButton;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.google.android.material.card.MaterialCardView;

import docreader.aidoc.pdfreader.R;
import docreader.aidoc.pdfreader.clickListener.SortingListener;

public class SortOptionsDialog extends Dialog {

    private int selectedOptionId;

    private MaterialCardView cardDateNewest;
    private MaterialCardView cardDateOldest;
    private MaterialCardView cardNameAz;
    private MaterialCardView cardNameZa;
    private MaterialCardView cardSizeLargest;
    private MaterialCardView cardSizeSmallest;

    private RadioButton rbDateNewest;
    private RadioButton rbDateOldest;
    private RadioButton rbNameAz;
    private RadioButton rbNameZa;
    private RadioButton rbSizeLargest;
    private RadioButton rbSizeSmallest;

    public SortOptionsDialog(@NonNull Context context, SortingListener sortingListener) {
        super(context, R.style.SortDialogTheme);
        setContentView(R.layout.dialog_sort_by);

        // Bind CardViews
        cardDateNewest = findViewById(R.id.card_date_newest);
        cardDateOldest = findViewById(R.id.card_date_oldest);
        cardNameAz = findViewById(R.id.card_name_az);
        cardNameZa = findViewById(R.id.card_name_za);
        cardSizeLargest = findViewById(R.id.card_size_largest);
        cardSizeSmallest = findViewById(R.id.card_size_smallest);

        // Bind RadioButtons
        rbDateNewest = findViewById(R.id.rb_date_newest);
        rbDateOldest = findViewById(R.id.rb_date_oldest);
        rbNameAz = findViewById(R.id.rb_name_az);
        rbNameZa = findViewById(R.id.rb_name_za);
        rbSizeLargest = findViewById(R.id.rb_size_largest);
        rbSizeSmallest = findViewById(R.id.rb_size_smallest);

        // Bind Close button
        findViewById(R.id.iv_close).setOnClickListener(v -> dismiss());

        // Set card click listeners
        cardDateNewest.setOnClickListener(v -> selectOption(R.id.rb_date_newest));
        cardDateOldest.setOnClickListener(v -> selectOption(R.id.rb_date_oldest));
        cardNameAz.setOnClickListener(v -> selectOption(R.id.rb_name_az));
        cardNameZa.setOnClickListener(v -> selectOption(R.id.rb_name_za));
        cardSizeLargest.setOnClickListener(v -> selectOption(R.id.rb_size_largest));
        cardSizeSmallest.setOnClickListener(v -> selectOption(R.id.rb_size_smallest));

        // Default selection: Date modified - Newest first
        selectOption(R.id.rb_date_newest);

        // Confirm button
        findViewById(R.id.btn_confirm_sort).setOnClickListener(v -> {
            if (selectedOptionId == R.id.rb_date_newest) {
                if (sortingListener != null) {
                    sortingListener.onSortingByDateNewest();
                }
            } else if (selectedOptionId == R.id.rb_date_oldest) {
                if (sortingListener != null) {
                    sortingListener.onSortingDateOldest();
                }
            } else if (selectedOptionId == R.id.rb_name_az) {
                if (sortingListener != null) {
                    sortingListener.onSortingAtoZ();
                }
            } else if (selectedOptionId == R.id.rb_name_za) {
                if (sortingListener != null) {
                    sortingListener.onSortingZtoA();
                }
            } else if (selectedOptionId == R.id.rb_size_smallest) {
                if (sortingListener != null) {
                    sortingListener.onSortingFileSizeUp();
                }
            } else if (selectedOptionId == R.id.rb_size_largest) {
                if (sortingListener != null) {
                    sortingListener.onSortingFileSizeDown();
                }
            }
            dismiss();
        });
    }

    private void selectOption(int id) {
        selectedOptionId = id;

        // Toggle checked state
        rbDateNewest.setChecked(id == R.id.rb_date_newest);
        rbDateOldest.setChecked(id == R.id.rb_date_oldest);
        rbNameAz.setChecked(id == R.id.rb_name_az);
        rbNameZa.setChecked(id == R.id.rb_name_za);
        rbSizeLargest.setChecked(id == R.id.rb_size_largest);
        rbSizeSmallest.setChecked(id == R.id.rb_size_smallest);

        // Colors
        int colorActive = ContextCompat.getColor(getContext(), R.color.app_main_color);
        int colorInactive = ContextCompat.getColor(getContext(), R.color.settings_card_stroke);

        // Toggle card borders
        cardDateNewest.setStrokeColor(id == R.id.rb_date_newest ? colorActive : colorInactive);
        cardDateOldest.setStrokeColor(id == R.id.rb_date_oldest ? colorActive : colorInactive);
        cardNameAz.setStrokeColor(id == R.id.rb_name_az ? colorActive : colorInactive);
        cardNameZa.setStrokeColor(id == R.id.rb_name_za ? colorActive : colorInactive);
        cardSizeLargest.setStrokeColor(id == R.id.rb_size_largest ? colorActive : colorInactive);
        cardSizeSmallest.setStrokeColor(id == R.id.rb_size_smallest ? colorActive : colorInactive);
    }
}
