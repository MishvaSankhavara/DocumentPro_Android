package com.example.documenpro.ui.fragments;

import com.docpro.scanner.settings.LocaleSelectionActivity;

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
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import com.example.documenpro.BuildConfig;
import com.example.documenpro.AppGlobalConstants;
import com.example.documenpro.R;
import com.example.documenpro.PreferenceUtils;
import com.example.documenpro.utils.Utils;

public class FragmentSetting extends Fragment implements View.OnClickListener {

    private Activity activityContext;

    private AppCompatTextView languageTextView;

    private AppCompatTextView versionTextView;

    public FragmentSetting() {
    }

    public FragmentSetting(Activity mActivity) {
        this.activityContext = mActivity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        initViews(view);
        initListener(view);
        initData();
        return view;
    }

    private void initData() {
        versionTextView.setText(BuildConfig.VERSION_NAME);

        languageTextView.setText(
                PreferenceUtils.getInstance(activityContext).getString(AppGlobalConstants.PREF_LANGUAGE_NAME, "English"));

    }

    private void initListener(View view) {
        view.findViewById(R.id.cl_file_manage).setOnClickListener(this);
        view.findViewById(R.id.cl_rate_app).setOnClickListener(this);
        view.findViewById(R.id.cl_share_app).setOnClickListener(this);
        view.findViewById(R.id.cl_language_options).setOnClickListener(this);
        view.findViewById(R.id.cl_faq).setOnClickListener(this);
        view.findViewById(R.id.cl_request).setOnClickListener(this);
        view.findViewById(R.id.cl_feedback).setOnClickListener(this);
        view.findViewById(R.id.cl_privacy_policy).setOnClickListener(this);

    }

    private void initViews(View view) {
        languageTextView = view.findViewById(R.id.tv_language_hint);
        versionTextView = view.findViewById(R.id.tv_version);
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

        } else if (idView == R.id.cl_rate_app) {
            Utils.showRateDialog(activityContext);
        } else if (idView == R.id.cl_share_app) {
            Utils.shareApp(activityContext);
        } else if (idView == R.id.cl_language_options) {
            Intent intentLang = new Intent(activityContext, LocaleSelectionActivity.class);
            startActivity(intentLang);
        } else if (idView == R.id.cl_faq) {

        } else if (idView == R.id.cl_request) {

        } else if (idView == R.id.cl_feedback) {
            Utils.feedbackApp(activityContext);
        } else if (idView == R.id.cl_privacy_policy) {
            try {
                String url = AppGlobalConstants.PRIVACY_POLICY_URL;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
