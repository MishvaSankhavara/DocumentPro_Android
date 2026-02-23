package com.example.documenpro.executor;

import android.media.MediaScannerConnection;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.example.documenpro.GlobalConstant;
import com.example.documenpro.MyApplication;
import com.example.documenpro.R;
import com.example.documenpro.model.PDFModel;
import com.docpro.scanner.engine.ProcessingTaskActivity;
import com.example.documenpro.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SplitFileExecutor {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final WeakReference<ProcessingTaskActivity> weakReference;
    ArrayList<Integer> numberPage;

    private final String pdfPath;
    private String generatedPDFPath;

    String mFileName;

    public SplitFileExecutor(ProcessingTaskActivity activity, String fileName, ArrayList<Integer> numberPages,
            String pdfPath) {
        this.weakReference = new WeakReference<>(activity);
        this.numberPage = numberPages;
        this.pdfPath = pdfPath;
        this.mFileName = fileName;
        this.weakReference.get().btnClose.setOnClickListener(v -> {
            weakReference.get().finish();
        });
        this.weakReference.get().btnStopExecutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executor.shutdownNow();
                weakReference.get().finish();
            }
        });
    }

    public void shutdownNow() {
        executor.shutdownNow();
    }

    public void executeTask() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                weakReference.get().runOnUiThread(() -> {
                    weakReference.get().tvTool
                            .setText(weakReference.get().getResources().getString(R.string.file_splitting));
                    weakReference.get().tvPercent.setText("0");
                });
                try {
                    Document doc;
                    PdfReader pr = new PdfReader(pdfPath);
                    PdfCopy pc;
                    File f = new File(pdfPath);
                    doc = new Document();
                    String splitPdfDocumentDir = GlobalConstant.RootDirectorySplit;
                    File file2 = new File(splitPdfDocumentDir);
                    if (!file2.exists()) {
                        file2.mkdirs();
                    }
                    ArrayList<Integer> pageRange = new ArrayList<>();
                    pageRange.add(numberPage.get(0));
                    pageRange.add(numberPage.get(numberPage.size() - 1));
                    pc = new PdfCopy(doc,
                            new FileOutputStream(splitPdfDocumentDir + mFileName + "page" + pageRange + ".pdf"));
                    generatedPDFPath = splitPdfDocumentDir + mFileName + "page" + pageRange + ".pdf";
                    doc.open();
                    for (int i = 0; i < numberPage.size(); i++) {
                        pc.addPage(pc.getImportedPage(pr, numberPage.get(i) + 1));
                        publishProgress(i);
                    }
                    doc.close();
                    MediaScannerConnection.scanFile(MyApplication.getInstance(), new String[] { generatedPDFPath },
                            new String[] { "application/pdf" }, null);

                    weakReference.get().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            weakReference.get().tvPercent.setText("100");
                            weakReference.get().motionLayout1.transitionToEnd();
                            weakReference.get().motionLayout2.setVisibility(View.VISIBLE);
                            weakReference.get().motionLayout2.transitionToEnd();
                            weakReference.get().ltAnimBg.playAnimation();
                            weakReference.get().ltAnimDone.playAnimation();
                            weakReference.get().tvPdfName.setText(mFileName);
                            weakReference.get().tvPdfPath.setText(generatedPDFPath);

                            File file = new File(generatedPDFPath);
                            PDFModel fileHolderModel = new PDFModel();
                            fileHolderModel.setName(file.getName());
                            fileHolderModel.setAbsolutePath(file.getAbsolutePath());
                            fileHolderModel.setFileUri(file.getAbsolutePath());
                            fileHolderModel.setLength(file.length());
                            fileHolderModel.setLastModified(file.lastModified());
                            fileHolderModel.setDirectory(file.isDirectory());
                            weakReference.get().pdfModelFinal = fileHolderModel;
                            Utils.displayPDFThumbnail(weakReference.get(), file, weakReference.get().imgThumbnail);
                            weakReference.get().tvPageNumber.setText(String.valueOf(Utils.getPageCountPDF(file)));

                        }
                    });

                } catch (Exception e) {
                    Log.e("error-->>", e.toString());

                }
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
