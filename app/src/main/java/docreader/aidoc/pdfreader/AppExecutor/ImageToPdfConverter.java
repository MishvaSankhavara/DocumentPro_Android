package docreader.aidoc.pdfreader.AppExecutor;

import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import docreader.aidoc.pdfreader.AppGlobalConstants;
import docreader.aidoc.pdfreader.DocumentMyApplication;
import docreader.aidoc.pdfreader.model_reader.PDFReaderModel;
import com.docpro.scanner.engine.ProcessingTaskActivity;
import docreader.aidoc.pdfreader.utils.ImageProcessorUtils;
import docreader.aidoc.pdfreader.utils.Utils;
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

public class ImageToPdfConverter {

    private final int numPages;
    private final ArrayList<String> arrayListPhoto_ImageToPdfConverter;
    private final String newFileName_ImageToPdfConverter;
    private String generatedPDFPath_ImageToPdfConverter;
    private final WeakReference<ProcessingTaskActivity> weakReference_ImageToPdfConverter;
    private final ExecutorService executor_ImageToPdfConverter = Executors.newSingleThreadExecutor();

    private void publishProgress_ImageToPdfConverter(int progress_ImageToPdfConverter) {
        int i3 = ((int) (((float) progress_ImageToPdfConverter) * 100.0f)) / this.numPages;
        String sb = i3 + "";
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            if (weakReference_ImageToPdfConverter.get() != null) {
                weakReference_ImageToPdfConverter.get().tvPercent.setText(sb);
                weakReference_ImageToPdfConverter.get().progressBar.setProgress(progress_ImageToPdfConverter);
            }
        });
    }

    public void executeTask_ImageToPdfConverter() {
        executor_ImageToPdfConverter.execute(() -> {
            try {
                String allPdfDocuments = AppGlobalConstants.DIRECTORY_IMAGE_TO_PDF;
                File file = new File(allPdfDocuments);
                generatedPDFPath_ImageToPdfConverter = allPdfDocuments + newFileName_ImageToPdfConverter + ".pdf";

                if (!file.exists()) {
                    file.mkdirs();
                }

                PDFBoxResourceLoader.init(weakReference_ImageToPdfConverter.get());
                PDDocument pDDocument = new PDDocument();

                int i = 0;
                while (i < arrayListPhoto_ImageToPdfConverter.size()) {

                    String path_ImageToPdfConverter = arrayListPhoto_ImageToPdfConverter.get(i);

                    Bitmap rotateBitmap_ImageToPdfConverter = ImageProcessorUtils.rotateBitmapImage(
                            ImageProcessorUtils.getInstant().getCompressedBitmapPDF(path_ImageToPdfConverter),
                            new ExifInterface(path_ImageToPdfConverter).getAttributeInt("Orientation", 0));

                    float width_ImageToPdfConverter = (float) rotateBitmap_ImageToPdfConverter.getWidth();
                    float height_ImageToPdfConverter = (float) rotateBitmap_ImageToPdfConverter.getHeight();

                    PDPage pDPage_ImageToPdfConverter = new PDPage(
                            new PDRectangle(width_ImageToPdfConverter, height_ImageToPdfConverter));

                    pDDocument.addPage(pDPage_ImageToPdfConverter);

                    PDImageXObject createFromImage = JPEGFactory.createFromImage(pDDocument,
                            rotateBitmap_ImageToPdfConverter);

                    PDPageContentStream pDPageContentStream = new PDPageContentStream(pDDocument,
                            pDPage_ImageToPdfConverter, true, true, true);

                    pDPageContentStream.drawImage(createFromImage, 0.0f, 0.0f, width_ImageToPdfConverter,
                            height_ImageToPdfConverter);

                    pDPageContentStream.close();

                    i++;
                    publishProgress_ImageToPdfConverter(i);
                }

                pDDocument.save(generatedPDFPath_ImageToPdfConverter);
                pDDocument.close();

                MediaScannerConnection.scanFile(DocumentMyApplication.getInstance(),
                        new String[] { generatedPDFPath_ImageToPdfConverter }, new String[] { "application/pdf" },
                        null);

            } catch (Exception e) {
                e.printStackTrace();
            }

            weakReference_ImageToPdfConverter.get().runOnUiThread(() -> {
                weakReference_ImageToPdfConverter.get().tvPercent.setText("100");
                weakReference_ImageToPdfConverter.get().showCompletionUI(() -> {
                    weakReference_ImageToPdfConverter.get().motionLayout1.transitionToEnd();
                    weakReference_ImageToPdfConverter.get().motionLayout2.setVisibility(View.VISIBLE);
                    weakReference_ImageToPdfConverter.get().motionLayout2.transitionToEnd();
                    weakReference_ImageToPdfConverter.get().ltAnimDone.playAnimation();

                    weakReference_ImageToPdfConverter.get().tvPdfName
                            .setText(new File(newFileName_ImageToPdfConverter).getName());
                    weakReference_ImageToPdfConverter.get().tvPdfPath.setText(generatedPDFPath_ImageToPdfConverter);

                    File file = new File(generatedPDFPath_ImageToPdfConverter);

                    PDFReaderModel fileHolderModel = new PDFReaderModel();
                    fileHolderModel.setName_PDFModel(file.getName());
                    fileHolderModel.setAbsolutePath_PDFModel(file.getAbsolutePath());
                    fileHolderModel.setFileUri_PDFModel(file.getAbsolutePath());
                    fileHolderModel.setLength_PDFModel(file.length());
                    fileHolderModel.setLastModified_PDFModel(file.lastModified());
                    fileHolderModel.setDirectory_PDFModel(file.isDirectory());

                    weakReference_ImageToPdfConverter.get().pdfModelFinal = fileHolderModel;

                    Utils.displayPDFThumbnail(weakReference_ImageToPdfConverter.get(), file,
                            weakReference_ImageToPdfConverter.get().imgThumbnail);
                    weakReference_ImageToPdfConverter.get().tvPageNumber
                            .setText(String.valueOf(Utils.getPageCountPDF(file)));
                });
            });
        });
    }

    public void shutdownNow_ImageToPdfConverter() {
        executor_ImageToPdfConverter.shutdownNow();
    }

    public ImageToPdfConverter(ProcessingTaskActivity activity_ImageToPdfConverter,
            String newFileName_ImageToPdfConverter, ArrayList<String> arrayListPhoto_ImageToPdfConverter) {

        this.weakReference_ImageToPdfConverter = new WeakReference<>(activity_ImageToPdfConverter);

        this.newFileName_ImageToPdfConverter = newFileName_ImageToPdfConverter;

        this.arrayListPhoto_ImageToPdfConverter = arrayListPhoto_ImageToPdfConverter;

        this.numPages = arrayListPhoto_ImageToPdfConverter.size();

        this.weakReference_ImageToPdfConverter.get().btnClose
                .setOnClickListener(v -> weakReference_ImageToPdfConverter.get().finish());

        this.weakReference_ImageToPdfConverter.get().btnStopExecutor
                .setOnClickListener(v -> executor_ImageToPdfConverter.shutdownNow());
    }
}