package com.arkay.gkinhindi.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.arkay.gkinhindi.BuildConfig;
import com.arkay.gkinhindi.Constants;
import com.arkay.gkinhindi.R;
import com.arkay.gkinhindi.PreferenceUtils;
import com.arkay.gkinhindi.utils.DialogManagerUtils;
import com.arkay.gkinhindi.utils.Utils;

import java.util.Objects;

public class AppSettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private AppCompatTextView languageTextView;

    private AppCompatTextView appVersionTextView;
    private android.widget.ImageView ivBack;
    private android.widget.TextView tvToolbarName;
    private SwitchCompat swDarkMode;

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
    protected void onResume() {
        super.onResume();
        com.arkay.gkinhindi.utils.AnalyticsHelper.logScreen(this, "Settings Screen");
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
                .setText(PreferenceUtils.getInstance(this).getString(Constants.PREF_LANGUAGE_NAME, "English"));

        swDarkMode.setChecked(PreferenceUtils.getInstance(this).getBoolean(Constants.PREF_NIGHT_MODE, false));
        swDarkMode.setOnCheckedChangeListener((buttonView, is_night) -> {
            PreferenceUtils.getInstance(AppSettingsActivity.this).setBoolean(Constants.PREF_NIGHT_MODE,
                    is_night);
            android.os.Bundle bundle = new android.os.Bundle();
            bundle.putBoolean("night_mode_enabled", is_night);
            com.arkay.gkinhindi.utils.AnalyticsHelper.logEvent("toggle_night_mode", bundle);
            Utils.setTheme(getApplication(), is_night);
            Intent intent = new Intent(AppSettingsActivity.this, AppSettingsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void initializeViews() {
        languageTextView = findViewById(R.id.tv_language_hint);
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
            com.arkay.gkinhindi.utils.AnalyticsHelper.logEvent("click_file_manage", null);
            Utils.chooseFileManager(this);
        } else if (idView == R.id.cl_rate_app) {
            com.arkay.gkinhindi.utils.AnalyticsHelper.logEvent("click_rate_app", null);
            Utils.showRateDialog(this);
        } else if (idView == R.id.cl_share_app) {
            com.arkay.gkinhindi.utils.AnalyticsHelper.logEvent("click_share_app", null);
            Utils.shareApp(this);
        } else if (idView == R.id.cl_language_options) {
            com.arkay.gkinhindi.utils.AnalyticsHelper.logEvent("click_language_options", null);
            DialogManagerUtils.showLanguageSelection(AppSettingsActivity.this);
        } else if (idView == R.id.cl_faq) {
            com.arkay.gkinhindi.utils.AnalyticsHelper.logEvent("click_faq", null);
        } else if (idView == R.id.cl_request) {
            com.arkay.gkinhindi.utils.AnalyticsHelper.logEvent("click_request", null);
        } else if (idView == R.id.cl_feedback) {
            com.arkay.gkinhindi.utils.AnalyticsHelper.logEvent("click_feedback", null);
            Utils.feedbackApp(this);
        } else if (idView == R.id.cl_dark_mode) {
            swDarkMode.setChecked(!swDarkMode.isChecked());
        } else if (idView == R.id.cl_privacy_policy) {
            com.arkay.gkinhindi.utils.AnalyticsHelper.logEvent("click_privacy_policy", null);
            Utils.openPrivacyPolicy(this);
        }

    }
}