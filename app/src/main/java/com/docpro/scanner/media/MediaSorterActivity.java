package com.docpro.scanner.media;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.example.documenpro.GlobalConstant;
import com.example.documenpro.MyApplication;
import com.example.documenpro.R;
import com.example.documenpro.SharedPreferenceUtils;
import com.example.documenpro.adapter_reader.PhotoSelectionAdapter;
import com.example.documenpro.clickListener.OnConfirmClickListener;
import com.example.documenpro.clickListener.OnRemovePhotoListener;
import com.example.documenpro.clickListener.RenameDialogClickListener;
import com.example.documenpro.model_reader.PhotoModel;
import com.example.documenpro.photopick.Matisse;
import com.docpro.scanner.engine.ProcessingTaskActivity;
import com.example.documenpro.utils.DialogManagerUtils;
import com.example.documenpro.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MediaSorterActivity extends AppCompatActivity {
    private PhotoSelectionAdapter mediaListAdapter;

    private ArrayList<PhotoModel> photoCollection;

    private LinearLayout containerTips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.lyt_sorter_media);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.lay_main_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        photoCollection = new ArrayList<>();
        setupToolbar();
        setupMediaViews();

        Intent incomingIntent = getIntent();
        if (incomingIntent != null) {
            List<String> pathsFound = Matisse.obtainPathResult(incomingIntent);
            for (int i = 0; i < pathsFound.size(); i++) {
                photoCollection.add(new PhotoModel(pathsFound.get(i), i));
            }
        }
    }

    private void setupToolbar() {
        Toolbar headerToolbar = findViewById(R.id.toolbar_media_sorter);
        setSupportActionBar(headerToolbar);
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
        Utils.showConfirmDialog(this, GlobalConstant.DIALOG_CONFIRM_EXIT_PHOTO_2_PDF, new OnConfirmClickListener() {
            @Override
            public void onConfirmClickListener() {
                finish();
            }

        });
    }

    private void setupMediaViews() {

        containerTips = findViewById(R.id.container_sorting_tips);

        if (SharedPreferenceUtils.getInstance(this).getBoolean(GlobalConstant.PHOTO_ORDER_TIP, false)) {
            containerTips.setVisibility(View.GONE);
        }

        findViewById(R.id.btn_close_tips).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                containerTips.animate().translationY((float) (-containerTips.getHeight())).alpha(0.0f)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                containerTips.setVisibility(View.GONE);
                                SharedPreferenceUtils.getInstance(MediaSorterActivity.this)
                                        .setBoolean(GlobalConstant.PHOTO_ORDER_TIP, true);
                            }
                        });
            }
        });

        RecyclerView mediaRecyclerView = findViewById(R.id.rv_media_gallery);
        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false);
        RecyclerViewDragDropManager dragDropManager = new RecyclerViewDragDropManager();

        dragDropManager.setInitiateOnLongPress(true);
        dragDropManager.setInitiateOnMove(false);
        dragDropManager.setLongPressTimeout(750);

        dragDropManager.setDragStartItemAnimationDuration(250);
        dragDropManager.setDraggingItemAlpha(0.8f);
        dragDropManager.setDraggingItemScale(1.3f);
        dragDropManager.setDraggingItemRotation(15.0f);

        final PhotoSelectionAdapter internalAdapter = new PhotoSelectionAdapter(this, photoCollection, new OnRemovePhotoListener() {
            @Override
            public void onRemoveListener(int position) {
                photoCollection.remove(position);
                mediaListAdapter.notifyItemRemoved(position);
                mediaListAdapter.notifyDataSetChanged();

                if (photoCollection.isEmpty()) {
                    finish();
                }
            }
        });

        mediaListAdapter = internalAdapter;
        RecyclerView.Adapter wrappedMediaAdapter = dragDropManager.createWrappedAdapter(internalAdapter);
        GeneralItemAnimator mediaAnimator = new DraggableItemAnimator();

        mediaRecyclerView.setLayoutManager(gridLayoutManager);
        mediaRecyclerView.setAdapter(wrappedMediaAdapter);
        mediaRecyclerView.setItemAnimator(mediaAnimator);

        dragDropManager.attachRecyclerView(mediaRecyclerView);

        AppCompatTextView btnExecuteConvert = findViewById(R.id.btn_proceed_convert);
        btnExecuteConvert.setEnabled(true);

        btnExecuteConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> mediaPaths = new ArrayList<>();
                for (int i = 0; i < photoCollection.size(); i++) {
                    mediaPaths.add(photoCollection.get(i).getFilePath_PhotoModel());
                }

                String defaultFileName = "Photo2PDF" + System.currentTimeMillis();
                DialogManagerUtils.showRenameDialog(MediaSorterActivity.this, defaultFileName, new RenameDialogClickListener() {
                    @Override
                    public void onRenameDialogListener(String newChosenName) {
                        String finalOutName = newChosenName + System.currentTimeMillis();
                        MyApplication.getInstance().setSelectedImages(mediaPaths);
                        Intent taskIntent = new Intent(MediaSorterActivity.this, ProcessingTaskActivity.class);
                        taskIntent.putExtra(GlobalConstant.TOOL_TYPE, GlobalConstant.TOOL_PHOTO_TO_PDF);
                        taskIntent.putExtra(GlobalConstant.PHOTO_2_PDF_FILE_NAME, finalOutName);
                        startActivity(taskIntent);
                        finish();
                    }

                });
            }
        });
    }
}
