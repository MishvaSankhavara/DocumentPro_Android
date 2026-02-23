package com.example.documenpro.executor;

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

public class SetPasswordActivityExecutor {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final WeakReference<ProcessingTaskActivity> weakReference;

    String mPassword;
    PDFModel mPdfModel;

    public SetPasswordActivityExecutor(ProcessingTaskActivity activity, String mPassword, PDFModel mPdfModel) {
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
                weakReference.get().tvTool.setText(weakReference.get().getResources().getString(R.string.locking));
                weakReference.get().tvPercent.setText("0");
            });
            for (int i = 0; i < 100; i++) {
                SystemClock.sleep(20);
                publishProgress(i);
            }
            boolean isSetPassword = Utils.setPassPDF(mPdfModel.getAbsolutePath(), mPdfModel.getName(), mPassword);
            if (isSetPassword) {
                PDFModel pdfModel = new PDFModel();
                String path = GlobalConstant.RootDirectoryLock;
                String filePathNeW = path + "Locked" + mPdfModel.getName();
                final File file = new File(filePathNeW);
                pdfModel.setName(file.getName());
                pdfModel.setAbsolutePath(file.getAbsolutePath());
                pdfModel.setFileUri(file.getAbsolutePath());
                pdfModel.setProtected(true);
                pdfModel.setLength(file.length());
                pdfModel.setLastModified(file.lastModified());
                pdfModel.setDirectory(file.isDirectory());
                MediaScannerConnection.scanFile(MyApplication.getInstance(),
                        new String[] { pdfModel.getAbsolutePath() }, new String[] { "application/pdf" }, null);
                weakReference.get().runOnUiThread(() -> {
                    weakReference.get().tvPercent.setText("100");
                    weakReference.get().motionLayout1.transitionToEnd();
                    weakReference.get().motionLayout2.setVisibility(View.VISIBLE);
                    weakReference.get().motionLayout2.transitionToEnd();
                    weakReference.get().ltAnimBg.playAnimation();
                    weakReference.get().ltAnimDone.playAnimation();
                    weakReference.get().tvPdfName.setText(new File(pdfModel.getName()).getName());
                    weakReference.get().tvPdfPath.setText(pdfModel.getAbsolutePath());
                    weakReference.get().pdfModelFinal = pdfModel;
                    // Utils.displayPDFThumbnail(weakReference.get(), file,
                    // weakReference.get().imgThumbnail);

                    weakReference.get().imgThumbnail.setImageResource(R.drawable.locked_pdf);
                    weakReference.get().tvPageNumber.setVisibility(View.GONE);
                });
            }

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
