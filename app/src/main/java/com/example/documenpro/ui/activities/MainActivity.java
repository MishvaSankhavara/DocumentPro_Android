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
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.documenpro.AppGlobalConstants;
import com.example.documenpro.BuildConfig;
import com.example.documenpro.R;
import com.example.documenpro.PreferenceUtils;
import com.example.documenpro.adapter_reader.PagerViewAdapter;
import com.example.documenpro.ui.customviews.switchdaynight.ThemeToggleSwitch;
import com.example.documenpro.ui.fragments.FragmentFiles;
import com.example.documenpro.ui.fragments.FragmentSetting;
import com.example.documenpro.ui.fragments.FragmentTools;
import com.example.documenpro.utils.DialogManagerUtils;
import com.example.documenpro.utils.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private Toolbar mainToolbar;
    private TextView toolbarTitleTextView;
    public BottomNavigationView bottomNavigationView;
    private ViewPager viewPager;
    AppCompatImageView filesIconImageView;
    TextView toolsTextView;
    TextView filesTextView;
    private ConstraintLayout toolsButtonLayout;
    private ConstraintLayout filesButtonLayout;
    Menu mainMenu;
    TextView languageTextView;
    private ThemeToggleSwitch dayNightSwitch;
    private AppCompatImageView ivSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.act_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        setupToolbar();
        initializeViews();
        initializeData();
        setupViewPager();

        initializeListeners();
        bottomNavigationView
                .setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
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

                        return true;
                    }
                });

        // Navigate to requested tab if launched with EXTRA_START_TAB
        int startTab = getIntent().getIntExtra("EXTRA_START_TAB", 0);
        if (startTab > 0) {
            viewPager.post(() -> {
                viewPager.setCurrentItem(startTab, false);
                bottomNavigationView.getMenu().getItem(startTab).setChecked(true);
            });
        }
    }

    private void initializeData() {
    }

    private void initializeListeners() {
        // btnFiles.setOnClickListener(this);
        // btnTools.setOnClickListener(this);
        // findViewById(R.id.pdf_act_main_select_image).setOnClickListener(this);
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
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mainToolbar.setVisibility(View.GONE);
    }

    private void initializeViews() {
        bottomNavigationView = findViewById(R.id.bottom_nav);
        viewPager = findViewById(R.id.viewpager_main);
        ivSearch = findViewById(R.id.iv_search);
        ivSearch.setOnClickListener(this);

        toolbarTitleTextView = findViewById(R.id.tv_name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_act_main, menu);
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
        if (idView == R.id.iv_search) {
            if (Utils.checkPermission(this)) {
                Intent intentSearch = new Intent(this, SearchDocumentActivity.class);
                startActivity(intentSearch);
            } else {
                Toast.makeText(this, "Need Permission!", Toast.LENGTH_SHORT).show();
            }
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

                mainToolbar.setVisibility(View.GONE);
                toolbarTitleTextView.setVisibility(View.GONE);
                ivSearch.setVisibility(View.GONE);
                if (getSupportActionBar() != null) {
                    Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);

                }
                break;
            case 1:
                bottomNavigationView.getMenu().findItem(R.id.navigation_tools).setChecked(true);
                mainToolbar.setVisibility(View.GONE);
                toolbarTitleTextView.setVisibility(View.GONE);
                ivSearch.setVisibility(View.GONE);

                if (getSupportActionBar() != null) {
                    Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
                }
                break;
            case 2:
                bottomNavigationView.getMenu().findItem(R.id.navigation_settings).setChecked(true);
                mainToolbar.setVisibility(View.GONE);
                toolbarTitleTextView.setVisibility(View.GONE);
                ivSearch.setVisibility(View.GONE);

                if (getSupportActionBar() != null) {
                    Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
                }

                // tvToolbar.setText(R.string.pdf_tools);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppGlobalConstants.REQUEST_CODE_FILE_PICKER) {
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
            File pathFolder = new File(
                    Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/DocumentReader/");
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