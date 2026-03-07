package com.docpro.scanner.selector;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.example.documenpro.AppGlobalConstants;
import com.example.documenpro.R;
import com.example.documenpro.adapter_reader.FileSelectionAdapter;
import com.example.documenpro.docHelper.PdfPrintUtils;
import com.example.documenpro.clickListener.PdfSelectionListener;
import com.example.documenpro.clickListener.PasswordClickListener;
import com.example.documenpro.model_reader.PDFReaderModel;
import com.example.documenpro.ui.activities.SharePdfAsImageActivity;
import com.example.documenpro.ui.activities.SplitChooseFileActivity;
import com.docpro.scanner.engine.ProcessingTaskActivity;
import com.example.documenpro.ui.customviews.EmptyStateRecyclerView;
import com.example.documenpro.utils.AdUtils;
import com.example.documenpro.utils.DialogManagerUtils;
import com.example.documenpro.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DocPickerActivity extends AppCompatActivity implements PdfSelectionListener {
    private EmptyStateRecyclerView rvPicker;
    private FileSelectionAdapter pickerAdapter;
    private LottieAnimationView lottieLoader;
    private ArrayList<PDFReaderModel> docList;
    private int operationMode;
    private int selectedPosition;
    private PDFReaderModel selectedDoc;
    private Context baseContextWrapper;
    private final Executor asyncExecutor = Executors.newSingleThreadExecutor();
    private FrameLayout adFrame;
    private AdView bannerAdView;

    @Override
    protected void attachBaseContext(Context newBase) {
        baseContextWrapper = newBase;
        super.attachBaseContext(newBase);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.layout_picker_doc);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.lay_picker_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        adFrame = findViewById(R.id.frame_banner_ads);
        initBannerAds();
        configureToolbar();
        setupPickerViews();
        extractIntentData();
        fetchFilesAsync();
    }

    private void extractIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            operationMode = intent.getIntExtra(AppGlobalConstants.EXTRA_TOOL_TYPE, 1);
        }
    }

    private void configureToolbar() {
        Toolbar topToolbar = findViewById(R.id.toolbar_doc_picker);
        setSupportActionBar(topToolbar);
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

    private void setupPickerViews() {
        lottieLoader = findViewById(R.id.lottie_loading_picker);
        rvPicker = findViewById(R.id.rv_document_picker);
        rvPicker.setHasFixedSize(true);
        LinearLayoutManager pickerLayoutManager = new LinearLayoutManager(this);
        rvPicker.setLayoutManager(pickerLayoutManager);
        rvPicker.setEmptyView(findViewById(R.id.txt_empty_doc_label));
        docList = new ArrayList<>();
    }

    private void fetchFilesAsync() {
        asyncExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (operationMode == AppGlobalConstants.TOOL_ID_UNLOCK_PDF) {
                    docList = Utils.getLockPDF(DocPickerActivity.this);
                } else {
                    docList = Utils.getUnLockPDF(DocPickerActivity.this);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pickerAdapter = new FileSelectionAdapter(DocPickerActivity.this, docList, DocPickerActivity.this);
                        rvPicker.setAdapter(pickerAdapter);
                        lottieLoader.setVisibility(View.GONE);
                        rvPicker.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    @Override
    public void onPdfSelect(PDFReaderModel pdfModel_listener, int position) {
        this.selectedPosition = position;
        this.selectedDoc = pdfModel_listener;

        if (operationMode == AppGlobalConstants.TOOL_ID_PRINT) {
            if (pdfModel_listener.isProtected_PDFModel()) {
                DialogManagerUtils.showProtectedFileDialog(DocPickerActivity.this, R.string.unable_print_toast);
            } else {
                PdfPrintUtils.printPdf_Utils(this, Uri.fromFile(new File(pdfModel_listener.getAbsolutePath_PDFModel())));
            }
        } else if (operationMode == AppGlobalConstants.TOOL_ID_COMPRESS) {
            if (pdfModel_listener.isProtected_PDFModel()) {
                DialogManagerUtils.showProtectedFileDialog(DocPickerActivity.this, R.string.unable_compress_toast);
            } else {
                Intent taskIntent = new Intent(this, ProcessingTaskActivity.class);
                taskIntent.putExtra(AppGlobalConstants.EXTRA_TOOL_TYPE, AppGlobalConstants.TOOL_ID_COMPRESS);
                taskIntent.putExtra(AppGlobalConstants.EXTRA_PDF_MODEL, pdfModel_listener);
                startActivity(taskIntent);
                finish();
            }
        } else if (operationMode == AppGlobalConstants.TOOL_ID_SPLIT) {
            if (pdfModel_listener.isProtected_PDFModel()) {
                DialogManagerUtils.showProtectedFileDialog(DocPickerActivity.this, R.string.unable_split_toast);
            } else {
                Intent splitIntent = new Intent(this, SplitChooseFileActivity.class);
                splitIntent.putExtra(AppGlobalConstants.EXTRA_PDF_MODEL, pdfModel_listener);
                startActivity(splitIntent);
                finish();
            }
        } else if (operationMode == AppGlobalConstants.TOOL_PDF_TO_PHOTO) {
            if (pdfModel_listener.isProtected_PDFModel()) {
                DialogManagerUtils.showProtectedFileDialog(DocPickerActivity.this, R.string.unable_convert_toast);
            } else {
                Intent convertIntent = new Intent(this, SharePdfAsImageActivity.class);
                convertIntent.putExtra(AppGlobalConstants.EXTRA_TOOL_TYPE, AppGlobalConstants.TOOL_PDF_TO_PHOTO);
                convertIntent.putExtra(AppGlobalConstants.EXTRA_PDF_MODEL, pdfModel_listener);
                startActivity(convertIntent);
                finish();
            }
        } else if (operationMode == AppGlobalConstants.TOOL_ID_LOCK_PDF) {
            if (pdfModel_listener.isProtected_PDFModel()) {
                DialogManagerUtils.showProtectedFileDialog(DocPickerActivity.this, R.string.unable_convert_toast);
            } else {
                DialogManagerUtils.showPasswordSetup(this, new PasswordClickListener() {
                    @Override
                    public void onOkClickListener(String pass) {
                        Intent taskIntent = new Intent(DocPickerActivity.this, ProcessingTaskActivity.class);
                        taskIntent.putExtra(AppGlobalConstants.EXTRA_TOOL_TYPE, AppGlobalConstants.TOOL_ID_LOCK_PDF);
                        taskIntent.putExtra(AppGlobalConstants.EXTRA_PDF_MODEL, pdfModel_listener);
                        taskIntent.putExtra(AppGlobalConstants.PDF_SET_PASSWORD, pass);
                        startActivity(taskIntent);
                        finish();
                    }

                });
            }
        } else if (operationMode == AppGlobalConstants.TOOL_ID_UNLOCK_PDF) {
            DialogManagerUtils.showPdfPasswordRemoval(this, pdfModel_listener, new PasswordClickListener() {
                @Override
                public void onOkClickListener(String pass) {
                    Intent taskIntent = new Intent(DocPickerActivity.this, ProcessingTaskActivity.class);
                    taskIntent.putExtra(AppGlobalConstants.EXTRA_TOOL_TYPE, AppGlobalConstants.TOOL_ID_UNLOCK_PDF);
                    taskIntent.putExtra(AppGlobalConstants.EXTRA_PDF_MODEL, pdfModel_listener);
                    taskIntent.putExtra(AppGlobalConstants.PDF_SET_PASSWORD, pass);
                    startActivity(taskIntent);
                    finish();
                }

            });
        }
    }

    private void initBannerAds() {
        bannerAdView = new AdView(this);
        bannerAdView.setAdUnitId(AdUtils.AD_UNIT_ID_BANNER_TEST);

        AdSize optimizedSize = AdUtils.calculateAdaptiveAdSize(DocPickerActivity.this, adFrame);
        bannerAdView.setAdSize(optimizedSize);

        Bundle adExtras = new Bundle();
        adExtras.putString("collapsible", "bottom");
        AdRequest bannerRequest = new AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter.class, adExtras)
                .build();
        bannerAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adFrame.removeAllViews();
                adFrame.addView(bannerAdView);
            }
        });

        bannerAdView.loadAd(bannerRequest);
    }
}
