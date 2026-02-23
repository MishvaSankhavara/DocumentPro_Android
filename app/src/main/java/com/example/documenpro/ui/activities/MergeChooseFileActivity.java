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
import com.example.documenpro.MyApplication;
import com.example.documenpro.R;
import com.example.documenpro.adapter_reader.MergeFileSelectionAdapter;
import com.example.documenpro.model_reader.PDFReaderModel;
import com.example.documenpro.ui.customviews.EmptyRecyclerView;
import com.example.documenpro.utils.Utils;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MergeChooseFileActivity extends AppCompatActivity {
    private AppCompatTextView btnContinue;
    private MergeFileSelectionAdapter adapter;
    private Toolbar toolbar;

    private ArrayList<PDFReaderModel> arrayList;

    private final Executor executor = Executors.newSingleThreadExecutor();

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
        toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle(getString(R.string.x_selected, "0"));
        setSupportActionBar(toolbar);
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
        LottieAnimationView loadingView = findViewById(R.id.loadingView);
        FrameLayout flContinue = findViewById(R.id.continue_fl);
        btnContinue = findViewById(R.id.tv_continue);
        EmptyRecyclerView recyclerView = findViewById(R.id.chooser_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setEmptyView(findViewById(R.id.tv_empty_title));

        executor.execute(() -> {
            arrayList = Utils.getUnLockPDF(MergeChooseFileActivity.this);
            runOnUiThread(() -> {
                adapter = new MergeFileSelectionAdapter(MergeChooseFileActivity.this, arrayList, position -> {
                    if (adapter.getSelected_MergeFileSelection().size() > 1) {
                        btnContinue.setEnabled(true);
                        btnContinue.setClickable(true);
                        btnContinue.setFocusable(true);
                    } else {
                        btnContinue.setEnabled(false);
                        btnContinue.setClickable(false);
                        btnContinue.setFocusable(false);
                    }
                    toolbar.setTitle(getString(R.string.x_selected, String.valueOf(adapter.getSelected_MergeFileSelection().size())));
                });
                recyclerView.setAdapter(adapter);
                loadingView.setVisibility(View.GONE);
                flContinue.setVisibility(View.VISIBLE);
            });
        });


        btnContinue.setOnClickListener(view -> {
            MyApplication.getInstance().setMergePdfList(adapter.getSelected_MergeFileSelection());
            Intent intent = new Intent(MergeChooseFileActivity.this, MergeReorderActivity.class);
            startActivity(intent);
            finish();
        });

    }


}