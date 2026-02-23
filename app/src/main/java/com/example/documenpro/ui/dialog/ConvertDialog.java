package com.example.documenpro.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.documenpro.GlobalConstant;
import com.example.documenpro.R;
import com.example.documenpro.model_reader.PDFPageModel;
import com.docpro.scanner.result.ResultViewerActivity;
import com.example.documenpro.ui.activities.ShareImageActivity;
import com.example.documenpro.ui.customviews.HorizontalProgressBar;
import com.example.documenpro.utils.Utils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ConvertDialog extends Dialog {
    private HorizontalProgressBar progressBar;
    private TextView tvDes;
    private ArrayList<PDFPageModel> listSave;

    private TextView btnCancel;
    private TextView btnViewImage;
    private TextView tvPercent;
    private ShareImageActivity mContext;

    public ConvertDialog(@NonNull ShareImageActivity context, ArrayList<PDFPageModel> arrayList) {
        super(context);
        this.mContext = context;
        this.listSave = arrayList;
        setContentView(R.layout.dialog_pdf_to_photo);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        initViews();

        initData();
        new CopyImage(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void initData() {

        tvDes.setVisibility(View.VISIBLE);
        btnViewImage.setVisibility(View.GONE);
        btnCancel.setVisibility(View.VISIBLE);

    }

    private void initViews() {
        tvDes = findViewById(R.id.tvDescription);
        progressBar = findViewById(R.id.progress_bar);
        btnViewImage = findViewById(R.id.tv_right_tip);
        btnCancel = findViewById(R.id.tv_left_tip);
        tvPercent = findViewById(R.id.tvPercent);
    }

    private static class CopyImage extends AsyncTask<Void, Integer, Void> {

        WeakReference<ConvertDialog> weakReference;

        public CopyImage(ConvertDialog progressSaveDialog) {
            this.weakReference = new WeakReference<>(progressSaveDialog);

            this.weakReference.get().btnCancel.setOnClickListener(view -> {
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
            weakReference.get().mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    weakReference.get().progressBar.setMax(weakReference.get().listSave.size());

                }
            });

            File pathFolder = new File(GlobalConstant.RootDirectoryImage);
            if (!pathFolder.exists()) {
                pathFolder.mkdirs();
            }

            for (int i = 0; i < weakReference.get().listSave.size(); i++) {
                String fileName = Utils.getFileNameFromUri(weakReference.get().listSave.get(i).getThumbnailUri_PDFPageModel());
                String pathCopy = pathFolder + "/" + fileName;
                Utils.copyFile(weakReference.get().mContext, weakReference.get().listSave.get(i).getThumbnailUri_PDFPageModel(),
                        pathCopy);
                publishProgress(i + 1);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            weakReference.get().btnViewImage.setVisibility(View.VISIBLE);
            weakReference.get().btnCancel.setVisibility(View.GONE);

            weakReference.get().btnViewImage.setOnClickListener(view -> {
                Intent intent = new Intent(weakReference.get().mContext, ResultViewerActivity.class);
                intent.putExtra(GlobalConstant.FROM_SAVE_IMAGE, 5);
                weakReference.get().mContext.startActivity(intent);
                weakReference.get().dismiss();
                weakReference.get().mContext.finish();

            });
            weakReference.get().setCancelable(true);
            weakReference.get().setCanceledOnTouchOutside(true);

        }

        @SuppressLint("StringFormatInvalid")
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int i3 = ((int) (((float) values[0]) * 100.0f)) / weakReference.get().listSave.size();
            weakReference.get().tvDes
                    .setText(weakReference.get().mContext.getResources().getString(R.string.save_image_progress,
                            String.valueOf(values[0]), String.valueOf(weakReference.get().listSave.size())));
            weakReference.get().tvPercent.setText(
                    weakReference.get().mContext.getResources().getString(R.string.x_percent_sign, String.valueOf(i3)));
            weakReference.get().progressBar.setProgress(values[0]);

        }
    }

}
