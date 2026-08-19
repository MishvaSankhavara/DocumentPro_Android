package com.arkay.gkinhindi.ui.activities;

import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.arkay.gkinhindi.Constants;
import com.arkay.gkinhindi.R;

public class PrivacyPolicyActivity extends AppCompatActivity {

    private static final String TAG = "PrivacyPolicyActivity";
    private static final String LOCAL_ASSET_URL = "file:///android_asset/privacy_policy.html";

    private WebView webView;
    private ProgressBar progressBar;
    private boolean isFallbackLoaded = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        initViews();
        setupWebView();
        setupBackPress();
        loadPrivacyPolicy();
    }

    private void initViews() {
        progressBar = findViewById(R.id.pb_loading);
        webView = findViewById(R.id.web_view);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
    }

    private void setupWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setSupportZoom(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setDefaultTextEncodingName("utf-8");

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (progressBar != null) {
                    progressBar.setProgress(newProgress);
                    if (newProgress >= 100) {
                        progressBar.setVisibility(View.GONE);
                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (progressBar != null) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (request != null && request.isForMainFrame()) {
                    Log.w(TAG, "Online privacy policy load failed on main frame: " + error);
                    loadLocalFallback();
                }
            }

            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.w(TAG, "Online privacy policy load failed: " + description + " (" + errorCode + ")");
                loadLocalFallback();
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                if (request != null && request.isForMainFrame() && errorResponse != null && errorResponse.getStatusCode() >= 400) {
                    Log.w(TAG, "HTTP error loading online policy: " + errorResponse.getStatusCode());
                    loadLocalFallback();
                }
            }
        });
    }

    private void loadPrivacyPolicy() {
        if (!isNetworkAvailable()) {
            Log.d(TAG, "No internet connection detected, loading local asset directly");
            loadLocalFallback();
            return;
        }

        String liveUrl = Constants.PRIVACY_POLICY_URL;
        if (liveUrl != null && !liveUrl.trim().isEmpty()) {
            Log.d(TAG, "Attempting to load live Privacy Policy URL: " + liveUrl);
            webView.loadUrl(liveUrl);
        } else {
            Log.d(TAG, "Live URL empty, loading local asset");
            loadLocalFallback();
        }
    }

    private void loadLocalFallback() {
        if (!isFallbackLoaded) {
            isFallbackLoaded = true;
            Log.d(TAG, "Loading local privacy policy fallback: " + LOCAL_ASSET_URL);
            webView.post(() -> webView.loadUrl(LOCAL_ASSET_URL));
        }
    }

    private boolean isNetworkAvailable() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                return activeNetwork != null && activeNetwork.isConnected();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking network availability: " + e.getMessage());
        }
        return false;
    }

    private void setupBackPress() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webView != null && webView.canGoBack()) {
                    webView.goBack();
                } else {
                    finish();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.stopLoading();
            webView.destroy();
        }
        super.onDestroy();
    }
}
