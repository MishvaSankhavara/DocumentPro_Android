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
import com.example.documenpro.ads.AdClosedListener;
import com.example.documenpro.ads.GoogleMobileAdsConsentManager;
import com.example.documenpro.utils.AdsUtils;
import com.example.documenpro.utils.Utils;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class SplashActivity extends AppCompatActivity {

    int adsCase;
    private FrameLayout adContainer;
    private AdView adView;
    private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);
    private final AtomicBoolean gatherConsentFinished = new AtomicBoolean(false);
    private GoogleMobileAdsConsentManager googleMobileAdsConsentManager;
    private static final long COUNTER_TIME_MILLISECONDS = 4000;
    private long secondsRemaining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        adContainer = findViewById(R.id.my_template_banner);

        // TextView tvAppName = findViewById(R.id.tvNameApp);
        // tvAppName.setText(Utils.setAppName(this));
        adsCase = 0;

        createTimer();
        googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(getApplicationContext());
        googleMobileAdsConsentManager.gatherConsent(this,
                new GoogleMobileAdsConsentManager.OnConsentGatheringCompleteListener() {
                    @Override
                    public void consentGatheringComplete(FormError consentError) {
                        if (consentError != null) {
                            // Consent not obtained in current session.
                            Log.w("CONSENT_FAILED",
                                    String.format("%s: %s", consentError.getErrorCode(), consentError.getMessage()));
                        }

                        gatherConsentFinished.set(true);

                        if (googleMobileAdsConsentManager.canRequestAds()) {
                            initializeMobileAdsSdk();
                        }

                        if (secondsRemaining <= 0) {
                            executeCase();
                        }
                    }
                });

        if (googleMobileAdsConsentManager.canRequestAds()) {
            initializeMobileAdsSdk();
        }
    }

    private void initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
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

    private void createTimer() {
        CountDownTimer countDownTimer = new CountDownTimer(COUNTER_TIME_MILLISECONDS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                secondsRemaining = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1;
            }

            @Override
            public void onFinish() {
                secondsRemaining = 0;

                Application application = getApplication();
                ((MyApplication) application).showAdIfAvailable(
                        SplashActivity.this,
                        new MyApplication.OnShowAdCompleteListener() {
                            @Override
                            public void onShowAdComplete() {
                                // Check if the consent form is currently on screen before moving to the
                                // main activity.
                                if (gatherConsentFinished.get()) {
                                    executeCase();
                                }
                            }
                        });

            }
        };
        countDownTimer.start();
    }

    private void executeCase() {
        if (getIntent() != null && getIntent().getData() != null) {
            adsCase = 3;
            Uri uri = getIntent().getData();
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            if (uri != null) {
                intent.putExtra(GlobalConstant.KEY_DATA_FROM_OUTSIDE, uri.toString());
            } else {
                Log.e("Thang123", "URI is null");
            }
            startActivity(intent);
            finish();
        } else {
            if (!SharedPreferenceUtils.getInstance(SplashActivity.this).getBoolean(GlobalConstant.LANGUAGE_SET,
                    false)) {
                adsCase = 1;
                startActivity(new Intent(SplashActivity.this, LocaleSelectionActivity.class));
                finish();
            } else {
                adsCase = 2;
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }
    }

    public class ExecuteAdsAction implements AdClosedListener {
        @Override
        public void AdClosed() {
            switch (adsCase) {
                case 1:
                    startActivity(new Intent(SplashActivity.this, LocaleSelectionActivity.class));
                    finish();
                    break;
                case 2:
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                    break;
                case 3:
                    Uri uri = getIntent().getData();
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
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

    private void loadAdBanner() {
        adView = new AdView(this);
        adView.setAdUnitId(AdsUtils.ADMOB_ID_BANNER_TM);

        AdSize adSize = Utils.getAdSize(SplashActivity.this, adContainer);
        adView.setAdSize(adSize);

        Bundle extras = new Bundle();
        extras.putString("collapsible", "bottom");
        AdRequest adRequest = new AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                .build();
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adContainer.removeAllViews();
                adContainer.addView(adView);
            }
        });

        adView.loadAd(adRequest);

    }
}