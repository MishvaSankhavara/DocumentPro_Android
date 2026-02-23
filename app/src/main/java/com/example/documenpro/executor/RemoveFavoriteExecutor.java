package com.example.documenpro.executor;

import android.graphics.drawable.ColorDrawable;
import android.os.SystemClock;
import android.view.ViewGroup;

import com.example.documenpro.R;
import com.example.documenpro.adapter.SelectFileAdapter;
import com.example.documenpro.db.DbHelper;
import com.example.documenpro.model.Document;
import com.example.documenpro.ui.activities.SelectActivity;
import com.example.documenpro.ui.dialog.ProgressDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RemoveFavoriteExecutor {
    private final WeakReference<SelectActivity> weakReference;
    private ProgressDialog dialog;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public RemoveFavoriteExecutor(SelectActivity activity) {
        this.weakReference = new WeakReference<>(activity);
    }

    public void executeTask() {
        executor.execute(() -> {
            weakReference.get().runOnUiThread(this::showProgressDialog);
            ArrayList<Document> arrayListRemove = weakReference.get().adapter.getSelected();
            for (int i = 0; i < arrayListRemove.size(); i++) {
                Document pdfModel = arrayListRemove.get(i);
                if (DbHelper.getInstance(weakReference.get()).isStared(pdfModel.getFileUri())) {
                    DbHelper.getInstance(weakReference.get()).removeStaredDocument(pdfModel.getFileUri());
                }
            }

            for (int i = 0; i < 100; i++) {
                SystemClock.sleep(10);
                final int progress = i;

                weakReference.get().runOnUiThread(() -> updateProgress(progress));
            }

            weakReference.get().runOnUiThread(() -> {
                dismissProgressDialog();
                updateUI();
            });
        });
    }

    private void showProgressDialog() {
        dialog = new ProgressDialog(weakReference.get());
        dialog.setTvTittle(weakReference.get().getString(R.string.str_action_removing));
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        }
        dialog.setCancelable(false);
        dialog.show();
    }

    private void updateProgress(int progress) {
        if (dialog != null && dialog.isShowing()) {
            dialog.setProgress(progress);
            dialog.setTvPercent(progress + "%");
        }
    }

    private void dismissProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void updateUI() {
        weakReference.get().arrayList = DbHelper.getInstance(weakReference.get()).getStarredDocuments();

        if (weakReference.get().arrayList.size() == 0) {
            weakReference.get().finish();
        } else {
            weakReference.get().adapter = new SelectFileAdapter(weakReference.get(), weakReference.get().arrayList, weakReference.get());
            weakReference.get().recyclerView.setAdapter(weakReference.get().adapter);
            weakReference.get().activeButton(weakReference.get().adapter.getSelected().size() > 0);
            weakReference.get().toolbar.setTitle(weakReference.get().getString(R.string.x_selected, String.valueOf(weakReference.get().adapter.getSelected().size())));
        }
    }
}
