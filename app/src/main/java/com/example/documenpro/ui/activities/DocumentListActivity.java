package com.example.documenpro.ui.activities;

import static com.example.documenpro.AppGlobalConstants.EXTRA_FILE_TYPE;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.airbnb.lottie.LottieAnimationView;
import com.example.documenpro.ActivityBase;
import com.example.documenpro.AppGlobalConstants;
import com.example.documenpro.R;
import com.example.documenpro.adapter_reader.FileListAdapter;

import com.example.documenpro.clickListener.DocClickListener;
import com.example.documenpro.clickListener.SortingListener;
import com.example.documenpro.model_reader.DocumentModel;
import com.example.documenpro.ui.customviews.EmptyStateRecyclerView;
import com.example.documenpro.utils.Utils;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DocumentListActivity extends ActivityBase implements DocClickListener, SortingListener {
    private Toolbar topToolbar;
    private ConstraintLayout mainContainerLayout;
    private EmptyStateRecyclerView documentRecyclerView;
    private LottieAnimationView loadingAnimationView;
    private FileListAdapter documentListAdapter;
    private ArrayList<DocumentModel> documentList;
    private android.widget.ImageView ivBack;
    private android.widget.TextView tvToolbarName;
    private String selectedFileType;
    private int fileType;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.act_list_file);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cl_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (getIntent() != null) {
            fileType = getIntent().getIntExtra(EXTRA_FILE_TYPE, AppGlobalConstants.FILE_TYPE_ALL);
        }
        initializeViews();

        setUpToolbar();
        loadDocuments(selectedFileType);
    }

    private void loadDocuments(String fileType) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                documentList = Utils.countFile(DocumentListActivity.this, fileType);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        documentListAdapter = new FileListAdapter(DocumentListActivity.this, documentList,
                                DocumentListActivity.this);
                        documentRecyclerView.setAdapter(documentListAdapter);
                        documentRecyclerView.setVisibility(View.VISIBLE);
                        loadingAnimationView.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private void setUpToolbar() {
        setSupportActionBar(topToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        applyToolbarTheme(fileType);
    }

    private void initializeViews() {
        mainContainerLayout = findViewById(R.id.cl_main);
        documentRecyclerView = findViewById(R.id.recycler);
        loadingAnimationView = findViewById(R.id.loadingView);
        topToolbar = findViewById(R.id.toolbar_list_file);
        ivBack = findViewById(R.id.iv_back);
        tvToolbarName = findViewById(R.id.tv_name);

        ivBack.setOnClickListener(v -> finish());

        documentRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        documentRecyclerView.setLayoutManager(layoutManager);
        documentRecyclerView.setEmptyView(findViewById(R.id.empty_layout));
        documentList = new ArrayList<>();
    }

    private void applyToolbarTheme(int fileType) {
        int colorCode = R.color.app_all_list_bg;
        int title = R.string.tittle_toolbar_all;
        selectedFileType = AppGlobalConstants.QUERY_ALL_DOCUMENT_FILES;
        if (fileType == AppGlobalConstants.FILE_TYPE_EXCEL) {
            colorCode = R.color.app_excel_list_bg;
            title = R.string.title_toolbar_excel;
            selectedFileType = AppGlobalConstants.QUERY_EXCEL_FILES;
        } else if (fileType == AppGlobalConstants.FILE_TYPE_PDF) {
            colorCode = R.color.app_pdf_list_bg;
            selectedFileType = AppGlobalConstants.QUERY_PDF_FILES;
            title = R.string.tittle_toolbar_pdf;
        } else if (fileType == AppGlobalConstants.FILE_TYPE_PPT) {
            colorCode = R.color.app_ppt_list_bg;
            title = R.string.title_toolbar_powerpoint;
            selectedFileType = AppGlobalConstants.QUERY_PPT_FILES;
        } else if (fileType == AppGlobalConstants.FILE_TYPE_WORD) {
            colorCode = R.color.app_word_list_bg;
            title = R.string.tittle_toolbar_word;
            selectedFileType = AppGlobalConstants.QUERY_WORD_FILES;
        } else if (fileType == AppGlobalConstants.FILE_TYPE_TEXT) {
            colorCode = R.color.app_txt_list_bg;
            title = R.string.tittle_toolbar_txt;
            selectedFileType = AppGlobalConstants.QUERY_TEXT_FILES;
        }

        topToolbar.setBackgroundColor(ContextCompat.getColor(this, colorCode));
        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), colorCode));
        mainContainerLayout.setBackgroundColor(ContextCompat.getColor(this, colorCode));
        tvToolbarName.setText(getResources().getString(title));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_file_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int idView = item.getItemId();

        if (idView == android.R.id.home) {
            finish();
        } else if (idView == R.id.item_search) {
            Intent intent = new Intent(DocumentListActivity.this, SearchDocumentActivity.class);
            intent.putExtra(AppGlobalConstants.EXTRA_FILE_TYPE, fileType);
            startActivity(intent);
            finish();

        } else if (idView == R.id.item_sort) {
            if (documentListAdapter.getItemCount() == 0) {
                Toast.makeText(DocumentListActivity.this, R.string.no_files_yet, Toast.LENGTH_SHORT).show();
            } else {
                Utils.showSortDialog(DocumentListActivity.this, this);
            }
        } else if (idView == R.id.item_multi_select) {

            Intent intent = new Intent(DocumentListActivity.this, SelectDocumentActivity.class);
            intent.putExtra(AppGlobalConstants.EXTRA_FILE_TYPE, fileType);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDocClick(DocumentModel document) {
        Utils.openFile(this, document);
    }

    @Override
    public void onSortingByDateNewest() {
        documentList.sort(DocumentModel.sortDateDescendingComparator_DocModel);
        documentListAdapter.setData(documentList);
    }

    @Override
    public void onSortingDateOldest() {
        documentList.sort(DocumentModel.sortDateAscendingComparator_DocModel);
        documentListAdapter.setData(documentList);
    }

    @Override
    public void onSortingZtoA() {
        documentList.sort(DocumentModel.sortNameZAComparator_DocModel);
        documentListAdapter.setData(documentList);
    }

    @Override
    public void onSortingAtoZ() {
        documentList.sort(DocumentModel.sortNameAZComparator_DocModel);
        documentListAdapter.setData(documentList);
    }

    @Override
    public void onSortingFileSizeDown() {
        documentList.sort(DocumentModel.sortFileSizeDescendingComparator_DocModel);
        documentListAdapter.setData(documentList);
    }

    @Override
    public void onSortingFileSizeUp() {
        documentList.sort(DocumentModel.sortFileSizeAscendingComparator_DocModel);
        documentListAdapter.setData(documentList);
    }
}