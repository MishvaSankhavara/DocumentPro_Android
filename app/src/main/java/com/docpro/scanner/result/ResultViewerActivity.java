package com.docpro.scanner.result;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.example.documenpro.AppGlobalConstants;
import com.example.documenpro.R;
import com.example.documenpro.adapter_reader.PdfViewerPagerAdapter;
import com.example.documenpro.utils.Utils;

import java.util.Objects;

public class ResultViewerActivity extends AppCompatActivity {
    private ViewPager2 contentPager;

    // Aliases for compatibility
    public TabLayout tabLayout;
    public ViewPager2 viewPager;
    private android.widget.ImageView ivBack;
    private android.widget.TextView tvToolbarName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.layout_result_viewer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.lay_result_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupHeader();
        initializePager();

        Intent intent = getIntent();
        if (intent != null) {
            final int targetPos = intent.getIntExtra(AppGlobalConstants.FROM_SAVE_IMAGE, 0);
            if (targetPos > 0) {
                // Post after the first layout pass so ViewPager2 honours the position
                contentPager.post(() -> contentPager.setCurrentItem(targetPos, false));
            }
        }
    }

    private void setupHeader() {
        Toolbar headerToolbar = findViewById(R.id.toolbar_result_viewer);
        ivBack = findViewById(R.id.iv_back);
        tvToolbarName = findViewById(R.id.tv_name);

        ivBack.setOnClickListener(v -> navigateToMainTools());

        setSupportActionBar(headerToolbar);
        if (getSupportActionBar() != null) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            tvToolbarName.setText(Utils.setToolBarPdfCreated(this));
        }
    }

    private void initializePager() {
        TabLayout categoryTabs = findViewById(R.id.tabs_result_categories);
        contentPager = findViewById(R.id.vp_result_content);

        // Bind aliases
        tabLayout = categoryTabs;
        viewPager = contentPager;

        PdfViewerPagerAdapter resultAdapter = new PdfViewerPagerAdapter(this, this);
        contentPager.setAdapter(resultAdapter);

        new TabLayoutMediator(categoryTabs, contentPager, (currentTab, pos) -> {
            switch (pos) {
                case 0:
                    currentTab.setText("Compressed");
                    break;
                case 1:
                    currentTab.setText("Split");
                    break;
                case 2:
                    currentTab.setText("Merged");
                    break;
                case 3:
                    currentTab.setText("Image to PDF");
                    break;
                case 4:
                    currentTab.setText("Lock PDF");
                    break;
                case 5:
                    currentTab.setText("Image");
                    break;
            }
        }).attach();
    }

    private void navigateToMainTools() {
        Intent intent = new Intent(this, com.example.documenpro.ui.activities.MainActivity.class);
        intent.putExtra("EXTRA_START_TAB", 1); // 1 = Tools tab
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        navigateToMainTools();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            navigateToMainTools();
        }
        return super.onOptionsItemSelected(item);
    }
}
