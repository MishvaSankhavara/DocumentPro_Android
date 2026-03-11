package com.example.documenpro.AppExecutor;

import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Handler;
import android.os.Looper;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.parser.PdfImageObject;
import com.example.documenpro.AppGlobalConstants;
import com.example.documenpro.DocumentMyApplication;
import com.example.documenpro.R;
import com.example.documenpro.model_reader.PDFReaderModel;
import com.docpro.scanner.engine.ProcessingTaskActivity;
import com.example.documenpro.utils.ImageProcessorUtils;
import com.example.documenpro.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileCompressionExecutor {

    int xrefSize_FileCompression;
    private String uncompressedFileSize_FileCompression;
    private Long uncompressedFileLength_FileCompression;
    private String reducedPercent_FileCompression;
    private final String pdfPath_FileCompression;
    private boolean isEncrypted_FileCompression = false;
    private int compressionQuality_FileCompression = 80;
    private String compressedPDF_FileCompression;
    private String compressedFileSize_FileCompression;
    private Long compressedFileLength_FileCompression;
    private String allPdfDocumentDir_FileCompression;
    private final WeakReference<ProcessingTaskActivity> weakReference_FileCompression;
    private final ExecutorService executor_FileCompression = Executors.newSingleThreadExecutor();

    private void publishProgress_FileCompression(int progress_FileCompression) {
        int i3 = ((int) (((float) progress_FileCompression) * 100.0f)) / this.xrefSize_FileCompression;
        String sb = i3 + "";
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            if (weakReference_FileCompression.get() != null) {
                weakReference_FileCompression.get().tvPercent.setText(sb);
                weakReference_FileCompression.get().progressBar.setProgress(progress_FileCompression);
            }
        });
    }

    public void executeTask_FileCompression() {
        executor_FileCompression.execute(() -> {
            weakReference_FileCompression.get().runOnUiThread(() -> {
                weakReference_FileCompression.get().tvTool.setText(
                        weakReference_FileCompression.get().getResources().getString(R.string.message_compressing));
                weakReference_FileCompression.get().tvPercent.setText("0");
            });

            File file = new File(pdfPath_FileCompression);
            String strPdfName = file.getName();
            uncompressedFileLength_FileCompression = file.length();
            uncompressedFileSize_FileCompression = Formatter.formatShortFileSize(weakReference_FileCompression.get(),
                    uncompressedFileLength_FileCompression);

            allPdfDocumentDir_FileCompression = AppGlobalConstants.DIRECTORY_COMPRESSED_PDF;
            compressedPDF_FileCompression = allPdfDocumentDir_FileCompression + Utils.removeFileExtension(strPdfName)
                    + "-Compressed.pdf";

            File file2 = new File(allPdfDocumentDir_FileCompression);
            if (!file2.exists()) {
                file2.mkdirs();
            }

            try {
                PdfReader pdfReader_FileCompression = new PdfReader(pdfPath_FileCompression);

                if (pdfReader_FileCompression.isEncrypted()) {
                    isEncrypted_FileCompression = true;
                }

                xrefSize_FileCompression = pdfReader_FileCompression.getXrefSize();
                weakReference_FileCompression.get().progressBar.setMax(xrefSize_FileCompression);

                for (int i = 0; i < xrefSize_FileCompression; i++) {
                    PdfObject pdfObject = pdfReader_FileCompression.getPdfObject(i);
                    if (pdfObject != null && pdfObject.isStream()) {

                        PRStream prStream_FileCompression = (PRStream) pdfObject;
                        PdfObject pdfObject2_FileCompression = prStream_FileCompression.get(PdfName.SUBTYPE);

                        if (pdfObject2_FileCompression != null
                                && pdfObject2_FileCompression.toString().equals(PdfName.IMAGE.toString())) {
                            try {
                                Bitmap compressedBitmap_FileCompression = ImageProcessorUtils.getInstant()
                                        .getCompressedBitmapPDF(
                                                new PdfImageObject(prStream_FileCompression).getImageAsBytes());

                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                                compressedBitmap_FileCompression.compress(Bitmap.CompressFormat.JPEG,
                                        compressionQuality_FileCompression, byteArrayOutputStream);

                                prStream_FileCompression.setData(byteArrayOutputStream.toByteArray(), false, 9);

                                prStream_FileCompression.put(PdfName.FILTER, PdfName.DCTDECODE);

                                byteArrayOutputStream.close();
                                prStream_FileCompression.clear();

                                prStream_FileCompression.setData(byteArrayOutputStream.toByteArray(), false, 0);

                                prStream_FileCompression.put(PdfName.TYPE, PdfName.XOBJECT);
                                prStream_FileCompression.put(PdfName.SUBTYPE, PdfName.IMAGE);
                                prStream_FileCompression.put(PdfName.FILTER, PdfName.DCTDECODE);
                                prStream_FileCompression.put(PdfName.WIDTH,
                                        new PdfNumber(compressedBitmap_FileCompression.getWidth()));
                                prStream_FileCompression.put(PdfName.HEIGHT,
                                        new PdfNumber(compressedBitmap_FileCompression.getHeight()));
                                prStream_FileCompression.put(PdfName.BITSPERCOMPONENT, new PdfNumber(8));
                                prStream_FileCompression.put(PdfName.COLORSPACE, PdfName.DEVICERGB);

                                if (!compressedBitmap_FileCompression.isRecycled()) {
                                    compressedBitmap_FileCompression.recycle();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        publishProgress_FileCompression(i + 1);
                    }
                }

                pdfReader_FileCompression.removeUnusedObjects();
                PdfStamper pdfStamper = new PdfStamper(pdfReader_FileCompression,
                        new FileOutputStream(compressedPDF_FileCompression));

                pdfStamper.setFullCompression();
                pdfStamper.close();

                compressedFileLength_FileCompression = new File(compressedPDF_FileCompression).length();

                compressedFileSize_FileCompression = Formatter.formatShortFileSize(weakReference_FileCompression.get(),
                        compressedFileLength_FileCompression);

                reducedPercent_FileCompression = (100 - ((int) ((compressedFileLength_FileCompression * 100)
                        / uncompressedFileLength_FileCompression))) + "%";

                MediaScannerConnection.scanFile(DocumentMyApplication.getInstance(),
                        new String[] { compressedPDF_FileCompression }, new String[] { "application/pdf" }, null);

                if (!isEncrypted_FileCompression) {

                    if (weakReference_FileCompression.get() != null) {
                        weakReference_FileCompression.get().runOnUiThread(() -> {

                            weakReference_FileCompression.get().tvPercent.setText("100");
                            weakReference_FileCompression.get().motionLayout1.transitionToEnd();
                            weakReference_FileCompression.get().motionLayout2.setVisibility(View.VISIBLE);
                            weakReference_FileCompression.get().motionLayout2.transitionToEnd();
                            weakReference_FileCompression.get().ltAnimBg.playAnimation();
                            weakReference_FileCompression.get().ltAnimDone.playAnimation();

                            File file1 = new File(compressedPDF_FileCompression);

                            weakReference_FileCompression.get().tvPdfName.setText(file1.getName());
                            weakReference_FileCompression.get().tvPdfPath.setText(compressedPDF_FileCompression);

                            String sb2 = weakReference_FileCompression.get().getResources()
                                    .getString(R.string.label_reduced_from) + " " + uncompressedFileSize_FileCompression
                                    + " " + weakReference_FileCompression.get().getResources().getString(R.string.to)
                                    + " " + compressedFileSize_FileCompression;

                            weakReference_FileCompression.get().tvResult.setText(sb2);

                            PDFReaderModel fileHolderModel_FileCompression = new PDFReaderModel();

                            fileHolderModel_FileCompression.setName_PDFModel(file1.getName());
                            fileHolderModel_FileCompression.setAbsolutePath_PDFModel(file1.getAbsolutePath());
                            fileHolderModel_FileCompression.setFileUri_PDFModel(file1.getAbsolutePath());
                            fileHolderModel_FileCompression.setLength_PDFModel(file1.length());
                            fileHolderModel_FileCompression.setLastModified_PDFModel(file1.lastModified());
                            fileHolderModel_FileCompression.setDirectory_PDFModel(file1.isDirectory());

                            weakReference_FileCompression.get().pdfModelFinal = fileHolderModel_FileCompression;

                            Utils.displayPDFThumbnail(weakReference_FileCompression.get(), file1,
                                    weakReference_FileCompression.get().imgThumbnail);

                            weakReference_FileCompression.get().tvPageNumber
                                    .setText(String.valueOf(Utils.getPageCountPDF(file1)));
                        });
                    }

                } else {
                    weakReference_FileCompression.get().finish();
                    Toast.makeText(weakReference_FileCompression.get(), R.string.toast_file_protected_unprotect,
                            Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                Log.e(AppGlobalConstants.DOC_APP_FATAL, "Error message", e);
            }
        });
    }

    public void shutdownNow_FileCompression() {
        executor_FileCompression.shutdownNow();
    }

    public FileCompressionExecutor(ProcessingTaskActivity activity_FileCompression, String string_FileCompression) {

        this.weakReference_FileCompression = new WeakReference<>(activity_FileCompression);

        this.pdfPath_FileCompression = string_FileCompression;

        this.weakReference_FileCompression.get().btnClose
                .setOnClickListener(v -> weakReference_FileCompression.get().finish());

        this.weakReference_FileCompression.get().btnStopExecutor.setOnClickListener(v -> {
            executor_FileCompression.shutdownNow();
            weakReference_FileCompression.get().finish();
        });
    }
}