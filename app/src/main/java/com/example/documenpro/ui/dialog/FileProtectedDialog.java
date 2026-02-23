package com.example.documenpro.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.example.documenpro.R;


public class FileProtectedDialog extends Dialog {


    public FileProtectedDialog(@NonNull Context context, int idTvTittle) {
        super(context);
        setContentView(R.layout.dialog_not_print);
        AppCompatTextView tvDes = findViewById(R.id.tv_content);
        tvDes.setText(idTvTittle);
        findViewById(R.id.tv_bt_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }
}
