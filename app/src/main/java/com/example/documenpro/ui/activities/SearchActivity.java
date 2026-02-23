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
import com.example.documenpro.ui.fragments.search.AllFileFragment;
import com.example.documenpro.ui.fragments.search.ExcelFragment;
import com.example.documenpro.ui.fragments.search.PdfFragment;
import com.example.documenpro.ui.fragments.search.PptFragment;
import com.example.documenpro.ui.fragments.search.TxtFragment;
import com.example.documenpro.ui.fragments.search.WordFragment;
import com.example.documenpro.viewmodel.SearchViewModel;

import java.util.Objects;

public class SearchActivity extends BaseActivity implements ViewPager.OnPageChangeListener {
    private TabLayout tabLayout;
    private AppCompatEditText edtSearch;
    private ConstraintLayout clToolbar;
    private ConstraintLayout mainCl;
    private AppCompatImageView btnClear;
    private SearchViewModel searchViewModel;
    private int fileType = GlobalConstant.ALL_FILE_TYPE;

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
            fileType = intent.getIntExtra(GlobalConstant.FILE_TYPE, GlobalConstant.ALL_FILE_TYPE);
        }
        initViews();
        AdMobNativeAdManager.showNativeBanner3_AdMob(this, null);
        setUpToolBarColor(fileType);
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
    }

    private void setUpToolBarColor(int fileType) {
        switch (fileType) {
            case GlobalConstant.ALL_FILE_TYPE:
                clToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.all_file_list_bg));
                mainCl.setBackgroundColor(ContextCompat.getColor(this, R.color.all_file_list_bg));

                tabLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.all_file_list_bg));
                getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.all_file_list_bg));
                break;
            case GlobalConstant.PDF_FILE_TYPE:
                clToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.pdf_file_list_bg));
                mainCl.setBackgroundColor(ContextCompat.getColor(this, R.color.pdf_file_list_bg));

                tabLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.pdf_file_list_bg));
                getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.pdf_file_list_bg));
                break;
            case GlobalConstant.WORD_FILE_TYPE:
                clToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.word_file_list_bg));
                mainCl.setBackgroundColor(ContextCompat.getColor(this, R.color.word_file_list_bg));

                tabLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.word_file_list_bg));
                getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.word_file_list_bg));

                break;
            case GlobalConstant.EXCEL_FILE_TYPE:
                clToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.excel_file_list_bg));
                mainCl.setBackgroundColor(ContextCompat.getColor(this, R.color.excel_file_list_bg));

                tabLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.excel_file_list_bg));
                getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.excel_file_list_bg));
                break;
            case GlobalConstant.PPT_FILE_TYPE:
                clToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.ppt_file_list_bg));
                mainCl.setBackgroundColor(ContextCompat.getColor(this, R.color.ppt_file_list_bg));

                tabLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.ppt_file_list_bg));
                getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.ppt_file_list_bg));
                break;
            case GlobalConstant.TXT_FILE_TYPE:
                clToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.txt_file_list_bg));
                mainCl.setBackgroundColor(ContextCompat.getColor(this, R.color.txt_file_list_bg));
                tabLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.txt_file_list_bg));
                getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.txt_file_list_bg));
                break;
        }
    }

    private void initViews() {
        btnClear = findViewById(R.id.iv_clear);
        mainCl = findViewById(R.id.main);
        clToolbar = findViewById(R.id.cl_toolbar);
        edtSearch = findViewById(R.id.et_search_text);
        tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.vp_content);
        findViewById(R.id.tv_back).setOnClickListener(view -> finish());
        PagerViewAdapter adapter = new PagerViewAdapter(getSupportFragmentManager());
        adapter.addFrag(new AllFileFragment(this), getResources().getString(R.string.document_all));
        adapter.addFrag(new ExcelFragment(this), getResources().getString(R.string.document_excel));
        adapter.addFrag(new PdfFragment(this), getResources().getString(R.string.document_pdf));
        adapter.addFrag(new WordFragment(this), getResources().getString(R.string.document_word));
        adapter.addFrag(new PptFragment(this), getResources().getString(R.string.document_ppt));
        adapter.addFrag(new TxtFragment(this), getResources().getString(R.string.document_txt));


        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(6);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(this);

        viewPager.setCurrentItem(fileType);

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    btnClear.setVisibility(View.VISIBLE);
                    searchViewModel.setQuery(charSequence.toString());
                } else if (charSequence.length() == 0) {
                    btnClear.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnClear.setOnClickListener(view -> Objects.requireNonNull(edtSearch.getText()).clear());
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                setUpToolBarColor(GlobalConstant.ALL_FILE_TYPE);
                break;
            case 1:
                setUpToolBarColor(GlobalConstant.EXCEL_FILE_TYPE);
                break;
            case 2:
                setUpToolBarColor(GlobalConstant.PDF_FILE_TYPE);
                break;
            case 3:
                setUpToolBarColor(GlobalConstant.WORD_FILE_TYPE);
                break;
            case 4:
                setUpToolBarColor(GlobalConstant.PPT_FILE_TYPE);
            case 5:
                setUpToolBarColor(GlobalConstant.TXT_FILE_TYPE);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}