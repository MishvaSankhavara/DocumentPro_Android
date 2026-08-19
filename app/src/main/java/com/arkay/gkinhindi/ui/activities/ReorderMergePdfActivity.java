package com.arkay.gkinhindi.ui.activities;

import com.arkay.gkinhindi.BuildConfig;
import com.arkay.gkinhindi.utils.AdsUtils;
import com.docpro.scanner.engine.ProcessingTaskActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arkay.gkinhindi.Constants;
import com.arkay.gkinhindi.MyApplication;
import com.arkay.gkinhindi.R;
import com.arkay.gkinhindi.PreferenceUtils;
import com.arkay.gkinhindi.adapter_reader.MergeReorderAdapter;
import com.arkay.gkinhindi.docHelper.ItemTouchCallback;
import com.arkay.gkinhindi.clickListener.OnConfirmClickListener;
import com.arkay.gkinhindi.clickListener.OnDragStartListener;
import com.arkay.gkinhindi.model_reader.PDFReaderModel;
import com.arkay.gkinhindi.ui.dialog.FileRenameDialog;
import com.arkay.gkinhindi.utils.DialogManagerUtils;

import java.util.ArrayList;
import java.util.Objects;

public class ReorderMergePdfActivity extends AppCompatActivity implements OnDragStartListener {
    private MergeReorderAdapter mergeReorderAdapter;
    private ArrayList<PDFReaderModel> mergePdfList = new ArrayList<>();
    private AppCompatTextView mergeButtonText;

    private LinearLayout tipsLayout;
    private android.widget.ImageView ivBack;
    private android.widget.TextView tvToolbarName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.act_merge_reorder);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initToolBar();
        initViews();
    }

    private void initToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar_merge_reorder);
        ivBack = findViewById(R.id.iv_back);
        tvToolbarName = findViewById(R.id.tv_name);

        ivBack.setOnClickListener(v -> onBackPressed());

        tvToolbarName.setText(R.string.tool_tittle_merge);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        DialogManagerUtils.showConfirmationDialog(this, Constants.DIALOG_CONFIRM_EXIT_MERGE,
                new OnConfirmClickListener() {
                    @Override
                    public void onConfirmClickListener() {
                        finish();
                    }

                });

    }

    private void initViews() {

        tipsLayout = findViewById(R.id.tipsLl);
        ImageView closeTipsImageView = findViewById(R.id.tipsCloseIv);

        if (PreferenceUtils.getInstance(this).getBoolean(Constants.MERGE_ORDER_TIP, false)) {
            tipsLayout.setVisibility(View.GONE);

        }

        closeTipsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tipsLayout.animate().translationY((float) (-tipsLayout.getHeight())).alpha(0.0f)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                tipsLayout.setVisibility(View.GONE);
                                PreferenceUtils.getInstance(ReorderMergePdfActivity.this)
                                        .setBoolean(Constants.MERGE_ORDER_TIP, true);
                            }
                        });
            }
        });
        RecyclerView recyclerView = findViewById(R.id.merge_recycler_view);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mergeButtonText = findViewById(R.id.tv_continue);
        mergeButtonText.setEnabled(true);
        mergeButtonText.setText(getResources().getString(R.string.action_merge_count,
                String.valueOf(MyApplication.getInstance().getMergedPdfList().size())));
        mergePdfList = MyApplication.getInstance().getMergedPdfList();

        mergeReorderAdapter = new MergeReorderAdapter(this, this, mergePdfList, (pdfModel, mPosition) -> {
            mergePdfList.remove(mPosition);
            mergeReorderAdapter.notifyItemRemoved(mPosition);
            saveMergePdfList();
            mergeButtonText.setText(getString(R.string.action_merge_count,
                    String.valueOf(MyApplication.getInstance().getMergedPdfList().size())));
            if (MyApplication.getInstance().getMergedPdfList().size() < 2) {
                mergeButtonText.setEnabled(false);
            }
            if (MyApplication.getInstance().getMergedPdfList().isEmpty()) {
                finish();
            }

        });
        recyclerView.setAdapter(mergeReorderAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchCallback(mergeReorderAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        mergeButtonText.setOnClickListener(view -> {
                AdsUtils.showInterstitialAdFunction(
                    ReorderMergePdfActivity.this,
                    BuildConfig.interstitial_function_2ID,
                    BuildConfig.interstitial_function,
                    Constants.interstitial_function_2ID,
                    Constants.interstitial_function,
                    () -> {
                        String sb2 = "Merged" + System.currentTimeMillis();
                        FileRenameDialog dialog = new FileRenameDialog(ReorderMergePdfActivity.this, sb2, nameFile -> {
                            Intent intent = new Intent(ReorderMergePdfActivity.this, ProcessingTaskActivity.class);
                            intent.putExtra(Constants.EXTRA_TOOL_TYPE, Constants.TOOL_ID_MERGE);
                            intent.putExtra(Constants.MERGE_PDF_FILE, nameFile);
                            startActivity(intent);
                            finish();
                        });
                        Window window2 = dialog.getWindow();
                        assert window2 != null;
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        window2.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialog.show();
                    }
            );
        });
    }

    private void saveMergePdfList() {
        MyApplication.getInstance().updateMergedPdfList(mergePdfList);
    }

    @Override
    public void onDragStart(RecyclerView.ViewHolder viewHolder) {

    }
}