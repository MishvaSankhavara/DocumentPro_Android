package com.example.documenpro.advertisement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.example.documenpro.R;
import com.example.documenpro.utils.AdUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AdMobNativeAdManager {

    public static void showNativeBanner3_AdMob(Activity mActivity_AdMob, View view_AdMob) {

        LinearLayout llLoadingAds_AdMob;
        FrameLayout frameLayout_AdMob;

        if (view_AdMob != null) {
            frameLayout_AdMob = view_AdMob.findViewById(R.id.ads_container_banner_3);
            llLoadingAds_AdMob = view_AdMob.findViewById(R.id.ll_loading_ad_banner_3);
        } else {
            frameLayout_AdMob = mActivity_AdMob.findViewById(R.id.ads_container_banner_3);
            llLoadingAds_AdMob = mActivity_AdMob.findViewById(R.id.ll_loading_ad_banner_3);
        }

        AdLoader.Builder builder_AdMob =
                new AdLoader.Builder(mActivity_AdMob, AdUtils.AD_UNIT_ID_NATIVE_TEST);

        builder_AdMob.forNativeAd(nativeAd -> {

            boolean isDestroyed = mActivity_AdMob.isDestroyed();
            if (isDestroyed || mActivity_AdMob.isFinishing() || mActivity_AdMob.isChangingConfigurations()) {
                nativeAd.destroy();
            }

            @SuppressLint("InflateParams")
            NativeAdView adView =
                    (NativeAdView) mActivity_AdMob.getLayoutInflater()
                            .inflate(R.layout.ad_native_admob_banner3, null);

            populateUnifiedNativeAdBanner_AdMob(nativeAd, adView);

            llLoadingAds_AdMob.setVisibility(View.GONE);
            frameLayout_AdMob.removeAllViews();
            frameLayout_AdMob.addView(adView);

        });

        AdLoader adLoader_AdMob = builder_AdMob.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NotNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
            }
        }).build();

        adLoader_AdMob.loadAd(new AdRequest.Builder().build());
    }

    public static void showNativeBanner2_AdMob(Activity mActivity_AdMob, View view_AdMob) {

        LinearLayout llLoadingAds_AdMob;
        FrameLayout frameLayout_AdMob;

        if (view_AdMob != null) {
            frameLayout_AdMob = view_AdMob.findViewById(R.id.ads_container_banner_2);
            llLoadingAds_AdMob = view_AdMob.findViewById(R.id.ll_loading_ad_banner_2);
        } else {
            frameLayout_AdMob = mActivity_AdMob.findViewById(R.id.ads_container_banner_2);
            llLoadingAds_AdMob = mActivity_AdMob.findViewById(R.id.ll_loading_ad_banner_2);
        }

        AdLoader.Builder builder =
                new AdLoader.Builder(mActivity_AdMob, AdUtils.AD_UNIT_ID_NATIVE_TEST);

        builder.forNativeAd(nativeAd -> {

            boolean isDestroyed = mActivity_AdMob.isDestroyed();
            if (isDestroyed || mActivity_AdMob.isFinishing() || mActivity_AdMob.isChangingConfigurations()) {
                nativeAd.destroy();
            }

            @SuppressLint("InflateParams")
            NativeAdView adView =
                    (NativeAdView) mActivity_AdMob.getLayoutInflater()
                            .inflate(R.layout.ad_native_admob_banner2, null);

            populateUnifiedNativeAdBanner_AdMob(nativeAd, adView);

            llLoadingAds_AdMob.setVisibility(View.GONE);
            frameLayout_AdMob.removeAllViews();
            frameLayout_AdMob.addView(adView);

        });

        AdLoader adLoader_AdMob = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NotNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
            }
        }).build();

        adLoader_AdMob.loadAd(new AdRequest.Builder().build());
    }

    public static void showNativeBanner1_AdMob(Activity mActivity_AdMob, View view_AdMob) {

        LinearLayout llLoadingAds;
        FrameLayout frameLayout;

        if (view_AdMob != null) {
            frameLayout = view_AdMob.findViewById(R.id.ads_container_banner_1);
            llLoadingAds = view_AdMob.findViewById(R.id.ll_loading_ad_banner_1);
        } else {
            frameLayout = mActivity_AdMob.findViewById(R.id.ads_container_banner_1);
            llLoadingAds = mActivity_AdMob.findViewById(R.id.ll_loading_ad_banner_1);
        }

        AdLoader.Builder builder =
                new AdLoader.Builder(mActivity_AdMob, AdUtils.AD_UNIT_ID_NATIVE_TEST);

        builder.forNativeAd(nativeAd -> {

            boolean isDestroyed = mActivity_AdMob.isDestroyed();
            if (isDestroyed || mActivity_AdMob.isFinishing() || mActivity_AdMob.isChangingConfigurations()) {
                nativeAd.destroy();
            }

            @SuppressLint("InflateParams")
            NativeAdView adView =
                    (NativeAdView) mActivity_AdMob.getLayoutInflater()
                            .inflate(R.layout.ad_native_admob_banner1, null);

            populateUnifiedNativeAdBanner_AdMob(nativeAd, adView);

            llLoadingAds.setVisibility(View.GONE);
            frameLayout.removeAllViews();
            frameLayout.addView(adView);

        });

        AdLoader adLoader_AdMob = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NotNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
            }
        }).build();

        adLoader_AdMob.loadAd(new AdRequest.Builder().build());
    }

    private static void populateUnifiedNativeAdBanner_AdMob(NativeAd nativeAd_AdMob, NativeAdView adView_AdMob) {

        adView_AdMob.setHeadlineView(adView_AdMob.findViewById(R.id.ad_headline));
        adView_AdMob.setBodyView(adView_AdMob.findViewById(R.id.ad_body));
        adView_AdMob.setCallToActionView(adView_AdMob.findViewById(R.id.ad_call_to_action));
        adView_AdMob.setIconView(adView_AdMob.findViewById(R.id.ad_app_icon));

        ((TextView) Objects.requireNonNull(adView_AdMob.getHeadlineView()))
                .setText(nativeAd_AdMob.getHeadline());

        if (nativeAd_AdMob.getBody() == null) {
            Objects.requireNonNull(adView_AdMob.getBodyView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView_AdMob.getBodyView()).setVisibility(View.VISIBLE);
            ((TextView) adView_AdMob.getBodyView()).setText(nativeAd_AdMob.getBody());
        }

        if (nativeAd_AdMob.getCallToAction() == null) {
            Objects.requireNonNull(adView_AdMob.getCallToActionView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView_AdMob.getCallToActionView()).setVisibility(View.VISIBLE);
            ((TextView) adView_AdMob.getCallToActionView()).setText(nativeAd_AdMob.getCallToAction());
        }

        if (nativeAd_AdMob.getIcon() == null) {
            Objects.requireNonNull(adView_AdMob.getIconView()).setVisibility(View.GONE);
        } else {
            ((ImageView) Objects.requireNonNull(adView_AdMob.getIconView()))
                    .setImageDrawable(nativeAd_AdMob.getIcon().getDrawable());
            adView_AdMob.getIconView().setVisibility(View.VISIBLE);
        }

        adView_AdMob.setNativeAd(nativeAd_AdMob);
    }
}