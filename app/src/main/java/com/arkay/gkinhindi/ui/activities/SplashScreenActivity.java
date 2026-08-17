package com.arkay.gkinhindi.ui.activities;

import com.docpro.scanner.settings.LocaleSelectionActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.arkay.gkinhindi.Constants;
import com.arkay.gkinhindi.R;
import com.arkay.gkinhindi.PreferenceUtils;

import android.widget.ProgressBar;
import android.view.WindowManager;

import java.util.concurrent.TimeUnit;

public class SplashScreenActivity extends AppCompatActivity {

    private static final long SPLASH_DURATION_MS = 3500;
    private long remainingSeconds;
    private ProgressBar pbSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.act_splash);
        pbSplash = findViewById(R.id.pb_splash);
        loadSplashBannerAd();
        fetchRemoteConfig();
        startSplashTimer();
    }

    private void fetchRemoteConfig() {
        try {
            com.google.firebase.remoteconfig.FirebaseRemoteConfig remoteConfig =
                    com.google.firebase.remoteconfig.FirebaseRemoteConfig.getInstance();
            com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings configSettings =
                    new com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings.Builder()
                            .setMinimumFetchIntervalInSeconds(0)
                            .build();
            remoteConfig.setConfigSettingsAsync(configSettings);

            remoteConfig.fetchAndActivate().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    boolean enableAllAds = true;
                    if (remoteConfig.getAll().containsKey("enable_all_ads")) {
                        enableAllAds = remoteConfig.getBoolean("enable_all_ads");
                    } else if (remoteConfig.getAll().containsKey("enable_all_inapp_ads")) {
                        enableAllAds = remoteConfig.getBoolean("enable_all_inapp_ads");
                    }
                    Constants.enable_all_ads = enableAllAds;

                    if (remoteConfig.getAll().containsKey("native_onboarding")) {
                        Constants.native_onboarding = remoteConfig.getBoolean("native_onboarding");
                    } else {
                        Constants.native_onboarding = true;
                    }

                    if (remoteConfig.getAll().containsKey("native_onboarding_2ID")) {
                        Constants.native_onboarding_2ID = remoteConfig.getBoolean("native_onboarding_2ID");
                    } else {
                        Constants.native_onboarding_2ID = true;
                    }

                    if (remoteConfig.getAll().containsKey("native_home")) {
                        Constants.native_home = remoteConfig.getBoolean("native_home");
                    } else {
                        Constants.native_home = true;
                    }

                    if (remoteConfig.getAll().containsKey("native_home_2ID")) {
                        Constants.native_home_2ID = remoteConfig.getBoolean("native_home_2ID");
                    } else {
                        Constants.native_home_2ID = true;
                    }

                    if (remoteConfig.getAll().containsKey("banner_splash")) {
                        Constants.banner_splash = remoteConfig.getBoolean("banner_splash");
                    } else {
                        Constants.banner_splash = true;
                    }

                    if (remoteConfig.getAll().containsKey("banner_splash_2ID")) {
                        Constants.banner_splash_2ID = remoteConfig.getBoolean("banner_splash_2ID");
                    } else {
                        Constants.banner_splash_2ID = true;
                    }

                    Log.d("RemoteConfig", "Fetched enable_all_ads=" + Constants.enable_all_ads +
                            ", native_onboarding=" + Constants.native_onboarding +
                            ", native_onboarding_2ID=" + Constants.native_onboarding_2ID +
                            ", native_home=" + Constants.native_home +
                            ", native_home_2ID=" + Constants.native_home_2ID +
                            ", banner_splash=" + Constants.banner_splash +
                            ", banner_splash_2ID=" + Constants.banner_splash_2ID);

                    loadSplashBannerAd();

                } else {
                    Log.w("RemoteConfig", "Fetch failed or pending");
                    loadSplashBannerAd();
                }
            });
        } catch (Exception e) {
            Log.e("RemoteConfig", "Error fetching remote config: " + e.getMessage());
            loadSplashBannerAd();
        }
    }

    private void loadSplashBannerAd() {
        android.widget.FrameLayout bannerAdContainer = findViewById(R.id.banner_ad_container);
        if (bannerAdContainer != null) {
            com.arkay.gkinhindi.utils.AdsUtils.showBannerAd(
                    SplashScreenActivity.this,
                    bannerAdContainer,
                    com.arkay.gkinhindi.BuildConfig.banner_splash_1,
                    com.arkay.gkinhindi.BuildConfig.banner_splash_2,
                    Constants.banner_splash,
                    Constants.banner_splash_2ID
            );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        com.arkay.gkinhindi.utils.AnalyticsHelper.logScreen(this, "Splash Screen");
    }

    private void startSplashTimer() {
        CountDownTimer countDownTimer = new CountDownTimer(SPLASH_DURATION_MS, 50) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingSeconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1;
                if (pbSplash != null) {
                    int progress = (int) (((SPLASH_DURATION_MS - millisUntilFinished) / (float) SPLASH_DURATION_MS) * 100);
                    pbSplash.setProgress(progress);
                }
            }

            @Override
            public void onFinish() {
                remainingSeconds = 0;
                handleNavigationAfterSplash();
            }
        };
        countDownTimer.start();
    }

    private void handleNavigationAfterSplash() {
        if (getIntent() != null && getIntent().getData() != null) {
            Uri uri = getIntent().getData();
            Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
            if (uri != null) {
                intent.putExtra(Constants.EXTRA_DATA_FROM_OUTSIDE, uri.toString());
            } else {
                Log.e("Thang123", "URI is null");
            }
            startActivity(intent);
            finish();
        } else {
            if (!PreferenceUtils.getInstance(SplashScreenActivity.this).getBoolean(Constants.PREF_LANGUAGE_SET,
                    false)) {
                startActivity(new Intent(SplashScreenActivity.this, LocaleSelectionActivity.class));
                finish();
            } else if (!PreferenceUtils.getInstance(SplashScreenActivity.this).getBoolean(Constants.PREF_GUIDE_COMPLETED,
                    false)) {
                startActivity(new Intent(SplashScreenActivity.this, OnBoardActivity.class));
                finish();
            } else {
                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                finish();
            }
        }
    }
}