package com.example.documenpro.AppExecutor;

import android.graphics.drawable.ColorDrawable;
import android.os.SystemClock;
import android.view.ViewGroup;

import com.example.documenpro.R;
import com.example.documenpro.adapter_reader.FilePickerAdapter;
import com.example.documenpro.database.DatabaseHelper;
import com.example.documenpro.model_reader.DocumentModel;
import com.example.documenpro.ui.activities.SelectDocumentActivity;
import com.example.documenpro.ui.dialog.FileProgressDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FavoriteRemovalExecutor {

    private final Executor executor_removeFav = Executors.newSingleThreadExecutor();
    private FileProgressDialog dialog_removeFav;
    private final WeakReference<SelectDocumentActivity> weakReference_removeFav;

    private void updateUI_removeFav() {
        weakReference_removeFav.get().documentList = DatabaseHelper.getInstance(weakReference_removeFav.get()).getStarredDocuments_DatabaseHelper();

        if (weakReference_removeFav.get().documentList.size() == 0) {
            weakReference_removeFav.get().finish();
        } else {
            weakReference_removeFav.get().filePickerAdapter = new FilePickerAdapter(weakReference_removeFav.get(), weakReference_removeFav.get().documentList, weakReference_removeFav.get());

            weakReference_removeFav.get().documentRecyclerView.setAdapter(weakReference_removeFav.get().filePickerAdapter);

            weakReference_removeFav.get().updateActionButtonsState(weakReference_removeFav.get().filePickerAdapter.getSelected_FilePicker().size() > 0);

            weakReference_removeFav.get().topToolbar.setTitle(weakReference_removeFav.get().getString(R.string.x_selected, String.valueOf(weakReference_removeFav.get().filePickerAdapter.getSelected_FilePicker().size())));
        }
    }

    private void dismissProgressDialog_removeFav() {
        if (dialog_removeFav != null && dialog_removeFav.isShowing()) {
            dialog_removeFav.dismiss();
        }
    }

    private void updateProgress_removeFav(int progress_removeFav) {
        if (dialog_removeFav != null && dialog_removeFav.isShowing()) {
            dialog_removeFav.updateProgress(progress_removeFav);
            dialog_removeFav.setPercentText(progress_removeFav + "%");
        }
    }

    private void showProgressDialog_removeFav() {
        dialog_removeFav = new FileProgressDialog(weakReference_removeFav.get());
        dialog_removeFav.setTitleText(weakReference_removeFav.get().getString(R.string.str_action_removing));

        if (dialog_removeFav.getWindow() != null) {
            dialog_removeFav.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog_removeFav.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        dialog_removeFav.setCancelable(false);
        dialog_removeFav.show();
    }

    public void executeTask_removeFav() {
        executor_removeFav.execute(() -> {

            weakReference_removeFav.get().runOnUiThread(this::showProgressDialog_removeFav);

            ArrayList<DocumentModel> arrayListRemove_removeFav = weakReference_removeFav.get().filePickerAdapter.getSelected_FilePicker();

            for (int i = 0; i < arrayListRemove_removeFav.size(); i++) {
                DocumentModel pdfModel_removeFav = arrayListRemove_removeFav.get(i);

                if (DatabaseHelper.getInstance(weakReference_removeFav.get()).isStared_DatabaseHelper(pdfModel_removeFav.getFileUri_DocModel())) {

                    DatabaseHelper.getInstance(weakReference_removeFav.get()).removeStaredDocument_DatabaseHelper(pdfModel_removeFav.getFileUri_DocModel());
                }
            }

            for (int i = 0; i < 100; i++) {
                SystemClock.sleep(10);
                final int progress = i;

                weakReference_removeFav.get().runOnUiThread(() -> updateProgress_removeFav(progress));
            }

            weakReference_removeFav.get().runOnUiThread(() -> {
                dismissProgressDialog_removeFav();
                updateUI_removeFav();
            });
        });
    }

    public FavoriteRemovalExecutor(SelectDocumentActivity activity_removeFav) {
        this.weakReference_removeFav = new WeakReference<>(activity_removeFav);
    }
}