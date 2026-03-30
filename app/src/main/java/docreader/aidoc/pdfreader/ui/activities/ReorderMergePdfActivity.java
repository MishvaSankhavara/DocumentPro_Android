package docreader.aidoc.pdfreader.ui.activities;

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

import docreader.aidoc.pdfreader.AppGlobalConstants;
import docreader.aidoc.pdfreader.DocumentMyApplication;
import docreader.aidoc.pdfreader.R;
import docreader.aidoc.pdfreader.PreferenceUtils;
import docreader.aidoc.pdfreader.adapter_reader.MergeReorderAdapter;
import docreader.aidoc.pdfreader.docHelper.ItemTouchCallback;
import docreader.aidoc.pdfreader.clickListener.OnConfirmClickListener;
import docreader.aidoc.pdfreader.clickListener.OnDragStartListener;
import docreader.aidoc.pdfreader.model_reader.PDFReaderModel;
import docreader.aidoc.pdfreader.ui.dialog.FileRenameDialog;
import docreader.aidoc.pdfreader.utils.DialogManagerUtils;

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

        DialogManagerUtils.showConfirmationDialog(this, AppGlobalConstants.DIALOG_CONFIRM_EXIT_MERGE,
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

        if (PreferenceUtils.getInstance(this).getBoolean(AppGlobalConstants.MERGE_ORDER_TIP, false)) {
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
                                        .setBoolean(AppGlobalConstants.MERGE_ORDER_TIP, true);
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
                String.valueOf(DocumentMyApplication.getInstance().getMergedPdfList().size())));
        mergePdfList = DocumentMyApplication.getInstance().getMergedPdfList();

        mergeReorderAdapter = new MergeReorderAdapter(this, this, mergePdfList, (pdfModel, mPosition) -> {
            mergePdfList.remove(mPosition);
            mergeReorderAdapter.notifyItemRemoved(mPosition);
            saveMergePdfList();
            mergeButtonText.setText(getString(R.string.action_merge_count,
                    String.valueOf(DocumentMyApplication.getInstance().getMergedPdfList().size())));
            if (DocumentMyApplication.getInstance().getMergedPdfList().size() < 2) {
                mergeButtonText.setEnabled(false);
            }
            if (DocumentMyApplication.getInstance().getMergedPdfList().isEmpty()) {
                finish();
            }

        });
        recyclerView.setAdapter(mergeReorderAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchCallback(mergeReorderAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        mergeButtonText.setOnClickListener(view -> {
            String sb2 = "Merged" + System.currentTimeMillis();
            FileRenameDialog dialog = new FileRenameDialog(ReorderMergePdfActivity.this, sb2, nameFile -> {
                Intent intent = new Intent(ReorderMergePdfActivity.this, ProcessingTaskActivity.class);
                intent.putExtra(AppGlobalConstants.EXTRA_TOOL_TYPE, AppGlobalConstants.TOOL_ID_MERGE);
                intent.putExtra(AppGlobalConstants.MERGE_PDF_FILE, nameFile);
                startActivity(intent);
                finish();
            });
            Window window2 = dialog.getWindow();
            assert window2 != null;
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            window2.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.show();

        });
    }

    private void saveMergePdfList() {
        DocumentMyApplication.getInstance().updateMergedPdfList(mergePdfList);
    }

    @Override
    public void onDragStart(RecyclerView.ViewHolder viewHolder) {

    }
}