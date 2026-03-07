package com.example.documenpro.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hjq.language.MultiLanguages;
import com.example.documenpro.AppGlobalConstants;
import com.example.documenpro.R;
import com.example.documenpro.PreferenceUtils;
import com.example.documenpro.adapter_reader.LanguagePickerDialogAdapter;
import com.example.documenpro.model_reader.LanguageModel;
import com.example.documenpro.ui.activities.SplashScreenActivity;

import java.util.ArrayList;
import java.util.Locale;

public class LanguageSelectionDialog extends Dialog {
    RecyclerView languageRecyclerView;
    LanguagePickerDialogAdapter adalanguageAdapterter;
    Context context;
    int selectedLanguageIndex;

    public LanguageSelectionDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_language);
        this.context = context;
        selectedLanguageIndex = PreferenceUtils.getInstance(this.context).getInt(AppGlobalConstants.PREF_LANGUAGE_NUMBER, 0);
        languageRecyclerView = findViewById(R.id.rcv_list);
        final ArrayList<LanguageModel> arrayList = AppGlobalConstants.createArrayLanguage();
        adalanguageAdapterter = new LanguagePickerDialogAdapter(getContext(), lang -> selectedLanguageIndex = lang);
        languageRecyclerView.setLayoutManager(new LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false));
        languageRecyclerView.setAdapter(adalanguageAdapterter);
        findViewById(R.id.tv_ok).setOnClickListener(view1 -> {
            PreferenceUtils.getInstance(this.context).setBoolean(AppGlobalConstants.PREF_LANGUAGE_SET, true);
            PreferenceUtils.getInstance(this.context).setString(AppGlobalConstants.PREF_LANGUAGE_NAME, AppGlobalConstants.createArrayLanguage().get(selectedLanguageIndex).getNameLanguage_LanModel());
            PreferenceUtils.getInstance(this.context).setString(AppGlobalConstants.PREF_LANGUAGE_KEY, AppGlobalConstants.createArrayLanguage().get(selectedLanguageIndex).getKeyLanguage_LanModel());
            PreferenceUtils.getInstance(this.context).setInt(AppGlobalConstants.PREF_LANGUAGE_NUMBER, selectedLanguageIndex);
            Intent refresh = new Intent(this.context, SplashScreenActivity.class);
            refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            MultiLanguages.setAppLanguage(context, new Locale(AppGlobalConstants.createArrayLanguage().get(selectedLanguageIndex).getKeyLanguage_LanModel()));

            this.context.startActivity(refresh);
            dismiss();
        });
    }
}
