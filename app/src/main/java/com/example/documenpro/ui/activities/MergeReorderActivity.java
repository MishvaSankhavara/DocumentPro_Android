package com.example.documenpro.ui.activities;

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

import com.example.documenpro.GlobalConstant;
import com.example.documenpro.MyApplication;
import com.example.documenpro.R;
import com.example.documenpro.SharedPreferenceUtils;
import com.example.documenpro.adapter_reader.MergeReorderAdapter;
import com.example.documenpro.docHelper.ItemTouchCallback;
import com.example.documenpro.listener.OnConfirmListener;
import com.example.documenpro.listener.OnStartDragListener;
import com.example.documenpro.model.PDFModel;
import com.example.documenpro.ui.dialog.RenameDialog;
import com.example.documenpro.utils.DialogUtils;

import java.util.ArrayList;
import java.util.Objects;

public class MergeReorderActivity extends AppCompatActivity implements OnStartDragListener {
    private MergeReorderAdapter adapter;
    private ArrayList<PDFModel> arrayList = new ArrayList<>();
    private AppCompatTextView btnMerge;

    private LinearLayout llTip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_merge_reorder);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // NativeAdsAdmob.showNativeBanner1(this,null);

        initToolBar();
        initViews();
    }

    private void initToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
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

        DialogUtils.showConfirmDialog(this, GlobalConstant.DIALOG_CONFIRM_EXIT_MERGE, new OnConfirmListener() {
            @Override
            public void onConfirm() {
                finish();
            }

        });

    }

    private void initViews() {

        llTip = findViewById(R.id.tipsLl);
        ImageView imgClose = findViewById(R.id.tipsCloseIv);

        if (SharedPreferenceUtils.getInstance(this).getBoolean(GlobalConstant.MERGE_ORDER_TIP, false)) {
            llTip.setVisibility(View.GONE);

        }

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llTip.animate().translationY((float) (-llTip.getHeight())).alpha(0.0f)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                llTip.setVisibility(View.GONE);
                                SharedPreferenceUtils.getInstance(MergeReorderActivity.this)
                                        .setBoolean(GlobalConstant.MERGE_ORDER_TIP, true);
                            }
                        });
            }
        });
        RecyclerView recyclerView = findViewById(R.id.merge_recycler_view);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnMerge = findViewById(R.id.tv_continue);
        btnMerge.setEnabled(true);
        btnMerge.setText(getResources().getString(R.string.merge_x,
                String.valueOf(MyApplication.getInstance().getArrayListMerge().size())));
        arrayList = MyApplication.getInstance().getArrayListMerge();

        adapter = new MergeReorderAdapter(this, this, arrayList, (pdfModel, mPosition) -> {
            arrayList.remove(mPosition);
            adapter.notifyItemRemoved(mPosition);
            saveList();
            btnMerge.setText(getString(R.string.merge_x,
                    String.valueOf(MyApplication.getInstance().getArrayListMerge().size())));
            if (MyApplication.getInstance().getArrayListMerge().size() < 2) {
                btnMerge.setEnabled(false);
            }
            if (MyApplication.getInstance().getArrayListMerge().isEmpty()) {
                finish();
            }

        });
        recyclerView.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchCallback(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        btnMerge.setOnClickListener(view -> {
            String sb2 = "Merged" + System.currentTimeMillis();
            RenameDialog dialog = new RenameDialog(MergeReorderActivity.this, sb2, nameFile -> {
                Intent intent = new Intent(MergeReorderActivity.this, ProcessingTaskActivity.class);
                intent.putExtra(GlobalConstant.TOOL_TYPE, GlobalConstant.TOOL_MERGE);
                intent.putExtra(GlobalConstant.MERGE_PDF_FILE_NAME, nameFile);
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

    private void saveList() {
        MyApplication.getInstance().setMergePdfList(arrayList);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {

    }
}