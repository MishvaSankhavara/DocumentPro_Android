package com.example.documenpro.AppExecutor;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import com.example.documenpro.R;
import com.example.documenpro.model_reader.PDFReaderModel;
import com.docpro.scanner.engine.ProcessingTaskActivity;
import com.example.documenpro.utils.Utils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RemovePasswordExecutor {

    private final PDFReaderModel mPdfModel_removePW;
    private final String mPassword_removePW;
    private final WeakReference<ProcessingTaskActivity> weakReference_removePW;
    private final ExecutorService executor_removePW = Executors.newSingleThreadExecutor();

    private void publishProgress_removePW(int progress_removePW) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            if (weakReference_removePW.get() != null) {
                weakReference_removePW.get().tvPercent.setText(String.valueOf(progress_removePW));
                weakReference_removePW.get().progressBar.setProgress(progress_removePW);
            }
        });
    }

    public void executeTask_removePW() {
        executor_removePW.execute(() -> {

            weakReference_removePW.get().runOnUiThread(() -> {
                weakReference_removePW.get().tvTool.setText(weakReference_removePW.get().getResources().getString(R.string.unlocking));
                weakReference_removePW.get().tvPercent.setText("0");
            });

            for (int i = 0; i < 100; i++) {
                SystemClock.sleep(20);
                publishProgress_removePW(i);
            }

            PDFReaderModel pdfModelNew = Utils.removePassWordPDF(mPdfModel_removePW, mPassword_removePW);

            File file = new File(mPdfModel_removePW.getFileUri_PDFModel());
            Log.i("THANGPDFLOG", mPdfModel_removePW.getFileUri_PDFModel());
            file.delete();

            weakReference_removePW.get().runOnUiThread(() -> {

                weakReference_removePW.get().tvPercent.setText("100");
                weakReference_removePW.get().motionLayout1.transitionToEnd();
                weakReference_removePW.get().motionLayout2.setVisibility(View.VISIBLE);
                weakReference_removePW.get().motionLayout2.transitionToEnd();
                weakReference_removePW.get().ltAnimBg.playAnimation();
                weakReference_removePW.get().ltAnimDone.playAnimation();

                weakReference_removePW.get().tvPdfName.setText(new File(pdfModelNew.getName_PDFModel()).getName());

                weakReference_removePW.get().tvPdfPath.setText(pdfModelNew.getAbsolutePath_PDFModel());

                weakReference_removePW.get().pdfModelFinal = pdfModelNew;

                Utils.displayPDFThumbnail(weakReference_removePW.get(), new File(pdfModelNew.getAbsolutePath_PDFModel()), weakReference_removePW.get().imgThumbnail);

                weakReference_removePW.get().tvPageNumber.setText(String.valueOf(Utils.getPageCountPDF(new File(pdfModelNew.getAbsolutePath_PDFModel()))));
            });
        });
    }

    public void shutdownNow_removePW() {
        executor_removePW.shutdownNow();
    }

    public RemovePasswordExecutor(ProcessingTaskActivity activity_removePW, String mPassword_removePW, PDFReaderModel mPdfModel_removePW) {

        this.weakReference_removePW = new WeakReference<>(activity_removePW);

        this.mPassword_removePW = mPassword_removePW;
        this.mPdfModel_removePW = mPdfModel_removePW;

        this.weakReference_removePW.get().btnClose.setOnClickListener(v -> weakReference_removePW.get().finish());

        this.weakReference_removePW.get().btnStopExecutor.setOnClickListener(v -> {
            executor_removePW.shutdownNow();
            weakReference_removePW.get().finish();
        });
    }
}