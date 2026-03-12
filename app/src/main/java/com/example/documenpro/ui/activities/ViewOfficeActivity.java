package com.example.documenpro.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.artifex.sonui.AppNUIActivity;
import com.example.documenpro.AppGlobalConstants;
import com.example.documenpro.R;
import com.example.documenpro.PreferenceUtils;
import com.example.documenpro.utils.Utils;

public class ViewOfficeActivity extends AppNUIActivity {

    public static final int REQUEST_CAMERA_PERMISSION_CODE = 0x11111;
    String openedFileExtension;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.artifex.sonui.editor.Utilities.setDataLeakHandlers(new com.artifex.sonui.SaveAsPdfHandler(this));
        try {
            super.onCreate(savedInstanceState);

            ImageView backButton = findViewById(R.id.img_back);
            if (backButton != null)
                backButton.setOnClickListener(view -> finish());
            if (PreferenceUtils.getInstance(this).getBoolean(AppGlobalConstants.ACTION_RATE_APP, true)) {
                Utils.showRateDialog(this);
            }
            Intent intent = getIntent();

            String selectedFilePath;
            if (intent.getData() != null && intent.getData().getPath() != null
                    && !intent.getData().getPath().isEmpty()) {
                selectedFilePath = intent.getStringExtra(AppGlobalConstants.EXTRA_SELECTED_FILE_URI);
                if (selectedFilePath != null)
                    openedFileExtension = selectedFilePath.substring(selectedFilePath.lastIndexOf("."));
                getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_tittle));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
