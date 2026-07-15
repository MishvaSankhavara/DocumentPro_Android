package com.arkay.gkinhindi.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.arkay.gkinhindi.Constants;
import com.arkay.gkinhindi.R;
import com.arkay.gkinhindi.adapter_reader.PagerViewAdapter;

import com.arkay.gkinhindi.ui.fragments.FragmentFiles;
import com.arkay.gkinhindi.ui.fragments.FragmentSetting;
import com.arkay.gkinhindi.ui.fragments.FragmentTools;
import com.arkay.gkinhindi.utils.Utils;


import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private Toolbar mainToolbar;
    private TextView toolbarTitleTextView;
    private ViewPager viewPager;
    // Bottom bar tab containers
    private LinearLayout btnFilesTab;
    private LinearLayout btnToolsTab;
    private LinearLayout btnSettingsTab;
    // Bottom bar icons and labels (for selected-state tinting)
    private ImageView imgButton_files;
    private ImageView imgButton_tools;
    private ImageView imgButton_settings;
    private FrameLayout fl_circle_bg_tools;
    private TextView tvButton_files;
    private TextView tvButton_tools;
    private TextView tvButton_settings;
    Menu mainMenu;
    private AppCompatImageView ivSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.act_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Apply BOTH top (status bar) and bottom (nav bar) padding to main container.
            // This shifts the entire ConstraintLayout up above the system nav bar so
            // rltBottomBar's constraintBottom_toBottomOf="parent" lands correctly.
            v.setPadding(0, systemBars.top, 0, systemBars.bottom);
            return insets;
        });

        setupToolbar();
        initializeViews();
        initializeData();
        setupViewPager();
        initializeListeners();

        // Select the first tab by default
        updateTabSelection(0);


        // Navigate to requested tab if launched with EXTRA_START_TAB
        int startTab = getIntent().getIntExtra("EXTRA_START_TAB", 0);
        if (startTab > 0) {
            viewPager.post(() -> {
                viewPager.setCurrentItem(startTab, false);
                updateTabSelection(startTab);
            });
        }

        // Schedule daily reminder notification
        com.arkay.gkinhindi.utils.NotificationHelper.scheduleDailyNotification(this);

        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                androidx.core.app.ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        com.arkay.gkinhindi.utils.AnalyticsHelper.logScreen(this, "Main Screen");
        if (Utils.checkPermission(this)) {
            Utils.dismissPermissionDialog();
        }
        initializeData();
    }

    private void initializeData() {
        com.arkay.gkinhindi.viewmodel.DataSingletonRecent.getInstance().refresh(this);
        com.arkay.gkinhindi.viewmodel.DataSingletonFavorite.getInstance().refresh(this);
    }

    private void initializeListeners() {
        btnFilesTab.setOnClickListener(v -> {
            viewPager.setCurrentItem(0, true);
            updateTabSelection(0);
        });
        btnToolsTab.setOnClickListener(v -> {
            viewPager.setCurrentItem(1, true);
            updateTabSelection(1);
        });
        btnSettingsTab.setOnClickListener(v -> {
            viewPager.setCurrentItem(2, true);
            updateTabSelection(2);
        });
    }

    /** Updates the visual selected state of all bottom-bar tabs. */
    private void updateTabSelection(int selectedIndex) {
        // Files — icon + label blue when selected
        boolean filesSelected = (selectedIndex == 0);
        btnFilesTab.setSelected(filesSelected);
        imgButton_files.setSelected(filesSelected);
        tvButton_files.setSelected(filesSelected);

        // Tools — circle is always blue; only drive the label colour
        boolean toolsSelected = (selectedIndex == 1);
        tvButton_tools.setSelected(toolsSelected);

        // Settings — icon + label blue when selected
        boolean settingsSelected = (selectedIndex == 2);
        btnSettingsTab.setSelected(settingsSelected);
        imgButton_settings.setSelected(settingsSelected);
        tvButton_settings.setSelected(settingsSelected);

        String[] tabs = {"Files", "Tools", "Settings"};
        android.os.Bundle bundle = new android.os.Bundle();
        bundle.putString("tab_name", tabs[selectedIndex]);
        bundle.putInt("tab_index", selectedIndex);
        com.arkay.gkinhindi.utils.AnalyticsHelper.logEvent("select_tab", bundle);
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
        viewPager = findViewById(R.id.viewpager_main);
        ivSearch = findViewById(R.id.iv_search);
        ivSearch.setOnClickListener(this);
        toolbarTitleTextView = findViewById(R.id.tv_name);

        // Custom bottom bar views
        btnFilesTab    = findViewById(R.id.btnFilesTab);
        btnToolsTab    = findViewById(R.id.btnToolsTab);
        btnSettingsTab = findViewById(R.id.btnSettingsTab);

        imgButton_files    = findViewById(R.id.imgButton_files);
        imgButton_tools    = findViewById(R.id.imgButton_tools);
        imgButton_settings = findViewById(R.id.imgButton_settings);
        fl_circle_bg_tools = findViewById(R.id.fl_circle_bg_tools);

        tvButton_files    = findViewById(R.id.tvButton_files);
        tvButton_tools    = findViewById(R.id.tvButton_tools);
        tvButton_settings = findViewById(R.id.tvButton_settings);
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
        updateTabSelection(position);
        mainToolbar.setVisibility(View.GONE);
        toolbarTitleTextView.setVisibility(View.GONE);
        ivSearch.setVisibility(View.GONE);
        if (getSupportActionBar() != null) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_FILE_PICKER) {
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