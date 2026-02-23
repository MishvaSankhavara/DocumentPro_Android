package com.example.documenpro.ui.activities;

import static com.example.documenpro.GlobalConstant.FILE_TYPE;

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
import com.example.documenpro.BaseActivity;
import com.example.documenpro.GlobalConstant;
import com.example.documenpro.R;
import com.example.documenpro.adapter_reader.FileListAdapter;
import com.example.documenpro.advertisement.AdManager;
import com.example.documenpro.advertisement.AdMobNativeAdManager;
import com.example.documenpro.clickListener.DocClickListener;
import com.example.documenpro.clickListener.SortingListener;
import com.example.documenpro.model.Document;
import com.example.documenpro.ui.customviews.EmptyRecyclerView;
import com.example.documenpro.utils.Utils;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ListFileActivity extends BaseActivity implements DocClickListener, SortingListener {
    private EmptyRecyclerView recyclerView;

    private LottieAnimationView loadingView;
    Toolbar toolbar;

    private ArrayList<Document> itemsList;
    private FileListAdapter adapter;
    private ConstraintLayout clMain;
    private String codeType;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private int fileType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_list_file);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cl_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (getIntent() != null) {
            fileType = getIntent().getIntExtra(FILE_TYPE, GlobalConstant.ALL_FILE_TYPE);
        }
        intViews();
        AdMobNativeAdManager.showNativeBanner1_AdMob(this, null);
        setUpToolbar();
        loadFiles(codeType);
    }

    private void loadFiles(String fileType) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                itemsList = Utils.countFile(ListFileActivity.this, fileType);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new FileListAdapter(ListFileActivity.this, itemsList, ListFileActivity.this);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setVisibility(View.VISIBLE);
                        loadingView.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setToolbarColor(fileType);
    }

    private void setToolbarColor(int fileType) {
        int colorCode = R.color.all_file_list_bg;
        int title = R.string.tittle_toolbar_all;
        codeType = GlobalConstant.COUNT_ALL_FILE;
        if (fileType == GlobalConstant.EXCEL_FILE_TYPE) {
            colorCode = R.color.excel_file_list_bg;
            title = R.string.tittle_toolbar_excel;
            codeType = GlobalConstant.COUNT_EXCEL_FILE;
        } else if (fileType == GlobalConstant.PDF_FILE_TYPE) {
            colorCode = R.color.pdf_file_list_bg;
            codeType = GlobalConstant.COUNT_PDF_FILE;
            title = R.string.tittle_toolbar_pdf;
        } else if (fileType == GlobalConstant.PPT_FILE_TYPE) {
            colorCode = R.color.ppt_file_list_bg;
            title = R.string.tittle_toolbar_ppt;
            codeType = GlobalConstant.COUNT_PPT_FILE;
        } else if (fileType == GlobalConstant.WORD_FILE_TYPE) {
            colorCode = R.color.word_file_list_bg;
            title = R.string.tittle_toolbar_word;
            codeType = GlobalConstant.COUNT_WORD_FILE;
        } else if (fileType == GlobalConstant.TXT_FILE_TYPE) {
            colorCode = R.color.txt_file_list_bg;
            title = R.string.tittle_toolbar_txt;
            codeType = GlobalConstant.COUNT_TXT_FILE;
        }

        toolbar.setBackgroundColor(ContextCompat.getColor(this, colorCode));
        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), colorCode));
        clMain.setBackgroundColor(ContextCompat.getColor(this, colorCode));
        toolbar.setTitle(getResources().getString(title));
    }

    private void intViews() {
        clMain = findViewById(R.id.cl_main);
        recyclerView = findViewById(R.id.recycler);
        loadingView = findViewById(R.id.loadingView);
        toolbar = findViewById(R.id.toolbar_list_file);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setEmptyView(findViewById(R.id.empty_layout));
        itemsList = new ArrayList<>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_list_file_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int idView = item.getItemId();

        if (idView == android.R.id.home) {
            finish();
        } else if (idView == R.id.item_search) {

            Intent intent = new Intent(ListFileActivity.this, SearchActivity.class);
            intent.putExtra(GlobalConstant.FILE_TYPE, fileType);
            startActivity(intent);
            finish();


        } else if (idView == R.id.item_sort) {
            if (adapter.getItemCount() == 0) {
                Toast.makeText(ListFileActivity.this, R.string.str_no_files_yet, Toast.LENGTH_SHORT).show();
            } else {
                Utils.showSortDialog(ListFileActivity.this, this);
            }
        } else if (idView == R.id.item_multi_select) {

            Intent intent = new Intent(ListFileActivity.this, SelectActivity.class);
            intent.putExtra(GlobalConstant.FILE_TYPE, fileType);
            startActivity(intent);
            finish();

        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDocClick(Document document) {
//        Utils.openFile(this, document);
        AdManager.showAds_AdManager(this, () -> Utils.openFile(this, document));

//        Utils.openFileWithAds(this, document, 2);
    }

    @Override
    public void onSortingDateOldest() {
        itemsList.sort(Document.sortDateAscendingComparator);
        adapter.setData(itemsList);
    }

    @Override
    public void onSortingByDateNewest() {
        itemsList.sort(Document.sortDateDescendingComparator);
        adapter.setData(itemsList);
    }

    @Override
    public void onSortingAtoZ() {
        itemsList.sort(Document.sortNameAZComparator);
        adapter.setData(itemsList);
    }

    @Override
    public void onSortingZtoA() {
        itemsList.sort(Document.sortNameZAComparator);
        adapter.setData(itemsList);
    }

    @Override
    public void onSortingFileSizeUp() {
        itemsList.sort(Document.sortFileSizeAscendingComparator);
        adapter.setData(itemsList);
    }

    @Override
    public void onSortingFileSizeDown() {
        itemsList.sort(Document.sortFileSizeDescendingComparator);
        adapter.setData(itemsList);
    }
}