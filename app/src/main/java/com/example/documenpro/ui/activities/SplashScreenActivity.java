package com.example.documenpro.ui.activities;

import com.docpro.scanner.settings.LocaleSelectionActivity;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.ump.FormError;
import com.example.documenpro.GlobalConstant;
import com.example.documenpro.MyApplication;
import com.example.documenpro.R;
import com.example.documenpro.SharedPreferenceUtils;
import com.example.documenpro.advertisement.OnAdDismissedListener;
import com.example.documenpro.advertisement.AdConsentManager;
import com.example.documenpro.utils.AdsUtils;
import com.example.documenpro.utils.Utils;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class SplashScreenActivity extends AppCompatActivity {

    int navigationCase;
    private FrameLayout bannerAdContainer;
    private AdView bannerAdView;
    private final AtomicBoolean isMobileAdsInitialized = new AtomicBoolean(false);
    private final AtomicBoolean isConsentProcessCompleted = new AtomicBoolean(false);
    private AdConsentManager adsConsentManager;
    private static final long SPLASH_DURATION_MS = 4000;
    private long remainingSeconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        bannerAdContainer = findViewById(R.id.my_template_banner);

        // TextView tvAppName = findViewById(R.id.tvNameApp);
        // tvAppName.setText(Utils.setAppName(this));
        navigationCase = 0;

        startSplashTimer();
        adsConsentManager = AdConsentManager.getInstance(getApplicationContext());
        adsConsentManager.gatherConsent_AdConsentManager(this,
                new AdConsentManager.OnConsentGatheringCompleteListener() {
                    @Override
                    public void consentGatheringComplete(FormError consentError) {
                        if (consentError != null) {
                            // Consent not obtained in current session.
                            Log.w("CONSENT_FAILED",
                                    String.format("%s: %s", consentError.getErrorCode(), consentError.getMessage()));
                        }

                        isConsentProcessCompleted.set(true);

                        if (adsConsentManager.canRequestAds()) {
                            initializeAdsSdk();
                        }

                        if (remainingSeconds <= 0) {
                            handleNavigationAfterSplash();
                        }
                    }
                });

        if (adsConsentManager.canRequestAds()) {
            initializeAdsSdk();
        }
    }

    private void initializeAdsSdk() {
        if (isMobileAdsInitialized.getAndSet(true)) {
            return;
        }
        // Set your test devices.
        MobileAds.setRequestConfiguration(
                new RequestConfiguration.Builder()
                        .setTestDeviceIds(Arrays.asList(GlobalConstant.TEST_DEVICE_HASHED_ID))
                        .build());

        new Thread(
                () -> {
                    MobileAds.initialize(this, initializationStatus -> {
                    });

                })
                .start();
    }

    private void startSplashTimer() {
        CountDownTimer countDownTimer = new CountDownTimer(SPLASH_DURATION_MS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingSeconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1;
            }

            @Override
            public void onFinish() {
                remainingSeconds = 0;

                Application application = getApplication();
                ((MyApplication) application).showAdIfAvailable(
                        SplashScreenActivity.this,
                        new MyApplication.OnShowAdCompleteListener() {
                            @Override
                            public void onShowAdComplete() {
                                // Check if the consent form is currently on screen before moving to the
                                // main activity.
                                if (isConsentProcessCompleted.get()) {
                                    handleNavigationAfterSplash();
                                }
                            }
                        });
            }
        };
        countDownTimer.start();
    }

    private void handleNavigationAfterSplash() {
        if (getIntent() != null && getIntent().getData() != null) {
            navigationCase = 3;
            Uri uri = getIntent().getData();
            Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
            if (uri != null) {
                intent.putExtra(GlobalConstant.KEY_DATA_FROM_OUTSIDE, uri.toString());
            } else {
                Log.e("Thang123", "URI is null");
            }
            startActivity(intent);
            finish();
        } else {
            if (!SharedPreferenceUtils.getInstance(SplashScreenActivity.this).getBoolean(GlobalConstant.LANGUAGE_SET,
                    false)) {
                navigationCase = 1;
                startActivity(new Intent(SplashScreenActivity.this, LocaleSelectionActivity.class));
                finish();
            } else {
                navigationCase = 2;
                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                finish();
            }
        }
    }

    public class AdDismissActionHandler implements OnAdDismissedListener {
        @Override
        public void OnAdDismissedListener() {
            switch (navigationCase) {
                case 1:
                    startActivity(new Intent(SplashScreenActivity.this, LocaleSelectionActivity.class));
                    finish();
                    break;
                case 2:
                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                    finish();
                    break;
                case 3:
                    Uri uri = getIntent().getData();
                    Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                    if (uri != null) {
                        intent.putExtra(GlobalConstant.KEY_DATA_FROM_OUTSIDE, uri.toString());
                    } else {
                        Log.e("Thang123", "URI is null");
                    }
                    startActivity(intent);
                    finish();
            }
        }
    }

    private void loadBannerAd() {
        bannerAdView = new AdView(this);
        bannerAdView.setAdUnitId(AdsUtils.ADMOB_ID_BANNER_TM);

        AdSize adSize = Utils.getAdSize(SplashScreenActivity.this, bannerAdContainer);
        bannerAdView.setAdSize(adSize);

        Bundle extras = new Bundle();
        extras.putString("collapsible", "bottom");
        AdRequest adRequest = new AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                .build();
        bannerAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                bannerAdContainer.removeAllViews();
                bannerAdContainer.addView(bannerAdView);
            }
        });

        bannerAdView.loadAd(adRequest);
    }
}