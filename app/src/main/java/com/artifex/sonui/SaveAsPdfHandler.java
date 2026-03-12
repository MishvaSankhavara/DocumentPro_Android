package com.artifex.sonui;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.artifex.solib.SODoc;
import com.artifex.solib.SODocSaveListener;
import com.artifex.sonui.editor.NUIDocView;
import com.artifex.sonui.editor.SODataLeakHandlers;
import com.artifex.sonui.editor.SOSaveAsComplete;
import com.artifex.sonui.editor.SOCustomSaveComplete;
import com.artifex.sonui.editor.Utilities;

import java.io.File;
import java.io.IOException;

public class SaveAsPdfHandler implements SODataLeakHandlers {
    private static final String TAG = "SaveAsPdfHandler";
    private final Activity activity;

    public SaveAsPdfHandler(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void initDataLeakHandlers(Activity activity) throws IOException {
        Log.d(TAG, "initDataLeakHandlers called");
    }

    @Override
    public void finaliseDataLeakHandlers() {
        Log.d(TAG, "finaliseDataLeakHandlers called");
    }

    @Override
    public void saveAsPdfHandler(final String fileName, final SODoc doc) {
        Log.d(TAG, "saveAsPdfHandler called for file: " + fileName);

        ChoosePathActivity.a(activity, 2, false, new ChoosePathActivity.a() {
            @Override
            public void a() {
                Log.d(TAG, "ChoosePathActivity cancelled");
            }

            @Override
            public void a(FileBrowser fileBrowser) {
                if (fileBrowser == null) {
                    Log.e(TAG, "FileBrowser is null in selection callback");
                    return;
                }

                String selectedFileName = fileBrowser.getFileName();
                AppFile folderAppFile = fileBrowser.getFolderAppFile();

                if (selectedFileName == null || folderAppFile == null) {
                    Log.e(TAG, "Selected filename or folder is null");
                    return;
                }

                String selectedFolder = folderAppFile.b(); // Accessible in com.artifex.sonui

                if (selectedFolder == null) {
                    Log.e(TAG, "Selected folder path is null");
                    return;
                }

                if (!selectedFileName.toLowerCase().endsWith(".pdf")) {
                    selectedFileName += ".pdf";
                }

                File destinationFile = new File(selectedFolder, selectedFileName);
                final String finalPath = destinationFile.getAbsolutePath();

                Log.d(TAG, "Saving PDF to: " + finalPath);

                if (destinationFile.exists()) {
                    Utilities.yesNoMessage(activity, "File Exists", "File already exists. Overwrite?", "Yes", "No",
                            new Runnable() {
                                @Override
                                public void run() {
                                    performSave(doc, finalPath);
                                }
                            }, null);
                } else {
                    performSave(doc, finalPath);
                }
            }
        }, fileName);
    }

    private void performSave(SODoc doc, String path) {
        // Using SODoc.b for PDF export/save-as-pdf
        doc.b(path, false, new SODocSaveListener() {
            @Override
            public void onComplete(int result, int error) {
                Log.d(TAG, "save onComplete: result=" + result + ", error=" + error);
                if (result == 0) {
                    activity.runOnUiThread(() -> {
                        Toast.makeText(activity, "PDF saved successfully", Toast.LENGTH_LONG).show();
                    });
                } else {
                    activity.runOnUiThread(() -> {
                        Toast.makeText(activity, "Error saving PDF: " + error, Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }

    @Override
    public void postSaveHandler(SOSaveAsComplete var1) {
        Log.d(TAG, "postSaveHandler called");
        if (var1 != null) {
            var1.onComplete(0, null);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult called: requestCode=" + requestCode + ", resultCode=" + resultCode);
    }

    @Override
    public void insertImageHandler(NUIDocView nuiDocView) throws UnsupportedOperationException {
        Log.d(TAG, "insertImageHandler called");
    }

    @Override
    public void insertPhotoHandler(NUIDocView nuiDocView) throws UnsupportedOperationException {
        Log.d(TAG, "insertPhotoHandler called");
    }

    @Override
    public void pauseHandler(SODoc doc, boolean hasBeenModified) {
        Log.d(TAG, "pauseHandler called, hasBeenModified=" + hasBeenModified);
    }

    @Override
    public void openInHandler(String var1, SODoc var2) throws UnsupportedOperationException {
        Log.d(TAG, "openInHandler called");
    }

    @Override
    public void openPdfInHandler(String var1, SODoc var2) throws UnsupportedOperationException {
        Log.d(TAG, "openPdfInHandler called");
    }

    @Override
    public void printHandler(SODoc var1) throws UnsupportedOperationException {
        Log.d(TAG, "printHandler called");
    }

    @Override
    public void saveAsHandler(String var1, SODoc var2, SOSaveAsComplete var3) throws UnsupportedOperationException {
        Log.d(TAG, "saveAsHandler called");
    }

    @Override
    public void shareHandler(String var1, SODoc var2) throws UnsupportedOperationException {
        Log.d(TAG, "shareHandler called");
    }

    @Override
    public void customSaveHandler(String var1, SODoc var2, String var3, SOCustomSaveComplete var4)
            throws UnsupportedOperationException, IOException {
        Log.d(TAG, "customSaveHandler called");
    }

    @Override
    public void doInsert() {
        Log.d(TAG, "doInsert called");
    }

    @Override
    public void launchUrlHandler(String url) throws UnsupportedOperationException {
        Log.d(TAG, "launchUrlHandler called: " + url);
    }
}
