package docreader.aidoc.pdfreader.ui.activities;

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

import docreader.aidoc.pdfreader.ActivityBase;
import docreader.aidoc.pdfreader.AppGlobalConstants;
import docreader.aidoc.pdfreader.R;
import docreader.aidoc.pdfreader.adapter_reader.FilePickerAdapter;

import docreader.aidoc.pdfreader.database.DatabaseHelper;
import docreader.aidoc.pdfreader.AppExecutor.FavoriteRemovalExecutor;
import docreader.aidoc.pdfreader.clickListener.OnItemSelectListener;
import docreader.aidoc.pdfreader.clickListener.OnConfirmClickListener;
import docreader.aidoc.pdfreader.model_reader.DocumentModel;
import docreader.aidoc.pdfreader.ui.customviews.EmptyStateRecyclerView;
import docreader.aidoc.pdfreader.ui.dialog.FileProgressDialog;
import docreader.aidoc.pdfreader.utils.DialogManagerUtils;
import docreader.aidoc.pdfreader.utils.Utils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;

public class SelectDocumentActivity extends ActivityBase implements View.OnClickListener, OnItemSelectListener {
    public EmptyStateRecyclerView documentRecyclerView;
    public FilePickerAdapter filePickerAdapter;
    public Toolbar topToolbar;
    private LinearLayout shareButtonLayout;
    private LinearLayout moveButtonLayout;
    private LinearLayout deleteButtonLayout;
    private AppCompatImageView shareIconImageView;
    private AppCompatImageView moveIconImageView;
    private AppCompatImageView deleteIconImageView;
    private AppCompatTextView shareTextView;
    private AppCompatTextView moveTextView;
    public ArrayList<DocumentModel> documentList;
    public ConstraintLayout mainContainerLayout;
    private Menu optionsMenu;
    private int selectedFileType;

    private AppCompatTextView deleteTextView;
    private android.widget.ImageView ivBack;
    private android.widget.TextView tvToolbarName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.act_select);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initToolbar();
        readIntentData();
        initViews();

        updateActionButtonsState(false);
    }

    private void initToolbar() {
        topToolbar = findViewById(R.id.toolbar);
        ivBack = findViewById(R.id.iv_back);
        tvToolbarName = findViewById(R.id.tv_name);

        ivBack.setOnClickListener(v -> finish());
        tvToolbarName.setText(getString(R.string.label_items_selected, String.valueOf(0)));

        setSupportActionBar(topToolbar);
        if (getSupportActionBar() != null) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void readIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            selectedFileType = getIntent().getIntExtra(AppGlobalConstants.EXTRA_FILE_TYPE,
                    AppGlobalConstants.FILE_TYPE_ALL);
        }

    }

    private void initViews() {
        mainContainerLayout = findViewById(R.id.main);
        shareButtonLayout = findViewById(R.id.ll_share);
        shareButtonLayout.setOnClickListener(this);
        moveButtonLayout = findViewById(R.id.ll_remove);
        moveButtonLayout.setOnClickListener(this);
        deleteButtonLayout = findViewById(R.id.ll_delete);
        deleteButtonLayout.setOnClickListener(this);
        shareTextView = findViewById(R.id.tv_share);
        moveTextView = findViewById(R.id.tv_remove);
        deleteTextView = findViewById(R.id.tv_delete);
        shareIconImageView = findViewById(R.id.iv_share);
        moveIconImageView = findViewById(R.id.iv_remove);
        deleteIconImageView = findViewById(R.id.iv_delete);

        ProgressBar progressBar = findViewById(R.id.loadingView);
        documentRecyclerView = findViewById(R.id.rcv_select_file_list);
        documentRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        documentRecyclerView.setLayoutManager(layoutManager);
        documentRecyclerView.setEmptyView(findViewById(R.id.empty_layout));
        if (selectedFileType == AppGlobalConstants.FILE_TYPE_ALL) {
            mainContainerLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.app_all_list_bg));
            topToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.app_all_list_bg));
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.app_all_list_bg));
            moveButtonLayout.setVisibility(View.GONE);
            documentList = Utils.countFile(this, AppGlobalConstants.QUERY_ALL_DOCUMENT_FILES);
        } else if (selectedFileType == AppGlobalConstants.FILE_TYPE_EXCEL) {
            mainContainerLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.app_excel_list_bg));

            topToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.app_excel_list_bg));
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.app_excel_list_bg));
            moveButtonLayout.setVisibility(View.GONE);
            documentList = Utils.countFile(this, AppGlobalConstants.QUERY_EXCEL_FILES);
        } else if (selectedFileType == AppGlobalConstants.FILE_TYPE_PDF) {
            mainContainerLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.app_pdf_list_bg));

            topToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.app_pdf_list_bg));
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.app_pdf_list_bg));
            moveButtonLayout.setVisibility(View.GONE);
            documentList = Utils.countFile(this, AppGlobalConstants.QUERY_PDF_FILES);
        } else if (selectedFileType == AppGlobalConstants.FILE_TYPE_PPT) {
            mainContainerLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.app_ppt_list_bg));

            topToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.app_ppt_list_bg));
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.app_ppt_list_bg));
            moveButtonLayout.setVisibility(View.GONE);
            documentList = Utils.countFile(this, AppGlobalConstants.QUERY_PPT_FILES);
        } else if (selectedFileType == AppGlobalConstants.FILE_TYPE_WORD) {
            mainContainerLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.app_word_list_bg));

            topToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.app_word_list_bg));
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.app_word_list_bg));
            moveButtonLayout.setVisibility(View.GONE);
            documentList = Utils.countFile(this, AppGlobalConstants.QUERY_WORD_FILES);

        } else if (selectedFileType == AppGlobalConstants.FILE_TYPE_FAVORITE) {
            mainContainerLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.app_all_list_bg));

            topToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.app_all_list_bg));
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.app_all_list_bg));
            moveButtonLayout.setVisibility(View.VISIBLE);
            documentList = DatabaseHelper.getInstance(this).getStarredDocuments_DatabaseHelper();
            moveIconImageView.setImageResource(R.drawable.ic_un_bookmark);
            moveTextView.setText(getResources().getString(R.string.action_remove_favorite));
        } else if (selectedFileType == AppGlobalConstants.FILE_TYPE_RECENT) {
            mainContainerLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.app_all_list_bg));

            topToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.app_all_list_bg));
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.app_all_list_bg));
            moveButtonLayout.setVisibility(View.VISIBLE);
            documentList = DatabaseHelper.getInstance(this).getRecentDocuments_DatabaseHelper();
            moveIconImageView.setImageResource(R.drawable.ic_remove);
            moveTextView.setText(R.string.move_out);
        }
        filePickerAdapter = new FilePickerAdapter(this, documentList, this);

        documentRecyclerView.setAdapter(filePickerAdapter);
        progressBar.setVisibility(View.GONE);

    }

    @Override
    public void onClick(View v) {
        int idView = v.getId();
        if (idView == R.id.ll_share) {
            if (filePickerAdapter.getSelected_FilePicker().size() < 50) {
                try {
                    ArrayList<Uri> arrayList = new ArrayList<>();
                    for (int i = 0; i < filePickerAdapter.getSelected_FilePicker().size(); i++) {
                        arrayList.add(FileProvider.getUriForFile(this,
                                getApplicationContext().getPackageName() + ".provider",
                                new File(filePickerAdapter.getSelected_FilePicker().get(i).getFileUri_DocModel())));
                    }
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_via));
                    intent.setType("application/pdf");
                    String appUrl = "https://play.google.com/store/apps/details?id=" + getPackageName();
                    String shareMessage = String.format(getString(R.string.custom_share_message), appUrl);
                    intent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, arrayList);
                    startActivity(intent);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(this, getString(R.string.toast_maximum_file_share), Toast.LENGTH_SHORT).show();
            }
        } else if (idView == R.id.ll_remove) {
            if (selectedFileType == AppGlobalConstants.FILE_TYPE_RECENT) {
                DialogManagerUtils.showConfirmationDialog(SelectDocumentActivity.this,
                        AppGlobalConstants.DIALOG_CONFIRM_REMOVE_RECENT, new OnConfirmClickListener() {
                            @Override
                            public void onConfirmClickListener() {
                                new RemoveRecent(SelectDocumentActivity.this).execute();
                            }
                        });
            } else if (selectedFileType == AppGlobalConstants.FILE_TYPE_FAVORITE) {
                DialogManagerUtils.showConfirmationDialog(SelectDocumentActivity.this,
                        AppGlobalConstants.DIALOG_CONFIRM_REMOVE_FAVORITE, new OnConfirmClickListener() {
                            @Override
                            public void onConfirmClickListener() {
                                FavoriteRemovalExecutor removeFavoriteExecutor = new FavoriteRemovalExecutor(
                                        SelectDocumentActivity.this);
                                removeFavoriteExecutor.executeTask_removeFav();
                            }
                        });
            }
        } else if (idView == R.id.ll_delete) {
            DialogManagerUtils.showConfirmationDialog(SelectDocumentActivity.this,
                    AppGlobalConstants.DIALOG_CONFIRM_DELETE, new OnConfirmClickListener() {
                        @Override
                        public void onConfirmClickListener() {
                            new DeleteMultiFile(SelectDocumentActivity.this).execute();
                        }
                    });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        this.optionsMenu = menu;
        inflater.inflate(R.menu.menu_act_select, menu);
        return true;
    }

    public void updateActionButtonsState(boolean onOff) {
        shareButtonLayout.setClickable(onOff);
        shareButtonLayout.setFocusable(onOff);
        deleteButtonLayout.setClickable(onOff);
        deleteButtonLayout.setFocusable(onOff);
        moveButtonLayout.setClickable(onOff);
        moveButtonLayout.setFocusable(onOff);

        if (onOff) {
            shareIconImageView.setColorFilter(ContextCompat.getColor(this, R.color.app_primary_text));
            moveIconImageView.setColorFilter(ContextCompat.getColor(this, R.color.app_primary_text));
            deleteIconImageView.setColorFilter(ContextCompat.getColor(this, R.color.app_primary_text));

            shareTextView.setTextColor(ContextCompat.getColor(this, R.color.app_primary_text));
            moveTextView.setTextColor(ContextCompat.getColor(this, R.color.app_primary_text));
            deleteTextView.setTextColor(ContextCompat.getColor(this, R.color.app_primary_text));

        } else {
            shareIconImageView.setColorFilter(ContextCompat.getColor(this, R.color.color_disable_button));
            moveIconImageView.setColorFilter(ContextCompat.getColor(this, R.color.color_disable_button));
            deleteIconImageView.setColorFilter(ContextCompat.getColor(this, R.color.color_disable_button));

            shareTextView.setTextColor(ContextCompat.getColor(this, R.color.color_disable_button));
            moveTextView.setTextColor(ContextCompat.getColor(this, R.color.color_disable_button));
            deleteTextView.setTextColor(ContextCompat.getColor(this, R.color.color_disable_button));
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.item_select_all) {

            if (filePickerAdapter.isSelectedAll_FilePicker) {
                filePickerAdapter.setUnSelectedAll_FilePicker();
                optionsMenu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_select_all));
                updateActionButtonsState(false);
            } else {
                filePickerAdapter.setSelectedAll_FilePicker();
                optionsMenu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_unselect_all));
                updateActionButtonsState(true);
            }
            tvToolbarName.setText(getString(R.string.label_items_selected,
                    String.valueOf(filePickerAdapter.getSelected_FilePicker().size())));

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected() {
        updateActionButtonsState(!filePickerAdapter.getSelected_FilePicker().isEmpty());
        tvToolbarName.setText(getString(R.string.label_items_selected,
                String.valueOf(filePickerAdapter.getSelected_FilePicker().size())));
        if (filePickerAdapter.getSelected_FilePicker().size() == documentList.size()) {
            optionsMenu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_unselect_all));
            filePickerAdapter.isSelectedAll_FilePicker = true;
        } else {
            optionsMenu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_select_all));
            filePickerAdapter.isSelectedAll_FilePicker = false;
        }
    }

    public static class DeleteMultiFile extends AsyncTask<Void, Integer, Void> {
        WeakReference<SelectDocumentActivity> weakReference;
        FileProgressDialog dialog;
        int progress;

        public DeleteMultiFile(SelectDocumentActivity activity) {
            this.weakReference = new WeakReference<>(activity);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = 0;
            dialog = new FileProgressDialog(weakReference.get());
            dialog.setTitleText(weakReference.get().getString(R.string.action_deleting));
            Window window5 = dialog.getWindow();
            assert window5 != null;
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            window5.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ArrayList<DocumentModel> arrayList1 = weakReference.get().filePickerAdapter.getSelected_FilePicker();
            for (int i = 0; i < arrayList1.size(); i++) {
                DocumentModel pdfModel = arrayList1.get(i);
                File file = new File(pdfModel.getFileUri_DocModel());
                file.delete();
                MediaScannerConnection.scanFile(weakReference.get(), new String[] { pdfModel.getFileUri_DocModel() },
                        null, null);
                if (DatabaseHelper.getInstance(weakReference.get())
                        .isStared_DatabaseHelper(pdfModel.getFileUri_DocModel())) {
                    DatabaseHelper.getInstance(weakReference.get())
                            .removeStaredDocument_DatabaseHelper(pdfModel.getFileUri_DocModel());

                }
                if (DatabaseHelper.getInstance(weakReference.get())
                        .isRecent_DatabaseHelper(pdfModel.getFileUri_DocModel())) {
                    DatabaseHelper.getInstance(weakReference.get())
                            .removeRecentDocument_DatabaseHelper(pdfModel.getFileUri_DocModel());

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

            if (weakReference.get().selectedFileType == AppGlobalConstants.FILE_TYPE_ALL) {

                weakReference.get().documentList = Utils.countFile(weakReference.get(),
                        AppGlobalConstants.QUERY_ALL_DOCUMENT_FILES);
            } else if (weakReference.get().selectedFileType == AppGlobalConstants.FILE_TYPE_EXCEL) {
                weakReference.get().documentList = Utils.countFile(weakReference.get(),
                        AppGlobalConstants.QUERY_EXCEL_FILES);
            } else if (weakReference.get().selectedFileType == AppGlobalConstants.FILE_TYPE_PDF) {

                weakReference.get().documentList = Utils.countFile(weakReference.get(),
                        AppGlobalConstants.QUERY_PDF_FILES);
            } else if (weakReference.get().selectedFileType == AppGlobalConstants.FILE_TYPE_PPT) {

                weakReference.get().documentList = Utils.countFile(weakReference.get(),
                        AppGlobalConstants.QUERY_PPT_FILES);
            } else if (weakReference.get().selectedFileType == AppGlobalConstants.FILE_TYPE_WORD) {

                weakReference.get().documentList = Utils.countFile(weakReference.get(),
                        AppGlobalConstants.QUERY_WORD_FILES);

            } else if (weakReference.get().selectedFileType == AppGlobalConstants.FILE_TYPE_FAVORITE) {
                weakReference.get().documentList = DatabaseHelper.getInstance(weakReference.get())
                        .getStarredDocuments_DatabaseHelper();

            } else if (weakReference.get().selectedFileType == AppGlobalConstants.FILE_TYPE_RECENT) {
                weakReference.get().documentList = DatabaseHelper.getInstance(weakReference.get())
                        .getRecentDocuments_DatabaseHelper();
            }

            weakReference.get().filePickerAdapter = new FilePickerAdapter(weakReference.get(),
                    weakReference.get().documentList, weakReference.get());
            weakReference.get().documentRecyclerView.setAdapter(weakReference.get().filePickerAdapter);

            weakReference.get().updateActionButtonsState(
                    !weakReference.get().filePickerAdapter.getSelected_FilePicker().isEmpty());
            weakReference.get().tvToolbarName.setText(weakReference.get().getString(R.string.label_items_selected,
                    String.valueOf(weakReference.get().filePickerAdapter.getSelected_FilePicker().size())));

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int number = values[0];
            dialog.updateProgress(number);
            dialog.setPercentText(number + "%");
        }
    }

    public static class RemoveRecent extends AsyncTask<Void, Integer, Void> {
        WeakReference<SelectDocumentActivity> weakReference;
        FileProgressDialog dialog;
        int progress;

        public RemoveRecent(SelectDocumentActivity activity) {
            this.weakReference = new WeakReference<>(activity);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = 0;
            dialog = new FileProgressDialog(weakReference.get());
            dialog.setTitleText(weakReference.get().getString(R.string.action_removing));
            Window window6 = dialog.getWindow();
            assert window6 != null;
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            window6.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ArrayList<DocumentModel> arrayListRemove = weakReference.get().filePickerAdapter.getSelected_FilePicker();
            for (int i = 0; i < arrayListRemove.size(); i++) {
                DocumentModel pdfModel = arrayListRemove.get(i);
                if (DatabaseHelper.getInstance(weakReference.get())
                        .isRecent_DatabaseHelper(pdfModel.getFileUri_DocModel())) {
                    DatabaseHelper.getInstance(weakReference.get())
                            .removeRecentDocument_DatabaseHelper(pdfModel.getFileUri_DocModel());
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
            this.weakReference.get().documentList = DatabaseHelper.getInstance(weakReference.get())
                    .getRecentDocuments_DatabaseHelper();

            if (weakReference.get().documentList.isEmpty()) {
                weakReference.get().finish();
            } else {
                weakReference.get().filePickerAdapter = new FilePickerAdapter(weakReference.get(),
                        weakReference.get().documentList, weakReference.get());
                weakReference.get().documentRecyclerView.setAdapter(weakReference.get().filePickerAdapter);
                weakReference.get().updateActionButtonsState(
                        !weakReference.get().filePickerAdapter.getSelected_FilePicker().isEmpty());
                weakReference.get().tvToolbarName.setText(weakReference.get().getString(R.string.label_items_selected,
                        String.valueOf(weakReference.get().filePickerAdapter.getSelected_FilePicker().size())));
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int number = values[0];
            dialog.updateProgress(number);
            dialog.setPercentText(number + "%");
        }
    }

}