package com.example.documenpro.ui.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.documenpro.BaseActivity;
import com.example.documenpro.GlobalConstant;
import com.example.documenpro.R;
import com.example.documenpro.adapter_reader.FilePickerAdapter;
import com.example.documenpro.advertisement.AdMobNativeAdManager;
import com.example.documenpro.database.DatabaseHelper;
import com.example.documenpro.AppExecutor.FavoriteRemovalExecutor;
import com.example.documenpro.clickListener.OnItemSelectListener;
import com.example.documenpro.clickListener.OnConfirmClickListener;
import com.example.documenpro.model.Document;
import com.example.documenpro.ui.customviews.EmptyRecyclerView;
import com.example.documenpro.ui.dialog.ProgressDialog;
import com.example.documenpro.utils.DialogUtils;
import com.example.documenpro.utils.Utils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;

public class SelectActivity extends BaseActivity implements View.OnClickListener, OnItemSelectListener {
    public EmptyRecyclerView recyclerView;
    public FilePickerAdapter adapter;
    public Toolbar toolbar;
    private LinearLayout btnShare;
    private LinearLayout btnMove;
    private LinearLayout btnDelete;
    private AppCompatImageView imgShare;
    private AppCompatImageView imgMove;
    private AppCompatImageView imgDelete;
    private AppCompatTextView tvShare;
    private AppCompatTextView tvMove;
    public ArrayList<Document> arrayList;
    public ConstraintLayout clMain;
    private Menu menu;
    private int fileType;

    private AppCompatTextView tvDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_select);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initToolbar();
        getData();
        initViews();
        AdMobNativeAdManager.showNativeBanner3_AdMob(this, null);
        activeButton(false);
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.x_selected, String.valueOf(0)));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
        }
    }

    private void getData() {
        Intent intent = getIntent();
        if (intent != null) {
            fileType = getIntent().getIntExtra(GlobalConstant.FILE_TYPE, GlobalConstant.ALL_FILE_TYPE);
        }

    }

    private void initViews() {
        clMain = findViewById(R.id.main);
        btnShare = findViewById(R.id.ll_share);
        btnShare.setOnClickListener(this);
        btnMove = findViewById(R.id.ll_remove);
        btnMove.setOnClickListener(this);
        btnDelete = findViewById(R.id.ll_delete);
        btnDelete.setOnClickListener(this);
        tvShare = findViewById(R.id.tv_share);
        tvMove = findViewById(R.id.tv_remove);
        tvDelete = findViewById(R.id.tv_delete);
        imgShare = findViewById(R.id.iv_share);
        imgMove = findViewById(R.id.iv_remove);
        imgDelete = findViewById(R.id.iv_delete);


        ProgressBar progressBar = findViewById(R.id.loadingView);
        recyclerView = findViewById(R.id.rcv_select_file_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setEmptyView(findViewById(R.id.tv_empty_title));
        if (fileType == GlobalConstant.ALL_FILE_TYPE) {
            clMain.setBackgroundColor(ContextCompat.getColor(this, R.color.all_file_list_bg));
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.all_file_list_bg));
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.all_file_list_bg));
            btnMove.setVisibility(View.GONE);
            arrayList = Utils.countFile(this, GlobalConstant.COUNT_ALL_FILE);
        } else if (fileType == GlobalConstant.EXCEL_FILE_TYPE) {
            clMain.setBackgroundColor(ContextCompat.getColor(this, R.color.excel_file_list_bg));

            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.excel_file_list_bg));
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.excel_file_list_bg));
            btnMove.setVisibility(View.GONE);
            arrayList = Utils.countFile(this, GlobalConstant.COUNT_EXCEL_FILE);
        } else if (fileType == GlobalConstant.PDF_FILE_TYPE) {
            clMain.setBackgroundColor(ContextCompat.getColor(this, R.color.pdf_file_list_bg));

            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.pdf_file_list_bg));
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.pdf_file_list_bg));
            btnMove.setVisibility(View.GONE);
            arrayList = Utils.countFile(this, GlobalConstant.COUNT_PDF_FILE);
        } else if (fileType == GlobalConstant.PPT_FILE_TYPE) {
            clMain.setBackgroundColor(ContextCompat.getColor(this, R.color.ppt_file_list_bg));

            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.ppt_file_list_bg));
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.ppt_file_list_bg));
            btnMove.setVisibility(View.GONE);
            arrayList = Utils.countFile(this, GlobalConstant.COUNT_PPT_FILE);
        } else if (fileType == GlobalConstant.WORD_FILE_TYPE) {
            clMain.setBackgroundColor(ContextCompat.getColor(this, R.color.word_file_list_bg));

            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.word_file_list_bg));
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.word_file_list_bg));
            btnMove.setVisibility(View.GONE);
            arrayList = Utils.countFile(this, GlobalConstant.COUNT_WORD_FILE);

        } else if (fileType == GlobalConstant.FAV_FILE_TYPE) {
            clMain.setBackgroundColor(ContextCompat.getColor(this, R.color.all_file_list_bg));

            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.all_file_list_bg));
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.all_file_list_bg));
            btnMove.setVisibility(View.VISIBLE);
            arrayList = DatabaseHelper.getInstance(this).getStarredDocuments_DatabaseHelper();
            imgMove.setImageResource(R.drawable.ic_un_bookmark);
            tvMove.setText(getResources().getString(R.string.str_remove_favorite));
        } else if (fileType == GlobalConstant.RECENT_FILE_TYPE) {
            clMain.setBackgroundColor(ContextCompat.getColor(this, R.color.all_file_list_bg));

            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.all_file_list_bg));
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.all_file_list_bg));
            btnMove.setVisibility(View.VISIBLE);
            arrayList = DatabaseHelper.getInstance(this).getRecentDocuments_DatabaseHelper();
            imgMove.setImageResource(R.drawable.ic_remove);
            tvMove.setText(R.string.str_move_out);
        }
        adapter = new FilePickerAdapter(this, arrayList, this);

        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);

    }

    @Override
    public void onClick(View v) {
        int idView = v.getId();
        if (idView == R.id.ll_share) {
            if (adapter.getSelected_FilePicker().size() < 50) {
                try {
                    ArrayList<Uri> arrayList = new ArrayList<>();
                    for (int i = 0; i < adapter.getSelected_FilePicker().size(); i++) {
                        arrayList.add(FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", new File(adapter.getSelected_FilePicker().get(i).getFileUri())));
                    }
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.str_share_via));
                    intent.setType("application/pdf");
                    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, arrayList);
                    startActivity(intent);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(this, getString(R.string.toast_maximum_file_share), Toast.LENGTH_SHORT).show();
            }
        } else if (idView == R.id.ll_remove) {
            if (fileType == GlobalConstant.RECENT_FILE_TYPE) {
                DialogUtils.showConfirmDialog(SelectActivity.this, GlobalConstant.DIALOG_CONFIRM_REMOVE_RECENT, new OnConfirmClickListener() {
                    @Override
                    public void onConfirmClickListener() {
                        new RemoveRecent(SelectActivity.this).execute();
                    }
                });
            } else if (fileType == GlobalConstant.FAV_FILE_TYPE) {
                DialogUtils.showConfirmDialog(SelectActivity.this, GlobalConstant.DIALOG_CONFIRM_REMOVE_FAV, new OnConfirmClickListener() {
                    @Override
                    public void onConfirmClickListener() {
                        FavoriteRemovalExecutor removeFavoriteExecutor = new FavoriteRemovalExecutor(SelectActivity.this);
                        removeFavoriteExecutor.executeTask_removeFav();
                    }
                });
            }
        } else if (idView == R.id.ll_delete) {
            DialogUtils.showConfirmDialog(SelectActivity.this, GlobalConstant.DIALOG_CONFIRM_DELETE, new OnConfirmClickListener() {
                @Override
                public void onConfirmClickListener() {
                    new DeleteMultiFile(SelectActivity.this).execute();
                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        this.menu = menu;
        inflater.inflate(R.menu.menu_activity_select, menu);
        return true;
    }

    public void activeButton(boolean onOff) {
        btnShare.setClickable(onOff);
        btnShare.setFocusable(onOff);
        btnDelete.setClickable(onOff);
        btnDelete.setFocusable(onOff);
        btnMove.setClickable(onOff);
        btnMove.setFocusable(onOff);

        if (onOff) {
            imgShare.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryText));
            imgMove.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryText));
            imgDelete.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryText));

            tvShare.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryText));
            tvMove.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryText));
            tvDelete.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryText));

        } else {
            imgShare.setColorFilter(ContextCompat.getColor(this, R.color.color_disable_button));
            imgMove.setColorFilter(ContextCompat.getColor(this, R.color.color_disable_button));
            imgDelete.setColorFilter(ContextCompat.getColor(this, R.color.color_disable_button));

            tvShare.setTextColor(ContextCompat.getColor(this, R.color.color_disable_button));
            tvMove.setTextColor(ContextCompat.getColor(this, R.color.color_disable_button));
            tvDelete.setTextColor(ContextCompat.getColor(this, R.color.color_disable_button));
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.item_select_all) {

            if (adapter.isSelectedAll_FilePicker) {
                adapter.setUnSelectedAll_FilePicker();
                menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_select_all));
                activeButton(false);
            } else {
                adapter.setSelectedAll_FilePicker();
                menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_unselect_all));
                activeButton(true);
            }
            toolbar.setTitle(getString(R.string.x_selected, String.valueOf(adapter.getSelected_FilePicker().size())));

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected() {
        activeButton(!adapter.getSelected_FilePicker().isEmpty());
        toolbar.setTitle(getString(R.string.x_selected, String.valueOf(adapter.getSelected_FilePicker().size())));
        if (adapter.getSelected_FilePicker().size() == arrayList.size()) {
            menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_unselect_all));
            adapter.isSelectedAll_FilePicker = true;
        } else {
            menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_select_all));
            adapter.isSelectedAll_FilePicker = false;
        }
    }

    public static class DeleteMultiFile extends AsyncTask<Void, Integer, Void> {
        WeakReference<SelectActivity> weakReference;
        ProgressDialog dialog;
        int progress;

        public DeleteMultiFile(SelectActivity activity) {
            this.weakReference = new WeakReference<>(activity);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = 0;
            dialog = new ProgressDialog(weakReference.get());
            dialog.setTvTittle(weakReference.get().getString(R.string.str_action_deleting));
            Window window5 = dialog.getWindow();
            assert window5 != null;
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            window5.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ArrayList<Document> arrayList1 = weakReference.get().adapter.getSelected_FilePicker();
            for (int i = 0; i < arrayList1.size(); i++) {
                Document pdfModel = arrayList1.get(i);
                File file = new File(pdfModel.getFileUri());
                file.delete();
                MediaScannerConnection.scanFile(weakReference.get(), new String[]{pdfModel.getFileUri()}, null, null);
                if (DatabaseHelper.getInstance(weakReference.get()).isStared_DatabaseHelper(pdfModel.getFileUri())) {
                    DatabaseHelper.getInstance(weakReference.get()).removeStaredDocument_DatabaseHelper(pdfModel.getFileUri());

                }
                if (DatabaseHelper.getInstance(weakReference.get()).isRecent_DatabaseHelper(pdfModel.getFileUri())) {
                    DatabaseHelper.getInstance(weakReference.get()).removeRecentDocument_DatabaseHelper(pdfModel.getFileUri());

                }
            }
            for (int i = 0; i < 100; i++) {
                SystemClock.sleep(10);
                publishProgress(i);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            dialog.dismiss();

            if (weakReference.get().fileType == GlobalConstant.ALL_FILE_TYPE) {

                weakReference.get().arrayList = Utils.countFile(weakReference.get(), GlobalConstant.COUNT_ALL_FILE);
            } else if (weakReference.get().fileType == GlobalConstant.EXCEL_FILE_TYPE) {
                weakReference.get().arrayList = Utils.countFile(weakReference.get(), GlobalConstant.COUNT_EXCEL_FILE);
            } else if (weakReference.get().fileType == GlobalConstant.PDF_FILE_TYPE) {

                weakReference.get().arrayList = Utils.countFile(weakReference.get(), GlobalConstant.COUNT_PDF_FILE);
            } else if (weakReference.get().fileType == GlobalConstant.PPT_FILE_TYPE) {

                weakReference.get().arrayList = Utils.countFile(weakReference.get(), GlobalConstant.COUNT_PPT_FILE);
            } else if (weakReference.get().fileType == GlobalConstant.WORD_FILE_TYPE) {

                weakReference.get().arrayList = Utils.countFile(weakReference.get(), GlobalConstant.COUNT_WORD_FILE);

            } else if (weakReference.get().fileType == GlobalConstant.FAV_FILE_TYPE) {
                weakReference.get().arrayList = DatabaseHelper.getInstance(weakReference.get()).getStarredDocuments_DatabaseHelper();

            } else if (weakReference.get().fileType == GlobalConstant.RECENT_FILE_TYPE) {
                weakReference.get().arrayList = DatabaseHelper.getInstance(weakReference.get()).getRecentDocuments_DatabaseHelper();
            }


            weakReference.get().adapter = new FilePickerAdapter(weakReference.get(), weakReference.get().arrayList, weakReference.get());
            weakReference.get().recyclerView.setAdapter(weakReference.get().adapter);

            weakReference.get().activeButton(!weakReference.get().adapter.getSelected_FilePicker().isEmpty());
            weakReference.get().toolbar.setTitle(weakReference.get().getString(R.string.x_selected, String.valueOf(weakReference.get().adapter.getSelected_FilePicker().size())));


        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int number = values[0];
            dialog.setProgress(number);
            dialog.setTvPercent(number + "%");
        }
    }

    public static class RemoveRecent extends AsyncTask<Void, Integer, Void> {
        WeakReference<SelectActivity> weakReference;
        ProgressDialog dialog;
        int progress;

        public RemoveRecent(SelectActivity activity) {
            this.weakReference = new WeakReference<>(activity);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = 0;
            dialog = new ProgressDialog(weakReference.get());
            dialog.setTvTittle(weakReference.get().getString(R.string.str_action_removing));
            Window window6 = dialog.getWindow();
            assert window6 != null;
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            window6.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ArrayList<Document> arrayListRemove = weakReference.get().adapter.getSelected_FilePicker();
            for (int i = 0; i < arrayListRemove.size(); i++) {
                Document pdfModel = arrayListRemove.get(i);
                if (DatabaseHelper.getInstance(weakReference.get()).isRecent_DatabaseHelper(pdfModel.getFileUri())) {
                    DatabaseHelper.getInstance(weakReference.get()).removeRecentDocument_DatabaseHelper(pdfModel.getFileUri());
                }
            }
            for (int i = 0; i < 100; i++) {
                SystemClock.sleep(10);
                publishProgress(i);
            }

            return null;
        }

        @Override
        protected void
        onPostExecute(Void unused) {
            super.onPostExecute(unused);
            dialog.dismiss();
            this.weakReference.get().arrayList = DatabaseHelper.getInstance(weakReference.get()).getRecentDocuments_DatabaseHelper();

            if (weakReference.get().arrayList.isEmpty()) {
                weakReference.get().finish();
            } else {
                weakReference.get().adapter = new FilePickerAdapter(weakReference.get(), weakReference.get().arrayList, weakReference.get());
                weakReference.get().recyclerView.setAdapter(weakReference.get().adapter);
                weakReference.get().activeButton(!weakReference.get().adapter.getSelected_FilePicker().isEmpty());
                weakReference.get().toolbar.setTitle(weakReference.get().getString(R.string.x_selected, String.valueOf(weakReference.get().adapter.getSelected_FilePicker().size())));
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int number = values[0];
            dialog.setProgress(number);
            dialog.setTvPercent(number + "%");
        }
    }

}