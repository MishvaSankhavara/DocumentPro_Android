package com.arkay.gkinhindi.utils;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.arkay.gkinhindi.Constants;
import com.arkay.gkinhindi.R;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

public class AdsUtils {

    private static final String TAG = "====NativeAd";

    public static void showLargeNativeAd(
            final Activity activity,
            final FrameLayout adContainer,
            final String adsPriority1,
            final String adsPriority2,
            final boolean flag1,
            final boolean flag2
    ) {
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

        if (!shouldLoad || targetAdUnitId == null || targetAdUnitId.trim().isEmpty()) {
            if (!usingFallback && flag2 && fallbackAdUnitId != null && !fallbackAdUnitId.trim().isEmpty()) {
                loadNativeAdInternal(activity, adContainer, primaryAdUnitId, fallbackAdUnitId, flag1, flag2, true);
            } else {
                adContainer.removeAllViews();
                adContainer.setVisibility(View.GONE);
            }
            return;
        }

        Log.d(TAG, "loadNativeAdInternal: loading " + (usingFallback ? "fallback" : "primary") + " id=" + targetAdUnitId);

        AdLoader adLoader = new AdLoader.Builder(activity, targetAdUnitId)
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                        if (activity.isDestroyed() || activity.isFinishing()) {
                            Log.d(TAG, "onNativeAdLoaded: activity destroyed/finishing, discarding ad");
                            nativeAd.destroy();
                            return;
                        }

                        if (!Constants.enable_all_ads) {
                            adContainer.removeAllViews();
                            adContainer.setVisibility(View.GONE);
                            nativeAd.destroy();
                            return;
                        }

                        try {
                            NativeAdView adView = (NativeAdView) activity.getLayoutInflater()
                                    .inflate(R.layout.layout_native_ad_large, null);

                            populateNativeAdView(nativeAd, adView);

                            adContainer.removeAllViews();
                            adContainer.addView(adView);
                            adContainer.setVisibility(View.VISIBLE);
                            Log.d(TAG, "onNativeAdLoaded: ad displayed successfully");
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
}
