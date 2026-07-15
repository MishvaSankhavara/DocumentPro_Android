package com.arkay.gkinhindi.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class AnalyticsHelper {
    private static FirebaseAnalytics mFirebaseAnalytics;

    public static void init(Context context) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context.getApplicationContext());
    }

    public static void logEvent(String eventName, Bundle bundle) {
        if (mFirebaseAnalytics != null) {
            mFirebaseAnalytics.logEvent(eventName, bundle);
        }
    }

    public static void logScreen(Activity activity, String screenName) {
        if (mFirebaseAnalytics != null) {
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
            bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, activity.getClass().getSimpleName());
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
        }
    }

    public static void logTaskStart(String taskType) {
        Bundle bundle = new Bundle();
        bundle.putString("task_type", taskType);
        logEvent("task_start", bundle);
    }

    public static void logTaskSuccess(String taskType, String fileName) {
        Bundle bundle = new Bundle();
        bundle.putString("task_type", taskType);
        bundle.putString("file_name", fileName);
        logEvent("task_success", bundle);
    }

    public static void logTaskFailure(String taskType, String errorMsg, Throwable throwable) {
        Bundle bundle = new Bundle();
        bundle.putString("task_type", taskType);
        bundle.putString("error_msg", errorMsg);
        logEvent("task_failure", bundle);

        if (throwable != null) {
            FirebaseCrashlytics.getInstance().recordException(throwable);
        } else {
            FirebaseCrashlytics.getInstance().log("Task Failure: " + taskType + " - " + errorMsg);
        }
    }

    public static void recordException(Throwable throwable) {
        FirebaseCrashlytics.getInstance().recordException(throwable);
    }

    public static void log(String msg) {
        FirebaseCrashlytics.getInstance().log(msg);
    }
}
