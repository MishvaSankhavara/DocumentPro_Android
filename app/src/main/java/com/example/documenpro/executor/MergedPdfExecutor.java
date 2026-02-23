package com.example.documenpro.executor;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.example.documenpro.R;
import com.example.documenpro.model.PDFModel;
import com.docpro.scanner.engine.ProcessingTaskActivity;
import com.example.documenpro.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MergedPdfExecutor {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final WeakReference<ProcessingTaskActivity> weakReference;
    String allPdfMergedDir;
    boolean mergeSuccess = true;

    int totalPageNumber = 1;
    int currentProgress = 0;
    String mergedFileName;
    String mergedFilePath;
    int numFiles;
    ArrayList<String> pdfPaths;

    public MergedPdfExecutor(ProcessingTaskActivity activity, ArrayList<String> arrayList, String mergedFileName) {
        this.weakReference = new WeakReference<>(activity);
        this.pdfPaths = arrayList;
        this.mergedFileName = mergedFileName;
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
        executor.execute(() -> {
            weakReference.get().runOnUiThread(() -> {
                weakReference.get().tvTool.setText(weakReference.get().getResources().getString(R.string.file_merging));
                weakReference.get().tvPercent.setText("0");
                for (int i = 0; i < pdfPaths.size(); i++) {
                    try {
                        PdfReader pdfReader2 = new PdfReader(pdfPaths.get(i));
                        int numberPage = pdfReader2.getNumberOfPages();
                        totalPageNumber = totalPageNumber + numberPage;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                weakReference.get().progressBar.setMax(totalPageNumber);
            });
            allPdfMergedDir = Environment.getExternalStorageDirectory() + "/Documents/AllPdf/Merged/";
            try {
                allPdfMergedDir = Environment.getExternalStorageDirectory() + "/Documents/AllPdf/Merged/";
                File file = new File(allPdfMergedDir);
                if (!file.exists()) {
                    file.mkdirs();
                }
                mergedFilePath = allPdfMergedDir + mergedFileName + ".pdf";
                numFiles = pdfPaths.size();
                Document document = new Document();

                PdfCopy pdfCopy = new PdfCopy(document, new FileOutputStream(mergedFilePath));
                document.open();
                for (String pdfReader : pdfPaths) {
                    PdfReader pdfReader2 = new PdfReader(pdfReader);
                    int numberOfPages = pdfReader2.getNumberOfPages();
                    for (int i = 1; i <= numberOfPages; i++) {
                        pdfCopy.addPage(pdfCopy.getImportedPage(pdfReader2, i));
                        currentProgress = currentProgress + 1;

                        publishProgress(currentProgress);
                        Log.i("Thang1234", currentProgress + "sss");
                    }
                }
                document.close();
            } catch (DocumentException | IOException e) {
                throw new RuntimeException(e);
            }
            weakReference.get().runOnUiThread(() -> {
                weakReference.get().tvPercent.setText("100");
                weakReference.get().motionLayout1.transitionToEnd();
                weakReference.get().motionLayout2.setVisibility(View.VISIBLE);
                weakReference.get().motionLayout2.transitionToEnd();
                weakReference.get().ltAnimBg.playAnimation();
                weakReference.get().ltAnimDone.playAnimation();

                weakReference.get().tvPdfPath.setText(mergedFilePath);
                weakReference.get().tvPdfName.setText(mergedFileName);

                File file1 = new File(mergedFilePath);
                PDFModel fileHolderModel = new PDFModel();
                fileHolderModel.setName(file1.getName());
                fileHolderModel.setAbsolutePath(file1.getAbsolutePath());
                fileHolderModel.setFileUri(file1.getAbsolutePath());
                fileHolderModel.setLength(file1.length());
                fileHolderModel.setLastModified(file1.lastModified());
                fileHolderModel.setDirectory(file1.isDirectory());
                weakReference.get().pdfModelFinal = fileHolderModel;
                Utils.displayPDFThumbnail(weakReference.get(), file1, weakReference.get().imgThumbnail);
                weakReference.get().tvPageNumber.setText(String.valueOf(Utils.getPageCountPDF(file1)));

            });

        });
    }

    private void publishProgress(int progress) {
        int i3 = ((int) (((float) progress) * 100.0f)) / this.totalPageNumber;
        String sb = i3 + "";
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            if (weakReference.get() != null) {
                weakReference.get().tvPercent.setText(sb);
                weakReference.get().progressBar.setProgress(progress);
            }

        });

    }
}
