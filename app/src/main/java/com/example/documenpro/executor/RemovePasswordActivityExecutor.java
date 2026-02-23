package com.example.documenpro.executor;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import com.example.documenpro.R;
import com.example.documenpro.model.PDFModel;
import com.docpro.scanner.engine.ProcessingTaskActivity;
import com.example.documenpro.utils.Utils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RemovePasswordActivityExecutor {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final WeakReference<ProcessingTaskActivity> weakReference;
    private final String mPassword;
    private final PDFModel mPdfModel;

    public RemovePasswordActivityExecutor(ProcessingTaskActivity activity, String mPassword, PDFModel mPdfModel) {
        this.weakReference = new WeakReference<>(activity);
        this.mPassword = mPassword;
        this.mPdfModel = mPdfModel;
        this.weakReference.get().btnClose.setOnClickListener(v -> {
            weakReference.get().finish();
        });
        this.weakReference.get().btnStopExecutor.setOnClickListener(v -> {
            executor.shutdownNow();
            weakReference.get().finish();
        });
    }

    public void shutdownNow() {
        executor.shutdownNow();
    }

    public void executeTask() {
        executor.execute(() -> {
            weakReference.get().runOnUiThread(() -> {
                weakReference.get().tvTool.setText(weakReference.get().getResources().getString(R.string.unlocking));
                weakReference.get().tvPercent.setText("0");
            });
            for (int i = 0; i < 100; i++) {
                SystemClock.sleep(20);
                publishProgress(i);
            }
            PDFModel pdfModelNew = Utils.removePassWordPDF(mPdfModel, mPassword);
            File file = new File(mPdfModel.getFileUri());
            Log.i("THANGPDFLOG", mPdfModel.getFileUri().toString());
            file.delete();
            weakReference.get().runOnUiThread(() -> {
                weakReference.get().tvPercent.setText("100");
                weakReference.get().motionLayout1.transitionToEnd();
                weakReference.get().motionLayout2.setVisibility(View.VISIBLE);
                weakReference.get().motionLayout2.transitionToEnd();
                weakReference.get().ltAnimBg.playAnimation();
                weakReference.get().ltAnimDone.playAnimation();
                weakReference.get().tvPdfName.setText(new File(pdfModelNew.getName()).getName());
                weakReference.get().tvPdfPath.setText(pdfModelNew.getAbsolutePath());
                weakReference.get().pdfModelFinal = pdfModelNew;
                Utils.displayPDFThumbnail(weakReference.get(), new File(pdfModelNew.getAbsolutePath()),
                        weakReference.get().imgThumbnail);
                weakReference.get().tvPageNumber
                        .setText(String.valueOf(Utils.getPageCountPDF(new File(pdfModelNew.getAbsolutePath()))));

            });

        });
    }

    private void publishProgress(int progress) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            if (weakReference.get() != null) {
                weakReference.get().tvPercent.setText(String.valueOf(progress));
                weakReference.get().progressBar.setProgress(progress);
            }
        });
    }
}
