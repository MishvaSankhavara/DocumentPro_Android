package com.arkay.gkinhindi.ui.fragments;

import com.docpro.scanner.settings.LanguageSelectionActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.TextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.arkay.gkinhindi.Constants;
import com.arkay.gkinhindi.R;
import com.arkay.gkinhindi.PreferenceUtils;
import com.arkay.gkinhindi.ui.dialog.ThemeLoadingDialog;
import com.arkay.gkinhindi.utils.Utils;
import android.os.Handler;
import android.os.Looper;

public class FragmentSetting extends Fragment implements View.OnClickListener {

    private Activity activityContext;

    private TextView tv_language_hint;
    private SwitchCompat swDarkMode;

    public FragmentSetting() {
    }

    public FragmentSetting(Activity mActivity) {
        this.activityContext = mActivity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_settings, container, false);
        initViews(view);
        initListener(view);
        initData();
        return view;
    }

    private void initData() {

        if (tv_language_hint != null) {
            String langName = PreferenceUtils.getInstance(activityContext).getString(Constants.PREF_LANGUAGE_NAME, "English");
            tv_language_hint.setText(langName);
        }

        if (swDarkMode != null) {
            swDarkMode.setChecked(
                    PreferenceUtils.getInstance(activityContext).getBoolean(Constants.PREF_NIGHT_MODE, false));
            swDarkMode.setOnCheckedChangeListener((buttonView, is_night) -> {
                if (buttonView.isPressed()) {
                    boolean currentNightMode = PreferenceUtils.getInstance(activityContext)
                            .getBoolean(Constants.PREF_NIGHT_MODE, false);
                    if (is_night != currentNightMode) {
                        ThemeLoadingDialog loadingDialog = new ThemeLoadingDialog(activityContext);
                        loadingDialog.show();
                        
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            Utils.setTheme(activityContext.getApplication(), is_night);
                            // activity will recreate, dialog will be gone
                        }, 800);
                    }
                }
            });
        }
    }

    private void initListener(View view) {
        view.findViewById(R.id.cl_file_manage).setOnClickListener(this);
        view.findViewById(R.id.cl_rate_app).setOnClickListener(this);
        view.findViewById(R.id.cl_share_app).setOnClickListener(this);
        view.findViewById(R.id.cl_language_options).setOnClickListener(this);
        view.findViewById(R.id.cl_feedback).setOnClickListener(this);
        view.findViewById(R.id.cl_privacy_policy).setOnClickListener(this);
        view.findViewById(R.id.cl_dark_mode).setOnClickListener(this);
        view.findViewById(R.id.iv_back).setOnClickListener(this);

    }

    private void initViews(View view) {
        tv_language_hint = view.findViewById(R.id.tv_language_hint);
        swDarkMode = view.findViewById(R.id.sw_dark_mode);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.activityContext = (Activity) context;
        }
    }

    @Override
    public void onClick(View v) {
        int idView = v.getId();
        if (idView == R.id.cl_file_manage) {
            Utils.chooseFileManager(activityContext);
        } else if (idView == R.id.cl_dark_mode) {
            if (swDarkMode != null) {
                swDarkMode.setChecked(!swDarkMode.isChecked());
            }
        } else if (idView == R.id.cl_rate_app) {
            Utils.showRateDialog(activityContext);
        } else if (idView == R.id.cl_share_app) {
            Utils.shareApp(activityContext);
        } else if (idView == R.id.cl_language_options) {
            Intent intentLang = new Intent(activityContext, LanguageSelectionActivity.class);
            startActivity(intentLang);
        } else if (idView == R.id.cl_feedback) {
            Utils.feedbackApp(activityContext);
        } else if (idView == R.id.cl_privacy_policy) {
            Utils.openPrivacyPolicy(activityContext);
        } else if (idView == R.id.iv_back) {
            if (activityContext != null) {
                activityContext.onBackPressed();
            }
        }
    }
}
