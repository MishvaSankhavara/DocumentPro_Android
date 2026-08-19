package com.arkay.gkinhindi.ui.activities;

import com.arkay.gkinhindi.BuildConfig;
import com.arkay.gkinhindi.Constants;
import com.arkay.gkinhindi.utils.AdsUtils;
import com.artifex.sonui.editor.SlideShowActivity;

public class CustomSlideShowActivity extends SlideShowActivity {

    private boolean isAdHandled = false;

    @Override
    public void onBackPressed() {
        exitWithAd();
    }

    @Override
    public void finish() {
        if (isAdHandled) {
            super.finish();
            return;
        }
        exitWithAd();
    }

    private void exitWithAd() {
        if (isAdHandled) {
            super.finish();
            return;
        }
        isAdHandled = true;
        AdsUtils.showInterstitialAdFunction(
                this,
                BuildConfig.interstitial_function,
                BuildConfig.interstitial_function_2ID,
                Constants.interstitial_function,
                Constants.interstitial_function_2ID,
                CustomSlideShowActivity.super::finish
        );
    }
}
