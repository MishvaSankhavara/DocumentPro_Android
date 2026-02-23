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
import com.example.documenpro.listener.MoreListener;
import com.example.documenpro.listener.OnConfirmListener;
import com.example.documenpro.listener.RenameDialogListener;
import com.example.documenpro.model.Document;
import com.example.documenpro.utils.DialogUtils;
import com.example.documenpro.utils.Utils;

import java.io.File;

public class MoreDialog extends Dialog implements View.OnClickListener {

    Document mDocument;
    Activity mContext;
    boolean mIsRecent;
    MoreListener mListener;

    public MoreDialog(@NonNull Activity context, Document document, boolean isRecent, MoreListener listener) {
        super(context);
        this.mDocument = document;
        this.mContext = context;
        this.mIsRecent = isRecent;
        this.mListener = listener;
        setContentView(R.layout.dialog_more);
        TextView tvName = findViewById(R.id.tv_name);
        TextView tvFileDate = findViewById(R.id.tvFileDate);
        TextView tvFileSize = findViewById(R.id.tvFileSize);
        AppCompatImageView imgIcon = findViewById(R.id.iv_icon);
        tvName.setText(mDocument.getFileName());
        imgIcon.setImageResource(mDocument.getSrcImage());
        tvFileDate.setText(Utils.formatDateToHumanReadable(mDocument.getLastModified()));
        tvFileSize.setText(Formatter.formatFileSize(mContext, mDocument.getLength()));
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
            DialogUtils.showRenameDialog(mContext, mDocument.getFileName(), new RenameDialogListener() {
                @Override
                public void onRenameDialog(String newName) {
                    if (mListener != null) {
                        mListener.onRename(newName);
                    }
                }
            });
            dismiss();
        } else if (idView == R.id.ll_share) {
            Utils.shareFile(mContext, new File(mDocument.getFileUri()));
            dismiss();
        } else if (idView == R.id.ll_info) {
            DialogUtils.showInformationDialog(mContext, mDocument);
            dismiss();
        } else if (idView == R.id.ll_create_shortcut) {
            Utils.createShortcut(mContext, mDocument);
            dismiss();
        } else if (idView == R.id.ll_remove_from_recent) {
            DialogUtils.showConfirmDialog(mContext, GlobalConstant.DIALOG_CONFIRM_REMOVE_RECENT, new OnConfirmListener() {
                @Override
                public void onConfirm() {
                    if (mListener != null) {
                        mListener.onRemove();
                    }
                }
            });
            dismiss();
        } else if (idView == R.id.ll_delete) {
            DialogUtils.showConfirmDialog(mContext, GlobalConstant.DIALOG_CONFIRM_DELETE, new OnConfirmListener() {
                @Override
                public void onConfirm() {
                    if (mListener != null) {
                        mListener.onDelete();
                    }
                }
            });
            dismiss();
        }
    }
}
