package com.example.documenpro.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.text.format.Formatter;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import com.example.documenpro.GlobalConstant;
import com.example.documenpro.R;
import com.example.documenpro.clickListener.MoreClickListener;
import com.example.documenpro.clickListener.OnConfirmClickListener;
import com.example.documenpro.clickListener.RenameDialogClickListener;
import com.example.documenpro.model_reader.DocumentModel;
import com.example.documenpro.utils.DialogUtils;
import com.example.documenpro.utils.Utils;

import java.io.File;

public class MoreDialog extends Dialog implements View.OnClickListener {

    DocumentModel documentModel;
    Activity activity;
    boolean isRecentFile;
    MoreClickListener optionsListener;

    public MoreDialog(@NonNull Activity context, DocumentModel document, boolean isRecent, MoreClickListener listener) {
        super(context);
        this.documentModel = document;
        this.activity = context;
        this.isRecentFile = isRecent;
        this.optionsListener = listener;
        setContentView(R.layout.dialog_more);
        TextView tvName = findViewById(R.id.tv_name);
        TextView tvFileDate = findViewById(R.id.tvFileDate);
        TextView tvFileSize = findViewById(R.id.tvFileSize);
        AppCompatImageView imgIcon = findViewById(R.id.iv_icon);
        tvName.setText(documentModel.getFileName_DocModel());
        imgIcon.setImageResource(documentModel.getSrcImage_DocModel());
        tvFileDate.setText(Utils.formatDateToHumanReadable(documentModel.getLastModified_DocModel()));
        tvFileSize.setText(Formatter.formatFileSize(activity, documentModel.getLength_DocModel()));
        if (isRecent) {
            findViewById(R.id.ll_remove_from_recent).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.ll_remove_from_recent).setVisibility(View.GONE);
        }
        findViewById(R.id.ll_rename).setOnClickListener(this);
        findViewById(R.id.ll_share).setOnClickListener(this);
        findViewById(R.id.ll_info).setOnClickListener(this);
        findViewById(R.id.ll_create_shortcut).setOnClickListener(this);
        findViewById(R.id.ll_remove_from_recent).setOnClickListener(this);
        findViewById(R.id.ll_delete).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int idView = v.getId();
        if (idView == R.id.ll_rename) {
            DialogUtils.showRenameDialog(activity, documentModel.getFileName_DocModel(), new RenameDialogClickListener() {
                @Override
                public void onRenameDialogListener(String newName) {
                    if (optionsListener != null) {
                        optionsListener.onRenameListener(newName);
                    }
                }
            });
            dismiss();
        } else if (idView == R.id.ll_share) {
            Utils.shareFile(activity, new File(documentModel.getFileUri_DocModel()));
            dismiss();
        } else if (idView == R.id.ll_info) {
            DialogUtils.showInformationDialog(activity, documentModel);
            dismiss();
        } else if (idView == R.id.ll_create_shortcut) {
            Utils.createShortcut(activity, documentModel);
            dismiss();
        } else if (idView == R.id.ll_remove_from_recent) {
            DialogUtils.showConfirmDialog(activity, GlobalConstant.DIALOG_CONFIRM_REMOVE_RECENT, new OnConfirmClickListener() {
                @Override
                public void onConfirmClickListener() {
                    if (optionsListener != null) {
                        optionsListener.onRemoveListener();
                    }
                }
            });
            dismiss();
        } else if (idView == R.id.ll_delete) {
            DialogUtils.showConfirmDialog(activity, GlobalConstant.DIALOG_CONFIRM_DELETE, new OnConfirmClickListener() {
                @Override
                public void onConfirmClickListener() {
                    if (optionsListener != null) {
                        optionsListener.onDeleteListener();
                    }
                }
            });
            dismiss();
        }
    }
}
