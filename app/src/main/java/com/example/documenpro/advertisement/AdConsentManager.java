package com.example.documenpro.advertisement;

import android.app.Activity;
import android.content.Context;

import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.FormError;
import com.google.android.ump.UserMessagingPlatform;
import com.example.documenpro.AppGlobalConstants;

public final class AdConsentManager {

    private final ConsentInformation consentInformation_AdConsentManager;
    private static AdConsentManager instance_AdConsentManager;

    public void gatherConsent_AdConsentManager(Activity activity, OnConsentGatheringCompleteListener onConsentGatheringCompleteListener) {

        ConsentDebugSettings debugSettings = new ConsentDebugSettings.Builder(activity).addTestDeviceHashedId(AppGlobalConstants.TEST_DEVICE_HASHED_ID).build();

        ConsentRequestParameters params = new ConsentRequestParameters.Builder().build();

        consentInformation_AdConsentManager.requestConsentInfoUpdate(activity, params, () -> UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity, formError -> onConsentGatheringCompleteListener.consentGatheringComplete(formError)), requestConsentError -> onConsentGatheringCompleteListener.consentGatheringComplete(requestConsentError));
    }

    public boolean canRequestAds() {
        return consentInformation_AdConsentManager.canRequestAds();
    }

    public static AdConsentManager getInstance(Context context_AdConsentManager) {
        if (instance_AdConsentManager == null) {
            instance_AdConsentManager = new AdConsentManager(context_AdConsentManager);
        }
        return instance_AdConsentManager;
    }

    public interface OnConsentGatheringCompleteListener {
        void consentGatheringComplete(FormError error);
    }

    private AdConsentManager(Context context_AdConsentManager) {
        this.consentInformation_AdConsentManager = UserMessagingPlatform.getConsentInformation(context_AdConsentManager);
    }
}