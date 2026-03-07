package com.example.documenpro.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.documenpro.BuildConfig;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.example.documenpro.GlobalConstant;
import com.example.documenpro.R;
import com.example.documenpro.SharedPreferenceUtils;
import com.example.documenpro.adapter_reader.PagerViewAdapter;
import com.example.documenpro.advertisement.OnAdDismissedListener;
import com.example.documenpro.advertisement.AdManager;
import com.example.documenpro.ui.customviews.switchdaynight.ThemeToggleSwitch;
import com.example.documenpro.ui.fragments.FragmentFiles;
import com.example.documenpro.ui.fragments.FragmentSetting;
import com.example.documenpro.ui.fragments.FragmentTools;
import com.example.documenpro.utils.AdsUtils;
import com.example.documenpro.utils.DialogUtils;
import com.example.documenpro.utils.Utils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener, NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mainToolbar;
    private TextView toolbarTitleTextView;
    public BottomNavigationView bottomNavigationView;
    DrawerLayout mainDrawerLayout;
    private ViewPager viewPager;
    AppCompatImageView ivToolsImage;
    AppCompatImageView filesIconImageView;
    TextView toolsTextView;
    TextView filesTextView;
    private ConstraintLayout toolsButtonLayout;
    private ConstraintLayout filesButtonLayout;
    Menu mainMenu;
    TextView languageTextView;
    private ThemeToggleSwitch dayNightSwitch;
    private AdView bannerAdView;
    private FrameLayout adContainer;
    private TextView appVersionTextView;
    private int navigationAdClickCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bottom_nav), (v, insets) -> {
            // Không áp dụng padding nào cả

            return insets;
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.my_template_banner), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, 0, 0, systemBars.bottom); // chỉ padding bottom
            return insets;
        });
        navigationAdClickCount = SharedPreferenceUtils.getInstance(this).getInt(GlobalConstant.NAVIGATION_CLICK_COUNT, 0);
        setupToolbar();
        initializeViews();
        initializeData();
        setupViewPager();
        loadBannerAd();
        initializeListeners();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int idMenu = menuItem.getItemId();
                if (idMenu == R.id.navigation_files) {
                    viewPager.setCurrentItem(0);
                } else if (idMenu == R.id.navigation_tools) {
                    viewPager.setCurrentItem(1);
                } else if (idMenu == R.id.navigation_settings) {
                    viewPager.setCurrentItem(2);
                }
                // Tăng số lần click
                navigationAdClickCount++;
                if (navigationAdClickCount % 3 == 0) {
                    AdManager.showAds_AdManager(MainActivity.this, new OnAdDismissedListener() {
                        @Override
                        public void OnAdDismissedListener() {

                        }
                    });
                }
                SharedPreferenceUtils.getInstance(MainActivity.this).setInt(GlobalConstant.NAVIGATION_CLICK_COUNT, navigationAdClickCount);

                return true;
            }
        });
    }

    private void initializeData() {
        dayNightSwitch.setNightMode(SharedPreferenceUtils.getInstance(this).getBoolean(GlobalConstant.NIGHT_MODE_KEY, false));
        dayNightSwitch.setSwitchListener(is_night -> {
            startActivity(new Intent(MainActivity.this, SplashScreenActivity.class));
            finish();
            Utils.setTheme(getApplication(), is_night);
        });
    }

    private void initializeListeners() {
//        btnFiles.setOnClickListener(this);
//        btnTools.setOnClickListener(this);
//        findViewById(R.id.pdf_act_main_select_image).setOnClickListener(this);
    }

    private void setupViewPager() {
        PagerViewAdapter viewPagerAdapter = new PagerViewAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFrag(new FragmentFiles(this), "Home");
        viewPagerAdapter.addFrag(new FragmentTools(this), "Files");
        viewPagerAdapter.addFrag(new FragmentSetting(this), "Settings");

        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(this);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setCurrentItem(0);
    }

    private void setupToolbar() {
        mainToolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(mainToolbar);
        if (getSupportActionBar() != null) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void initializeViews() {
        appVersionTextView = findViewById(R.id.tv_app_version);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        adContainer = findViewById(R.id.my_template_banner);
//        btnTools = findViewById(R.id.cly_tab_tools);
//        btnFiles = findViewById(R.id.cly_tab_files);
//        tvTools = findViewById(R.id.tv_tab_tools);
//        tvFile = findViewById(R.id.tv_tab_files);
//        ivTools = findViewById(R.id.iv_tab_tools);
//        ivFiles = findViewById(R.id.iv_tab_files);
        viewPager = findViewById(R.id.viewpager_main);
        toolbarTitleTextView = findViewById(R.id.tv_name);
        mainDrawerLayout = findViewById(R.id.activity_main_drawer);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.findViewById(R.id.cl_share_app).setOnClickListener(this);
        navigationView.findViewById(R.id.cl_rate_us).setOnClickListener(this);
        navigationView.findViewById(R.id.cl_file_manage).setOnClickListener(this);
        navigationView.findViewById(R.id.cl_language_options).setOnClickListener(this);
        navigationView.findViewById(R.id.cl_feedback).setOnClickListener(this);
        navigationView.findViewById(R.id.cl_privacy_policy).setOnClickListener(this);
        navigationView.findViewById(R.id.nav_dark_mode).setOnClickListener(this);

        dayNightSwitch = navigationView.findViewById(R.id.nav_dark_mode_switch);

        languageTextView = navigationView.findViewById(R.id.tv_language_hint);

        mainToolbar.setNavigationOnClickListener(view -> mainDrawerLayout.openDrawer(GravityCompat.START));

        languageTextView.setText(SharedPreferenceUtils.getInstance(this).getString(GlobalConstant.LANGUAGE_NAME, "English"));
        appVersionTextView.setText("Version: " + BuildConfig.VERSION_NAME);
    }

    private void loadBannerAd() {
        bannerAdView = new AdView(this);
        bannerAdView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");

        AdSize adSize = AdsUtils.getAdSize(MainActivity.this, adContainer);
        bannerAdView.setAdSize(adSize);

        Bundle extras = new Bundle();
        extras.putString("collapsible", "bottom");
        AdRequest adRequest = new AdRequest.Builder()

                .build();
        bannerAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adContainer.removeAllViews();
                adContainer.addView(bannerAdView);
            }
        });
        bannerAdView.loadAd(adRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_main, menu);
        mainMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_search) {
            if (Utils.checkPermission(this)) {
                Intent intentSearch = new Intent(this, SearchDocumentActivity.class);
                startActivity(intentSearch);
            } else {
                Toast.makeText(this, "Need Permission!", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int idView = v.getId();
//        if (idView == R.id.cly_tab_files) {
//            viewPager.setCurrentItem(0);
//        } else if (idView == R.id.cly_tab_tools) {
//            viewPager.setCurrentItem(1);
//        }else if (idView==R.id.pdf_act_main_select_image){
//            Toast.makeText(this, "Feature coming soon!", Toast.LENGTH_SHORT).show();
//        }
        if (idView == R.id.cl_share_app) {
            Utils.shareApp(MainActivity.this);
            mainDrawerLayout.closeDrawers();
        } else if (idView == R.id.cl_rate_us) {
            Utils.showRateDialog(MainActivity.this);
            mainDrawerLayout.closeDrawers();
        } else if (idView == R.id.cl_file_manage) {
            Utils.chooseFileManager(MainActivity.this);
            mainDrawerLayout.closeDrawers();
        } else if (idView == R.id.cl_language_options) {
            DialogUtils.showLanguageDialog(this);
        } else if (idView == R.id.cl_feedback) {
            Utils.feedbackApp(MainActivity.this);
            mainDrawerLayout.closeDrawers();
        } else if (idView == R.id.cl_privacy_policy) {

        } else if (idView == R.id.nav_dark_mode) {
            dayNightSwitch.setNightMode(!dayNightSwitch.isNightMode());

        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        int currentItem = viewPager.getCurrentItem();
        switch (currentItem) {
            case 0:
                bottomNavigationView.getMenu().findItem(R.id.navigation_files).setChecked(true);

                toolbarTitleTextView.setText(R.string.app_name);
                if (getSupportActionBar() != null) {
                    Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

                }
                break;
            case 1:
                bottomNavigationView.getMenu().findItem(R.id.navigation_tools).setChecked(true);

                if (getSupportActionBar() != null) {
                    Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
                }
                break;
            case 2:
                bottomNavigationView.getMenu().findItem(R.id.navigation_settings).setChecked(true);
                if (getSupportActionBar() != null) {
                    Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
                }

//                tvToolbar.setText(R.string.pdf_tools);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GlobalConstant.REQUEST_CODE_PICK_FILE) {
            if (resultCode == RESULT_OK && data != null) {
                Uri selectedUri = data.getData();
                new ImportFileTask(this, selectedUri).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }

    public static class ImportFileTask extends AsyncTask<Void, Void, Void> {
        WeakReference<MainActivity> weakReference;
        Intent intent;
        Uri intentData;

        public ImportFileTask(MainActivity activity, Uri uri) {
            this.weakReference = new WeakReference<>(activity);
            this.intentData = uri;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String filename = Utils.getFileNameFromUri(intentData, weakReference.get().getContentResolver());
            File pathFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/DocumentReader/");
            if (!pathFolder.exists()) {
                pathFolder.mkdirs();
            }
            String pathCopy = pathFolder + "/" + filename;
            Utils.copy(weakReference.get(), intentData, pathCopy);
            File file = new File(pathCopy);

            Utils.openFile(weakReference.get(), file);

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
        }
    }
}