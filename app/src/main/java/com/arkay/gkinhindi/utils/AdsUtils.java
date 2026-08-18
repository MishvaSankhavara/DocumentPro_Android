package com.arkay.gkinhindi.utils;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.arkay.gkinhindi.Constants;
import com.arkay.gkinhindi.R;
import com.arkay.gkinhindi.ui.dialog.AppLoadingDialog;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

public class AdsUtils {

    private static final String TAG = "====NativeAd";
    private static InterstitialAd splashInterstitialAd = null;

    private static boolean isSplashInterstitialLoading = false;

    public static void preloadSplashInterstitialAd(
            final Activity activity,
            final String primaryAdUnitId,
            final String fallbackAdUnitId,
            final boolean flag1,
            final boolean flag2
    ) {
        Log.d("====SplashInterstitial", "preloadSplashInterstitialAd: enable_all_ads = " + Constants.enable_all_ads +
                " \n flag1 = " + flag1 + " \n flag2 = " + flag2 +
                " \n primaryID = " + primaryAdUnitId + " \n secondaryID = " + fallbackAdUnitId);

        if (activity == null || !Constants.enable_all_ads || (!flag1 && !flag2)) {
            Log.d("====SplashInterstitial", "preloadSplashInterstitialAd: SKIPPED — ads disabled or both flags are false");
            return;
        }

        if (splashInterstitialAd != null) {
            Log.d("====SplashInterstitial", "preloadSplashInterstitialAd: Already preloaded!");
            return;
        }

        if (isSplashInterstitialLoading) {
            Log.d("====SplashInterstitial", "preloadSplashInterstitialAd: Already loading in background...");
            return;
        }

        isSplashInterstitialLoading = true;
        loadSplashInterstitialInternal(activity, primaryAdUnitId, fallbackAdUnitId, flag1, flag2, false);
    }

    private static void loadSplashInterstitialInternal(
            final Activity activity,
            final String primaryAdUnitId,
            final String fallbackAdUnitId,
            final boolean flag1,
            final boolean flag2,
            final boolean usingFallback
    ) {
        boolean shouldLoad = usingFallback ? flag2 : flag1;
        String targetAdUnitId = usingFallback ? fallbackAdUnitId : primaryAdUnitId;

        Log.d("====SplashInterstitial", "loadSplashInterstitialInternal: usingFallback = " + usingFallback +
                " \n targetID = " + targetAdUnitId +
                " \n primaryID = " + primaryAdUnitId + " \n secondaryID = " + fallbackAdUnitId +
                " \n flag1 = " + flag1 + " \n flag2 = " + flag2);

        if (!shouldLoad || targetAdUnitId == null || targetAdUnitId.trim().isEmpty()) {
            Log.d("====SplashInterstitial", "loadSplashInterstitialInternal: SKIPPED — shouldLoad = " + shouldLoad + " or targetID invalid");

            if (!usingFallback && flag2 && fallbackAdUnitId != null && !fallbackAdUnitId.trim().isEmpty()) {
                loadSplashInterstitialInternal(activity, primaryAdUnitId, fallbackAdUnitId, flag1, flag2, true);
            } else {
                isSplashInterstitialLoading = false;
            }
            return;
        }

        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(activity, targetAdUnitId, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                Log.d("====SplashInterstitial", "onAdLoaded: Splash Interstitial loaded successfully!");
                isSplashInterstitialLoading = false;
                splashInterstitialAd = interstitialAd;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.d("====SplashInterstitial", "onAdFailedToLoad: " + loadAdError.getMessage() +
                        " \n usingFallback = " + usingFallback + " | targetID = " + targetAdUnitId);
                if (!usingFallback && flag2 && fallbackAdUnitId != null && !fallbackAdUnitId.trim().isEmpty()) {
                    Log.d("====SplashInterstitial", "onAdFailedToLoad: Triggering fallback load");
                    loadSplashInterstitialInternal(activity, primaryAdUnitId, fallbackAdUnitId, flag1, flag2, true);
                } else {
                    isSplashInterstitialLoading = false;
                    splashInterstitialAd = null;
                }
            }
        });
    }

    public static void showSplashInterstitialAd(final Activity activity, final Runnable onDismiss) {
        Log.d("====SplashInterstitial", "showSplashInterstitialAd: enable_all_ads = " + Constants.enable_all_ads +
                " \n interstitial_splash = " + Constants.interstitial_splash +
                " \n interstitial_splash_2ID = " + Constants.interstitial_splash_2ID +
                " \n adLoaded = " + (splashInterstitialAd != null) +
                " \n isLoading = " + isSplashInterstitialLoading);

        if (activity == null || activity.isDestroyed() || activity.isFinishing()) {
            if (onDismiss != null) onDismiss.run();
            return;
        }

        if (!Constants.enable_all_ads || (!Constants.interstitial_splash && !Constants.interstitial_splash_2ID)) {
            Log.d("====SplashInterstitial", "showSplashInterstitialAd: Ads disabled globally or flags are false");
            splashInterstitialAd = null;
            if (onDismiss != null) onDismiss.run();
            return;
        }

        if (splashInterstitialAd != null) {
            displayLoadedSplashInterstitial(activity, onDismiss);
        } else if (isSplashInterstitialLoading) {
            Log.d("====SplashInterstitial", "showSplashInterstitialAd: Ad is still loading, waiting up to 1.5s...");
            final android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
            final long startTime = System.currentTimeMillis();
            final Runnable checkRunnable = new Runnable() {
                @Override
                public void run() {
                    if (activity.isDestroyed() || activity.isFinishing()) return;
                    if (splashInterstitialAd != null) {
                        displayLoadedSplashInterstitial(activity, onDismiss);
                    } else if (isSplashInterstitialLoading && (System.currentTimeMillis() - startTime < 1500)) {
                        handler.postDelayed(this, 100);
                    } else {
                        Log.d("====SplashInterstitial", "showSplashInterstitialAd: Timed out waiting for ad load, proceeding");
                        if (onDismiss != null) onDismiss.run();
                    }
                }
            };
            handler.postDelayed(checkRunnable, 100);
        } else {
            Log.d("====SplashInterstitial", "showSplashInterstitialAd: No ad loaded or loading, proceeding to navigate");
            splashInterstitialAd = null;
            if (onDismiss != null) onDismiss.run();
        }
    }

    private static void displayLoadedSplashInterstitial(final Activity activity, final Runnable onDismiss) {
        if (splashInterstitialAd == null) {
            if (onDismiss != null) onDismiss.run();
            return;
        }
        splashInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                Log.d("====SplashInterstitial", "onAdDismissedFullScreenContent");
                splashInterstitialAd = null;
                if (onDismiss != null) onDismiss.run();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                Log.d("====SplashInterstitial", "onAdFailedToShowFullScreenContent: " + adError.getMessage());
                splashInterstitialAd = null;
                if (onDismiss != null) onDismiss.run();
            }
        });
        splashInterstitialAd.show(activity);
    }

    private static int functionClickCounter = 0;

    public static void showInterstitialAdFunction(
            final Activity activity,
            final String primaryAdUnitId,
            final String fallbackAdUnitId,
            final boolean flag1,
            final boolean flag2,
            final Runnable onAdDismissed
    ) {
        Log.d("====InterstitialAd", "showInterstitialAdFunction: showing ad on tap | enable_all_ads = " + Constants.enable_all_ads +
                " \n flag1 = " + flag1 + " \n flag2 = " + flag2 +
                " \n primaryID = " + primaryAdUnitId + " \n secondaryID = " + fallbackAdUnitId);

        if (activity == null || activity.isDestroyed() || activity.isFinishing() || !Constants.enable_all_ads || (!flag1 && !flag2)) {
            Log.d("====InterstitialAd", "showInterstitialAdFunction: SKIPPED — ads disabled or flags false");
            if (onAdDismissed != null) onAdDismissed.run();
            return;
        }

        AppLoadingDialog loadingDialog = new AppLoadingDialog(activity);
        try {
            if (!activity.isFinishing() && !activity.isDestroyed()) {
                loadingDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            try {
                if (loadingDialog.isShowing() && !activity.isFinishing() && !activity.isDestroyed()) {
                    loadingDialog.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (activity.isFinishing() || activity.isDestroyed()) {
                if (onAdDismissed != null) onAdDismissed.run();
                return;
            }

            if (splashInterstitialAd != null) {
                splashInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        Log.d("====InterstitialAd", "onAdDismissedFullScreenContent");
                        splashInterstitialAd = null;
                        preloadSplashInterstitialAd(activity, primaryAdUnitId, fallbackAdUnitId, flag1, flag2);
                        if (onAdDismissed != null) onAdDismissed.run();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        Log.d("====InterstitialAd", "onAdFailedToShowFullScreenContent: " + adError.getMessage());
                        splashInterstitialAd = null;
                        preloadSplashInterstitialAd(activity, primaryAdUnitId, fallbackAdUnitId, flag1, flag2);
                        if (onAdDismissed != null) onAdDismissed.run();
                    }
                });
                splashInterstitialAd.show(activity);
            } else {
                Log.d("====InterstitialAd", "No preloaded ad ready, preloading now and opening file");
                preloadSplashInterstitialAd(activity, primaryAdUnitId, fallbackAdUnitId, flag1, flag2);
                if (onAdDismissed != null) onAdDismissed.run();
            }
        }, 500);
    }

    public static void showLargeNativeAd(
            final Activity activity,
            final FrameLayout adContainer,
            final String adsPriority1,
            final String adsPriority2,
            final boolean flag1,
            final boolean flag2
    ) {
        Log.d(TAG, "showLargeNativeAd: enable_all_ads=" + Constants.enable_all_ads +
                " | flag1=" + flag1 + " | flag2=" + flag2 +
                " | primaryID=" + adsPriority1 + " | secondaryID=" + adsPriority2);

        if (activity == null || adContainer == null) {
            Log.d(TAG, "showLargeNativeAd: SKIPPED — activity or container is null");
            return;
        }

        if (!Constants.enable_all_ads || (!flag1 && !flag2)) {
            Log.d(TAG, "showLargeNativeAd: SKIPPED — ads disabled globally or flags are false");
            adContainer.removeAllViews();
            adContainer.setVisibility(View.GONE);
            return;
        }

        try {
            View shimmerView = activity.getLayoutInflater().inflate(R.layout.layout_native_shimmer, adContainer, false);
            adContainer.removeAllViews();
            adContainer.addView(shimmerView);
            adContainer.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Log.d(TAG, "shimmer inflate error: " + e.getMessage());
        }

        loadNativeAdInternal(activity, adContainer, adsPriority1, adsPriority2, flag1, flag2, false);
    }

    private static void loadNativeAdInternal(
            final Activity activity,
            final FrameLayout adContainer,
            final String primaryAdUnitId,
            final String fallbackAdUnitId,
            final boolean flag1,
            final boolean flag2,
            final boolean usingFallback
    ) {
        if (activity == null || adContainer == null) return;

        boolean shouldLoad = usingFallback ? flag2 : flag1;
        String targetAdUnitId = usingFallback ? fallbackAdUnitId : primaryAdUnitId;

        Log.d(TAG, "loadNativeAdInternal: usingFallback=" + usingFallback +
                " | targetID=" + targetAdUnitId +
                " | primaryID=" + primaryAdUnitId + " | secondaryID=" + fallbackAdUnitId +
                " | flag1=" + flag1 + " | flag2=" + flag2);

        if (!shouldLoad || targetAdUnitId == null || targetAdUnitId.trim().isEmpty()) {
            if (!usingFallback && flag2 && fallbackAdUnitId != null && !fallbackAdUnitId.trim().isEmpty()) {
                Log.d(TAG, "loadNativeAdInternal: Retrying with fallback ID");
                loadNativeAdInternal(activity, adContainer, primaryAdUnitId, fallbackAdUnitId, flag1, flag2, true);
            } else {
                adContainer.removeAllViews();
                adContainer.setVisibility(View.GONE);
            }
            return;
        }

        AdLoader adLoader = new AdLoader.Builder(activity, targetAdUnitId)
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                        if (activity.isDestroyed() || activity.isFinishing()) {
                            Log.d(TAG, "onNativeAdLoaded: activity destroyed/finishing, discarding ad");
                            nativeAd.destroy();
                            return;
                        }

                        if (!Constants.enable_all_ads || (!flag1 && !flag2)) {
                            Log.d(TAG, "onNativeAdLoaded: SKIPPED at render time — ads disabled or flags are false");
                            adContainer.removeAllViews();
                            adContainer.setVisibility(View.GONE);
                            nativeAd.destroy();
                            return;
                        }

                        try {
                            NativeAdView adView = (NativeAdView) activity.getLayoutInflater()
                                    .inflate(R.layout.layout_native_ad_large, null);

                            adView.setBackgroundResource(R.drawable.bg_native_ad_card);
                            populateNativeAdView(nativeAd, adView);

                            adContainer.removeAllViews();
                            adContainer.addView(adView);
                            adContainer.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            Log.e(TAG, "onNativeAdLoaded layout error: " + e.getMessage());
                            nativeAd.destroy();
                        }
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                        Log.d(TAG, "onAdFailedToLoad: " + adError.getMessage() + " | usingFallback=" + usingFallback);
                        if (!usingFallback && flag2 && fallbackAdUnitId != null && !fallbackAdUnitId.trim().isEmpty()) {
                            loadNativeAdInternal(activity, adContainer, primaryAdUnitId, fallbackAdUnitId, flag1, flag2, true);
                        } else {
                            adContainer.removeAllViews();
                            adContainer.setVisibility(View.GONE);
                        }
                    }
                })
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }

    private static void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        adView.setMediaView(adView.findViewById(R.id.ad_media));
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        if (adView.getHeadlineView() != null) {
            ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        }

        if (adView.getBodyView() != null) {
            if (nativeAd.getBody() == null) {
                adView.getBodyView().setVisibility(View.GONE);
            } else {
                adView.getBodyView().setVisibility(View.VISIBLE);
                ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
            }
        }

        if (adView.getCallToActionView() != null) {
            if (nativeAd.getCallToAction() == null) {
                adView.getCallToActionView().setVisibility(View.GONE);
            } else {
                adView.getCallToActionView().setVisibility(View.VISIBLE);
                if (adView.getCallToActionView() instanceof TextView) {
                    ((TextView) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
                }
            }
        }

        if (adView.getIconView() != null) {
            if (nativeAd.getIcon() == null) {
                adView.getIconView().setVisibility(View.GONE);
            } else {
                ((ImageView) adView.getIconView()).setImageDrawable(
                        nativeAd.getIcon().getDrawable());
                adView.getIconView().setVisibility(View.VISIBLE);
            }
        }

        if (adView.getAdvertiserView() != null) {
            if (nativeAd.getAdvertiser() == null) {
                adView.getAdvertiserView().setVisibility(View.GONE);
            } else {
                ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
                adView.getAdvertiserView().setVisibility(View.VISIBLE);
            }
        }

        adView.setNativeAd(nativeAd);
    }

    public static void showBannerAd(
            final Activity activity,
            final FrameLayout bannerContainer,
            final String primaryAdUnitId,
            final String fallbackAdUnitId,
            final boolean flag1,
            final boolean flag2
    ) {
        Log.d("====BannerAd", "showBannerAd: enable_all_ads=" + Constants.enable_all_ads +
                " | flag1=" + flag1 + " | flag2=" + flag2 +
                " | primaryID=" + primaryAdUnitId + " | secondaryID=" + fallbackAdUnitId);

        if (activity == null || bannerContainer == null) {
            Log.d("====BannerAd", "showBannerAd: SKIPPED — activity or container is null");
            return;
        }

        if (!Constants.enable_all_ads || (!flag1 && !flag2)) {
            Log.d("====BannerAd", "showBannerAd: SKIPPED — ads disabled globally or flags are false");
            bannerContainer.removeAllViews();
            bannerContainer.setVisibility(View.GONE);
            return;
        }

        loadBannerAdInternal(activity, bannerContainer, primaryAdUnitId, fallbackAdUnitId, flag1, flag2, false);
    }

    private static void loadBannerAdInternal(
            final Activity activity,
            final FrameLayout bannerContainer,
            final String primaryAdUnitId,
            final String fallbackAdUnitId,
            final boolean flag1,
            final boolean flag2,
            final boolean usingFallback
    ) {
        if (activity == null || bannerContainer == null) return;

        boolean shouldLoad = usingFallback ? flag2 : flag1;
        String targetAdUnitId = usingFallback ? fallbackAdUnitId : primaryAdUnitId;

        Log.d("====BannerAd", "loadBannerAdInternal: usingFallback=" + usingFallback +
                " | targetID=" + targetAdUnitId +
                " | primaryID=" + primaryAdUnitId + " | secondaryID=" + fallbackAdUnitId +
                " | flag1=" + flag1 + " | flag2=" + flag2);

        if (!shouldLoad || targetAdUnitId == null || targetAdUnitId.trim().isEmpty()) {
            if (!usingFallback && flag2 && fallbackAdUnitId != null && !fallbackAdUnitId.trim().isEmpty()) {
                Log.d("====BannerAd", "loadBannerAdInternal: Retrying with fallback ID");
                loadBannerAdInternal(activity, bannerContainer, primaryAdUnitId, fallbackAdUnitId, flag1, flag2, true);
            } else {
                bannerContainer.removeAllViews();
                bannerContainer.setVisibility(View.GONE);
            }
            return;
        }

        try {
            com.google.android.gms.ads.AdView adView = new com.google.android.gms.ads.AdView(activity);
            adView.setAdUnitId(targetAdUnitId);

            int adWidth = activity.getResources().getDisplayMetrics().widthPixels;
            if (adWidth <= 0) adWidth = 320;
            float density = activity.getResources().getDisplayMetrics().density;
            int adaptiveWidth = density > 0 ? (int) (adWidth / density) : 320;
            com.google.android.gms.ads.AdSize adSize = com.google.android.gms.ads.AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adaptiveWidth);
            adView.setAdSize(adSize);

            adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    if (activity.isDestroyed() || activity.isFinishing()) {
                        adView.destroy();
                        return;
                    }
                    if (!Constants.enable_all_ads || (!flag1 && !flag2)) {
                        Log.d("====BannerAd", "onAdLoaded: SKIPPED at render time — ads disabled or flags are false");
                        bannerContainer.removeAllViews();
                        bannerContainer.setVisibility(View.GONE);
                        adView.destroy();
                        return;
                    }
                    bannerContainer.removeAllViews();
                    bannerContainer.addView(adView);
                    bannerContainer.setVisibility(View.VISIBLE);
                    Log.d("====BannerAd", "onAdLoaded: banner ad displayed successfully");
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                    Log.d("====BannerAd", "onAdFailedToLoad: " + adError.getMessage() + " \n usingFallback=" + usingFallback);
                    if (!usingFallback && flag2 && fallbackAdUnitId != null && !fallbackAdUnitId.trim().isEmpty()) {
                        loadBannerAdInternal(activity, bannerContainer, primaryAdUnitId, fallbackAdUnitId, flag1, flag2, true);
                    } else {
                        bannerContainer.removeAllViews();
                        bannerContainer.setVisibility(View.GONE);
                    }
                }
            });

            adView.loadAd(new AdRequest.Builder().build());
        } catch (Exception e) {
            Log.e("====BannerAd", "loadBannerAdInternal error: " + e.getMessage());
            bannerContainer.removeAllViews();
            bannerContainer.setVisibility(View.GONE);
        }
    }

    public interface OnNativeAdStateListener {
        void onAdLoaded();
        void onAdFailed();
    }

    public static void showFullScreenNativeAd(
            final Activity activity,
            final FrameLayout adContainer,
            final View shimmerView,
            final String adsPriority1,
            final String adsPriority2,
            final boolean flag1,
            final boolean flag2,
            final View.OnClickListener onCloseClickListener,
            final OnNativeAdStateListener listener
    ) {
        Log.d("====FullNativeAd", "showFullScreenNativeAd: enable_all_ads=" + Constants.enable_all_ads +
                " | flag1=" + flag1 + " | flag2=" + flag2 +
                " | primaryID=" + adsPriority1 + " | secondaryID=" + adsPriority2);

        if (activity == null || adContainer == null) return;

        if (!Constants.enable_all_ads || (!flag1 && !flag2)) {
            if (shimmerView != null) {
                shimmerView.setVisibility(View.GONE);
            }
            if (adContainer != null) {
                adContainer.removeAllViews();
                adContainer.setVisibility(View.GONE);
            }
            if (listener != null) {
                listener.onAdFailed();
            }
            return;
        }

        loadFullScreenNativeAdInternal(activity, adContainer, shimmerView, adsPriority1, adsPriority2, flag1, flag2, false, onCloseClickListener, listener);
    }

    private static void loadFullScreenNativeAdInternal(
            final Activity activity,
            final FrameLayout adContainer,
            final View shimmerView,
            final String primaryAdUnitId,
            final String fallbackAdUnitId,
            final boolean flag1,
            final boolean flag2,
            final boolean usingFallback,
            final View.OnClickListener onCloseClickListener,
            final OnNativeAdStateListener listener
    ) {
        if (activity == null || adContainer == null) return;

        boolean shouldLoad = usingFallback ? flag2 : flag1;
        String targetAdUnitId = usingFallback ? fallbackAdUnitId : primaryAdUnitId;

        Log.d("====FullNativeAd", "loadFullScreenNativeAdInternal: usingFallback=" + usingFallback +
                " | targetID=" + targetAdUnitId +
                " | primaryID=" + primaryAdUnitId + " | secondaryID=" + fallbackAdUnitId +
                " | flag1=" + flag1 + " | flag2=" + flag2);

        if (!shouldLoad || targetAdUnitId == null || targetAdUnitId.trim().isEmpty()) {
            if (!usingFallback && flag2 && fallbackAdUnitId != null && !fallbackAdUnitId.trim().isEmpty()) {
                loadFullScreenNativeAdInternal(activity, adContainer, shimmerView, primaryAdUnitId, fallbackAdUnitId, flag1, flag2, true, onCloseClickListener, listener);
            } else {
                if (shimmerView != null) {
                    shimmerView.setVisibility(View.GONE);
                }
                if (adContainer != null) {
                    adContainer.removeAllViews();
                    adContainer.setVisibility(View.GONE);
                }
                if (listener != null) {
                    listener.onAdFailed();
                }
            }
            return;
        }

        AdLoader adLoader = new AdLoader.Builder(activity, targetAdUnitId)
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                        if (activity.isDestroyed() || activity.isFinishing()) {
                            nativeAd.destroy();
                            return;
                        }

                        if (!Constants.enable_all_ads) {
                            nativeAd.destroy();
                            if (shimmerView != null) {
                                shimmerView.setVisibility(View.GONE);
                            }
                            if (adContainer != null) {
                                adContainer.removeAllViews();
                                adContainer.setVisibility(View.GONE);
                            }
                            if (listener != null) {
                                listener.onAdFailed();
                            }
                            return;
                        }

                        try {
                            NativeAdView adView = (NativeAdView) activity.getLayoutInflater()
                                    .inflate(R.layout.layout_native_ad_fullscreen, null);

                            populateNativeAdView(nativeAd, adView);

                            View btnClose = adView.findViewById(R.id.btn_full_ad_close);
                            TextView tvTimer = adView.findViewById(R.id.tv_full_ad_timer);
                            ImageView ivClose = adView.findViewById(R.id.iv_full_ad_close);

                            if (btnClose != null && tvTimer != null && ivClose != null) {
                                tvTimer.setText("10s");
                                tvTimer.setVisibility(View.VISIBLE);
                                ivClose.setVisibility(View.GONE);
                                btnClose.setClickable(false);

                                adView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                                    private android.os.CountDownTimer countDownTimer;

                                    @Override
                                    public void onViewAttachedToWindow(View v) {
                                        if (countDownTimer != null) {
                                            countDownTimer.cancel();
                                        }
                                        tvTimer.setText("10s");
                                        tvTimer.setVisibility(View.VISIBLE);
                                        ivClose.setVisibility(View.GONE);
                                        btnClose.setClickable(false);

                                        countDownTimer = new android.os.CountDownTimer(10000, 1000) {
                                            @Override
                                            public void onTick(long millisUntilFinished) {
                                                if (activity.isDestroyed() || activity.isFinishing()) {
                                                    cancel();
                                                    return;
                                                }
                                                long secondsLeft = (millisUntilFinished + 999) / 1000;
                                                if (secondsLeft > 10) secondsLeft = 10;
                                                tvTimer.setText(secondsLeft + "s");
                                            }

                                            @Override
                                            public void onFinish() {
                                                if (activity.isDestroyed() || activity.isFinishing()) return;
                                                tvTimer.setVisibility(View.GONE);
                                                ivClose.setVisibility(View.VISIBLE);
                                                btnClose.setClickable(true);
                                                btnClose.setOnClickListener(clickVal -> {
                                                    if (onCloseClickListener != null) {
                                                        onCloseClickListener.onClick(clickVal);
                                                    }
                                                });
                                            }
                                        };
                                        countDownTimer.start();
                                    }

                                    @Override
                                    public void onViewDetachedFromWindow(View v) {
                                        if (countDownTimer != null) {
                                            countDownTimer.cancel();
                                        }
                                    }
                                });
                            }

                            if (shimmerView != null) {
                                shimmerView.setVisibility(View.GONE);
                            }
                            adContainer.removeAllViews();
                            adContainer.addView(adView);
                            adContainer.setVisibility(View.VISIBLE);
                            Log.d(TAG, "showFullScreenNativeAd: ad displayed successfully");
                            if (listener != null) {
                                listener.onAdLoaded();
                            }
                        } catch (Exception e) {
                            Log.e("====FullNativeAd", "onNativeAdLoaded layout error: " + e.getMessage());
                            nativeAd.destroy();
                            if (shimmerView != null) {
                                shimmerView.setVisibility(View.GONE);
                            }
                            if (adContainer != null) {
                                adContainer.removeAllViews();
                                adContainer.setVisibility(View.GONE);
                            }
                            if (listener != null) {
                                listener.onAdFailed();
                            }
                        }
                    }
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                        Log.d("====FullNativeAd", "onAdFailedToLoad: " + adError.getMessage() + " | usingFallback=" + usingFallback);
                        if (!usingFallback && flag2 && fallbackAdUnitId != null && !fallbackAdUnitId.trim().isEmpty()) {
                            loadFullScreenNativeAdInternal(activity, adContainer, shimmerView, primaryAdUnitId, fallbackAdUnitId, flag1, flag2, true, onCloseClickListener, listener);
                        } else {
                            if (shimmerView != null) {
                                shimmerView.setVisibility(View.GONE);
                            }
                            if (adContainer != null) {
                                adContainer.removeAllViews();
                                adContainer.setVisibility(View.GONE);
                            }
                            if (listener != null) {
                                listener.onAdFailed();
                            }
                        }
                    }
                })
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }
}
