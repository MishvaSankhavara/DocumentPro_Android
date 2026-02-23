package com.example.documenpro;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.hjq.language.MultiLanguages;
import com.example.documenpro.advertisement.AdManager;
import com.example.documenpro.advertisement.AdConsentManager;
import com.example.documenpro.model.PDFModel;
import com.example.documenpro.utils.AdsUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks, DefaultLifecycleObserver {
    private AppOpenAdManager appOpenAdManager;
    private Activity currentActivity;
    private static MyApplication mInstance;
    private static ArrayList<PDFModel> arrayListAll;
    private static ArrayList<PDFModel> arrayListMerge;
    private static ArrayList<Integer> arraySplit;
    private static ArrayList<String> arrayPhoto;

    public static MyApplication getInstance() {
        return mInstance;
    }

    private static synchronized void setInstance(MyApplication myApplication) {
        synchronized (MyApplication.class) {
            mInstance = myApplication;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (mInstance == null) {
            setInstance(this);
        }
        MultiLanguages.init(this);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        arrayListAll = new ArrayList<>();
        arrayListMerge = new ArrayList<>();
        arraySplit = new ArrayList<>();
        arrayPhoto = new ArrayList<>();


        List<String> testDeviceIds = Collections.singletonList("4569589F401E976491F382B0980BE50E____");

        RequestConfiguration configuration = new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);
        new Thread(() -> {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this, initializationStatus -> {
            });

        }).start();

        this.registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        appOpenAdManager = new AppOpenAdManager();

        AdManager.loadAds_AdManager(this);

    }

    public void setAllPdfList(ArrayList<PDFModel> arrayList) {
        arrayListAll = arrayList;
    }

    public ArrayList<PDFModel> getArrayListAll() {
        return arrayListAll;
    }

    public void setMergePdfList(ArrayList<PDFModel> arrayList) {
        arrayListMerge = arrayList;
    }

    public ArrayList<PDFModel> getArrayListMerge() {
        return arrayListMerge;
    }

    public void clearArrayListMerge() {
        arrayListMerge.clear();
    }

    public void clearArrayListSplit() {
        arraySplit.clear();
    }

    public void setArraylistSplit(ArrayList<Integer> arrayList) {
        arraySplit = arrayList;
    }

    public ArrayList<Integer> getArrayListSplit() {
        return arraySplit;
    }

    public ArrayList<String> getSelectedImages() {
        return arrayPhoto;
    }

    public void setSelectedImages(ArrayList<String> array) {
        arrayPhoto = array;
    }

    public void clearSelectedImages() {
        arrayPhoto.clear();
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onStart(owner);
        // Show the ad (if available) when the app moves to foreground.
        appOpenAdManager.showAdIfAvailable(currentActivity);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {


    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        if (!appOpenAdManager.isShowingAd) {
            currentActivity = activity;
        }
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }

    public void loadAd(@NonNull Activity activity) {
        // We wrap the loadAd to enforce that other classes only interact with MyApplication
        // class.
        appOpenAdManager.loadAd(activity);
    }

    public void showAdIfAvailable(
            @NonNull Activity activity, @NonNull OnShowAdCompleteListener onShowAdCompleteListener) {
        // We wrap the showAdIfAvailable to enforce that other classes only interact with MyApplication
        // class.
        appOpenAdManager.showAdIfAvailable(activity, onShowAdCompleteListener);
    }

    public interface OnShowAdCompleteListener {
        void onShowAdComplete();
    }

    private class AppOpenAdManager {

        private static final String LOG_TAG = "AppOpenAdManager";
        private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/9257395921";

        private final AdConsentManager googleMobileAdsConsentManager =
                AdConsentManager.getInstance(getApplicationContext());
        private AppOpenAd appOpenAd = null;
        private boolean isLoadingAd = false;
        private boolean isShowingAd = false;

        /**
         * Keep track of the time an app open ad is loaded to ensure you don't show an expired ad.
         */
        private long loadTime = 0;

        /**
         * Constructor.
         */
        public AppOpenAdManager() {
        }

        /**
         * Load an ad.
         *
         * @param context the context of the activity that loads the ad
         */
        private void loadAd(Context context) {
            // Do not load ad if there is an unused ad or one is already loading.
            if (isLoadingAd || isAdAvailable()) {
                return;
            }

            isLoadingAd = true;
            AdRequest request = new AdRequest.Builder().build();
            AppOpenAd.load(
                    context, AdsUtils.ADMOB_ID_OPEN_TEST,
                    request,
                    new AppOpenAd.AppOpenAdLoadCallback() {
                        /**
                         * Called when an app open ad has loaded.
                         *
                         * @param ad the loaded app open ad.
                         */
                        @Override
                        public void onAdLoaded(AppOpenAd ad) {
                            appOpenAd = ad;
                            isLoadingAd = false;
                            loadTime = (new Date()).getTime();

                            Log.d(LOG_TAG, "onAdLoaded.");
                        }

                        /**
                         * Called when an app open ad has failed to load.
                         *
                         * @param loadAdError the error.
                         */
                        @Override
                        public void onAdFailedToLoad(LoadAdError loadAdError) {
                            isLoadingAd = false;
                            Log.d(LOG_TAG, "onAdFailedToLoad: " + loadAdError.getMessage());
                        }
                    });
        }

        /**
         * Check if ad was loaded more than n hours ago.
         */
        private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
            long dateDifference = (new Date()).getTime() - loadTime;
            long numMilliSecondsPerHour = 3600000;
            return (dateDifference < (numMilliSecondsPerHour * numHours));
        }

        /**
         * Check if ad exists and can be shown.
         */
        private boolean isAdAvailable() {
            // Ad references in the app open beta will time out after four hours, but this time limit
            // may change in future beta versions. For details, see:
            // https://support.google.com/admob/answer/9341964?hl=en
            return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
        }

        /**
         * Show the ad if one isn't already showing.
         *
         * @param activity the activity that shows the app open ad
         */
        private void showAdIfAvailable(@NonNull final Activity activity) {
            showAdIfAvailable(
                    activity,
                    new OnShowAdCompleteListener() {
                        @Override
                        public void onShowAdComplete() {
                            // Empty because the user will go back to the activity that shows the ad.
                        }
                    });
        }

        /**
         * Show the ad if one isn't already showing.
         *
         * @param activity                 the activity that shows the app open ad
         * @param onShowAdCompleteListener the listener to be notified when an app open ad is complete
         */
        private void showAdIfAvailable(
                @NonNull final Activity activity,
                @NonNull OnShowAdCompleteListener onShowAdCompleteListener) {
            // If the app open ad is already showing, do not show the ad again.
            if (isShowingAd) {
                Log.d(LOG_TAG, "The app open ad is already showing.");
                return;
            }

            // If the app open ad is not available yet, invoke the callback then load the ad.
            if (!isAdAvailable()) {
                Log.d(LOG_TAG, "The app open ad is not ready yet.");
                onShowAdCompleteListener.onShowAdComplete();
                if (googleMobileAdsConsentManager.canRequestAds()) {
                    loadAd(currentActivity);
                }
                return;
            }

            Log.d(LOG_TAG, "Will show ad.");

            appOpenAd.setFullScreenContentCallback(
                    new FullScreenContentCallback() {
                        /** Called when full screen content is dismissed. */
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Set the reference to null so isAdAvailable() returns false.
                            appOpenAd = null;
                            isShowingAd = false;

                            Log.d(LOG_TAG, "onAdDismissedFullScreenContent.");

                            onShowAdCompleteListener.onShowAdComplete();
                            if (googleMobileAdsConsentManager.canRequestAds()) {
                                loadAd(activity);
                            }
                        }

                        /** Called when fullscreen content failed to show. */
                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            appOpenAd = null;
                            isShowingAd = false;

                            Log.d(LOG_TAG, "onAdFailedToShowFullScreenContent: " + adError.getMessage());


                            onShowAdCompleteListener.onShowAdComplete();
                            if (googleMobileAdsConsentManager.canRequestAds()) {
                                loadAd(activity);
                            }
                        }

                        /** Called when fullscreen content is shown. */
                        @Override
                        public void onAdShowedFullScreenContent() {
                            Log.d(LOG_TAG, "onAdShowedFullScreenContent.");
                        }
                    });

            isShowingAd = true;
            appOpenAd.show(activity);
        }
    }

}
