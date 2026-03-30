package com.docpro.scanner.engine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.motion.widget.MotionLayout;

import com.airbnb.lottie.LottieAnimationView;
import docreader.aidoc.pdfreader.AppGlobalConstants;
import docreader.aidoc.pdfreader.DocumentMyApplication;
import docreader.aidoc.pdfreader.R;
import docreader.aidoc.pdfreader.AppExecutor.FileCompressionExecutor;
import docreader.aidoc.pdfreader.AppExecutor.PdfMergeManager;
import docreader.aidoc.pdfreader.AppExecutor.ImageToPdfConverter;
import docreader.aidoc.pdfreader.AppExecutor.RemovePasswordExecutor;
import docreader.aidoc.pdfreader.AppExecutor.SetPasswordManager;
import docreader.aidoc.pdfreader.AppExecutor.SplitDocExecutor;
import docreader.aidoc.pdfreader.model_reader.PDFReaderModel;
import docreader.aidoc.pdfreader.ui.dialog.AppLoadingDialog;
import docreader.aidoc.pdfreader.utils.Utils;
import com.docpro.scanner.result.ResultViewerActivity;

import java.io.File;
import java.util.ArrayList;

public class ProcessingTaskActivity extends AppCompatActivity {

    public AppCompatTextView txtProgressPercent;
    public ProgressBar taskProgressBar;
    public AppCompatTextView txtToolTitle;
    public TextView txtDocumentName;
    public TextView txtDocumentPath;
    public TextView txtExecutionResult;
    public AppCompatTextView btnShareResult;
    public AppCompatTextView btnOpenResult;
    public MotionLayout layoutPhase1;
    public MotionLayout layoutPhase2;
    public LottieAnimationView animSuccess;
    public LottieAnimationView animWorkingBackground;
    public AppCompatImageView btnAbortTask;
    public AppCompatImageView imgDocPreview;

    public AppCompatTextView txtCurrentPageStatus;
    public AppCompatTextView txtStatusInfo;

    public PDFReaderModel finalPdfResult;
    public AppCompatImageView btnStopTaskExecutor;

    // Aliases for compatibility with old executors
    public AppCompatImageView btnClose;
    public AppCompatImageView btnStopExecutor;
    public ProgressBar progressBar;
    public AppCompatTextView tvTool;
    public AppCompatTextView tvPercent;
    public MotionLayout motionLayout1;
    public MotionLayout motionLayout2;
    public LottieAnimationView ltAnimBg;
    public LottieAnimationView ltAnimDone;
    public TextView tvPdfName;
    public TextView tvPdfPath;
    public TextView tvResult;
    public AppCompatImageView imgThumbnail;
    public TextView tvPageNumber;
    public PDFReaderModel pdfModelFinal;

    public FileCompressionExecutor coreCompressExecutor;
    public ImageToPdfConverter corePhotoToPdfExecutor;
    public PdfMergeManager coreMergeExecutor;
    public SetPasswordManager coreLockExecutor;
    public SplitDocExecutor coreSplitExecutor;
    public RemovePasswordExecutor coreUnlockExecutor;

    private int activeTaskId = -1;
    private boolean redirectedToResult = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_task_processor);
        bindViews();
        processIntentTask();
    }

    private void processIntentTask() {
        Intent taskIntent = getIntent();
        if (taskIntent != null) {
            int taskId = taskIntent.getIntExtra(AppGlobalConstants.EXTRA_TOOL_TYPE, 1);
            activeTaskId = taskId;
            if (taskId == AppGlobalConstants.TOOL_ID_COMPRESS) {
                PDFReaderModel model = (PDFReaderModel) taskIntent
                        .getSerializableExtra(AppGlobalConstants.EXTRA_PDF_MODEL);
                if (model != null) {
                    coreCompressExecutor = new FileCompressionExecutor(this, model.getAbsolutePath_PDFModel());
                    coreCompressExecutor.executeTask_FileCompression();
                }
            } else if (taskId == AppGlobalConstants.TOOL_ID_MERGE) {
                String name = taskIntent.getStringExtra(AppGlobalConstants.MERGE_PDF_FILE);
                ArrayList<PDFReaderModel> models = DocumentMyApplication.getInstance().getMergedPdfList();
                ArrayList<String> filePaths = new ArrayList<>();
                for (int i = 0; i < models.size(); i++) {
                    filePaths.add(models.get(i).getAbsolutePath_PDFModel());
                }
                coreMergeExecutor = new PdfMergeManager(this, filePaths, name);
                coreMergeExecutor.executeTask_PdfMergeManager();
            } else if (taskId == AppGlobalConstants.TOOL_ID_SPLIT) {
                PDFReaderModel model = (PDFReaderModel) taskIntent
                        .getSerializableExtra(AppGlobalConstants.EXTRA_PDF_MODEL);
                if (model != null) {
                    String name = taskIntent.getStringExtra(AppGlobalConstants.EXTRA_PDF_FILE_NAME);
                    coreSplitExecutor = new SplitDocExecutor(this, name,
                            DocumentMyApplication.getInstance().getArrayListSplit(), model.getAbsolutePath_PDFModel());
                    coreSplitExecutor.executeTask_SplitDoc();
                }
            } else if (taskId == AppGlobalConstants.TOOL_ID_LOCK_PDF) {
                PDFReaderModel model = (PDFReaderModel) taskIntent
                        .getSerializableExtra(AppGlobalConstants.EXTRA_PDF_MODEL);
                String pass = taskIntent.getStringExtra(AppGlobalConstants.PDF_SET_PASSWORD);
                if (model != null && pass != null) {
                    coreLockExecutor = new SetPasswordManager(ProcessingTaskActivity.this, pass, model);
                    coreLockExecutor.executeTask_setPW();
                }
            } else if (taskId == AppGlobalConstants.TOOL_ID_UNLOCK_PDF) {
                PDFReaderModel model = (PDFReaderModel) taskIntent
                        .getSerializableExtra(AppGlobalConstants.EXTRA_PDF_MODEL);
                String pass = taskIntent.getStringExtra(AppGlobalConstants.PDF_SET_PASSWORD);
                if (model != null && pass != null) {
                    coreUnlockExecutor = new RemovePasswordExecutor(ProcessingTaskActivity.this, pass, model);
                    coreUnlockExecutor.executeTask_removePW();
                }
            } else if (taskId == AppGlobalConstants.TOOL_ID_PHOTO_TO_PDF) {
                String name = taskIntent.getStringExtra(AppGlobalConstants.PHOTO_TO_PDF_FILE_NAME);
                corePhotoToPdfExecutor = new ImageToPdfConverter(this, name,
                        DocumentMyApplication.getInstance().getSplitIndices());
                corePhotoToPdfExecutor.executeTask_ImageToPdfConverter();
            }
        }
    }

    public void showCompletionUI(Runnable successUIRunner) {
        if (isFinishing() || isDestroyed()) {
            return;
        }

        AppLoadingDialog dialog = new AppLoadingDialog(this);
        dialog.setMessage(getString(R.string.sodk_editor_please_wait));
        dialog.show();

        new Handler(getMainLooper()).postDelayed(() -> {
            try {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                dialog.dismiss();
                if (successUIRunner != null) {
                    successUIRunner.run();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 1000);
    }

    public void redirectToResultsAfterDone() {
        // Redirection is now manual via ResultViewerActivity if needed,
        // or we just show the success UI in this Activity.
        // Keeping the method for compatibility but it will do nothing now
        // as we want to stay on the success screen.
    }

    private void bindViews() {
        btnStopTaskExecutor = findViewById(R.id.btnStopExecutor);
        txtProgressPercent = findViewById(R.id.operateProgressTv);
        taskProgressBar = findViewById(R.id.progressbar);
        txtToolTitle = findViewById(R.id.operateDescTv);

        txtDocumentName = findViewById(R.id.fileNameTv);
        txtDocumentPath = findViewById(R.id.filePathTv);
        txtExecutionResult = findViewById(R.id.titleTv);
        txtStatusInfo = findViewById(R.id.resultTv);
        btnShareResult = findViewById(R.id.shareTv);
        btnOpenResult = findViewById(R.id.openTv);
        animWorkingBackground = findViewById(R.id.bgAnimView);
        animSuccess = findViewById(R.id.animationView);
        btnAbortTask = findViewById(R.id.operateCloseImg);
        imgDocPreview = findViewById(R.id.previewImg);
        txtCurrentPageStatus = findViewById(R.id.indexTv);
        layoutPhase1 = findViewById(R.id.inc_task_progress);
        layoutPhase2 = findViewById(R.id.inc_task_completion);

        // Bind aliases
        btnClose = btnAbortTask;
        btnStopExecutor = btnStopTaskExecutor;
        progressBar = taskProgressBar;
        tvTool = txtToolTitle;
        tvPercent = txtProgressPercent;
        motionLayout1 = layoutPhase1;
        motionLayout2 = layoutPhase2;
        ltAnimBg = animWorkingBackground;
        ltAnimDone = animSuccess;
        tvPdfName = txtDocumentName;
        tvPdfPath = txtDocumentPath;
        tvResult = txtStatusInfo;
        imgThumbnail = imgDocPreview;
        tvPageNumber = txtCurrentPageStatus;
        // pdfModelFinal will be set by executors directly as before

        btnOpenResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File targetFile = new File(pdfModelFinal.getAbsolutePath_PDFModel());
                Utils.openFile(ProcessingTaskActivity.this, targetFile);
            }
        });
        btnShareResult.setOnClickListener(
                v -> Utils.shareFile(ProcessingTaskActivity.this, new File(pdfModelFinal.getFileUri_PDFModel())));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (coreCompressExecutor != null)
            coreCompressExecutor.shutdownNow_FileCompression();
        if (corePhotoToPdfExecutor != null)
            corePhotoToPdfExecutor.shutdownNow_ImageToPdfConverter();
        if (coreMergeExecutor != null)
            coreMergeExecutor.shutdownNow_PdfMergeManager();
        if (coreLockExecutor != null)
            coreLockExecutor.shutdownNow_setPW();
        if (coreUnlockExecutor != null)
            coreUnlockExecutor.shutdownNow_removePW();
        if (coreSplitExecutor != null)
            coreSplitExecutor.shutdownNow_SplitDoc();
    }
}
