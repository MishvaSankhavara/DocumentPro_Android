package com.example.documenpro.ui.activities;

import com.docpro.scanner.engine.ProcessingTaskActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.documenpro.AppGlobalConstants;
import com.example.documenpro.DocumentMyApplication;
import com.example.documenpro.R;
import com.example.documenpro.adapter_reader.PdfPreviewThumbnailAdapter;
import com.example.documenpro.clickListener.OnConfirmClickListener;
import com.example.documenpro.clickListener.RenameDialogClickListener;
import com.example.documenpro.clickListener.OnThumbnailClickListener;
import com.example.documenpro.model_reader.PDFReaderModel;
import com.example.documenpro.model_reader.PDFPageModel;
import com.example.documenpro.ui.customviews.EmptyStateRecyclerView;
import com.example.documenpro.utils.DialogManagerUtils;
import com.example.documenpro.utils.Utils;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;
import com.tom_roush.pdfbox.pdmodel.common.PDPageLabelRange;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;

public class SplitChooseFileActivity extends AppCompatActivity implements OnThumbnailClickListener {

    private Toolbar topToolbar;
    private AppCompatTextView continueButton;
    private LottieAnimationView loadingAnimationView;
    private EmptyStateRecyclerView pdfPagesRecyclerView;
    private PdfPreviewThumbnailAdapter pdfThumbnailAdapter;
    private final ArrayList<PDFPageModel> pdfPageList = new ArrayList<>();
    PDFReaderModel selectedPdfModel;
    private String pdfFileName;
    private String tempPdfImagesDirectory;
    private String pdfTempFolderName;
    private boolean isLoadingFinished = false;
    private Menu optionsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.act_split_choose_file);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setupToolbar();
        tempPdfImagesDirectory = Environment.getExternalStorageDirectory() + "/Pictures/AllPdf/tmp/";
        initializeViews();
        Intent intent = getIntent();
        if (intent != null) {
            selectedPdfModel = (PDFReaderModel) intent.getSerializableExtra(AppGlobalConstants.EXTRA_PDF_MODEL);
            if (selectedPdfModel != null) {
                pdfFileName = selectedPdfModel.getName_PDFModel();
                String pdfSavedFile = selectedPdfModel.getAbsolutePath_PDFModel();
                Uri uri = Uri.fromFile(new File(pdfSavedFile));
                new LoadPdfPageThumbnailsTask(this).execute(uri.toString());
            }
        }
    }

    private void setupToolbar() {
        topToolbar = findViewById(R.id.toolbar);
        topToolbar.setTitle(
                getString(R.string.label_items_selected, String.valueOf(DocumentMyApplication.getInstance().getArrayListSplit().size())));
        setSupportActionBar(topToolbar);
        if (getSupportActionBar() != null) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        this.optionsMenu = menu;
        inflater.inflate(R.menu.menu_act_select, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.item_select_all) {
            if (isLoadingFinished) {
                if (pdfThumbnailAdapter.isSelectedAll_PdfPreview) {
                    pdfThumbnailAdapter.setUnSelectedAll_PdfPreview();
                    optionsMenu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_select_all));
                    setContinueButtonEnabled(false);
                } else {
                    pdfThumbnailAdapter.setSelectedAll_PdfPreview();
                    optionsMenu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_unselect_all));
                    setContinueButtonEnabled(true);
                }
                topToolbar.setTitle(getString(R.string.label_items_selected, String.valueOf(pdfThumbnailAdapter.getSelected_PdfPreview().size())));
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setContinueButtonEnabled(boolean b) {
        continueButton.setEnabled(b);
        continueButton.setClickable(b);
        continueButton.setFocusable(b);
    }

    private void initializeViews() {
        loadingAnimationView = findViewById(R.id.loadingView);
        continueButton = findViewById(R.id.tv_continue);
        pdfPagesRecyclerView = findViewById(R.id.chooser_recycler_view);

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameFile = "Split-" + System.currentTimeMillis();
                DialogManagerUtils.showRenameDialog(SplitChooseFileActivity.this, nameFile, new RenameDialogClickListener() {
                    @Override
                    public void onRenameDialogListener(String newName) {
                        DocumentMyApplication.getInstance().updateSplitIndices(pdfThumbnailAdapter.getPageNumbers_PdfPreview());
                        Intent intent = new Intent(SplitChooseFileActivity.this, ProcessingTaskActivity.class);
                        intent.putExtra(AppGlobalConstants.EXTRA_PDF_FILE_NAME, newName);
                        intent.putExtra(AppGlobalConstants.EXTRA_TOOL_TYPE, AppGlobalConstants.TOOL_ID_SPLIT);
                        intent.putExtra(AppGlobalConstants.EXTRA_PDF_MODEL, selectedPdfModel);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        File dir = new File(this.tempPdfImagesDirectory);

        Utils.deleteFilesRecursively(this.tempPdfImagesDirectory);
        String sb = "Deleting temp dir " +
                this.tempPdfImagesDirectory;
    }

    @Override
    public void onBackPressed() {

        Utils.showConfirmDialog(this, AppGlobalConstants.DIALOG_CONFIRM_EXIT_SPLIT, new OnConfirmClickListener() {
            @Override
            public void onConfirmClickListener() {
                finish();
            }

        });
    }

    @Override
    public void onChoosePdfSplitListener() {
        setContinueButtonEnabled(!pdfThumbnailAdapter.getSelected_PdfPreview().isEmpty());
        topToolbar.setTitle(getString(R.string.label_items_selected, String.valueOf(pdfThumbnailAdapter.getSelected_PdfPreview().size())));
        if (pdfThumbnailAdapter.getSelected_PdfPreview().size() == pdfPageList.size()) {
            optionsMenu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_unselect_all));
            pdfThumbnailAdapter.isSelectedAll_PdfPreview = true;
        } else {
            optionsMenu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_select_all));
            pdfThumbnailAdapter.isSelectedAll_PdfPreview = false;
        }

    }

    public static class LoadPdfPageThumbnailsTask extends AsyncTask<String, Void, Void> {
        WeakReference<SplitChooseFileActivity> weakReference;

        public LoadPdfPageThumbnailsTask(SplitChooseFileActivity activity) {
            this.weakReference = new WeakReference<>(activity);
        }

        @Override
        protected Void doInBackground(String... strings) {
            int i;
            String str;
            FileOutputStream fileOutputStream;
            PdfiumCore pdfiumCore = new PdfiumCore(weakReference.get());
            weakReference.get().pdfTempFolderName = weakReference.get().pdfFileName;
            Uri parse = Uri.parse(strings[0]);
            String sb = "Loading page thumbs from uri " +
                    parse.toString();
            try {
                if (!isCancelled()) {

                    PdfDocument newDocument = pdfiumCore.newDocument(weakReference.get().getContentResolver()
                            .openFileDescriptor(parse, PDPageLabelRange.STYLE_ROMAN_LOWER));
                    int pageCount = pdfiumCore.getPageCount(newDocument);
                    String sb2 = "Total number of pages " +
                            pageCount;
                    String sb3 = weakReference.get().tempPdfImagesDirectory +
                            weakReference.get().pdfTempFolderName;
                    File file = new File(sb3);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    int i2 = 0;
                    while (i2 < pageCount) {
                        int i3 = i2 + 1;
                        String sb5 = weakReference.get().tempPdfImagesDirectory +
                                weakReference.get().pdfTempFolderName +
                                "page-" +
                                i3 +
                                ".jpg";
                        String sb6 = "Generating temp share img " +
                                sb5;
                        FileOutputStream fileOutputStream2 = new FileOutputStream(sb5);
                        pdfiumCore.openPage(newDocument, i2);
                        int pageWidthPoint = pdfiumCore.getPageWidthPoint(newDocument, i2);
                        int pageHeightPoint = pdfiumCore.getPageHeightPoint(newDocument, i2);
                        OutOfMemoryError e;
                        try {
                            Bitmap createBitmap = Bitmap.createBitmap(pageWidthPoint, pageHeightPoint,
                                    Bitmap.Config.ARGB_8888);
                            i = pageCount;
                            str = sb5;
                            try {
                                pdfiumCore.renderPageBitmap(newDocument, createBitmap, i2, 0, 0, pageWidthPoint,
                                        pageHeightPoint, true);
                                fileOutputStream = fileOutputStream2;
                                try {
                                    createBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                                } catch (OutOfMemoryError ignored) {
                                }
                            } catch (OutOfMemoryError e2) {
                                e = e2;
                                fileOutputStream = fileOutputStream2;
                                Toast.makeText(weakReference.get(),
                                        weakReference.get().getString(R.string.toast_low_memory_error),
                                        Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                                weakReference.get().pdfPageList.add(new PDFPageModel(i3, Uri.fromFile(new File(str))));
                                fileOutputStream.close();
                            }
                        } catch (OutOfMemoryError e3) {
                            e = e3;
                            fileOutputStream = fileOutputStream2;
                            i = pageCount;
                            str = sb5;
                            Toast.makeText(weakReference.get(),
                                    weakReference.get().getString(R.string.toast_low_memory_error), Toast.LENGTH_LONG)
                                    .show();
                            e.printStackTrace();
                            weakReference.get().pdfPageList.add(new PDFPageModel(i3, Uri.fromFile(new File(str))));
                            fileOutputStream.close();
                        }
                        weakReference.get().pdfPageList.add(new PDFPageModel(i3, Uri.fromFile(new File(str))));
                        fileOutputStream.close();
                        i2 = i3;
                        pageCount = i;
                    }
                    pdfiumCore.closeDocument(newDocument);
                    return null;
                }

            } catch (Exception e4) {
                e4.printStackTrace();
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            if (weakReference.get() != null) {
                weakReference.get().pdfThumbnailAdapter = new PdfPreviewThumbnailAdapter(weakReference.get(),
                        weakReference.get().pdfPageList, weakReference.get());
                int i = Utils.isTabletDevice(weakReference.get()) ? 6 : 2;
                weakReference.get().pdfPagesRecyclerView
                        .setLayoutManager(new GridLayoutManager(weakReference.get(), i, RecyclerView.VERTICAL, false));
                weakReference.get().loadingAnimationView.setVisibility(View.GONE);
                weakReference.get().pdfPagesRecyclerView.setAdapter(weakReference.get().pdfThumbnailAdapter);
                weakReference.get().isLoadingFinished = true;
            }
        }
    }
}