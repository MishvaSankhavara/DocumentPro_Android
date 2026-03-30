package docreader.aidoc.pdfreader.docHelper;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.widget.Toast;

import docreader.aidoc.pdfreader.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfPrintUtils {

    private static class CustomPrintDocumentAdapter_Utils extends PrintDocumentAdapter {

        private final Uri pdfUri_Utils;
        private final Activity activity_Utils;

        @Override
        public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, android.os.CancellationSignal cancellationSignal, WriteResultCallback callback) {

            try (ParcelFileDescriptor source_Utils = activity_Utils.getContentResolver().openFileDescriptor(pdfUri_Utils, "r"); FileInputStream input_Utils = new FileInputStream(source_Utils.getFileDescriptor()); FileOutputStream output_Utils = new FileOutputStream(destination.getFileDescriptor())) {

                byte[] buffer = new byte[1024];
                int bytesRead;

                while ((bytesRead = input_Utils.read(buffer)) > 0) {
                    output_Utils.write(buffer, 0, bytesRead);
                }

                callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});

            } catch (IOException error_Utils) {
                error_Utils.printStackTrace();
                callback.onWriteFailed(error_Utils.getMessage());
            }
        }

        @Override
        public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, android.os.CancellationSignal cancellationSignal, LayoutResultCallback callback, android.os.Bundle extras) {

            if (cancellationSignal.isCanceled()) {
                callback.onLayoutCancelled();
                return;
            }

            PrintDocumentInfo info_Utils = new PrintDocumentInfo.Builder("document.pdf").setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN).build();

            callback.onLayoutFinished(info_Utils, true);
        }

        public CustomPrintDocumentAdapter_Utils(Activity activity_Utils, Uri pdfUri_Utils) {

            this.pdfUri_Utils = pdfUri_Utils;
            this.activity_Utils = activity_Utils;
        }
    }

    public static void printPdf_Utils(Activity activity, Uri pdfUri_Utils) {

        if (activity == null || pdfUri_Utils == null) {
            Toast.makeText(activity, "Invalid activity or file URI", Toast.LENGTH_LONG).show();
            return;
        }

        PrintManager printManager_Utils = (PrintManager) activity.getSystemService(Context.PRINT_SERVICE);

        if (printManager_Utils == null) {
            Toast.makeText(activity, "This device does not support printing", Toast.LENGTH_LONG).show();
            return;
        }

        String jobName = activity.getString(R.string.app_name) + " Document";

        printManager_Utils.print(jobName, new CustomPrintDocumentAdapter_Utils(activity, pdfUri_Utils), null);
    }
}