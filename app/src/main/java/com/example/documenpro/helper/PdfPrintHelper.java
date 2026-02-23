package com.example.documenpro.helper;

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

import com.example.documenpro.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfPrintHelper {
    public static void printPdf(Activity activity, Uri pdfUri) {
        if (activity == null || pdfUri == null) {
            Toast.makeText(activity, "Invalid activity or file URI", Toast.LENGTH_LONG).show();
            return;
        }

        // Kiểm tra xem thiết bị có hỗ trợ in không
        PrintManager printManager = (PrintManager) activity.getSystemService(Context.PRINT_SERVICE);
        if (printManager == null) {
            Toast.makeText(activity, "This device does not support printing", Toast.LENGTH_LONG).show();
            return;
        }

        String jobName = activity.getString(R.string.app_name) + " Document";
        printManager.print(jobName, new CustomPrintDocumentAdapter(activity, pdfUri), null);
    }
    private static class CustomPrintDocumentAdapter extends PrintDocumentAdapter {
        private final Activity activity;
        private final Uri pdfUri;

        public CustomPrintDocumentAdapter(Activity activity, Uri pdfUri) {
            this.activity = activity;
            this.pdfUri = pdfUri;
        }

        @Override
        public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, android.os.CancellationSignal cancellationSignal, LayoutResultCallback callback, android.os.Bundle extras) {
            if (cancellationSignal.isCanceled()) {
                callback.onLayoutCancelled();
                return;
            }

            PrintDocumentInfo info = new PrintDocumentInfo.Builder("document.pdf")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
                    .build();
            callback.onLayoutFinished(info, true);
        }

        @Override
        public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, android.os.CancellationSignal cancellationSignal, WriteResultCallback callback) {
            try (ParcelFileDescriptor source = activity.getContentResolver().openFileDescriptor(pdfUri, "r");
                 FileInputStream input = new FileInputStream(source.getFileDescriptor());
                 FileOutputStream output = new FileOutputStream(destination.getFileDescriptor())) {

                byte[] buffer = new byte[1024];
                int bytesRead;

                while ((bytesRead = input.read(buffer)) > 0) {
                    output.write(buffer, 0, bytesRead);
                }

                callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
            } catch (IOException e) {
                e.printStackTrace();
                callback.onWriteFailed(e.getMessage());
            }
        }
    }

}
