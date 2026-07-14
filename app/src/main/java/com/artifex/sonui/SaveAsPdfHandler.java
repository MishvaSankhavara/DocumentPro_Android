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
import docreader.aidoc.pdfreader.R;

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
        saveAsPdfHandlerInternal(fileName, doc, null, null, true);
    }

    private void saveAsPdfHandlerInternal(String fileName, final SODoc doc, final SOSaveAsComplete saveAsComplete,
            final SOCustomSaveComplete customSaveComplete, final boolean isSaveAsPdf) {
        Log.d(TAG, "saveAsPdfHandlerInternal called");

        String resolvedFileName = fileName;

        if (activity != null) {
            android.widget.TextView titleView = activity.findViewById(docreader.aidoc.pdfreader.R.id.tvTittle);
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
                            docreader.aidoc.pdfreader.AppGlobalConstants.EXTRA_SELECTED_FILE_NAME);
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

                String originalExtension = "";
                if (activity != null && activity.getIntent() != null) {
                    String uriStr = activity.getIntent().getStringExtra(
                            docreader.aidoc.pdfreader.AppGlobalConstants.EXTRA_SELECTED_FILE_URI);
                    if (uriStr != null) {
                        int idx = uriStr.lastIndexOf('.');
                        if (idx >= 0) {
                            originalExtension = uriStr.substring(idx);
                        }
                    }
                }
                if (originalExtension.isEmpty() && selectedFileName != null) {
                    int idx = selectedFileName.lastIndexOf('.');
                    if (idx >= 0) {
                        originalExtension = selectedFileName.substring(idx);
                    }
                }

                String targetExtension = isSaveAsPdf ? ".pdf" : originalExtension;
                if (!targetExtension.isEmpty()) {
                    int lastDot = selectedFileName.lastIndexOf('.');
                    if (lastDot >= 0) {
                        selectedFileName = selectedFileName.substring(0, lastDot);
                    }
                    selectedFileName += targetExtension;
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
                                    performSave(doc, finalPath, saveAsComplete, customSaveComplete, isSaveAsPdf);
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
                    performSave(doc, finalPath, saveAsComplete, customSaveComplete, isSaveAsPdf);
                }
            }
        }, latestFileName);
    }

    private boolean copyFile(File source, File dest) {
        try (java.io.FileInputStream in = new java.io.FileInputStream(source);
             java.io.FileOutputStream out = new java.io.FileOutputStream(dest)) {
            byte[] buf = new byte[32768];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void performSave(SODoc doc, String path, final SOSaveAsComplete saveAsComplete,
            final SOCustomSaveComplete customSaveComplete, boolean isSaveAsPdf) {
        final String finalSavePath;
        if (!isSaveAsPdf && mNuiDocView != null && mNuiDocView.getSession() != null 
                && mNuiDocView.getSession().getFileState() != null) {
            String internalPath = mNuiDocView.getSession().getFileState().getInternalPath();
            if (internalPath != null && !internalPath.isEmpty()) {
                finalSavePath = internalPath;
            } else {
                finalSavePath = path;
            }
        } else {
            finalSavePath = path;
        }

        SODocSaveListener saveListener = new SODocSaveListener() {
            @Override
            public void onComplete(int result, int error) {
                Log.d(TAG, "save onComplete: result=" + result + ", error=" + error);
                if (result == 0) {
                    if (!finalSavePath.equals(path)) {
                        boolean copied = copyFile(new File(finalSavePath), new File(path));
                        if (!copied) {
                            activity.runOnUiThread(() -> {
                                Toast.makeText(activity, "Error writing to destination folder", Toast.LENGTH_LONG).show();
                                if (saveAsComplete != null)
                                    saveAsComplete.onComplete(1, null);
                                if (customSaveComplete != null)
                                    customSaveComplete.onComplete(1, null, false);
                            });
                            return;
                        }
                    }

                    final File file = new File(path);
                    final String fileName = file.getName();
                    activity.runOnUiThread(() -> {
                        Toast.makeText(activity, "File saved successfully", Toast.LENGTH_LONG).show();
                    });
                    // Defer direct update so SDK can finish any internal cleanup first
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        if (activity.isFinishing() || activity.isDestroyed())
                            return;

                        // Stay on current document, update footer and state directly
                        android.widget.TextView titleView = activity
                                .findViewById(docreader.aidoc.pdfreader.R.id.tvTittle);
                        if (titleView != null) {
                            titleView.setText(fileName);
                        }
                        Intent intent = activity.getIntent();
                        if (intent != null) {
                            intent.putExtra(
                                    docreader.aidoc.pdfreader.AppGlobalConstants.EXTRA_SELECTED_FILE_URI,
                                    path);
                            intent.putExtra(
                                    docreader.aidoc.pdfreader.AppGlobalConstants.EXTRA_SELECTED_FILE_NAME,
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
                    }, 200);
                } else {
                    activity.runOnUiThread(() -> {
                        Toast.makeText(activity, "Error saving file: " + error, Toast.LENGTH_LONG).show();
                        if (saveAsComplete != null)
                            saveAsComplete.onComplete(1, null);
                        if (customSaveComplete != null)
                            customSaveComplete.onComplete(1, null, false);
                    });
                }
            }
        };

        if (mNuiDocView instanceof com.artifex.sonui.editor.NUIDocViewPdf) {
            ((com.artifex.sonui.editor.NUIDocViewPdf) mNuiDocView).saveCustomAnnotations(finalSavePath);
        }

        if (isSaveAsPdf) {
            doc.b(finalSavePath, true, saveListener);
        } else {
            doc.a(finalSavePath, saveListener);
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
        if (requestCode == docreader.aidoc.pdfreader.AppGlobalConstants.REQUEST_CODE_INSERT_IMAGE) {
            if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
                Uri selectedUri = data.getData();
                if (mNuiDocView != null) {
                    insertImageFromUri(selectedUri);
                }
            }
        }
    }

    private void insertImageFromUri(Uri uri) {
        String filename = docreader.aidoc.pdfreader.utils.Utils.getFileNameFromUri(uri, activity.getContentResolver());
        File tempFolder = new File(activity.getExternalFilesDir(null), "temp_images");
        if (!tempFolder.exists()) {
            tempFolder.mkdirs();
        }

        File destFile = new File(tempFolder, filename);
        docreader.aidoc.pdfreader.utils.Utils.copy(activity, uri, destFile.getAbsolutePath());

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
                docreader.aidoc.pdfreader.AppGlobalConstants.REQUEST_CODE_INSERT_IMAGE);
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
        saveAsPdfHandlerInternal(fileName, doc, completion, null, false);
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
        saveAsPdfHandlerInternal(fileName, doc, null, completion, false);
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