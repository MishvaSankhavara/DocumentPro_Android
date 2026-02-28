package com.example.documenpro.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.documenpro.R;
import com.example.documenpro.ui.customviews.HorizontalProgressBar;

public class FileProgressDialog extends Dialog {
    TextView percentTextView;
    TextView titleTextView;
    HorizontalProgressBar progressBarView;

    public FileProgressDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_progress);
        percentTextView = findViewById(R.id.tvPercent);
        titleTextView = findViewById(R.id.tvTitle);
        progressBarView = findViewById(R.id.progress_bar);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }
    public void setTitleText(String tittle) {
        this.titleTextView.setText(tittle);
    }

    public void setPercentText(String percent) {
        this.percentTextView.setText(percent);
    }

    public void updateProgress(int progress) {
        this.progressBarView.setProgress(progress);
    }
}
