package com.example.documenpro.AppExecutor;

import android.media.MediaScannerConnection;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;

import com.example.documenpro.GlobalConstant;
import com.example.documenpro.MyApplication;
import com.example.documenpro.R;
import com.example.documenpro.model_reader.PDFReaderModel;
import com.docpro.scanner.engine.ProcessingTaskActivity;
import com.example.documenpro.utils.Utils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SetPasswordManager {

    PDFReaderModel mPdfModel_setPW;
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

            boolean isSetPassword = Utils.encryptPdfFile(mPdfModel_setPW.getAbsolutePath_PDFModel(), mPdfModel_setPW.getName_PDFModel(), mPassword_setPW);

            if (isSetPassword) {

                PDFReaderModel pdfModel_setPW = new PDFReaderModel();
                String path_setPW = GlobalConstant.RootDirectoryLock;
                String filePathNeW_setPW = path_setPW + "Locked" + mPdfModel_setPW.getName_PDFModel();

                final File file = new File(filePathNeW_setPW);

                pdfModel_setPW.setName_PDFModel(file.getName());
                pdfModel_setPW.setAbsolutePath_PDFModel(file.getAbsolutePath());
                pdfModel_setPW.setFileUri_PDFModel(file.getAbsolutePath());
                pdfModel_setPW.setProtected_PDFModel(true);
                pdfModel_setPW.setLength_PDFModel(file.length());
                pdfModel_setPW.setLastModified_PDFModel(file.lastModified());
                pdfModel_setPW.setDirectory_PDFModel(file.isDirectory());

                MediaScannerConnection.scanFile(MyApplication.getInstance(), new String[]{pdfModel_setPW.getAbsolutePath_PDFModel()}, new String[]{"application/pdf"}, null);

                weakReference_setPW.get().runOnUiThread(() -> {

                    weakReference_setPW.get().tvPercent.setText("100");
                    weakReference_setPW.get().motionLayout1.transitionToEnd();
                    weakReference_setPW.get().motionLayout2.setVisibility(View.VISIBLE);
                    weakReference_setPW.get().motionLayout2.transitionToEnd();
                    weakReference_setPW.get().ltAnimBg.playAnimation();
                    weakReference_setPW.get().ltAnimDone.playAnimation();

                    weakReference_setPW.get().tvPdfName.setText(new File(pdfModel_setPW.getName_PDFModel()).getName());

                    weakReference_setPW.get().tvPdfPath.setText(pdfModel_setPW.getAbsolutePath_PDFModel());

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

    public SetPasswordManager(ProcessingTaskActivity activity_setPW, String mPassword_setPW, PDFReaderModel mPdfModel_setPW) {

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