package com.arkay.gkinhindi.ui.activities;

import com.arkay.gkinhindi.BuildConfig;
import com.arkay.gkinhindi.utils.AdsUtils;
import com.docpro.scanner.settings.LanguageSelectionActivity;

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
        fetchRemoteConfig();
        startSplashTimer();
    }

    private void preloadSplashInterstitialAd() {
        AdsUtils.preloadSplashInterstitialAd(
                SplashScreenActivity.this,
                BuildConfig.interstitial_splash_2ID,
                BuildConfig.interstitial_splash,
                Constants.interstitial_splash_2ID,
                Constants.interstitial_splash
        );
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

                    if (remoteConfig.getAll().containsKey("enable_all_ads")) {
                        Constants.enable_all_ads = remoteConfig.getBoolean("enable_all_ads");
                    }

                    if (remoteConfig.getAll().containsKey("native_onboarding")) {
                        Constants.native_onboarding = remoteConfig.getBoolean("native_onboarding");
                    }

                    if (remoteConfig.getAll().containsKey("native_onboarding_2ID")) {
                        Constants.native_onboarding_2ID = remoteConfig.getBoolean("native_onboarding_2ID");
                    }

                    if (remoteConfig.getAll().containsKey("native_language")) {
                        Constants.native_language = remoteConfig.getBoolean("native_language");
                    }

                    if (remoteConfig.getAll().containsKey("native_language_2ID")) {
                        Constants.native_language_2ID = remoteConfig.getBoolean("native_language_2ID");
                    }

                    if (remoteConfig.getAll().containsKey("native_onboarding_full_screen")) {
                        Constants.native_onboarding_full_screen = remoteConfig.getBoolean("native_onboarding_full_screen");
                    }

                    if (remoteConfig.getAll().containsKey("native_onboarding_full_screen_2ID")) {
                        Constants.native_onboarding_full_screen_2ID = remoteConfig.getBoolean("native_onboarding_full_screen_2ID");
                    }

                    if (remoteConfig.getAll().containsKey("native_home")) {
                        Constants.native_home = remoteConfig.getBoolean("native_home");
                    }

                    if (remoteConfig.getAll().containsKey("native_home_2ID")) {
                        Constants.native_home_2ID = remoteConfig.getBoolean("native_home_2ID");
                    }

                    if (remoteConfig.getAll().containsKey("banner_splash")) {
                        Constants.banner_splash = remoteConfig.getBoolean("banner_splash");
                    }

                    if (remoteConfig.getAll().containsKey("banner_splash_2ID")) {
                        Constants.banner_splash_2ID = remoteConfig.getBoolean("banner_splash_2ID");
                    }

                    if (remoteConfig.getAll().containsKey("banner_all")) {
                        Constants.banner_all = remoteConfig.getBoolean("banner_all");
                    }

                    if (remoteConfig.getAll().containsKey("banner_all_2ID")) {
                        Constants.banner_all_2ID = remoteConfig.getBoolean("banner_all_2ID");
                    }

                    if (remoteConfig.getAll().containsKey("interstitial_splash")) {
                        Constants.interstitial_splash = remoteConfig.getBoolean("interstitial_splash");
                    }

                    if (remoteConfig.getAll().containsKey("interstitial_splash_2ID")) {
                        Constants.interstitial_splash_2ID = remoteConfig.getBoolean("interstitial_splash_2ID");
                    }

                    if (remoteConfig.getAll().containsKey("interstitial_function")) {
                        Constants.interstitial_function = remoteConfig.getBoolean("interstitial_function");
                    }

                    if (remoteConfig.getAll().containsKey("interstitial_function_2ID")) {
                        Constants.interstitial_function_2ID = remoteConfig.getBoolean("interstitial_function_2ID");
                    }

                    if (remoteConfig.getAll().containsKey("reward_save")) {
                        Constants.reward_save = remoteConfig.getBoolean("reward_save");
                    }

                    if (remoteConfig.getAll().containsKey("reward_save_2ID")) {
                        Constants.reward_save_2ID = remoteConfig.getBoolean("reward_save_2ID");
                    }

                    if (remoteConfig.getAll().containsKey("open_resume")) {
                        Constants.open_resume = remoteConfig.getBoolean("open_resume");
                    } else if (remoteConfig.getAll().containsKey("app_open_ad")) {
                        Constants.open_resume = remoteConfig.getBoolean("app_open_ad");
                    }

                    Log.d("=====RemoteConfig", "enable_all_ads=" + Constants.enable_all_ads);
                    Log.d("=====RemoteConfig", "open_resume=" + Constants.open_resume);
                    Log.d("=====RemoteConfig", "native_onboarding=" + Constants.native_onboarding);
                    Log.d("=====RemoteConfig", "native_onboarding_2ID=" + Constants.native_onboarding_2ID);
                    Log.d("=====RemoteConfig", "native_language=" + Constants.native_language);
                    Log.d("=====RemoteConfig", "native_language_2ID=" + Constants.native_language_2ID);
                    Log.d("=====RemoteConfig", "native_onboarding_full_screen=" + Constants.native_onboarding_full_screen);
                    Log.d("=====RemoteConfig", "native_onboarding_full_screen_2ID=" + Constants.native_onboarding_full_screen_2ID);
                    Log.d("=====RemoteConfig", "banner_splash=" + Constants.banner_splash);
                    Log.d("=====RemoteConfig", "banner_splash_2ID=" + Constants.banner_splash_2ID);
                    Log.d("=====RemoteConfig", "banner_all=" + Constants.banner_all);
                    Log.d("=====RemoteConfig", "banner_all_2ID=" + Constants.banner_all_2ID);
                    Log.d("=====RemoteConfig", "interstitial_splash=" + Constants.interstitial_splash);
                    Log.d("=====RemoteConfig", "interstitial_splash_2ID=" + Constants.interstitial_splash_2ID);
                    Log.d("=====RemoteConfig", "interstitial_function=" + Constants.interstitial_function);
                    Log.d("=====RemoteConfig", "interstitial_function_2ID=" + Constants.interstitial_function_2ID);
                    Log.d("=====RemoteConfig", "reward_save=" + Constants.reward_save);
                    Log.d("=====RemoteConfig", "reward_save_2ID=" + Constants.reward_save_2ID);

                    loadSplashBannerAd();
                    preloadSplashInterstitialAd();

                } else {
                    Log.w("=====RemoteConfig", "Fetch failed or pending");
                    loadSplashBannerAd();
                    preloadSplashInterstitialAd();
                }
            });
        } catch (Exception e) {
            Log.e("=====RemoteConfig", "Error fetching remote config: " + e.getMessage());
            loadSplashBannerAd();
            preloadSplashInterstitialAd();
        }
    }

    private void loadSplashBannerAd() {
        android.widget.FrameLayout bannerAdContainer = findViewById(R.id.banner_ad_container);
        if (bannerAdContainer != null) {
            com.arkay.gkinhindi.utils.AdsUtils.showBannerAd(
                    SplashScreenActivity.this,
                    bannerAdContainer,
                    BuildConfig.banner_splash_2ID,
                    BuildConfig.banner_splash,
                    Constants.banner_splash_2ID,
                    Constants.banner_splash
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
                AdsUtils.showSplashInterstitialAd(
                        SplashScreenActivity.this,
                        () -> handleNavigationAfterSplash()
                );
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
                startActivity(new Intent(SplashScreenActivity.this, LanguageSelectionActivity.class));
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