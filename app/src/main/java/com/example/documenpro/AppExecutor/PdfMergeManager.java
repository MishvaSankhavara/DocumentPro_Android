package com.example.documenpro.AppExecutor;

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
import com.example.documenpro.model_reader.PDFReaderModel;
import com.docpro.scanner.engine.ProcessingTaskActivity;
import com.example.documenpro.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PdfMergeManager {

    ArrayList<String> pdfPaths_PdfMergeManager;
    int numFiles_PdfMergeManager;
    String mergedFilePath_PdfMergeManager;
    String mergedFileName_PdfMergeManager;
    int currentProgress_PdfMergeManager = 0;
    int totalPageNumber_PdfMergeManager = 1;
    String allPdfMergedDir_PdfMergeManager;
    private final WeakReference<ProcessingTaskActivity> weakReference_PdfMergeManager;
    private final ExecutorService executor_PdfMergeManager = Executors.newSingleThreadExecutor();

    private void publishProgress_PdfMergeManager(int progress_PdfMergeManager) {
        int i3 = ((int) (((float) progress_PdfMergeManager) * 100.0f)) / this.totalPageNumber_PdfMergeManager;
        String sb = i3 + "";
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            if (weakReference_PdfMergeManager.get() != null) {
                weakReference_PdfMergeManager.get().tvPercent.setText(sb);
                weakReference_PdfMergeManager.get().progressBar.setProgress(progress_PdfMergeManager);
            }
        });
    }

    public void executeTask_PdfMergeManager() {
        executor_PdfMergeManager.execute(() -> {

            weakReference_PdfMergeManager.get().runOnUiThread(() -> {
                weakReference_PdfMergeManager.get().tvTool.setText(weakReference_PdfMergeManager.get().getResources().getString(R.string.message_merging_files));
                weakReference_PdfMergeManager.get().tvPercent.setText("0");

                for (int i = 0; i < pdfPaths_PdfMergeManager.size(); i++) {
                    try {
                        PdfReader pdfReader2_PdfMergeManager = new PdfReader(pdfPaths_PdfMergeManager.get(i));
                        int numberPage_PdfMergeManager = pdfReader2_PdfMergeManager.getNumberOfPages();
                        totalPageNumber_PdfMergeManager = totalPageNumber_PdfMergeManager + numberPage_PdfMergeManager;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                weakReference_PdfMergeManager.get().progressBar.setMax(totalPageNumber_PdfMergeManager);
            });

            allPdfMergedDir_PdfMergeManager = Environment.getExternalStorageDirectory() + "/Documents/AllPdf/Merged/";

            try {

                File file = new File(allPdfMergedDir_PdfMergeManager);
                if (!file.exists()) {
                    file.mkdirs();
                }

                mergedFilePath_PdfMergeManager = allPdfMergedDir_PdfMergeManager + mergedFileName_PdfMergeManager + ".pdf";

                numFiles_PdfMergeManager = pdfPaths_PdfMergeManager.size();

                Document document = new Document();
                PdfCopy pdfCopy = new PdfCopy(document, new FileOutputStream(mergedFilePath_PdfMergeManager));

                document.open();

                for (String pdfReader : pdfPaths_PdfMergeManager) {

                    PdfReader pdfReader2 = new PdfReader(pdfReader);
                    int numberOfPages = pdfReader2.getNumberOfPages();

                    for (int i = 1; i <= numberOfPages; i++) {
                        pdfCopy.addPage(pdfCopy.getImportedPage(pdfReader2, i));

                        currentProgress_PdfMergeManager = currentProgress_PdfMergeManager + 1;

                        publishProgress_PdfMergeManager(currentProgress_PdfMergeManager);

                        Log.i("Thang1234", currentProgress_PdfMergeManager + "sss");
                    }
                }

                document.close();

            } catch (DocumentException | IOException e) {
                throw new RuntimeException(e);
            }

            weakReference_PdfMergeManager.get().runOnUiThread(() -> {

                weakReference_PdfMergeManager.get().tvPercent.setText("100");
                weakReference_PdfMergeManager.get().motionLayout1.transitionToEnd();
                weakReference_PdfMergeManager.get().motionLayout2.setVisibility(View.VISIBLE);
                weakReference_PdfMergeManager.get().motionLayout2.transitionToEnd();
                weakReference_PdfMergeManager.get().ltAnimBg.playAnimation();
                weakReference_PdfMergeManager.get().ltAnimDone.playAnimation();

                weakReference_PdfMergeManager.get().tvPdfPath.setText(mergedFilePath_PdfMergeManager);

                weakReference_PdfMergeManager.get().tvPdfName.setText(mergedFileName_PdfMergeManager);

                File file1_PdfMergeManager = new File(mergedFilePath_PdfMergeManager);

                PDFReaderModel fileHolderModel_PdfMergeManager = new PDFReaderModel();

                fileHolderModel_PdfMergeManager.setName_PDFModel(file1_PdfMergeManager.getName());
                fileHolderModel_PdfMergeManager.setAbsolutePath_PDFModel(file1_PdfMergeManager.getAbsolutePath());
                fileHolderModel_PdfMergeManager.setFileUri_PDFModel(file1_PdfMergeManager.getAbsolutePath());
                fileHolderModel_PdfMergeManager.setLength_PDFModel(file1_PdfMergeManager.length());
                fileHolderModel_PdfMergeManager.setLastModified_PDFModel(file1_PdfMergeManager.lastModified());
                fileHolderModel_PdfMergeManager.setDirectory_PDFModel(file1_PdfMergeManager.isDirectory());

                weakReference_PdfMergeManager.get().pdfModelFinal = fileHolderModel_PdfMergeManager;

                Utils.displayPDFThumbnail(weakReference_PdfMergeManager.get(), file1_PdfMergeManager, weakReference_PdfMergeManager.get().imgThumbnail);

                weakReference_PdfMergeManager.get().tvPageNumber.setText(String.valueOf(Utils.getPageCountPDF(file1_PdfMergeManager)));

            });

        });
    }

    public void shutdownNow_PdfMergeManager() {
        executor_PdfMergeManager.shutdownNow();
    }

    public PdfMergeManager(ProcessingTaskActivity activity_PdfMergeManager, ArrayList<String> arrayList_PdfMergeManager, String mergedFileName_PdfMergeManager) {

        this.weakReference_PdfMergeManager = new WeakReference<>(activity_PdfMergeManager);

        this.pdfPaths_PdfMergeManager = arrayList_PdfMergeManager;
        this.mergedFileName_PdfMergeManager = mergedFileName_PdfMergeManager;

        this.weakReference_PdfMergeManager.get().btnClose.setOnClickListener(v -> weakReference_PdfMergeManager.get().finish());

        this.weakReference_PdfMergeManager.get().btnStopExecutor.setOnClickListener(v -> {
            executor_PdfMergeManager.shutdownNow();
            weakReference_PdfMergeManager.get().finish();
        });
    }
}