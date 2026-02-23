package com.example.documenpro.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;

import com.example.documenpro.R;
import com.example.documenpro.clickListener.SortingListener;

public class SortByDialog extends Dialog {
    private final RadioGroup radioGroup;

    public SortByDialog(@NonNull Context context, SortingListener listener) {
        super(context);
        setContentView(R.layout.dialog_sort);
        radioGroup = findViewById(R.id.radioGroup);
        findViewById(R.id.tv_bt_ok).setOnClickListener(v -> {
            int idRadio = radioGroup.getCheckedRadioButtonId();
            if (idRadio == R.id.rbTimeNew) {
                if (listener != null) {
                    listener.onSortingByDateNewest();
                    dismiss();
                }
            } else if (idRadio == R.id.rbTimeOld) {
                if (listener != null) {
                    listener.onSortingDateOldest();
                    dismiss();
                }
            } else if (idRadio == R.id.rbAZ) {
                if (listener != null) {
                    listener.onSortingAtoZ();
                    dismiss();
                }
            } else if (idRadio == R.id.rbZA) {
                if (listener != null) {
                    listener.onSortingZtoA();
                    dismiss();
                }
            } else if (idRadio == R.id.rbMinimum) {
                if (listener != null) {
                    listener.onSortingFileSizeUp();
                    dismiss();
                }
            } else if (idRadio == R.id.rbLargest) {
                if (listener != null) {
                    listener.onSortingFileSizeDown();
                    dismiss();
                }
            } else {
                dismiss();
            }
        });
    }
}
