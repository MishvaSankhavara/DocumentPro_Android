package com.arkay.gkinhindi.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hjq.language.MultiLanguages;
import com.arkay.gkinhindi.Constants;
import com.arkay.gkinhindi.R;
import com.arkay.gkinhindi.PreferenceUtils;
import com.arkay.gkinhindi.adapter_reader.LanguagePickerDialogAdapter;
import com.arkay.gkinhindi.model_reader.LanguageModel;
import com.arkay.gkinhindi.ui.activities.SplashScreenActivity;

import java.util.ArrayList;
import java.util.Locale;

public class LanguageSelectionDialog extends Dialog {
    RecyclerView languageRecyclerView;
    LanguagePickerDialogAdapter adalanguageAdapterter;
    Context context;
    int selectedLanguageIndex;

    public LanguageSelectionDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_select_language);
        this.context = context;
        selectedLanguageIndex = PreferenceUtils.getInstance(this.context).getInt(Constants.PREF_LANGUAGE_NUMBER, 0);
        languageRecyclerView = findViewById(R.id.rcv_list);
        final ArrayList<LanguageModel> arrayList = Constants.createArrayLanguage();
        adalanguageAdapterter = new LanguagePickerDialogAdapter(getContext(), lang -> selectedLanguageIndex = lang);
        languageRecyclerView.setLayoutManager(new LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false));
        languageRecyclerView.setAdapter(adalanguageAdapterter);
        findViewById(R.id.tv_ok).setOnClickListener(view1 -> {
            PreferenceUtils.getInstance(this.context).setBoolean(Constants.PREF_LANGUAGE_SET, true);
            PreferenceUtils.getInstance(this.context).setString(Constants.PREF_LANGUAGE_NAME, Constants.createArrayLanguage().get(selectedLanguageIndex).getNameLanguage_LanModel());
            PreferenceUtils.getInstance(this.context).setString(Constants.PREF_LANGUAGE_KEY, Constants.createArrayLanguage().get(selectedLanguageIndex).getKeyLanguage_LanModel());
            PreferenceUtils.getInstance(this.context).setInt(Constants.PREF_LANGUAGE_NUMBER, selectedLanguageIndex);
            Intent refresh = new Intent(this.context, SplashScreenActivity.class);
            refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            MultiLanguages.setAppLanguage(context, new Locale(Constants.createArrayLanguage().get(selectedLanguageIndex).getKeyLanguage_LanModel()));

            this.context.startActivity(refresh);
            dismiss();
        });
    }
}
