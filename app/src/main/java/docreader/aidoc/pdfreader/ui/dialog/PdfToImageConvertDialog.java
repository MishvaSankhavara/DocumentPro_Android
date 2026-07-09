package docreader.aidoc.pdfreader.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import docreader.aidoc.pdfreader.AppGlobalConstants;
import docreader.aidoc.pdfreader.R;
import docreader.aidoc.pdfreader.model_reader.PDFPageModel;
import com.docpro.scanner.result.ResultViewerActivity;
import docreader.aidoc.pdfreader.ui.activities.SharePdfAsImageActivity;
import docreader.aidoc.pdfreader.ui.customviews.HorizontalProgressBar;
import docreader.aidoc.pdfreader.utils.Utils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class PdfToImageConvertDialog extends Dialog {
    private HorizontalProgressBar progressBarView;
    private TextView tvDescription;
    private ArrayList<PDFPageModel> pdfPageList;
    private TextView btnCancelAction;
    private TextView btnViewImages;
    private TextView tvProgressPercent;
    private SharePdfAsImageActivity activityContext;
    private CopyImage copyImageTask;

    public PdfToImageConvertDialog(@NonNull SharePdfAsImageActivity context, ArrayList<PDFPageModel> arrayList) {
        super(context);
        this.activityContext = context;
        this.pdfPageList = arrayList;
        setContentView(R.layout.dialog_pdf_export_image);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        initializeViews();
        initializeData();
        copyImageTask = new CopyImage(this);
        copyImageTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void initializeViews() {
        tvDescription = findViewById(R.id.tvDescription);
        progressBarView = findViewById(R.id.progress_bar);
        btnViewImages = findViewById(R.id.tv_right_tip);
        btnCancelAction = findViewById(R.id.tv_left_tip);
        tvProgressPercent = findViewById(R.id.tvPercent);
    }

    private void initializeData() {
        tvDescription.setVisibility(View.VISIBLE);
        btnViewImages.setVisibility(View.GONE);
        btnCancelAction.setVisibility(View.VISIBLE);
        btnCancelAction.setOnClickListener(view -> {
            if (copyImageTask != null) {
                copyImageTask.cancel(true);
            }
            dismiss();
        });
    }

    /** Called on the main thread once the export task completes. */
    private void showCompletionState() {
        // Re-enable dismissal by touch
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        // Show "View files" button
        btnViewImages.setVisibility(View.VISIBLE);
        btnViewImages.setOnClickListener(view -> {
            dismiss();
            Intent intent = new Intent(activityContext, ResultViewerActivity.class);
            intent.putExtra(AppGlobalConstants.FROM_SAVE_IMAGE, 5);
            activityContext.startActivity(intent);
            activityContext.finish();
        });

        // Change Cancel → OK to dismiss only
        btnCancelAction.setVisibility(View.VISIBLE);
        btnCancelAction.setText(android.R.string.ok);
        btnCancelAction.setOnClickListener(view -> dismiss());
    }

    private static class CopyImage extends AsyncTask<Void, Integer, Void> {

        WeakReference<PdfToImageConvertDialog> weakReference;

        public CopyImage(PdfToImageConvertDialog progressSaveDialog) {
            this.weakReference = new WeakReference<>(progressSaveDialog);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            PdfToImageConvertDialog dialog = weakReference.get();
            if (dialog == null) return null;

            dialog.activityContext.runOnUiThread(() ->
                    dialog.progressBarView.setMax(dialog.pdfPageList.size()));

            File pathFolder = new File(AppGlobalConstants.DIRECTORY_IMAGES);
            if (!pathFolder.exists()) {
                pathFolder.mkdirs();
            }

            for (int i = 0; i < dialog.pdfPageList.size(); i++) {
                if (isCancelled()) break;
                String fileName = Utils
                        .getFileNameFromUri(dialog.pdfPageList.get(i).getThumbnailUri_PDFPageModel());
                String pathCopy = pathFolder + "/" + fileName;
                Utils.copyFile(dialog.activityContext,
                        dialog.pdfPageList.get(i).getThumbnailUri_PDFPageModel(),
                        pathCopy);
                publishProgress(i + 1);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            PdfToImageConvertDialog dialog = weakReference.get();
            if (dialog == null || !dialog.isShowing()) return;

            // Run on UI thread to guarantee view updates and dismiss work correctly
            dialog.activityContext.runOnUiThread(dialog::showCompletionState);
        }

        @SuppressLint("StringFormatInvalid")
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            PdfToImageConvertDialog dialog = weakReference.get();
            if (dialog == null) return;

            int percent = ((int) (((float) values[0]) * 100.0f)) / dialog.pdfPageList.size();
            dialog.tvDescription.setText(
                    dialog.activityContext.getResources().getString(
                            R.string.message_save_image_progress,
                            String.valueOf(values[0]),
                            String.valueOf(dialog.pdfPageList.size())));
            dialog.tvProgressPercent.setText(
                    dialog.activityContext.getResources().getString(
                            R.string.format_percent_value,
                            String.valueOf(percent)));
            dialog.progressBarView.setProgress(values[0]);
        }
    }

}
