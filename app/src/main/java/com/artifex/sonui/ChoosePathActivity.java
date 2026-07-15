package com.artifex.sonui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;

import androidx.appcompat.widget.AppCompatTextView;

import com.artifex.sonui.editor.BaseActivity;
import com.artifex.sonui.editor.SOEditText;
import com.artifex.sonui.editor.SOEditTextOnEditorActionListener;
import com.artifex.sonui.editor.Utilities;
import docreader.aidoc.pdfreader.R;

public class ChoosePathActivity extends BaseActivity {
    private static ChoosePathActivity.a a;
    private static int b;
    private static String c;
    private static boolean d;
    public static Uri selectedFolderUri;

    private static final int REQUEST_FOLDER_PICK = 5001;

    /** Reference kept so onActivityResult can call setSelectedFolderPath. */
    private FileBrowser fileBrowser;

    public ChoosePathActivity() {
    }

    public static void a(Activity var0, int var1, boolean var2, ChoosePathActivity.a var3, String var4) {
        a = var3;
        b = var1;
        c = var4;
        d = false;
        var0.startActivity(new Intent(var0, ChoosePathActivity.class));
    }

    public void finish() {
        if (!d) {
            a.a();
        }

        super.finish();
    }

    protected void onCreate(Bundle var1) {
        super.onCreate(var1);
        this.setContentView(R.layout.choose_path);
        selectedFolderUri = null;
        String var4 = c;
        fileBrowser = this.findViewById(R.id.file_browser);
        fileBrowser.a(this, var4);

        // Check and request storage permission if not already granted so that we can write the saved file
        if (!docreader.aidoc.pdfreader.utils.Utils.checkPermission(this)) {
            docreader.aidoc.pdfreader.utils.Utils.askPermission(this);
        }

        // Wire the + Browse button to open the system folder picker
        fileBrowser.setBrowseFolderListener(() -> {
            if (!docreader.aidoc.pdfreader.utils.Utils.checkPermission(this)) {
                docreader.aidoc.pdfreader.utils.Utils.askPermission(this);
                return;
            }
            Intent pickerIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            pickerIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            try {
                startActivityForResult(pickerIntent, REQUEST_FOLDER_PICK);
            } catch (Exception e) {
                android.util.Log.e("ChoosePathActivity", "Folder picker not available", e);
            }
        });

        AppCompatTextView tvSave = this.findViewById(R.id.save_button);
        if (b == 3) {
            var4 = getString(R.string.editor_copy);
        } else {
            var4 = getString(R.string.editor_save);
        }

        tvSave.setText(var4);
        tvSave.setOnClickListener(new OnClickListener() {
            public void onClick(View var1) {
                if (!docreader.aidoc.pdfreader.utils.Utils.checkPermission(ChoosePathActivity.this)) {
                    docreader.aidoc.pdfreader.utils.Utils.askPermission(ChoosePathActivity.this);
                    return;
                }
                ChoosePathActivity.this.completeSave(fileBrowser);
            }
        });

        this.findViewById(R.id.cancel_button).setOnClickListener(new OnClickListener() {
            public void onClick(View var1) {
                Utilities.hideKeyboard(ChoosePathActivity.this);
                ChoosePathActivity.d = true;
                ChoosePathActivity.this.finish();
                if (a != null) {
                    ChoosePathActivity.a.a();
                }
            }
        });

        SOEditText var5 = fileBrowser.getEditText();
        var5.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View var1, int var2x, KeyEvent var3) {
                if (var3.getAction() == 0 && var2x == 66) {
                    if (!docreader.aidoc.pdfreader.utils.Utils.checkPermission(ChoosePathActivity.this)) {
                        docreader.aidoc.pdfreader.utils.Utils.askPermission(ChoosePathActivity.this);
                        return true;
                    }
                    ChoosePathActivity.this.completeSave(fileBrowser);
                    return true;
                } else {
                    return false;
                }
            }
        });

        var5.setOnEditorActionListener(new SOEditTextOnEditorActionListener() {
            public boolean onEditorAction(SOEditText var1, int var2x, KeyEvent var3) {
                boolean var4 = true;
                if (var2x == 6) {
                    if (!docreader.aidoc.pdfreader.utils.Utils.checkPermission(ChoosePathActivity.this)) {
                        docreader.aidoc.pdfreader.utils.Utils.askPermission(ChoosePathActivity.this);
                        return true;
                    }
                    ChoosePathActivity.this.completeSave(fileBrowser);
                } else {
                    var4 = false;
                }

                return var4;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FOLDER_PICK && resultCode == RESULT_OK && data != null) {
            Uri treeUri = data.getData();
            if (treeUri != null) {
                // Take persistent permission so the app can access this folder later
                try {
                    getContentResolver().takePersistableUriPermission(
                            treeUri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                } catch (Exception e) {
                    android.util.Log.w("ChoosePathActivity", "takePersistableUriPermission failed", e);
                }
                ChoosePathActivity.selectedFolderUri = treeUri;
                String folderPath = treeUriToPath(treeUri);
                if (folderPath != null && fileBrowser != null) {
                    fileBrowser.setSelectedFolderPath(folderPath);
                }
            }
        }
    }

    /**
     * Converts a tree URI from ACTION_OPEN_DOCUMENT_TREE to a real file-system path.
     * Works for primary (internal) storage and most secondary (SD card) storage.
     */
    private String treeUriToPath(Uri treeUri) {
        try {
            String docId;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                docId = DocumentsContract.getTreeDocumentId(treeUri);
            } else {
                return null;
            }
            // docId format: "primary:Download" or "primary:" (root) or "<sdcardId>:path"
            String[] parts = docId.split(":");
            String storageType = parts[0];
            String relativePath = (parts.length > 1) ? parts[1] : "";

            String basePath;
            if ("primary".equalsIgnoreCase(storageType)) {
                basePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            } else {
                // SD card or other secondary storage
                basePath = "/storage/" + storageType;
            }

            return relativePath.isEmpty() ? basePath : basePath + "/" + relativePath;
        } catch (Exception e) {
            android.util.Log.e("ChoosePathActivity", "treeUriToPath failed", e);
            return null;
        }
    }

    private void completeSave(final FileBrowser fileBrowser) {
        if (fileBrowser != null) {
            android.util.Log.d("ChoosePathActivity", "completeSave: fileName=" + fileBrowser.getFileName());
            android.util.Log.d("ChoosePathActivity", "completeSave: folder="
                    + (fileBrowser.getFolderAppFile() != null ? fileBrowser.getFolderAppFile().b() : "null"));
        } else {
            android.util.Log.e("ChoosePathActivity", "completeSave: fileBrowser is NULL");
        }
        Utilities.hideKeyboard(this);
        d = true;
        try {
            a.a(fileBrowser);
            finish(); // Close the selection activity after calling the callback
        } catch (Throwable e) {
            android.util.Log.e("ChoosePathActivity", "Error in a.a(fileBrowser)", e);
            throw e;
        }
    }

    public interface a {
        void a();

        void a(FileBrowser var1);
    }
}
