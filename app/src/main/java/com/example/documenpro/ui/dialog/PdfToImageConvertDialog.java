package com.example.documenpro.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.documenpro.AppGlobalConstants;
import com.example.documenpro.R;
import com.example.documenpro.model_reader.PDFPageModel;
import com.docpro.scanner.result.ResultViewerActivity;
import com.example.documenpro.ui.activities.SharePdfAsImageActivity;
import com.example.documenpro.ui.customviews.HorizontalProgressBar;
import com.example.documenpro.utils.Utils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class PdfToImageConvertDialog extends Dialog {
    private HorizontalProgressBar progressBarView;
    private TextView tvDescription;
    private ArrayList<PDFPageModel> pdfPageList;
    private TextView btnCancelAction;
    private TextView btnViewImages;
    private TextView tvProgressPercent;
    private SharePdfAsImageActivity activityContext;

    public PdfToImageConvertDialog(@NonNull SharePdfAsImageActivity context, ArrayList<PDFPageModel> arrayList) {
        super(context);
        this.activityContext = context;
        this.pdfPageList = arrayList;
        setContentView(R.layout.dialog_pdf_export_image);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        initializeViews();

        initializeData();
        new CopyImage(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void initializeViews() {
        tvDescription = findViewById(R.id.tvDescription);
        progressBarView = findViewById(R.id.progress_bar);
        btnViewImages = findViewById(R.id.tv_right_tip);
        btnCancelAction = findViewById(R.id.tv_left_tip);
        tvProgressPercent = findViewById(R.id.tvPercent);
    }

    private void initializeData() {

        tvDescription.setVisibility(View.VISIBLE);
        btnViewImages.setVisibility(View.GONE);
        btnCancelAction.setVisibility(View.VISIBLE);

    }

    private static class CopyImage extends AsyncTask<Void, Integer, Void> {

        WeakReference<PdfToImageConvertDialog> weakReference;

        public CopyImage(PdfToImageConvertDialog progressSaveDialog) {
            this.weakReference = new WeakReference<>(progressSaveDialog);

            this.weakReference.get().btnCancelAction.setOnClickListener(view -> {
                CopyImage.this.cancel(true);
                weakReference.get().dismiss();
            });
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            weakReference.get().activityContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    weakReference.get().progressBarView.setMax(weakReference.get().pdfPageList.size());

                }
            });

            File pathFolder = new File(AppGlobalConstants.DIRECTORY_IMAGES);
            if (!pathFolder.exists()) {
                pathFolder.mkdirs();
            }

            for (int i = 0; i < weakReference.get().pdfPageList.size(); i++) {
                String fileName = Utils.getFileNameFromUri(weakReference.get().pdfPageList.get(i).getThumbnailUri_PDFPageModel());
                String pathCopy = pathFolder + "/" + fileName;
                Utils.copyFile(weakReference.get().activityContext, weakReference.get().pdfPageList.get(i).getThumbnailUri_PDFPageModel(),
                        pathCopy);
                publishProgress(i + 1);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            weakReference.get().btnViewImages.setVisibility(View.VISIBLE);
            weakReference.get().btnCancelAction.setVisibility(View.GONE);

            weakReference.get().btnViewImages.setOnClickListener(view -> {
                Intent intent = new Intent(weakReference.get().activityContext, ResultViewerActivity.class);
                intent.putExtra(AppGlobalConstants.FROM_SAVE_IMAGE, 5);
                weakReference.get().activityContext.startActivity(intent);
                weakReference.get().dismiss();
                weakReference.get().activityContext.finish();

            });
            weakReference.get().setCancelable(true);
            weakReference.get().setCanceledOnTouchOutside(true);

        }

        @SuppressLint("StringFormatInvalid")
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int i3 = ((int) (((float) values[0]) * 100.0f)) / weakReference.get().pdfPageList.size();
            weakReference.get().tvDescription
                    .setText(weakReference.get().activityContext.getResources().getString(R.string.save_image_progress,
                            String.valueOf(values[0]), String.valueOf(weakReference.get().pdfPageList.size())));
            weakReference.get().tvProgressPercent.setText(
                    weakReference.get().activityContext.getResources().getString(R.string.x_percent_sign, String.valueOf(i3)));
            weakReference.get().progressBarView.setProgress(values[0]);

        }
    }

}
