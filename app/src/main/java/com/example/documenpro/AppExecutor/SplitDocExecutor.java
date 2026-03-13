package com.example.documenpro.AppExecutor;

import android.media.MediaScannerConnection;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.example.documenpro.AppGlobalConstants;
import com.example.documenpro.DocumentMyApplication;
import com.example.documenpro.R;
import com.example.documenpro.model_reader.PDFReaderModel;
import com.docpro.scanner.engine.ProcessingTaskActivity;
import com.example.documenpro.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SplitDocExecutor {

    String mFileName_SplitDoc;
    private String generatedPDFPath_SplitDoc;
    private final String pdfPath_SplitDoc;
    ArrayList<Integer> numberPage_SplitDoc;
    private final WeakReference<ProcessingTaskActivity> weakReference_SplitDoc;
    private final ExecutorService executor_SplitDoc = Executors.newSingleThreadExecutor();

    private void publishProgress_SplitDoc(int progress_SplitDoc) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            if (weakReference_SplitDoc.get() != null) {
                weakReference_SplitDoc.get().tvPercent.setText(String.valueOf(progress_SplitDoc));
                weakReference_SplitDoc.get().progressBar.setProgress(progress_SplitDoc);
            }
        });
    }

    public void executeTask_SplitDoc() {
        executor_SplitDoc.execute(() -> {

            weakReference_SplitDoc.get().runOnUiThread(() -> {
                weakReference_SplitDoc.get().tvTool.setText(weakReference_SplitDoc.get().getResources().getString(R.string.message_splitting_files));
                weakReference_SplitDoc.get().tvPercent.setText("0");
            });

            try {

                PdfReader pr_SplitDoc = new PdfReader(pdfPath_SplitDoc);
                Document doc_SplitDoc = new Document();

                String splitPdfDocumentDir_SplitDoc = AppGlobalConstants.DIRECTORY_SPLIT_PDF;

                File file2_SplitDoc = new File(splitPdfDocumentDir_SplitDoc);

                if (!file2_SplitDoc.exists()) {
                    file2_SplitDoc.mkdirs();
                }

                ArrayList<Integer> pageRange_SplitDoc = new ArrayList<>();

                pageRange_SplitDoc.add(numberPage_SplitDoc.get(0));
                pageRange_SplitDoc.add(numberPage_SplitDoc.get(numberPage_SplitDoc.size() - 1));

                String outputPath = splitPdfDocumentDir_SplitDoc + mFileName_SplitDoc + "page" + pageRange_SplitDoc + ".pdf";

                PdfCopy pc_SplitDoc = new PdfCopy(doc_SplitDoc, new FileOutputStream(outputPath));

                generatedPDFPath_SplitDoc = outputPath;

                doc_SplitDoc.open();

                for (int i = 0; i < numberPage_SplitDoc.size(); i++) {
                    pc_SplitDoc.addPage(pc_SplitDoc.getImportedPage(pr_SplitDoc, numberPage_SplitDoc.get(i) + 1));
                    publishProgress_SplitDoc(i);
                }

                doc_SplitDoc.close();

                MediaScannerConnection.scanFile(DocumentMyApplication.getInstance(), new String[]{generatedPDFPath_SplitDoc}, new String[]{"application/pdf"}, null);

                weakReference_SplitDoc.get().runOnUiThread(() -> {

                    weakReference_SplitDoc.get().tvPercent.setText("100");
                    weakReference_SplitDoc.get().motionLayout1.transitionToEnd();
                    weakReference_SplitDoc.get().motionLayout2.setVisibility(View.VISIBLE);
                    weakReference_SplitDoc.get().motionLayout2.transitionToEnd();
                    weakReference_SplitDoc.get().ltAnimBg.playAnimation();
                    weakReference_SplitDoc.get().ltAnimDone.playAnimation();
                    weakReference_SplitDoc.get().tvPdfName.setText(mFileName_SplitDoc);
                    weakReference_SplitDoc.get().tvPdfPath.setText(generatedPDFPath_SplitDoc);

                    File file_SplitDoc = new File(generatedPDFPath_SplitDoc);

                    PDFReaderModel fileHolderModel_SplitDoc = new PDFReaderModel();

                    fileHolderModel_SplitDoc.setName_PDFModel(file_SplitDoc.getName());
                    fileHolderModel_SplitDoc.setAbsolutePath_PDFModel(file_SplitDoc.getAbsolutePath());
                    fileHolderModel_SplitDoc.setFileUri_PDFModel(file_SplitDoc.getAbsolutePath());
                    fileHolderModel_SplitDoc.setLength_PDFModel(file_SplitDoc.length());
                    fileHolderModel_SplitDoc.setLastModified_PDFModel(file_SplitDoc.lastModified());
                    fileHolderModel_SplitDoc.setDirectory_PDFModel(file_SplitDoc.isDirectory());

                    weakReference_SplitDoc.get().pdfModelFinal = fileHolderModel_SplitDoc;

                    Utils.displayPDFThumbnail(weakReference_SplitDoc.get(), file_SplitDoc, weakReference_SplitDoc.get().imgThumbnail);

                    weakReference_SplitDoc.get().tvPageNumber.setText(String.valueOf(Utils.getPageCountPDF(file_SplitDoc)));

                    // After showing completion UI, redirect to ResultViewerActivity (Split tab)
                    weakReference_SplitDoc.get().redirectToResultsAfterDone();
                });

            } catch (Exception error_SplitDoc) {
                Log.e("error-->>", error_SplitDoc.toString());
            }
        });
    }

    public void shutdownNow_SplitDoc() {
        executor_SplitDoc.shutdownNow();
    }

    public SplitDocExecutor(ProcessingTaskActivity activity_SplitDoc, String fileName_SplitDoc, ArrayList<Integer> numberPages_SplitDoc, String pdfPath) {

        this.weakReference_SplitDoc = new WeakReference<>(activity_SplitDoc);

        this.numberPage_SplitDoc = numberPages_SplitDoc;
        this.pdfPath_SplitDoc = pdfPath;
        this.mFileName_SplitDoc = fileName_SplitDoc;

        this.weakReference_SplitDoc.get().btnClose.setOnClickListener(v -> weakReference_SplitDoc.get().finish());

        this.weakReference_SplitDoc.get().btnStopExecutor.setOnClickListener(v -> {
            executor_SplitDoc.shutdownNow();
            weakReference_SplitDoc.get().finish();
        });
    }
}