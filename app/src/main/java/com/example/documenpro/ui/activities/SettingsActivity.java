package com.example.documenpro.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.documenpro.BuildConfig;
import com.example.documenpro.GlobalConstant;
import com.example.documenpro.R;
import com.example.documenpro.SharedPreferenceUtils;
import com.example.documenpro.utils.DialogUtils;
import com.example.documenpro.utils.Utils;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private AppCompatTextView tvLanguage;

    private AppCompatTextView tvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initToolbar();
        initViews();
        initListener();
        initData();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(true);

        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initData() {
        tvVersion.setText(BuildConfig.VERSION_NAME);

        tvLanguage.setText(SharedPreferenceUtils.getInstance(this).getString(GlobalConstant.LANGUAGE_NAME, "English"));

    }

    private void initListener() {
        findViewById(R.id.cl_file_manage).setOnClickListener(this);
        findViewById(R.id.cl_rate_app).setOnClickListener(this);
        findViewById(R.id.cl_share_app).setOnClickListener(this);
        findViewById(R.id.cl_language_options).setOnClickListener(this);
        findViewById(R.id.cl_faq).setOnClickListener(this);
        findViewById(R.id.cl_request).setOnClickListener(this);
        findViewById(R.id.cl_feedback).setOnClickListener(this);
        findViewById(R.id.cl_privacy_policy).setOnClickListener(this);

    }

    private void initViews() {
        tvLanguage = findViewById(R.id.tv_language_hint);
        tvVersion = findViewById(R.id.tv_version);
    }

    @Override
    public void onClick(View v) {
        int idView = v.getId();
        if (idView == R.id.cl_file_manage) {

        } else if (idView == R.id.cl_rate_app) {
            Utils.showRateDialog(this);
        } else if (idView == R.id.cl_share_app) {
            Utils.shareApp(this);
        } else if (idView == R.id.cl_language_options) {
            DialogUtils.showLanguageDialog(SettingsActivity.this);
        } else if (idView == R.id.cl_faq) {

        } else if (idView == R.id.cl_request) {

        } else if (idView == R.id.cl_feedback) {
            Utils.feedbackApp(this);
        } else if (idView == R.id.cl_privacy_policy) {
            try {
                String url = GlobalConstant.PRIVACY_URL;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}