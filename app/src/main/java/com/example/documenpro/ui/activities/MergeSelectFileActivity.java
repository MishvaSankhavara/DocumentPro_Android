package com.example.documenpro.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.airbnb.lottie.LottieAnimationView;
import com.example.documenpro.DocumentMyApplication;
import com.example.documenpro.R;
import com.example.documenpro.adapter_reader.MergeFileSelectionAdapter;
import com.example.documenpro.model_reader.PDFReaderModel;
import com.example.documenpro.ui.customviews.EmptyStateRecyclerView;
import com.example.documenpro.utils.Utils;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MergeSelectFileActivity extends AppCompatActivity {
    private AppCompatTextView continueButtonText;
    private MergeFileSelectionAdapter pdfSelectionAdapter;
    private Toolbar mergeToolbar;

    private ArrayList<PDFReaderModel> availablePdfList;

    private final Executor backgroundExecutor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_merge_choose_file);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
//        NativeAdsAdmob.showNativeBanner1(this, null);
        initToolBar();
        initViews();
    }

    private void initToolBar() {
        mergeToolbar = findViewById(R.id.toolbar);

        mergeToolbar.setTitle(getString(R.string.x_selected, "0"));
        setSupportActionBar(mergeToolbar);
        if (getSupportActionBar() != null) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        LottieAnimationView loadingAnimation = findViewById(R.id.loadingView);
        FrameLayout continueButtonContainer = findViewById(R.id.continue_fl);
        continueButtonText = findViewById(R.id.tv_continue);
        EmptyStateRecyclerView recyclerView = findViewById(R.id.chooser_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setEmptyView(findViewById(R.id.tv_empty_title));

        backgroundExecutor.execute(() -> {
            availablePdfList = Utils.getUnLockPDF(MergeSelectFileActivity.this);
            runOnUiThread(() -> {
                pdfSelectionAdapter = new MergeFileSelectionAdapter(MergeSelectFileActivity.this, availablePdfList, position -> {
                    if (pdfSelectionAdapter.getSelected_MergeFileSelection().size() > 1) {
                        continueButtonText.setEnabled(true);
                        continueButtonText.setClickable(true);
                        continueButtonText.setFocusable(true);
                    } else {
                        continueButtonText.setEnabled(false);
                        continueButtonText.setClickable(false);
                        continueButtonText.setFocusable(false);
                    }
                    mergeToolbar.setTitle(getString(R.string.x_selected, String.valueOf(pdfSelectionAdapter.getSelected_MergeFileSelection().size())));
                });
                recyclerView.setAdapter(pdfSelectionAdapter);
                loadingAnimation.setVisibility(View.GONE);
                continueButtonContainer.setVisibility(View.VISIBLE);
            });
        });

        continueButtonText.setOnClickListener(view -> {
            DocumentMyApplication.getInstance().updateMergedPdfList(pdfSelectionAdapter.getSelected_MergeFileSelection());
            Intent intent = new Intent(MergeSelectFileActivity.this, ReorderMergePdfActivity.class);
            startActivity(intent);
            finish();
        });
    }
}