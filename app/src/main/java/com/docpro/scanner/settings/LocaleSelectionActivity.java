package com.docpro.scanner.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hjq.language.MultiLanguages;
import docreader.aidoc.pdfreader.ActivityBase;
import docreader.aidoc.pdfreader.AppGlobalConstants;
import docreader.aidoc.pdfreader.R;
import docreader.aidoc.pdfreader.PreferenceUtils;
import docreader.aidoc.pdfreader.adapter_reader.LanguageListAdapter;

import docreader.aidoc.pdfreader.clickListener.LanguageClickListener;
import docreader.aidoc.pdfreader.ui.activities.MainActivity;
import docreader.aidoc.pdfreader.ui.activities.OnBoardActivity;

import java.util.Locale;

public class LocaleSelectionActivity extends ActivityBase {

    private RecyclerView rcvLocales;
    private LanguageListAdapter localeAdapter;
    private int selectedLocaleIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.layout_lang_picker);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.lay_locale_picker_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));

        rcvLocales = findViewById(R.id.rv_locale_list);
        localeAdapter = new LanguageListAdapter(this, new LanguageClickListener() {
            @Override
            public void onLangClickListener(int index) {
                selectedLocaleIndex = index;
            }
        });

        rcvLocales.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rcvLocales.setAdapter(localeAdapter);

        findViewById(R.id.btn_apply_locale).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceUtils utils = PreferenceUtils.getInstance(LocaleSelectionActivity.this);
                utils.setBoolean(AppGlobalConstants.PREF_LANGUAGE_SET, true);
                utils.setString(AppGlobalConstants.PREF_LANGUAGE_NAME,
                        AppGlobalConstants.createArrayLanguage().get(selectedLocaleIndex).getNameLanguage_LanModel());
                utils.setString(AppGlobalConstants.PREF_LANGUAGE_KEY,
                        AppGlobalConstants.createArrayLanguage().get(selectedLocaleIndex).getKeyLanguage_LanModel());
                utils.setInt(AppGlobalConstants.PREF_LANGUAGE_NUMBER, selectedLocaleIndex);

                Class<?> nextActivity;
                if (utils.getBoolean(AppGlobalConstants.PREF_GUIDE_COMPLETED, false)) {
                    nextActivity = MainActivity.class;
                } else {
                    nextActivity = OnBoardActivity.class;
                }

                Intent restartIntent = new Intent(LocaleSelectionActivity.this, nextActivity);
                restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                String key = AppGlobalConstants.createArrayLanguage().get(selectedLocaleIndex)
                        .getKeyLanguage_LanModel();
                MultiLanguages.setAppLanguage(context, new Locale(key));

                startActivity(restartIntent);
                finish();
            }
        });
    }
}
