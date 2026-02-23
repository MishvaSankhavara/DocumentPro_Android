package com.example.documenpro.executor;

import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.example.documenpro.GlobalConstant;
import com.example.documenpro.MyApplication;
import com.example.documenpro.model.PDFModel;
import com.docpro.scanner.engine.ProcessingTaskActivity;
import com.example.documenpro.utils.ImageUtils;
import com.example.documenpro.utils.Utils;
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.pdmodel.graphics.image.JPEGFactory;
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PhotoToPdfExecutor {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final WeakReference<ProcessingTaskActivity> weakReference;
    private String generatedPDFPath;
    private final String newFileName;
    private final ArrayList<String> arrayListPhoto;

    private final int numPages;

    public PhotoToPdfExecutor(ProcessingTaskActivity activity, String newFileName, ArrayList<String> arrayListPhoto) {
        this.weakReference = new WeakReference<>(activity);
        this.newFileName = newFileName;
        this.arrayListPhoto = arrayListPhoto;
        this.numPages = arrayListPhoto.size();

        this.weakReference.get().btnClose.setOnClickListener(v -> {
            weakReference.get().finish();
        });
        this.weakReference.get().btnStopExecutor.setOnClickListener(v -> executor.shutdownNow());
    }

    public void shutdownNow() {
        executor.shutdownNow();
    }

    public void executeTask() {
        executor.execute(() -> {
            try {
                String allPdfDocuments = GlobalConstant.RootDirectoryImage2Pdf;
                File file = new File(allPdfDocuments);
                generatedPDFPath = allPdfDocuments + newFileName + ".pdf";
                if (!file.exists()) {
                    file.mkdirs();
                }
                PDFBoxResourceLoader.init(weakReference.get());
                PDDocument pDDocument = new PDDocument();
                int i = 0;
                while (i < arrayListPhoto.size()) {
                    String path = arrayListPhoto.get(i);
                    Bitmap rotateBitmap = ImageUtils.rotateImageBitmap(
                            ImageUtils.getInstant().getPdfCompressedBitmap(path),
                            new ExifInterface(path).getAttributeInt("Orientation", 0));

                    float width = (float) rotateBitmap.getWidth();
                    float height = (float) rotateBitmap.getHeight();
                    PDPage pDPage = new PDPage(new PDRectangle(width, height));
                    pDDocument.addPage(pDPage);
                    PDImageXObject createFromImage = JPEGFactory.createFromImage(pDDocument, rotateBitmap);
                    PDPageContentStream pDPageContentStream = new PDPageContentStream(pDDocument, pDPage, true, true,
                            true);
                    pDPageContentStream.drawImage(createFromImage, 0.0f, 0.0f, width, height);
                    pDPageContentStream.close();
                    i++;
                    publishProgress(i);
                }
                pDDocument.save(generatedPDFPath);
                pDDocument.close();
                MediaScannerConnection.scanFile(MyApplication.getInstance(), new String[] { generatedPDFPath },
                        new String[] { "application/pdf" }, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            weakReference.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    weakReference.get().tvPercent.setText("100");
                    weakReference.get().motionLayout1.transitionToEnd();
                    weakReference.get().motionLayout2.setVisibility(View.VISIBLE);
                    weakReference.get().motionLayout2.transitionToEnd();
                    weakReference.get().ltAnimDone.playAnimation();
                    weakReference.get().tvPdfName.setText(new File(newFileName).getName());
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
        });

    }

    private void publishProgress(int progress) {
        int i3 = ((int) (((float) progress) * 100.0f)) / this.numPages;
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
