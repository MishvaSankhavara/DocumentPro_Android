package com.example.documenpro.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hjq.language.MultiLanguages;
import com.example.documenpro.GlobalConstant;
import com.example.documenpro.R;
import com.example.documenpro.SharedPreferenceUtils;
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
        selectedLanguageIndex = SharedPreferenceUtils.getInstance(this.context).getInt(GlobalConstant.LANGUAGE_KEY_NUMBER, 0);
        languageRecyclerView = findViewById(R.id.rcv_list);
        final ArrayList<LanguageModel> arrayList = GlobalConstant.createArrayLanguage();
        adalanguageAdapterter = new LanguagePickerDialogAdapter(getContext(), lang -> selectedLanguageIndex = lang);
        languageRecyclerView.setLayoutManager(new LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false));
        languageRecyclerView.setAdapter(adalanguageAdapterter);
        findViewById(R.id.tv_ok).setOnClickListener(view1 -> {
            SharedPreferenceUtils.getInstance(this.context).setBoolean(GlobalConstant.LANGUAGE_SET, true);
            SharedPreferenceUtils.getInstance(this.context).setString(GlobalConstant.LANGUAGE_NAME, GlobalConstant.createArrayLanguage().get(selectedLanguageIndex).getNameLanguage_LanModel());
            SharedPreferenceUtils.getInstance(this.context).setString(GlobalConstant.LANGUAGE_KEY, GlobalConstant.createArrayLanguage().get(selectedLanguageIndex).getKeyLanguage_LanModel());
            SharedPreferenceUtils.getInstance(this.context).setInt(GlobalConstant.LANGUAGE_KEY_NUMBER, selectedLanguageIndex);
            Intent refresh = new Intent(this.context, SplashScreenActivity.class);
            refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            MultiLanguages.setAppLanguage(context, new Locale(GlobalConstant.createArrayLanguage().get(selectedLanguageIndex).getKeyLanguage_LanModel()));

            this.context.startActivity(refresh);
            dismiss();
        });
    }
}
