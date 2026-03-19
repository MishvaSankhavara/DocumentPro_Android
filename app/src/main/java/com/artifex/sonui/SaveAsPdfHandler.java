package com.artifex.sonui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.artifex.solib.SODoc;
import com.artifex.solib.SODocSaveListener;
import com.artifex.sonui.editor.NUIDocView;
import com.artifex.sonui.editor.SODataLeakHandlers;
import com.artifex.sonui.editor.SOSaveAsComplete;
import com.artifex.sonui.editor.SOCustomSaveComplete;
import com.artifex.sonui.editor.Utilities;
import com.example.documenpro.R;

import java.io.File;
import java.io.IOException;

public class SaveAsPdfHandler implements SODataLeakHandlers {
    private static final String TAG = "SaveAsPdfHandler";
    private final Activity activity;
    private NUIDocView mNuiDocView;

    public SaveAsPdfHandler(Activity activity) {
        this.activity = activity;
    }

    public void setNUIDocView(NUIDocView nuiDocView) {
        this.mNuiDocView = nuiDocView;
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
    public void saveAsPdfHandler(String fileName, final SODoc doc) {
        saveAsPdfHandlerInternal(fileName, doc, null, null);
    }

    private void saveAsPdfHandlerInternal(String fileName, final SODoc doc, final SOSaveAsComplete saveAsComplete,
            final SOCustomSaveComplete customSaveComplete) {
        Log.d(TAG, "saveAsPdfHandlerInternal called");

        String resolvedFileName = fileName;

        if (activity != null) {
            android.widget.TextView titleView = activity.findViewById(com.example.documenpro.R.id.tvTittle);
            if (titleView != null) {
                String uiName = titleView.getText().toString();
                if (uiName != null && !uiName.isEmpty()) {
                    resolvedFileName = uiName;
                }
            }
        }

        if (resolvedFileName == null || resolvedFileName.isEmpty()) {
            if (activity != null) {
                Intent intent = activity.getIntent();
                if (intent != null) {
                    String intentFileName = intent.getStringExtra(
                            com.example.documenpro.AppGlobalConstants.EXTRA_SELECTED_FILE_NAME);
                    if (intentFileName != null && !intentFileName.isEmpty()) {
                        resolvedFileName = intentFileName;
                    }
                }
            }
        }

        final String latestFileName = (resolvedFileName != null) ? resolvedFileName : "";

        ChoosePathActivity.a(activity, 2, false, new ChoosePathActivity.a() {
            @Override
            public void a() {
                Log.d(TAG, "ChoosePathActivity cancelled");
                if (saveAsComplete != null) {
                    saveAsComplete.onComplete(1, null);
                }
                if (customSaveComplete != null) {
                    customSaveComplete.onComplete(1, null, false);
                }
            }

            @Override
            public void a(FileBrowser fileBrowser) {
                if (fileBrowser == null) {
                    if (saveAsComplete != null)
                        saveAsComplete.onComplete(1, null);
                    if (customSaveComplete != null)
                        customSaveComplete.onComplete(1, null, false);
                    return;
                }

                String selectedFileName = fileBrowser.getFileName();
                AppFile folderAppFile = fileBrowser.getFolderAppFile();

                if (selectedFileName == null || folderAppFile == null) {
                    if (saveAsComplete != null)
                        saveAsComplete.onComplete(1, null);
                    if (customSaveComplete != null)
                        customSaveComplete.onComplete(1, null, false);
                    return;
                }

                String selectedFolder = folderAppFile.b();
                if (selectedFolder == null) {
                    if (saveAsComplete != null)
                        saveAsComplete.onComplete(1, null);
                    if (customSaveComplete != null)
                        customSaveComplete.onComplete(1, null, false);
                    return;
                }

                if (!selectedFileName.toLowerCase().endsWith(".pdf")) {
                    selectedFileName += ".pdf";
                }

                File destinationFile = new File(selectedFolder, selectedFileName);
                final String finalPath = destinationFile.getAbsolutePath();

                if (destinationFile.exists()) {
                    Utilities.yesNoMessage(
                            activity,
                            "File Exists",
                            "File already exists. Overwrite?",
                            "Yes",
                            "No",
                            new Runnable() {
                                @Override
                                public void run() {
                                    performSave(doc, finalPath, saveAsComplete, customSaveComplete);
                                }
                            },
                            new Runnable() {
                                @Override
                                public void run() {
                                    if (saveAsComplete != null)
                                        saveAsComplete.onComplete(1, null);
                                    if (customSaveComplete != null)
                                        customSaveComplete.onComplete(1, null, false);
                                }
                            });
                } else {
                    performSave(doc, finalPath, saveAsComplete, customSaveComplete);
                }
            }
        }, latestFileName);
    }

    private void performSave(SODoc doc, String path, final SOSaveAsComplete saveAsComplete,
            final SOCustomSaveComplete customSaveComplete) {
        boolean isSourcePdf = false;
        if (activity != null) {
            Intent intent = activity.getIntent();
            if (intent != null) {
                String uri = intent.getStringExtra(
                        com.example.documenpro.AppGlobalConstants.EXTRA_SELECTED_FILE_URI);
                if (uri != null && uri.toLowerCase().endsWith(".pdf")) {
                    isSourcePdf = true;
                }
            }
        }

        SODocSaveListener saveListener = new SODocSaveListener() {
            @Override
            public void onComplete(int result, int error) {
                Log.d(TAG, "save onComplete: result=" + result + ", error=" + error);
                if (result == 0) {
                    final File file = new File(path);
                    final String fileName = file.getName();
                    activity.runOnUiThread(() -> {
                        Toast.makeText(activity, "PDF saved successfully", Toast.LENGTH_LONG).show();
                    });
                    // Defer dialog so SDK can finish any internal "Please wait" / cleanup first
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        if (activity.isFinishing() || activity.isDestroyed())
                            return;
                        // Show confirm dialog: "Open saved document?"
                        Utilities.yesNoMessage(
                                activity,
                                "Open saved document?",
                                "Would you like to open the saved document?",
                                "Yes",
                                "No",
                                () -> {
                                    // Yes: redirect to saved document
                                    Intent openIntent = new Intent(activity,
                                            com.example.documenpro.ui.activities.ViewOfficeActivity.class);
                                    openIntent.setAction(Intent.ACTION_VIEW);
                                    openIntent.setData(Uri.fromFile(file));
                                    openIntent.putExtra(
                                            com.example.documenpro.AppGlobalConstants.EXTRA_SELECTED_FILE_URI, path);
                                    openIntent.putExtra(
                                            com.example.documenpro.AppGlobalConstants.EXTRA_SELECTED_FILE_NAME,
                                            fileName);
                                    openIntent.putExtra("STARTED_FROM_EXPLORER", true);
                                    openIntent.putExtra("START_PAGE", 0);
                                    openIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    activity.startActivity(openIntent);
                                    activity.finish();
                                },
                                () -> {
                                    // No: stay on current document, update footer and state
                                    android.widget.TextView titleView = activity
                                            .findViewById(com.example.documenpro.R.id.tvTittle);
                                    if (titleView != null) {
                                        titleView.setText(fileName);
                                    }
                                    Intent intent = activity.getIntent();
                                    if (intent != null) {
                                        intent.putExtra(
                                                com.example.documenpro.AppGlobalConstants.EXTRA_SELECTED_FILE_URI,
                                                path);
                                        intent.putExtra(
                                                com.example.documenpro.AppGlobalConstants.EXTRA_SELECTED_FILE_NAME,
                                                fileName);
                                    }
                                    if (activity instanceof com.artifex.sonui.AppNUIActivity) {
                                        ((com.artifex.sonui.AppNUIActivity) activity).onNewIntent(intent);
                                    }
                                    if (saveAsComplete != null) {
                                        saveAsComplete.onComplete(0, path);
                                    }
                                    if (customSaveComplete != null) {
                                        customSaveComplete.onComplete(0, path, true);
                                    }
                                });
                    }, 200);
                } else {
                    activity.runOnUiThread(() -> {
                        Toast.makeText(activity, "Error saving PDF: " + error, Toast.LENGTH_LONG).show();
                        if (saveAsComplete != null)
                            saveAsComplete.onComplete(1, null);
                        if (customSaveComplete != null)
                            customSaveComplete.onComplete(1, null, false);
                    });
                }
            }
        };

        if (mNuiDocView instanceof com.artifex.sonui.editor.NUIDocViewPdf) {
            ((com.artifex.sonui.editor.NUIDocViewPdf) mNuiDocView).saveCustomAnnotations(path);
        }

        if (isSourcePdf) {
            doc.a(path, saveListener);
        } else {
            doc.b(path, true, saveListener);
        }
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
        Log.d(TAG, "onActivityResult called");
        if (requestCode == com.example.documenpro.AppGlobalConstants.REQUEST_CODE_INSERT_IMAGE) {
            if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
                Uri selectedUri = data.getData();
                if (mNuiDocView != null) {
                    insertImageFromUri(selectedUri);
                }
            }
        }
    }

    private void insertImageFromUri(Uri uri) {
        String filename = com.example.documenpro.utils.Utils.getFileNameFromUri(uri, activity.getContentResolver());
        File tempFolder = new File(activity.getExternalFilesDir(null), "temp_images");
        if (!tempFolder.exists()) {
            tempFolder.mkdirs();
        }

        File destFile = new File(tempFolder, filename);
        com.example.documenpro.utils.Utils.copy(activity, uri, destFile.getAbsolutePath());

        if (mNuiDocView != null) {
            mNuiDocView.doInsertImage(destFile.getAbsolutePath());
        }
    }

    @Override
    public void insertImageHandler(NUIDocView nuiDocView) {
        Log.d(TAG, "insertImageHandler called");
        this.mNuiDocView = nuiDocView;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        activity.startActivityForResult(Intent.createChooser(intent, "Select Image"),
                com.example.documenpro.AppGlobalConstants.REQUEST_CODE_INSERT_IMAGE);
    }

    @Override
    public void insertPhotoHandler(NUIDocView nuiDocView) {
        Log.d(TAG, "insertPhotoHandler called");
        insertImageHandler(nuiDocView);
    }

    @Override
    public void pauseHandler(SODoc doc, boolean hasBeenModified) {
        Log.d(TAG, "pauseHandler called");
    }

    @Override
    public void openInHandler(String var1, SODoc var2) {
        Log.d(TAG, "openInHandler called");
    }

    @Override
    public void openPdfInHandler(String var1, SODoc var2) {
        Log.d(TAG, "openPdfInHandler called");
    }

    @Override
    public void printHandler(SODoc var1) {
        Log.d(TAG, "printHandler called");
    }

    @Override
    public void saveAsHandler(String fileName, SODoc doc, SOSaveAsComplete completion) {
        Log.d(TAG, "saveAsHandler called");
        saveAsPdfHandlerInternal(fileName, doc, completion, null);
    }

    @Override
    public void shareHandler(String var1, SODoc var2) {
        Log.d(TAG, "shareHandler called");
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("application/*");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(var1)));
            String appUrl = "https://play.google.com/store/apps/details?id=" + activity.getPackageName();
            String shareMessage = String.format(activity.getString(R.string.custom_share_message), appUrl);
            intent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            activity.startActivity(Intent.createChooser(intent, activity.getString(R.string.share_file_using_title)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void customSaveHandler(String fileName, SODoc doc, String var3, SOCustomSaveComplete completion)
            throws IOException {
        Log.d(TAG, "customSaveHandler called");
        saveAsPdfHandlerInternal(fileName, doc, null, completion);
    }

    @Override
    public void doInsert() {
        Log.d(TAG, "doInsert called");
    }

    @Override
    public void launchUrlHandler(String url) {
        Log.d(TAG, "launchUrlHandler called: " + url);
    }
}