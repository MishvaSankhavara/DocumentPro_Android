package com.example.documenpro.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.example.documenpro.BaseActivity;
import com.example.documenpro.GlobalConstant;
import com.example.documenpro.R;
import com.example.documenpro.adapter_reader.PagerViewAdapter;
import com.example.documenpro.advertisement.AdMobNativeAdManager;
import com.example.documenpro.ui.fragments.search.FragmentAllFile;
import com.example.documenpro.ui.fragments.search.FragmentExcel;
import com.example.documenpro.ui.fragments.search.FragmentPdf;
import com.example.documenpro.ui.fragments.search.FragmentPpt;
import com.example.documenpro.ui.fragments.search.FragmentTxt;
import com.example.documenpro.ui.fragments.search.FragmentWord;
import com.example.documenpro.viewmodel.ViewModelSearch;

import java.util.Objects;

public class SearchDocumentActivity extends BaseActivity implements ViewPager.OnPageChangeListener {
    private ConstraintLayout mainContainer;
    private ConstraintLayout toolbarContainer;
    private TabLayout searchTabLayout;
    private AppCompatEditText searchEditText;
    private AppCompatImageView clearSearchButton;
    private ViewModelSearch searchViewModel;
    private int selectedFileType = GlobalConstant.ALL_FILE_TYPE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent = getIntent();
        if (intent != null) {
            selectedFileType = intent.getIntExtra(GlobalConstant.FILE_TYPE, GlobalConstant.ALL_FILE_TYPE);
        }
        initViews();
        AdMobNativeAdManager.showNativeBanner3_AdMob(this, null);
        applyToolbarTheme(selectedFileType);
        searchViewModel = new ViewModelProvider(this).get(ViewModelSearch.class);
    }

    private void applyToolbarTheme(int fileType) {
        switch (fileType) {
            case GlobalConstant.ALL_FILE_TYPE:
                toolbarContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.all_file_list_bg));
                mainContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.all_file_list_bg));

                searchTabLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.all_file_list_bg));
                getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.all_file_list_bg));
                break;
            case GlobalConstant.PDF_FILE_TYPE:
                toolbarContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.pdf_file_list_bg));
                mainContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.pdf_file_list_bg));

                searchTabLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.pdf_file_list_bg));
                getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.pdf_file_list_bg));
                break;
            case GlobalConstant.WORD_FILE_TYPE:
                toolbarContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.word_file_list_bg));
                mainContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.word_file_list_bg));

                searchTabLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.word_file_list_bg));
                getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.word_file_list_bg));

                break;
            case GlobalConstant.EXCEL_FILE_TYPE:
                toolbarContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.excel_file_list_bg));
                mainContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.excel_file_list_bg));

                searchTabLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.excel_file_list_bg));
                getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.excel_file_list_bg));
                break;
            case GlobalConstant.PPT_FILE_TYPE:
                toolbarContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.ppt_file_list_bg));
                mainContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.ppt_file_list_bg));

                searchTabLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.ppt_file_list_bg));
                getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.ppt_file_list_bg));
                break;
            case GlobalConstant.TXT_FILE_TYPE:
                toolbarContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.txt_file_list_bg));
                mainContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.txt_file_list_bg));
                searchTabLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.txt_file_list_bg));
                getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.txt_file_list_bg));
                break;
        }
    }

    private void initViews() {
        clearSearchButton = findViewById(R.id.iv_clear);
        mainContainer = findViewById(R.id.main);
        toolbarContainer = findViewById(R.id.cl_toolbar);
        searchEditText = findViewById(R.id.et_search_text);
        searchTabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.vp_content);
        findViewById(R.id.tv_back).setOnClickListener(view -> finish());
        PagerViewAdapter adapter = new PagerViewAdapter(getSupportFragmentManager());
        adapter.addFrag(new FragmentAllFile(this), getResources().getString(R.string.document_all));
        adapter.addFrag(new FragmentExcel(this), getResources().getString(R.string.document_excel));
        adapter.addFrag(new FragmentPdf(this), getResources().getString(R.string.document_pdf));
        adapter.addFrag(new FragmentWord(this), getResources().getString(R.string.document_word));
        adapter.addFrag(new FragmentPpt(this), getResources().getString(R.string.document_ppt));
        adapter.addFrag(new FragmentTxt(this), getResources().getString(R.string.document_txt));


        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(6);
        searchTabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(this);

        viewPager.setCurrentItem(selectedFileType);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    clearSearchButton.setVisibility(View.VISIBLE);
                    searchViewModel.setSearchQueryLiveData(charSequence.toString());
                } else if (charSequence.length() == 0) {
                    clearSearchButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        clearSearchButton.setOnClickListener(view -> Objects.requireNonNull(searchEditText.getText()).clear());
    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                applyToolbarTheme(GlobalConstant.ALL_FILE_TYPE);
                break;
            case 1:
                applyToolbarTheme(GlobalConstant.EXCEL_FILE_TYPE);
                break;
            case 2:
                applyToolbarTheme(GlobalConstant.PDF_FILE_TYPE);
                break;
            case 3:
                applyToolbarTheme(GlobalConstant.WORD_FILE_TYPE);
                break;
            case 4:
                applyToolbarTheme(GlobalConstant.PPT_FILE_TYPE);
            case 5:
                applyToolbarTheme(GlobalConstant.TXT_FILE_TYPE);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}