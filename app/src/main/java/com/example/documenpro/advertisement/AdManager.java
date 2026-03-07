package com.example.documenpro.advertisement;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.example.documenpro.utils.AdUtils;

public class AdManager {

    public static OnAdDismissedListener mListener_AdManager;
    public static InterstitialAd mInterstitialAd_AdManager;

    public static boolean showAds_AdManager(Activity mActivity_AdManager, OnAdDismissedListener listener_AdManager) {
        mListener_AdManager = listener_AdManager;
        InterstitialAd interstitialAd = mInterstitialAd_AdManager;

        if (interstitialAd != null) {
            interstitialAd.show(mActivity_AdManager);
            return true;
        }

        if (listener_AdManager != null) {
            listener_AdManager.OnAdDismissedListener();
        }

        loadAds_AdManager(mActivity_AdManager);
        return false;
    }

    public static void loadAds_AdManager(Context mContext_AdManager) {
        if (mInterstitialAd_AdManager == null) {
            InterstitialAd.load(
                    mContext_AdManager,
                    AdUtils.AD_UNIT_ID_INTERSTITIAL_TEST,
                    new AdRequest.Builder().build(),
                    new InterAdLoadCallBack_AdManager(mContext_AdManager)
            );
        }
    }

    public static class InterAdLoadCallBack_AdManager extends InterstitialAdLoadCallback {

        public final Context mContext_AdManager;

        @Override
        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
            super.onAdLoaded(interstitialAd);
            AdManager.mInterstitialAd_AdManager = interstitialAd;
            AdManager.mInterstitialAd_AdManager
                    .setFullScreenContentCallback(new C2926a_AdManager());
        }

        @Override
        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
            super.onAdFailedToLoad(loadAdError);
            AdManager.mInterstitialAd_AdManager = null;
        }

        public class C2926a_AdManager extends FullScreenContentCallback {

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                super.onAdFailedToShowFullScreenContent(adError);
                AdManager.mInterstitialAd_AdManager = null;

                if (AdManager.mListener_AdManager != null) {
                    AdManager.mListener_AdManager.OnAdDismissedListener();
                }

                AdManager.loadAds_AdManager(
                        InterAdLoadCallBack_AdManager.this.mContext_AdManager
                );
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent();
                AdManager.mInterstitialAd_AdManager = null;

                if (AdManager.mListener_AdManager != null) {
                    AdManager.mListener_AdManager.OnAdDismissedListener();
                }

                AdManager.loadAds_AdManager(
                        InterAdLoadCallBack_AdManager.this.mContext_AdManager
                );
            }
        }

        public InterAdLoadCallBack_AdManager(Context context) {
            this.mContext_AdManager = context;
        }
    }
}