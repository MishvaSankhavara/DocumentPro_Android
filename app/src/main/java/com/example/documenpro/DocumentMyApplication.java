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
import com.example.documenpro.model_reader.PDFReaderModel;
import com.example.documenpro.utils.AdUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class DocumentMyApplication extends Application implements Application.ActivityLifecycleCallbacks, DefaultLifecycleObserver {
    private AppOpenAdController appOpenAdController;
    private Activity foregroundActivity;
    private static DocumentMyApplication mInstance;
    private static ArrayList<PDFReaderModel> arrayListAll;
    private static ArrayList<PDFReaderModel> arrayListMerge;
    private static ArrayList<Integer> arraySplit;
    private static ArrayList<String> arrayPhoto;

    public static DocumentMyApplication getInstance() {
        return mInstance;
    }

    private static synchronized void setInstance(DocumentMyApplication myApplication) {
        synchronized (DocumentMyApplication.class) {
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
        appOpenAdController = new AppOpenAdController();

        AdManager.loadAds_AdManager(this);

    }

    public void updateAllPdfList(ArrayList<PDFReaderModel> arrayList) {
        arrayListAll = arrayList;
    }

    public ArrayList<PDFReaderModel> getAllPdfList() {
        return arrayListAll;
    }

    public void updateMergedPdfList(ArrayList<PDFReaderModel> arrayList) {
        arrayListMerge = arrayList;
    }

    public ArrayList<PDFReaderModel> getMergedPdfList() {
        return arrayListMerge;
    }

    public void clearArrayListMerge() {
        arrayListMerge.clear();
    }

    public void clearArrayListSplit() {
        arraySplit.clear();
    }

    public void updateSplitIndices(ArrayList<Integer> arrayList) {
        arraySplit = arrayList;
    }

    public ArrayList<Integer> getArrayListSplit() {
        return arraySplit;
    }

    public ArrayList<String> getSplitIndices() {
        return arrayPhoto;
    }

    public void updateSelectedImages(ArrayList<String> array) {
        arrayPhoto = array;
    }

    public void clearSelectedImageList() {
        arrayPhoto.clear();
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onStart(owner);
        // Show the ad (if available) when the app moves to foreground.
        appOpenAdController.displayAdIfAvailable(foregroundActivity);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        if (!appOpenAdController.isAdShowing) {
            foregroundActivity = activity;
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

    public void loadAppOpenAd(@NonNull Activity activity) {
        appOpenAdController.loadAd(activity);
    }

    public void showAppOpenAdIfAvailable(
            @NonNull Activity activity, @NonNull AppOpenAdListener onShowAdCompleteListener) {
        appOpenAdController.displayAdIfAvailable(activity, onShowAdCompleteListener);
    }

    public interface AppOpenAdListener {
        void onShowAdComplete();
    }

    private class AppOpenAdController {

        private static final String AD_LOG_TAG = "AppOpenAdManager";
        private static final String AD_UNIT_ID_TEST = "ca-app-pub-3940256099942544/9257395921";

        private final AdConsentManager adsConsentManager =
                AdConsentManager.getInstance(getApplicationContext());
        private AppOpenAd currentAppOpenAd = null;
        private boolean isAdLoading = false;
        private boolean isAdShowing = false;

        private long adLoadTime = 0;

        public AppOpenAdController() {
        }

        private void loadAd(Context context) {
            if (isAdLoading || checkAdAvailability()) {
                return;
            }

            isAdLoading = true;
            AdRequest request = new AdRequest.Builder().build();
            AppOpenAd.load(
                    context, AdUtils.AD_UNIT_ID_OPEN_APP_TEST,
                    request,
                    new AppOpenAd.AppOpenAdLoadCallback() {

                        @Override
                        public void onAdLoaded(AppOpenAd ad) {
                            currentAppOpenAd = ad;
                            isAdLoading = false;
                            adLoadTime = (new Date()).getTime();

                            Log.d(AD_LOG_TAG, "onAdLoaded.");
                        }

                        @Override
                        public void onAdFailedToLoad(LoadAdError loadAdError) {
                            isAdLoading = false;
                            Log.d(AD_LOG_TAG, "onAdFailedToLoad: " + loadAdError.getMessage());
                        }
                    });
        }

        private boolean isAdLoadRecent(long numHours) {
            long dateDifference = (new Date()).getTime() - adLoadTime;
            long numMilliSecondsPerHour = 3600000;
            return (dateDifference < (numMilliSecondsPerHour * numHours));
        }

        private boolean checkAdAvailability() {
            return currentAppOpenAd != null && isAdLoadRecent(4);
        }

        private void displayAdIfAvailable(@NonNull final Activity activity) {
            displayAdIfAvailable(
                    activity,
                    new AppOpenAdListener() {
                        @Override
                        public void onShowAdComplete() {
                        }
                    });
        }

        private void displayAdIfAvailable(
                @NonNull final Activity activity,
                @NonNull AppOpenAdListener onShowAdCompleteListener) {
            if (isAdShowing) {
                Log.d(AD_LOG_TAG, "The app open ad is already showing.");
                return;
            }

            if (!checkAdAvailability()) {
                Log.d(AD_LOG_TAG, "The app open ad is not ready yet.");
                onShowAdCompleteListener.onShowAdComplete();
                if (adsConsentManager.canRequestAds()) {
                    loadAd(foregroundActivity);
                }
                return;
            }

            Log.d(AD_LOG_TAG, "Will show ad.");

            currentAppOpenAd.setFullScreenContentCallback(
                    new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Set the reference to null so isAdAvailable() returns false.
                            currentAppOpenAd = null;
                            isAdShowing = false;

                            Log.d(AD_LOG_TAG, "onAdDismissedFullScreenContent.");

                            onShowAdCompleteListener.onShowAdComplete();
                            if (adsConsentManager.canRequestAds()) {
                                loadAd(activity);
                            }
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            currentAppOpenAd = null;
                            isAdShowing = false;

                            Log.d(AD_LOG_TAG, "onAdFailedToShowFullScreenContent: " + adError.getMessage());


                            onShowAdCompleteListener.onShowAdComplete();
                            if (adsConsentManager.canRequestAds()) {
                                loadAd(activity);
                            }
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            Log.d(AD_LOG_TAG, "onAdShowedFullScreenContent.");
                        }
                    });

            isAdShowing = true;
            currentAppOpenAd.show(activity);
        }
    }
}
