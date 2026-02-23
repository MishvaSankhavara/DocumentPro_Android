package com.example.documenpro.AppExecutor;

import android.media.MediaScannerConnection;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;

import com.example.documenpro.GlobalConstant;
import com.example.documenpro.MyApplication;
import com.example.documenpro.R;
import com.example.documenpro.model.PDFModel;
import com.docpro.scanner.engine.ProcessingTaskActivity;
import com.example.documenpro.utils.Utils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SetPasswordManager {

    PDFModel mPdfModel_setPW;
    String mPassword_setPW;
    private final WeakReference<ProcessingTaskActivity> weakReference_setPW;
    private final ExecutorService executor_setPW = Executors.newSingleThreadExecutor();

    private void publishProgress_setPW(int progress_setPW) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            if (weakReference_setPW.get() != null) {
                weakReference_setPW.get().tvPercent.setText(String.valueOf(progress_setPW));
                weakReference_setPW.get().progressBar.setProgress(progress_setPW);
            }
        });
    }

    public void executeTask_setPW() {
        executor_setPW.execute(() -> {

            weakReference_setPW.get().runOnUiThread(() -> {
                weakReference_setPW.get().tvTool.setText(weakReference_setPW.get().getResources().getString(R.string.locking));
                weakReference_setPW.get().tvPercent.setText("0");
            });

            for (int i = 0; i < 100; i++) {
                SystemClock.sleep(20);
                publishProgress_setPW(i);
            }

            boolean isSetPassword = Utils.setPassPDF(mPdfModel_setPW.getAbsolutePath(), mPdfModel_setPW.getName(), mPassword_setPW);

            if (isSetPassword) {

                PDFModel pdfModel_setPW = new PDFModel();
                String path_setPW = GlobalConstant.RootDirectoryLock;
                String filePathNeW_setPW = path_setPW + "Locked" + mPdfModel_setPW.getName();

                final File file = new File(filePathNeW_setPW);

                pdfModel_setPW.setName(file.getName());
                pdfModel_setPW.setAbsolutePath(file.getAbsolutePath());
                pdfModel_setPW.setFileUri(file.getAbsolutePath());
                pdfModel_setPW.setProtected(true);
                pdfModel_setPW.setLength(file.length());
                pdfModel_setPW.setLastModified(file.lastModified());
                pdfModel_setPW.setDirectory(file.isDirectory());

                MediaScannerConnection.scanFile(MyApplication.getInstance(), new String[]{pdfModel_setPW.getAbsolutePath()}, new String[]{"application/pdf"}, null);

                weakReference_setPW.get().runOnUiThread(() -> {

                    weakReference_setPW.get().tvPercent.setText("100");
                    weakReference_setPW.get().motionLayout1.transitionToEnd();
                    weakReference_setPW.get().motionLayout2.setVisibility(View.VISIBLE);
                    weakReference_setPW.get().motionLayout2.transitionToEnd();
                    weakReference_setPW.get().ltAnimBg.playAnimation();
                    weakReference_setPW.get().ltAnimDone.playAnimation();

                    weakReference_setPW.get().tvPdfName.setText(new File(pdfModel_setPW.getName()).getName());

                    weakReference_setPW.get().tvPdfPath.setText(pdfModel_setPW.getAbsolutePath());

                    weakReference_setPW.get().pdfModelFinal = pdfModel_setPW;

                    weakReference_setPW.get().imgThumbnail.setImageResource(R.drawable.locked_pdf);

                    weakReference_setPW.get().tvPageNumber.setVisibility(View.GONE);
                });
            }
        });
    }

    public void shutdownNow_setPW() {
        executor_setPW.shutdownNow();
    }

    public SetPasswordManager(ProcessingTaskActivity activity_setPW, String mPassword_setPW, PDFModel mPdfModel_setPW) {

        this.weakReference_setPW = new WeakReference<>(activity_setPW);

        this.mPassword_setPW = mPassword_setPW;
        this.mPdfModel_setPW = mPdfModel_setPW;

        this.weakReference_setPW.get().btnClose.setOnClickListener(v -> weakReference_setPW.get().finish());

        this.weakReference_setPW.get().btnStopExecutor.setOnClickListener(v -> {
            executor_setPW.shutdownNow();
            weakReference_setPW.get().finish();
        });
    }
}