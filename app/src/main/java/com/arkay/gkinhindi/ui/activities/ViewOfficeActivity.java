package com.arkay.gkinhindi.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.artifex.sonui.AppNUIActivity;
import com.arkay.gkinhindi.BuildConfig;
import com.arkay.gkinhindi.Constants;
import com.arkay.gkinhindi.R;
import com.arkay.gkinhindi.PreferenceUtils;
import com.arkay.gkinhindi.utils.AdsUtils;
import com.arkay.gkinhindi.utils.Utils;
import java.io.File;

public class ViewOfficeActivity extends AppNUIActivity {

    public static final int REQUEST_CAMERA_PERMISSION_CODE = 0x11111;
    String openedFileExtension;

    private File copyUriToCache(android.content.Context context, Uri uri) {
        try {
            String fileName = "temp_doc";
            if ("content".equalsIgnoreCase(uri.getScheme())) {
                try (android.database.Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        int idx = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                        if (idx >= 0) {
                            String name = cursor.getString(idx);
                            if (name != null && !name.isEmpty()) {
                                fileName = name;
                            }
                        }
                    }
                } catch (Exception ignored) {}
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                if (uri.getPath() != null) {
                    fileName = new File(uri.getPath()).getName();
                }
            }

            File cacheDir = new File(context.getCacheDir(), "docs_cache");
            if (!cacheDir.exists()) cacheDir.mkdirs();

            File sessionDir = new File(cacheDir, "session_" + System.currentTimeMillis());
            if (!sessionDir.exists()) sessionDir.mkdirs();

            File cachedFile = new File(sessionDir, fileName);
            try (java.io.InputStream in = context.getContentResolver().openInputStream(uri);
                 java.io.FileOutputStream out = new java.io.FileOutputStream(cachedFile)) {
                if (in == null) return null;
                byte[] buf = new byte[32768];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }

            // Clean up old session directories in the background
            new Thread(() -> {
                try {
                    File[] sessions = cacheDir.listFiles();
                    if (sessions != null) {
                        long now = System.currentTimeMillis();
                        for (File session : sessions) {
                            if (session.isDirectory() && session.getName().startsWith("session_")) {
                                String tsStr = session.getName().substring("session_".length());
                                try {
                                    long ts = Long.parseLong(tsStr);
                                    if (now - ts > 3600000) { // older than 1 hour
                                        deleteFolderRecursive(session);
                                    }
                                } catch (NumberFormatException ignored) {}
                            }
                        }
                    }
                } catch (Exception ignored) {}
            }).start();

            return cachedFile;
        } catch (Exception e) {
            e.printStackTrace();
            com.arkay.gkinhindi.utils.AnalyticsHelper.recordException(e);
            return null;
        }
    }

    private void deleteFolderRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            File[] children = fileOrDirectory.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteFolderRecursive(child);
                }
            }
        }
        fileOrDirectory.delete();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            Uri dataUri = intent.getData();
            if ("content".equalsIgnoreCase(dataUri.getScheme()) || "file".equalsIgnoreCase(dataUri.getScheme())) {
                File cachedFile = copyUriToCache(this, dataUri);
                if (cachedFile != null) {
                    intent.setData(Uri.fromFile(cachedFile));
                    if (!intent.hasExtra(Constants.EXTRA_SELECTED_FILE_URI)
                            || intent.getStringExtra(Constants.EXTRA_SELECTED_FILE_URI) == null) {
                        intent.putExtra(Constants.EXTRA_SELECTED_FILE_URI, dataUri.toString());
                    }
                    intent.putExtra(Constants.EXTRA_SELECTED_FILE_NAME, cachedFile.getName());
                }
            }
        }

        try {
            super.onCreate(savedInstanceState);
            com.artifex.sonui.editor.Utilities.setDataLeakHandlers(new com.artifex.sonui.SaveAsPdfHandler(this));

            ImageView backButton = findViewById(R.id.img_back);
            if (backButton != null)
                backButton.setOnClickListener(view -> finish());
            PreferenceUtils prefUtils = PreferenceUtils.getInstance(this);
            if (prefUtils.getBoolean(Constants.ACTION_RATE_APP, true)) {
                if (savedInstanceState == null) {
                    int openCount = prefUtils.getInt(Constants.KEY_DOCUMENT_OPEN_COUNT, 0) + 1;
                    prefUtils.setInt(Constants.KEY_DOCUMENT_OPEN_COUNT, openCount);
                    if (openCount == 4) {
                        Utils.showRateDialog(this);
                    }
                }
            }
            Intent currentIntent = getIntent();

            String selectedFilePath;
            if (currentIntent.getData() != null && currentIntent.getData().getPath() != null
                    && !currentIntent.getData().getPath().isEmpty()) {
                selectedFilePath = currentIntent.getStringExtra(Constants.EXTRA_SELECTED_FILE_URI);
                if (selectedFilePath != null) {
                    int lastDot = selectedFilePath.lastIndexOf(".");
                    if (lastDot >= 0) {
                        openedFileExtension = selectedFilePath.substring(lastDot);
                    }
                }
                getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_tittle));
            }

            View rootView = findViewById(android.R.id.content);
            if (rootView instanceof ViewGroup) {
                ViewGroup root = (ViewGroup) rootView;
                View mainContentView = root.getChildCount() > 0 ? root.getChildAt(0) : null;

                FrameLayout bannerAdContainer = new FrameLayout(this);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                params.gravity = Gravity.BOTTOM;
                bannerAdContainer.setLayoutParams(params);

                bannerAdContainer.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
                    int bannerHeight = bottom - top;
                    if (mainContentView != null) {
                        ViewGroup.LayoutParams lp = mainContentView.getLayoutParams();
                        if (lp instanceof ViewGroup.MarginLayoutParams) {
                            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) lp;
                            if (marginParams.bottomMargin != bannerHeight) {
                                marginParams.bottomMargin = bannerHeight;
                                mainContentView.setLayoutParams(marginParams);
                            }
                        }
                    }
                });

                root.addView(bannerAdContainer);

                AdsUtils.showBannerAd(
                        this,
                        bannerAdContainer,
                        BuildConfig.banner_all,
                        BuildConfig.banner_all_2ID,
                        Constants.banner_all,
                        Constants.banner_all_2ID
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            com.arkay.gkinhindi.utils.AnalyticsHelper.recordException(e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        com.arkay.gkinhindi.utils.AnalyticsHelper.logScreen(this, "Office Document Viewer");
        
        Intent intent = getIntent();
        if (intent != null) {
            String fileName = intent.getStringExtra(Constants.EXTRA_SELECTED_FILE_NAME);
            if (fileName == null && intent.getData() != null && intent.getData().getPath() != null) {
                fileName = new File(intent.getData().getPath()).getName();
            }
            if (fileName != null) {
                android.os.Bundle bundle = new android.os.Bundle();
                bundle.putString("file_name", fileName);
                bundle.putString("file_extension", openedFileExtension != null ? openedFileExtension : "");
                com.arkay.gkinhindi.utils.AnalyticsHelper.logEvent("view_document", bundle);
            }
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        // Removed recreate() as it was causing redirection/reloading issues after save
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        com.artifex.sonui.editor.SODataLeakHandlers handler = com.artifex.sonui.editor.Utilities.getDataLeakHandlers();
        if (handler != null) {
            handler.onActivityResult(requestCode, resultCode, data);
        }
    }
}
