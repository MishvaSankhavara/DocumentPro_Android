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
import com.example.documenpro.AppGlobalConstants;
import com.example.documenpro.R;
import com.example.documenpro.PreferenceUtils;
import com.example.documenpro.utils.DialogManagerUtils;
import com.example.documenpro.utils.Utils;

import java.util.Objects;

public class AppSettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private AppCompatTextView languageTextView;

    private AppCompatTextView appVersionTextView;
    private android.widget.ImageView ivBack;
    private android.widget.TextView tvToolbarName;
    private com.example.documenpro.ui.customviews.switchdaynight.ThemeToggleSwitch swDarkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.act_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setupToolbar();
        initializeViews();
        initializeListeners();
        loadSettingsData();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.settings_main_toolbar);
        ivBack = findViewById(R.id.iv_back);
        tvToolbarName = findViewById(R.id.tv_name);

        ivBack.setOnClickListener(v -> finish());
        tvToolbarName.setText(R.string.settings_title);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void loadSettingsData() {
        appVersionTextView.setText(BuildConfig.VERSION_NAME);

        languageTextView
                .setText(PreferenceUtils.getInstance(this).getString(AppGlobalConstants.PREF_LANGUAGE_NAME, "English"));

        swDarkMode
                .setNightMode(PreferenceUtils.getInstance(this).getBoolean(AppGlobalConstants.PREF_NIGHT_MODE, false));
        swDarkMode.setSwitchListener(is_night -> {
            PreferenceUtils.getInstance(AppSettingsActivity.this).setBoolean(AppGlobalConstants.PREF_NIGHT_MODE,
                    is_night);
            Utils.setTheme(getApplication(), is_night);
            Intent intent = new Intent(AppSettingsActivity.this, AppSettingsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void initializeViews() {
        languageTextView = findViewById(R.id.tv_language_hint);
        appVersionTextView = findViewById(R.id.tv_version);
        swDarkMode = findViewById(R.id.sw_dark_mode);
    }

    private void initializeListeners() {
        findViewById(R.id.cl_file_manage).setOnClickListener(this);
        findViewById(R.id.cl_rate_app).setOnClickListener(this);
        findViewById(R.id.cl_share_app).setOnClickListener(this);
        findViewById(R.id.cl_language_options).setOnClickListener(this);
        findViewById(R.id.cl_faq).setOnClickListener(this);
        findViewById(R.id.cl_request).setOnClickListener(this);
        findViewById(R.id.cl_feedback).setOnClickListener(this);
        findViewById(R.id.cl_privacy_policy).setOnClickListener(this);
        findViewById(R.id.cl_dark_mode).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int idView = v.getId();
        if (idView == R.id.cl_file_manage) {
            Utils.chooseFileManager(this);
        } else if (idView == R.id.cl_rate_app) {
            Utils.showRateDialog(this);
        } else if (idView == R.id.cl_share_app) {
            Utils.shareApp(this);
        } else if (idView == R.id.cl_language_options) {
            DialogManagerUtils.showLanguageSelection(AppSettingsActivity.this);
        } else if (idView == R.id.cl_faq) {

        } else if (idView == R.id.cl_request) {

        } else if (idView == R.id.cl_feedback) {
            Utils.feedbackApp(this);
        } else if (idView == R.id.cl_dark_mode) {
            swDarkMode.setNightMode(!swDarkMode.isNightMode());
        } else if (idView == R.id.cl_privacy_policy) {
            try {
                String url = AppGlobalConstants.PRIVACY_POLICY_URL;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}