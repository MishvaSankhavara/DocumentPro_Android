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
import com.example.documenpro.BaseActivity;
import com.example.documenpro.GlobalConstant;
import com.example.documenpro.R;
import com.example.documenpro.SharedPreferenceUtils;
import com.example.documenpro.adapter_reader.LanguageListAdapter;
import com.example.documenpro.ads.AdClosedListener;
import com.example.documenpro.ads.FullAds;
import com.example.documenpro.ads.NativeAdAdmob;
import com.example.documenpro.listener.LanguageListener;
import com.example.documenpro.ui.activities.OnBoardActivity;

import java.util.Locale;

public class LocaleSelectionActivity extends BaseActivity {

    private RecyclerView rcvLocales;
    private LanguageListAdapter localeAdapter;
    private int selectedLocaleIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.lyt_lang_picker);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.lay_locale_picker_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
        NativeAdAdmob.showNativeBanner1(this, null);

        rcvLocales = findViewById(R.id.rv_locale_list);
        localeAdapter = new LanguageListAdapter(this, new LanguageListener() {
            @Override
            public void onLangChoice(int index) {
                selectedLocaleIndex = index;
            }
        });

        rcvLocales.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rcvLocales.setAdapter(localeAdapter);

        findViewById(R.id.btn_apply_locale).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FullAds.showAds(LocaleSelectionActivity.this, new AdClosedListener() {
                    @Override
                    public void AdClosed() {
                        SharedPreferenceUtils utils = SharedPreferenceUtils.getInstance(LocaleSelectionActivity.this);
                        utils.setBoolean(GlobalConstant.LANGUAGE_SET, true);
                        utils.setString(GlobalConstant.LANGUAGE_NAME,
                                GlobalConstant.createArrayLanguage().get(selectedLocaleIndex).getNameLanguage());
                        utils.setString(GlobalConstant.LANGUAGE_KEY,
                                GlobalConstant.createArrayLanguage().get(selectedLocaleIndex).getKeyLanguage());
                        utils.setInt(GlobalConstant.LANGUAGE_KEY_NUMBER, selectedLocaleIndex);

                        Intent restartIntent = new Intent(LocaleSelectionActivity.this, OnBoardActivity.class);
                        restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                        String key = GlobalConstant.createArrayLanguage().get(selectedLocaleIndex).getKeyLanguage();
                        MultiLanguages.setAppLanguage(context, new Locale(key));

                        startActivity(restartIntent);
                        finish();
                    }
                });
            }
        });
    }
}
