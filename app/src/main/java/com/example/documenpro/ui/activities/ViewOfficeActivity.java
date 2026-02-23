package com.example.documenpro.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.artifex.sonui.AppNUIActivity;
import com.example.documenpro.GlobalConstant;
import com.example.documenpro.R;
import com.example.documenpro.SharedPreferenceUtils;
import com.example.documenpro.utils.Utils;

public class ViewOfficeActivity extends AppNUIActivity {

    public static final int REQUEST_CAMERA_PERMISSIONS = 0x11111;

    String tempFileExtension;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            ImageView imvBack = findViewById(R.id.img_back);
            if (imvBack != null)
                imvBack.setOnClickListener(view -> finish());
            if (SharedPreferenceUtils.getInstance(this).getBoolean(GlobalConstant.RATE_APP, true)) {
                Utils.showRateDialog(this);
            }
            Intent intent = getIntent();

            String filePath;
            if (intent.getData() != null && intent.getData().getPath() != null && !intent.getData().getPath().isEmpty()) {
                filePath = intent.getStringExtra(GlobalConstant.KEY_SELECTED_FILE_URI);
                if (filePath != null)
                    tempFileExtension = filePath.substring(filePath.lastIndexOf("."));
                getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.bg_tittle));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
