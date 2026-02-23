package com.example.documenpro.executor;

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
import com.example.documenpro.GlobalConstant;
import com.example.documenpro.MyApplication;
import com.example.documenpro.R;
import com.example.documenpro.model.PDFModel;
import com.docpro.scanner.engine.ProcessingTaskActivity;
import com.example.documenpro.utils.ImageUtils;
import com.example.documenpro.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompressActivityExecutor {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final WeakReference<ProcessingTaskActivity> weakReference;
    private String allPdfDocumentDir;
    private Long compressedFileLength;
    private String compressedFileSize;
    private String compressedPDF;
    private int compressionQuality;
    private boolean isEncrypted = false;
    private final String pdfPath;
    private String reducedPercent;
    private Long uncompressedFileLength;
    private String uncompressedFileSize;
    int xrefSize;

    public CompressActivityExecutor(ProcessingTaskActivity activity, String string) {
        this.weakReference = new WeakReference<>(activity);
        this.pdfPath = string;
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
                weakReference.get().tvTool.setText(weakReference.get().getResources().getString(R.string.compressing));
                weakReference.get().tvPercent.setText("0");
            });
            File file = new File(pdfPath);
            String strPdfName = file.getName();
            uncompressedFileLength = file.length();
            uncompressedFileSize = Formatter.formatShortFileSize(weakReference.get(), uncompressedFileLength);
            allPdfDocumentDir = GlobalConstant.RootDirectoryCompress;
            compressedPDF = allPdfDocumentDir + Utils.removeFileExtension(strPdfName) + "-Compressed.pdf";
            File file2 = new File(allPdfDocumentDir);
            if (!file2.exists()) {
                file2.mkdirs();
            }
            try {
                PdfReader pdfReader = new PdfReader(pdfPath);
                if (pdfReader.isEncrypted()) {
                    isEncrypted = true;
                }
                xrefSize = pdfReader.getXrefSize();
                weakReference.get().progressBar.setMax(xrefSize);
                for (int i = 0; i < xrefSize; i++) {
                    PdfObject pdfObject = pdfReader.getPdfObject(i);
                    if (pdfObject != null) {
                        if (pdfObject.isStream()) {
                            PRStream prStream = (PRStream) pdfObject;
                            PdfObject pdfObject2 = prStream.get(PdfName.SUBTYPE);
                            if (pdfObject2 != null && pdfObject2.toString().equals(PdfName.IMAGE.toString())) {
                                try {
                                    Bitmap compressedBitmap = ImageUtils.getInstant()
                                            .getPdfCompressedBitmap(new PdfImageObject(prStream).getImageAsBytes());
                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                    compressedBitmap.compress(Bitmap.CompressFormat.JPEG, compressionQuality,
                                            byteArrayOutputStream);
                                    prStream.setData(byteArrayOutputStream.toByteArray(), false, 9);
                                    prStream.put(PdfName.FILTER, PdfName.DCTDECODE);
                                    byteArrayOutputStream.close();
                                    prStream.clear();
                                    prStream.setData(byteArrayOutputStream.toByteArray(), false, 0);
                                    prStream.put(PdfName.TYPE, PdfName.XOBJECT);
                                    prStream.put(PdfName.SUBTYPE, PdfName.IMAGE);
                                    prStream.put(PdfName.FILTER, PdfName.DCTDECODE);
                                    prStream.put(PdfName.WIDTH, new PdfNumber(compressedBitmap.getWidth()));
                                    prStream.put(PdfName.HEIGHT, new PdfNumber(compressedBitmap.getHeight()));
                                    prStream.put(PdfName.BITSPERCOMPONENT, new PdfNumber(8));
                                    prStream.put(PdfName.COLORSPACE, PdfName.DEVICERGB);
                                    if (!compressedBitmap.isRecycled()) {
                                        compressedBitmap.recycle();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            publishProgress(i + 1);
                        }
                    }
                }

                pdfReader.removeUnusedObjects();
                PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(compressedPDF));
                pdfStamper.setFullCompression();
                pdfStamper.close();
                compressedFileLength = new File(compressedPDF).length();
                compressedFileSize = Formatter.formatShortFileSize(weakReference.get(), compressedFileLength);
                reducedPercent = (100 - ((int) ((compressedFileLength * 100) / uncompressedFileLength))) +
                        "%";

                MediaScannerConnection.scanFile(MyApplication.getInstance(), new String[] { compressedPDF },
                        new String[] { "application/pdf" }, null);

                if (!isEncrypted) {
                    if (weakReference.get() != null) {
                        weakReference.get().runOnUiThread(() -> {
                            weakReference.get().tvPercent.setText("100");
                            weakReference.get().motionLayout1.transitionToEnd();
                            weakReference.get().motionLayout2.setVisibility(View.VISIBLE);
                            weakReference.get().motionLayout2.transitionToEnd();
                            weakReference.get().ltAnimBg.playAnimation();
                            weakReference.get().ltAnimDone.playAnimation();
                            weakReference.get().tvPdfName.setText(new File(compressedPDF).getName());

                            weakReference.get().tvPdfPath.setText(compressedPDF);
                            String sb2 = weakReference.get().getResources().getString(R.string.reduced_from) +
                                    " " + uncompressedFileSize +
                                    " " +
                                    weakReference.get().getResources().getString(R.string.to) +
                                    " " +
                                    compressedFileSize;
                            weakReference.get().tvResult.setText(sb2);

                            File file1 = new File(compressedPDF);
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

                    }

                } else {
                    weakReference.get().finish();
                    Toast.makeText(weakReference.get(), R.string.toast_file_protected_unprotect, Toast.LENGTH_LONG)
                            .show();
                }

            } catch (Exception e) {
                Log.e(GlobalConstant.TAG_LOG, "Error message", e); // Ghi thông tin lỗi vào Logcat

            }
        });
    }

    private void publishProgress(int progress) {
        int i3 = ((int) (((float) progress) * 100.0f)) / this.xrefSize;
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
