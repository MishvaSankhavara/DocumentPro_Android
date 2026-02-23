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
import com.example.documenpro.adapter.ViewPagerAdapter;
import com.example.documenpro.ads.AdClosedListener;
import com.example.documenpro.ads.FullAds;
import com.example.documenpro.ui.customviews.switchdaynight.DayNightSwitch;
import com.example.documenpro.ui.fragments.FilesFragment;
import com.example.documenpro.ui.fragments.SettingFragment;
import com.example.documenpro.ui.fragments.ToolsFragment;
import com.example.documenpro.utils.AdsUtils;
import com.example.documenpro.utils.DialogUtils;
import com.example.documenpro.utils.Utils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener, NavigationView.OnNavigationItemSelectedListener {
    AppCompatImageView ivTools;
    AppCompatImageView ivFiles;
    TextView tvTools;
    TextView tvFile;
    private ConstraintLayout btnTools;
    private ConstraintLayout btnFiles;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private TextView tvToolbar;
    public BottomNavigationView mBottomNavigationView;
    DrawerLayout drawerLayout;
    TextView tvLang;
    Menu menuMain;
    private DayNightSwitch switchDayNight;
    private AdView adView;
    private FrameLayout adContainer;
    private TextView tvVersion;
    private int navigationClickCount = 0;

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
        navigationClickCount = SharedPreferenceUtils.getInstance(this).getInt(GlobalConstant.NAVIGATION_CLICK_COUNT, 0);
        initToolBar();
        initViews();
        initData();
        initViewPager();
        loadAdBanner();
        initListener();
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
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
                navigationClickCount++;
                if (navigationClickCount % 3 == 0) {
                    FullAds.showAds(MainActivity.this, new AdClosedListener() {
                        @Override
                        public void AdClosed() {

                        }
                    });
                }
                SharedPreferenceUtils.getInstance(MainActivity.this).setInt(GlobalConstant.NAVIGATION_CLICK_COUNT, navigationClickCount);

                return true;
            }
        });
    }

    private void loadAdBanner() {
        adView = new AdView(this);
        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");

        AdSize adSize = AdsUtils.getAdSize(MainActivity.this, adContainer);
        adView.setAdSize(adSize);

        Bundle extras = new Bundle();
        extras.putString("collapsible", "bottom");
        AdRequest adRequest = new AdRequest.Builder()

                .build();
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adContainer.removeAllViews();
                adContainer.addView(adView);
            }
        });

        adView.loadAd(adRequest);

    }

    private void initData() {
        switchDayNight.setIsNight(SharedPreferenceUtils.getInstance(this).getBoolean(GlobalConstant.NIGHT_MODE_KEY, false));
        switchDayNight.setListener(is_night -> {
            startActivity(new Intent(MainActivity.this, SplashActivity.class));
            finish();
            Utils.setTheme(getApplication(), is_night);
        });
    }

    private void initListener() {
//        btnFiles.setOnClickListener(this);
//        btnTools.setOnClickListener(this);
//        findViewById(R.id.pdf_act_main_select_image).setOnClickListener(this);
    }

    private void initViewPager() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFrag(new FilesFragment(this), "Home");
        viewPagerAdapter.addFrag(new ToolsFragment(this), "Files");
        viewPagerAdapter.addFrag(new SettingFragment(this), "Settings");

        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(this);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setCurrentItem(0);
    }

    private void initToolBar() {
        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        }

    }

    private void initViews() {
        tvVersion = findViewById(R.id.tv_app_version);
        mBottomNavigationView = findViewById(R.id.bottom_nav);
        adContainer = findViewById(R.id.my_template_banner);
//        btnTools = findViewById(R.id.cly_tab_tools);
//        btnFiles = findViewById(R.id.cly_tab_files);
//        tvTools = findViewById(R.id.tv_tab_tools);
//        tvFile = findViewById(R.id.tv_tab_files);
//        ivTools = findViewById(R.id.iv_tab_tools);
//        ivFiles = findViewById(R.id.iv_tab_files);
        viewPager = findViewById(R.id.viewpager_main);
        tvToolbar = findViewById(R.id.tv_name);
        drawerLayout = findViewById(R.id.activity_main_drawer);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.findViewById(R.id.cl_share_app).setOnClickListener(this);
        navigationView.findViewById(R.id.cl_rate_us).setOnClickListener(this);
        navigationView.findViewById(R.id.cl_file_manage).setOnClickListener(this);
        navigationView.findViewById(R.id.cl_language_options).setOnClickListener(this);
        navigationView.findViewById(R.id.cl_feedback).setOnClickListener(this);
        navigationView.findViewById(R.id.cl_privacy_policy).setOnClickListener(this);
        navigationView.findViewById(R.id.nav_dark_mode).setOnClickListener(this);

        switchDayNight = navigationView.findViewById(R.id.nav_dark_mode_switch);

        tvLang = navigationView.findViewById(R.id.tv_language_hint);

        toolbar.setNavigationOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));


        tvLang.setText(SharedPreferenceUtils.getInstance(this).getString(GlobalConstant.LANGUAGE_NAME, "English"));
        tvVersion.setText("Version: " + BuildConfig.VERSION_NAME);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_main, menu);
        menuMain = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_search) {
            if (Utils.checkPermission(this)) {
                Intent intentSearch = new Intent(this, SearchActivity.class);
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
            drawerLayout.closeDrawers();
        } else if (idView == R.id.cl_rate_us) {
            Utils.showRateDialog(MainActivity.this);
            drawerLayout.closeDrawers();
        } else if (idView == R.id.cl_file_manage) {
            Utils.chooseFileManager(MainActivity.this);
            drawerLayout.closeDrawers();
        } else if (idView == R.id.cl_language_options) {
            DialogUtils.showLanguageDialog(this);
        } else if (idView == R.id.cl_feedback) {
            Utils.feedbackApp(MainActivity.this);
            drawerLayout.closeDrawers();
        } else if (idView == R.id.cl_privacy_policy) {

        } else if (idView == R.id.nav_dark_mode) {
            switchDayNight.setIsNight(!switchDayNight.isNight());

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
                mBottomNavigationView.getMenu().findItem(R.id.navigation_files).setChecked(true);

                tvToolbar.setText(R.string.app_name);
                if (getSupportActionBar() != null) {
                    Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

                }
                break;
            case 1:
                mBottomNavigationView.getMenu().findItem(R.id.navigation_tools).setChecked(true);

                if (getSupportActionBar() != null) {
                    Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);

                }
                break;
            case 2:
                mBottomNavigationView.getMenu().findItem(R.id.navigation_settings).setChecked(true);
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
                new SendData(this, selectedUri).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }
        }
    }

    public static class SendData extends AsyncTask<Void, Void, Void> {
        WeakReference<MainActivity> weakReference;
        Intent intent;
        Uri intentData;


        public SendData(MainActivity activity, Uri uri) {
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