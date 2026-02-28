package com.example.documenpro.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.documenpro.GlobalConstant;
import com.example.documenpro.R;
import com.example.documenpro.adapter_reader.PdfPreviewThumbnailAdapter;
import com.example.documenpro.clickListener.OnThumbnailClickListener;
import com.example.documenpro.model_reader.PDFReaderModel;
import com.example.documenpro.model_reader.PDFPageModel;
import com.example.documenpro.ui.customviews.EmptyStateRecyclerView;
import com.example.documenpro.ui.dialog.PdfToImageConvertDialog;
import com.example.documenpro.utils.Utils;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;
import com.tom_roush.pdfbox.pdmodel.common.PDPageLabelRange;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;

public class SharePdfAsImageActivity extends AppCompatActivity implements OnThumbnailClickListener {
    private AppCompatTextView continueActionText;
    private LottieAnimationView loadingAnimationView;
    private Toolbar topToolbar;
    private EmptyStateRecyclerView pdfPageRecyclerView;
    private PdfPreviewThumbnailAdapter thumbnailAdapter;
    private final ArrayList<PDFPageModel> pdfPageList = new ArrayList<>();

    private String tempPdfImageDirectory;
    private String pdfFolderName;
    PDFReaderModel selectedPdfModel;
    private String pdfFileName;
    private Context context;
    private boolean isLoadCompleted = false;
    private int selectedToolType;
    private Menu optionsMenu;
    private final String logTag = SharePdfAsImageActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_share_image);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setupToolbar();
        tempPdfImageDirectory = Environment.getExternalStorageDirectory() + "/Pictures/AllPdf/tmp/";
        context = this;
        initializeViews();
        Intent intent = getIntent();
        if (intent != null) {
            selectedPdfModel = (PDFReaderModel) intent.getSerializableExtra(GlobalConstant.PDF_MODEL_SEND);

            selectedToolType = intent.getIntExtra(GlobalConstant.TOOL_TYPE, 1);
            if (selectedToolType == GlobalConstant.TOOL_SHARE_PDF_AS_PHOTO) {
                continueActionText.setText(R.string.str_action_share);
            } else if (selectedToolType == GlobalConstant.TOOL_PDF_TO_PHOTO) {
                continueActionText.setText(R.string.tool_tittle_pdf_to_image);
            }
            pdfFileName = selectedPdfModel.getName_PDFModel();
            String pdfSavedFile = selectedPdfModel.getAbsolutePath_PDFModel();
            Uri uri = Uri.fromFile(new File(pdfSavedFile));
            new LoadPdfThumbnailsTask(this).execute(uri.toString());

        }


    }

    private void initializeViews() {
        loadingAnimationView = findViewById(R.id.loadingView);
        continueActionText = findViewById(R.id.tv_continue);
        pdfPageRecyclerView = findViewById(R.id.chooser_recycler_view);
        continueActionText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedToolType == GlobalConstant.TOOL_SHARE_PDF_AS_PHOTO) {
                    if (thumbnailAdapter.getSelected_PdfPreview().size() < 50) {
                        ArrayList<Uri> arrayList = new ArrayList<>();
                        for (int i = 0; i < thumbnailAdapter.getSelected_PdfPreview().size(); i++) {
                            arrayList.add(FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", new File(Objects.requireNonNull(thumbnailAdapter.getSelected_PdfPreview().get(i).getThumbnailUri_PDFPageModel().getPath()))));
                        }
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.str_share_via_photo));
                        intent.setType("image/jpeg");
                        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, arrayList);
                        startActivity(intent);
                    } else {
                        Toast.makeText(context, getString(R.string.toast_maximum_file_share), Toast.LENGTH_SHORT).show();
                    }
                } else if (selectedToolType == GlobalConstant.TOOL_PDF_TO_PHOTO) {

                    PdfToImageConvertDialog dialog = new PdfToImageConvertDialog(SharePdfAsImageActivity.this, thumbnailAdapter.getSelected_PdfPreview());
                    Window window4 = dialog.getWindow();
                    assert window4 != null;
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    window4.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.show();

                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.item_select_all) {
            if (isLoadCompleted) {
                if (thumbnailAdapter.isSelectedAll_PdfPreview) {
                    thumbnailAdapter.setUnSelectedAll_PdfPreview();
                    optionsMenu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_select_all));
                    activeButtonEnabled(false);
                } else {
                    thumbnailAdapter.setSelectedAll_PdfPreview();
                    optionsMenu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_unselect_all));
                    activeButtonEnabled(true);
                }
                topToolbar.setTitle(getString(R.string.x_selected, String.valueOf(thumbnailAdapter.getSelected_PdfPreview().size())));
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        File dir = new File(this.tempPdfImageDirectory);

        Utils.deletePdfFiles(this.tempPdfImageDirectory);
        String sb = "Deleting temp dir " +
                this.tempPdfImageDirectory;
        Log.d(this.logTag, sb);
    }

    private void setupToolbar() {
        topToolbar = findViewById(R.id.toolbar);

        topToolbar.setTitle(getString(R.string.x_selected, "0"));
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

        inflater.inflate(R.menu.menu_activity_select, menu);
        return true;
    }

    private void updateContinueButtonState() {
        activeButtonEnabled(!thumbnailAdapter.getSelected_PdfPreview().isEmpty());
    }

    private void activeButtonEnabled(boolean b) {
        continueActionText.setEnabled(b);
        continueActionText.setClickable(b);
        continueActionText.setFocusable(b);
    }

    @Override
    public void onChoosePdfSplitListener() {
        updateContinueButtonState();
        topToolbar.setTitle(getString(R.string.x_selected, String.valueOf(thumbnailAdapter.getSelected_PdfPreview().size())));
        if (thumbnailAdapter.getSelected_PdfPreview().size() == pdfPageList.size()) {
            optionsMenu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_unselect_all));
            thumbnailAdapter.isSelectedAll_PdfPreview = true;
        } else {
            optionsMenu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_select_all));
            thumbnailAdapter.isSelectedAll_PdfPreview = false;
        }
    }

    public static class LoadPdfThumbnailsTask extends AsyncTask<String, Void, Void> {
        WeakReference<SharePdfAsImageActivity> weakReference;

        public LoadPdfThumbnailsTask(SharePdfAsImageActivity activity) {
            this.weakReference = new WeakReference<>(activity);
        }

        @Override
        protected Void doInBackground(String... strings) {
            int i;
            String str;
            FileOutputStream fileOutputStream;
            PdfiumCore pdfiumCore = new PdfiumCore(weakReference.get());
            weakReference.get().pdfFolderName = weakReference.get().pdfFileName;
            Uri parse = Uri.parse(strings[0]);
            String sb = "Loading page thumbs from uri " +
                    parse.toString();
            Log.d(weakReference.get().logTag, sb);
            try {
                if (!isCancelled()) {

                    PdfDocument newDocument = pdfiumCore.newDocument(weakReference.get().getContentResolver().openFileDescriptor(parse, PDPageLabelRange.STYLE_ROMAN_LOWER));
                    int pageCount = pdfiumCore.getPageCount(newDocument);
                    String sb2 = "Total number of pages " +
                            pageCount;
                    Log.d(weakReference.get().logTag, sb2);
                    String sb3 = weakReference.get().tempPdfImageDirectory +
                            weakReference.get().pdfFolderName;
                    File file = new File(sb3);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    int i2 = 0;
                    while (i2 < pageCount) {
                        int i3 = i2 + 1;
                        String sb5 = weakReference.get().tempPdfImageDirectory +
                                weakReference.get().pdfFolderName +
                                "page-" +
                                i3 +
                                ".jpg";
                        String sb6 = "Generating temp share img " +
                                sb5;
                        Log.d(weakReference.get().logTag, sb6);
                        FileOutputStream fileOutputStream2 = new FileOutputStream(sb5);
                        pdfiumCore.openPage(newDocument, i2);
                        int pageWidthPoint = pdfiumCore.getPageWidthPoint(newDocument, i2);
                        int pageHeightPoint = pdfiumCore.getPageHeightPoint(newDocument, i2);
                        OutOfMemoryError e;
                        try {
                            Bitmap createBitmap = Bitmap.createBitmap(pageWidthPoint, pageHeightPoint, Bitmap.Config.ARGB_8888);
                            i = pageCount;
                            str = sb5;
                            try {
                                pdfiumCore.renderPageBitmap(newDocument, createBitmap, i2, 0, 0, pageWidthPoint, pageHeightPoint, true);
                                fileOutputStream = fileOutputStream2;
                                try {
                                    createBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                                } catch (OutOfMemoryError ignored) {
                                }
                            } catch (OutOfMemoryError e2) {
                                e = e2;
                                fileOutputStream = fileOutputStream2;
                                Toast.makeText(weakReference.get(), weakReference.get().getString(R.string.toast_failed_low_memory), Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                                weakReference.get().pdfPageList.add(new PDFPageModel(i3, Uri.fromFile(new File(str))));
                                fileOutputStream.close();
                            }
                        } catch (OutOfMemoryError e3) {
                            e = e3;
                            fileOutputStream = fileOutputStream2;
                            i = pageCount;
                            str = sb5;
                            Toast.makeText(weakReference.get(), weakReference.get().getString(R.string.toast_failed_low_memory), Toast.LENGTH_LONG).show();
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
                weakReference.get().thumbnailAdapter = new PdfPreviewThumbnailAdapter(weakReference.get(), weakReference.get().pdfPageList, weakReference.get());
                int i = Utils.isTablet(weakReference.get()) ? 6 : 3;
                weakReference.get().pdfPageRecyclerView.setLayoutManager(new GridLayoutManager(weakReference.get(), i, RecyclerView.VERTICAL, false));
                weakReference.get().loadingAnimationView.setVisibility(View.GONE);
                weakReference.get().pdfPageRecyclerView.setAdapter(weakReference.get().thumbnailAdapter);
                weakReference.get().isLoadCompleted = true;
            }
        }
    }
}