package com.example.documenpro.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.WindowMetrics;

import com.google.android.gms.ads.AdSize;

public class AdsUtils {
    public static String ADMOB_APP_ID_TM = "ca-app-pub-9087067705102004~1306780371";
    public static String ADMOB_ID_FULL_TEST = "ca-app-pub-3940256099942544/1033173712";
    public static String ADMOB_ID_NATIVE_TEST = "ca-app-pub-3940256099942544/2247696110";
    public static String ADMOB_ID_BANNER_TEST = "ca-app-pub-3940256099942544/6300978111";
    public static String ADMOB_ID_OPEN_TEST = "ca-app-pub-3940256099942544/9257395921";

    public static String ADMOB_ID_BANNER_TM = "ca-app-pub-9087067705102004/1643727493";
    public static AdSize getAdSize(Activity context, View adContainer) {
        WindowMetrics windowMetrics = null;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            windowMetrics = context.getWindowManager().getCurrentWindowMetrics();
        }
        Rect bounds = null;


        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            bounds = windowMetrics.getBounds();
        }
        float adWidthPixels = adContainer.getWidth();
        if (adWidthPixels == 0f && bounds != null) {
            adWidthPixels = bounds.width();
        } else if (adWidthPixels == 0f) {
            adWidthPixels = context.getResources().getDisplayMetrics().widthPixels;
        }
        float density = context.getResources().getDisplayMetrics().density;
        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth);
    }

}
