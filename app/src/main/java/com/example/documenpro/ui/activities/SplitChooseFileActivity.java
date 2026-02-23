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
import com.example.documenpro.GlobalConstant;
import com.example.documenpro.MyApplication;
import com.example.documenpro.R;
import com.example.documenpro.adapter_reader.PdfPreviewThumbnailAdapter;
import com.example.documenpro.listener.OnConfirmListener;
import com.example.documenpro.listener.RenameDialogListener;
import com.example.documenpro.listener.ThumbnailClickListener;
import com.example.documenpro.model.PDFModel;
import com.example.documenpro.model.PDFPage;
import com.example.documenpro.ui.customviews.EmptyRecyclerView;
import com.example.documenpro.utils.DialogUtils;
import com.example.documenpro.utils.Utils;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;
import com.tom_roush.pdfbox.pdmodel.common.PDPageLabelRange;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;

public class SplitChooseFileActivity extends AppCompatActivity implements ThumbnailClickListener {
    private AppCompatTextView btnContinue;
    private LottieAnimationView loadingView;
    private Toolbar toolbar;
    private EmptyRecyclerView recyclerView;
    private PdfPreviewThumbnailAdapter adapter;
    private final ArrayList<PDFPage> listPdfPages = new ArrayList<>();
    private String strAllPdfPictureDir;
    private String pdfDirAsFileName;
    PDFModel pdfModel;
    private String fileName;
    private boolean finishLoad = false;

    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_split_choose_file);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initToolBar();
        strAllPdfPictureDir = Environment.getExternalStorageDirectory() + "/Pictures/AllPdf/tmp/";
        initViews();
        Intent intent = getIntent();
        if (intent != null) {
            pdfModel = (PDFModel) intent.getSerializableExtra(GlobalConstant.PDF_MODEL_SEND);
            if (pdfModel != null) {
                fileName = pdfModel.getName();
                String pdfSavedFile = pdfModel.getAbsolutePath();
                Uri uri = Uri.fromFile(new File(pdfSavedFile));
                new LoadThumbnailPdf(this).execute(uri.toString());
            }
        }
    }

    private void initToolBar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(
                getString(R.string.x_selected, String.valueOf(MyApplication.getInstance().getArrayListSplit().size())));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        this.menu = menu;
        inflater.inflate(R.menu.menu_activity_select, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.item_select_all) {
            if (finishLoad) {
                if (adapter.isSelectedAll_PdfPreview) {
                    adapter.setUnSelectedAll_PdfPreview();
                    menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_select_all));
                    activeButton(false);
                } else {
                    adapter.setSelectedAll_PdfPreview();
                    menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_unselect_all));
                    activeButton(true);
                }
                toolbar.setTitle(getString(R.string.x_selected, String.valueOf(adapter.getSelected_PdfPreview().size())));
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void activeButton(boolean b) {
        btnContinue.setEnabled(b);
        btnContinue.setClickable(b);
        btnContinue.setFocusable(b);
    }

    private void initViews() {
        loadingView = findViewById(R.id.loadingView);
        btnContinue = findViewById(R.id.tv_continue);
        recyclerView = findViewById(R.id.chooser_recycler_view);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameFile = "Split-" + System.currentTimeMillis();
                DialogUtils.showRenameDialog(SplitChooseFileActivity.this, nameFile, new RenameDialogListener() {
                    @Override
                    public void onRenameDialog(String newName) {
                        MyApplication.getInstance().setArraylistSplit(adapter.getPageNumbers_PdfPreview());
                        Intent intent = new Intent(SplitChooseFileActivity.this, ProcessingTaskActivity.class);
                        intent.putExtra(GlobalConstant.PDF_FILE_NAME, newName);
                        intent.putExtra(GlobalConstant.TOOL_TYPE, GlobalConstant.TOOL_SPLIT);
                        intent.putExtra(GlobalConstant.PDF_MODEL_SEND, pdfModel);
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
        File dir = new File(this.strAllPdfPictureDir);

        Utils.deletePdfFiles(this.strAllPdfPictureDir);
        String sb = "Deleting temp dir " +
                this.strAllPdfPictureDir;
    }

    @Override
    public void onBackPressed() {

        Utils.showConfirmDialog(this, GlobalConstant.DIALOG_CONFIRM_EXIT_SPLIT, new OnConfirmListener() {
            @Override
            public void onConfirm() {
                finish();
            }

        });
    }

    @Override
    public void onChoosePdfSplit() {
        activeButton(!adapter.getSelected_PdfPreview().isEmpty());
        toolbar.setTitle(getString(R.string.x_selected, String.valueOf(adapter.getSelected_PdfPreview().size())));
        if (adapter.getSelected_PdfPreview().size() == listPdfPages.size()) {
            menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_unselect_all));
            adapter.isSelectedAll_PdfPreview = true;
        } else {
            menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_select_all));
            adapter.isSelectedAll_PdfPreview = false;
        }

    }

    public static class LoadThumbnailPdf extends AsyncTask<String, Void, Void> {
        WeakReference<SplitChooseFileActivity> weakReference;

        public LoadThumbnailPdf(SplitChooseFileActivity activity) {
            this.weakReference = new WeakReference<>(activity);
        }

        @Override
        protected Void doInBackground(String... strings) {
            int i;
            String str;
            FileOutputStream fileOutputStream;
            PdfiumCore pdfiumCore = new PdfiumCore(weakReference.get());
            weakReference.get().pdfDirAsFileName = weakReference.get().fileName;
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
                    String sb3 = weakReference.get().strAllPdfPictureDir +
                            weakReference.get().pdfDirAsFileName;
                    File file = new File(sb3);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    int i2 = 0;
                    while (i2 < pageCount) {
                        int i3 = i2 + 1;
                        String sb5 = weakReference.get().strAllPdfPictureDir +
                                weakReference.get().pdfDirAsFileName +
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
                                        weakReference.get().getString(R.string.toast_failed_low_memory),
                                        Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                                weakReference.get().listPdfPages.add(new PDFPage(i3, Uri.fromFile(new File(str))));
                                fileOutputStream.close();
                            }
                        } catch (OutOfMemoryError e3) {
                            e = e3;
                            fileOutputStream = fileOutputStream2;
                            i = pageCount;
                            str = sb5;
                            Toast.makeText(weakReference.get(),
                                    weakReference.get().getString(R.string.toast_failed_low_memory), Toast.LENGTH_LONG)
                                    .show();
                            e.printStackTrace();
                            weakReference.get().listPdfPages.add(new PDFPage(i3, Uri.fromFile(new File(str))));
                            fileOutputStream.close();
                        }
                        weakReference.get().listPdfPages.add(new PDFPage(i3, Uri.fromFile(new File(str))));
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
                weakReference.get().adapter = new PdfPreviewThumbnailAdapter(weakReference.get(),
                        weakReference.get().listPdfPages, weakReference.get());
                int i = Utils.isTablet(weakReference.get()) ? 6 : 2;
                weakReference.get().recyclerView
                        .setLayoutManager(new GridLayoutManager(weakReference.get(), i, RecyclerView.VERTICAL, false));
                weakReference.get().loadingView.setVisibility(View.GONE);
                weakReference.get().recyclerView.setAdapter(weakReference.get().adapter);
                weakReference.get().finishLoad = true;
            }

        }
    }
    // public static class LoadThumbnailPdf extends AsyncTask<Uri, Void, Void> {
    //
    // WeakReference<SplitChooseFileActivity> weakReference;
    //
    // public LoadThumbnailPdf(SplitChooseFileActivity activity) {
    // this.weakReference = new WeakReference<>(activity);
    // }
    //
    //
    // @Override
    // protected Void doInBackground(Uri... uris) {
    // if (uris.length == 0 || uris[0] == null) {
    // return null;
    // }
    //
    // Uri pdfUri = uris[0];
    // Context context = weakReference.get();
    // if (context == null) {
    // return null;
    // }
    //
    // try {
    // ParcelFileDescriptor parcelFileDescriptor =
    // context.getContentResolver().openFileDescriptor(pdfUri, "r");
    // if (parcelFileDescriptor != null) {
    // PdfRenderer pdfRenderer = new PdfRenderer(parcelFileDescriptor);
    // int pageCount = pdfRenderer.getPageCount();
    // String pdfFileName = new File(pdfUri.getPath()).getName();
    //
    // File outputDir = new File(context.getCacheDir(), "thumbnails");
    // if (!outputDir.exists()) {
    // outputDir.mkdirs();
    // }
    //
    // for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
    // PdfRenderer.Page page = pdfRenderer.openPage(pageIndex);
    //
    // Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(),
    // Bitmap.Config.ARGB_8888);
    // page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
    //
    // File outputFile = new File(outputDir, pdfFileName + "_page_" + (pageIndex +
    // 1) + ".jpg");
    // FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
    // bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
    //
    // weakReference.get().listPdfPages.add(new PDFPage(pageIndex,
    // Uri.fromFile(outputFile)));
    //
    // page.close();
    // bitmap.recycle();
    // fileOutputStream.close();
    // }
    //
    // pdfRenderer.close();
    // parcelFileDescriptor.close();
    // }
    // } catch (IOException e) {
    // e.printStackTrace();
    // Toast.makeText(context, "Failed to load PDF thumbnails",
    // Toast.LENGTH_SHORT).show();
    // }
    //
    // return null;
    // }
    //
    //
    //
    // @Override
    // protected void onPostExecute(Void aVoid) {
    // super.onPostExecute(aVoid);
    // if (weakReference.get() != null) {
    // weakReference.get().adapter = new ThumbnailPdfAdapter(weakReference.get(),
    // weakReference.get().listPdfPages, weakReference.get());
    // int i = Utils.isTablet(weakReference.get()) ? 6 : 2;
    // weakReference.get().recyclerView.setLayoutManager(new
    // GridLayoutManager(weakReference.get(), i, RecyclerView.VERTICAL, false));
    // weakReference.get().loadingView.setVisibility(View.GONE);
    // weakReference.get().recyclerView.setAdapter(weakReference.get().adapter);
    // weakReference.get().finishLoad = true;
    // }
    //
    // // Handle the completion of the AsyncTask as needed (e.g., update UI)
    // }
    // }
}