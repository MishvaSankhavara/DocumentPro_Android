package com.arkay.gkinhindi.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.arkay.gkinhindi.ActivityBase;
import com.arkay.gkinhindi.BuildConfig;
import com.arkay.gkinhindi.Constants;
import com.arkay.gkinhindi.R;
import com.arkay.gkinhindi.adapter_reader.PagerViewAdapter;

import com.arkay.gkinhindi.ui.fragments.search.FragmentAllFile;
import com.arkay.gkinhindi.ui.fragments.search.FragmentExcel;
import com.arkay.gkinhindi.ui.fragments.search.FragmentPdf;
import com.arkay.gkinhindi.ui.fragments.search.FragmentPpt;
import com.arkay.gkinhindi.ui.fragments.search.FragmentTxt;
import com.arkay.gkinhindi.ui.fragments.search.FragmentWord;
import com.arkay.gkinhindi.utils.AdsUtils;
import com.arkay.gkinhindi.viewmodel.ViewModelSearch;

import java.util.Objects;

public class SearchDocumentActivity extends ActivityBase implements ViewPager.OnPageChangeListener {
    private ConstraintLayout mainContainer;
    private ConstraintLayout toolbarContainer;
    private TabLayout searchTabLayout;
    private AppCompatEditText searchEditText;
    private AppCompatImageView clearSearchButton;
    private ImageView filterIconButton;
    private TextView tvToolbarTitle;
    private ViewModelSearch searchViewModel;
    private int selectedFileType = Constants.FILE_TYPE_ALL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.act_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent = getIntent();
        if (intent != null) {
            selectedFileType = intent.getIntExtra(Constants.EXTRA_FILE_TYPE, Constants.FILE_TYPE_ALL);
        }
        initViews();

        applyToolbarTheme(selectedFileType);
        searchViewModel = new ViewModelProvider(this).get(ViewModelSearch.class);

        FrameLayout bannerAdContainer = findViewById(R.id.banner_ad_container);
        if (bannerAdContainer != null) {
            AdsUtils.showBannerAd(
                    this,
                    bannerAdContainer,
                    BuildConfig.banner_all,
                    BuildConfig.banner_all_2ID,
                    Constants.banner_all,
                    Constants.banner_all_2ID
            );
        }
    }

    private void applyToolbarTheme(int fileType) {
        int cardBgColor = ContextCompat.getColor(this, R.color.settings_card_bg);
        int listBgColor = ContextCompat.getColor(this, R.color.app_list_background);

        toolbarContainer.setBackgroundColor(cardBgColor);
        mainContainer.setBackgroundColor(listBgColor);
        searchTabLayout.setBackgroundColor(cardBgColor);
        getWindow().setStatusBarColor(cardBgColor);

        if (tvToolbarTitle != null) {
            tvToolbarTitle.setText(getFileTypeSearchTitle(fileType));
        }
    }

    private String getFileTypeSearchTitle(int fileType) {
        switch (fileType) {
            case Constants.FILE_TYPE_EXCEL:
                return "View Excel";
            case Constants.FILE_TYPE_PDF:
                return "View PDF";
            case Constants.FILE_TYPE_WORD:
                return "View Word";
            case Constants.FILE_TYPE_PPT:
                return "View PPT";
            case Constants.FILE_TYPE_TEXT:
                return "View TXT";
            case Constants.FILE_TYPE_ALL:
            default:
                return "Search Files";
        }
    }


    private View getTabView(int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_search_tab_item, null);
        TextView tvTitle = view.findViewById(R.id.tv_tab_title);
        ImageView ivIcon = view.findViewById(R.id.iv_tab_icon);

        /*
         * CUSTOMIZATION POINT (TO REPLACE TEXT INITIALS WITH YOUR CUSTOM TAB ICONS):
         *
         * If you want to use custom image icons instead of bold text initials (X, PDF, W, P):
         * 1. Place your icon drawable assets in the res/drawable folder (e.g. ic_excel_tab, ic_pdf_tab, etc.).
         * 2. For each case below, set the image resource on ivIcon:
         *    ivIcon.setImageResource(R.drawable.your_icon_name);
         * 3. Set ivIcon visibility to View.VISIBLE:
         *    ivIcon.setVisibility(View.VISIBLE);
         * 4. Set tvIconText visibility to View.GONE:
         *    tvIconText.setVisibility(View.GONE);
         */
        String title = "";
        switch (position) {
            case 0:
                title = getString(R.string.document_all);
                ivIcon.setImageResource(R.drawable.ic_document_file);
                ivIcon.setVisibility(View.VISIBLE);
                break;
            case 1:
                title = getString(R.string.document_excel);
                ivIcon.setImageResource(R.drawable.ic_document_excel);
                ivIcon.setVisibility(View.VISIBLE);
                break;
            case 2:
                title = getString(R.string.document_pdf);
                ivIcon.setImageResource(R.drawable.ic_document_pdf);
                ivIcon.setVisibility(View.VISIBLE);
                break;
            case 3:
                title = getString(R.string.document_word);
                ivIcon.setImageResource(R.drawable.ic_document_word);
                ivIcon.setVisibility(View.VISIBLE);
                break;
            case 4:
                title = getString(R.string.document_ppt);
                ivIcon.setImageResource(R.drawable.ic_document_ppt);
                ivIcon.setVisibility(View.VISIBLE);
                break;
            case 5:
                title = getString(R.string.document_txt);
                ivIcon.setImageResource(R.drawable.ic_document_txt);
                ivIcon.setVisibility(View.VISIBLE);
                break;
        }
        tvTitle.setText(title);
        return view;
    }

    private void updateTabSelection(TabLayout.Tab tab, boolean isSelected) {
        View customView = tab.getCustomView();
        if (customView == null) return;

        View outerContainer = customView.findViewById(R.id.fl_outer_container);
        View container = customView.findViewById(R.id.fl_icon_container);
        ImageView ivIcon = customView.findViewById(R.id.iv_tab_icon);
        TextView tvTitle = customView.findViewById(R.id.tv_tab_title);
        View indicator = customView.findViewById(R.id.v_indicator);

        int pos = tab.getPosition();


        if (isSelected) {
            // Selected: show outer white circle and colored indicator line
            outerContainer.setBackgroundResource(R.drawable.bg_outer_tab_circle);
        } else {
            // Unselected: transparent outer ring, grey title, hide indicator line
            outerContainer.setBackground(null);
            tvTitle.setTextColor(Color.parseColor("#64748B"));
            indicator.setVisibility(View.INVISIBLE);
        }
    }

    private void initViews() {
        clearSearchButton = findViewById(R.id.iv_clear);
        filterIconButton = findViewById(R.id.iv_filter_icon);
        mainContainer = findViewById(R.id.main);
        toolbarContainer = findViewById(R.id.cl_toolbar);
        searchEditText = findViewById(R.id.et_search_text);
        searchTabLayout = findViewById(R.id.tab_layout);
        tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
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

        // Bind custom layout tab views
        for (int i = 0; i < searchTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = searchTabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(getTabView(i));
                updateTabSelection(tab, tab.isSelected());
            }
        }

        searchTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateTabSelection(tab, true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                updateTabSelection(tab, false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

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
                    if (filterIconButton != null) {
                        filterIconButton.setVisibility(View.GONE);
                    }
                } else {
                    clearSearchButton.setVisibility(View.GONE);
                    if (filterIconButton != null) {
                        filterIconButton.setVisibility(View.VISIBLE);
                    }
                }
                searchViewModel.setSearchQueryLiveData(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        clearSearchButton.setOnClickListener(view -> Objects.requireNonNull(searchEditText.getText()).clear());
    }

    @Override
    public void onPageSelected(int position) {
        applyToolbarTheme(position);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}