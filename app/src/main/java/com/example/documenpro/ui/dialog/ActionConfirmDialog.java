package com.example.documenpro.ui.dialog;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.example.documenpro.AppGlobalConstants;
import com.example.documenpro.R;
import com.example.documenpro.clickListener.OnConfirmClickListener;

public class ActionConfirmDialog extends Dialog {
    private final OnConfirmClickListener confirmListener;
    private final int dialogType;

    private AppCompatTextView tvTitle;
    private AppCompatTextView tvMessage;
    private AppCompatTextView btnConfirm;
    private AppCompatTextView btnCancel;

    public ActionConfirmDialog(@NonNull Context context, int mTypeDialog, OnConfirmClickListener listener) {
        super(context);
        setContentView(R.layout.dialog_confirmation);
        this.confirmListener = listener;
        this.dialogType = mTypeDialog;
        initializeViews();
        setupDialogContent();
        btnConfirm.setOnClickListener(view -> {
            if (confirmListener != null) {
                confirmListener.onConfirmClickListener();
                dismiss();
            }
            dismiss();
        });
        btnCancel.setOnClickListener(view -> dismiss());
    }

    private void setupDialogContent() {
        if (dialogType == AppGlobalConstants.DIALOG_CONFIRM_DELETE) {
            tvTitle.setText(R.string.action_delete);
            tvMessage.setText(R.string.dialog_delete_body);
            btnCancel.setText(R.string.action_cancel);
            btnConfirm.setText(R.string.action_delete);
        } else if (dialogType == AppGlobalConstants.DIALOG_CONFIRM_REMOVE_RECENT) {
            tvTitle.setText(R.string.dialog_remove_title);
            tvMessage.setText(R.string.dialog_remove_body);
            btnCancel.setText(R.string.action_cancel);
            btnConfirm.setText(R.string.action_yes);
        } else if (dialogType == AppGlobalConstants.DIALOG_CONFIRM_REMOVE_FAVORITE) {
            tvTitle.setText(R.string.dialog_move_fav);
            tvMessage.setText(R.string.dialog_remove_body);
            btnCancel.setText(R.string.action_cancel);
            btnConfirm.setText(R.string.action_yes);

        }else if (dialogType == AppGlobalConstants.DIALOG_CONFIRM_EXIT_MERGE) {
            tvTitle.setText(R.string.dialog_merge_exit_tittle);
            tvMessage.setText(R.string.dialog_merge_content);
            btnCancel.setText(R.string.action_cancel);
            btnConfirm.setText(R.string.action_quit);
        }else if (dialogType == AppGlobalConstants.DIALOG_CONFIRM_EXIT_SPLIT) {
            tvTitle.setText(R.string.dialog_split_exit_tittle);
            tvMessage.setText(R.string.dialog_merge_content);
            btnCancel.setText(R.string.action_cancel);
            btnConfirm.setText(R.string.action_quit);
        }else if (dialogType == AppGlobalConstants.DIALOG_CONFIRM_EXIT_PHOTO_TO_PDF) {
            tvTitle.setText(R.string.dialog_convert_exit_tittle);
            tvMessage.setText(R.string.dialog_merge_content);
            btnCancel.setText(R.string.action_cancel);
            btnConfirm.setText(R.string.action_quit);
        }else if (dialogType == AppGlobalConstants.DIALOG_CONFIRM_CLEAR_RECENT) {
            tvTitle.setText(R.string.dialog_clear_tittle);
            tvMessage.setText(R.string.dialog_clear_body_1);
            btnCancel.setText(R.string.action_cancel);
            btnConfirm.setText(R.string.action_clear_all);
        } else if (dialogType == AppGlobalConstants.DIALOG_CONFIRM_CLEAR_FAV) {
            tvTitle.setText(R.string.dialog_clear_tittle);
            tvMessage.setText(R.string.dialog_clear_body_2);
            btnCancel.setText(R.string.action_cancel);
            btnConfirm.setText(R.string.action_clear_all);
        }


    }

    private void initializeViews() {
        tvTitle = findViewById(R.id.tv_title);
        tvMessage = findViewById(R.id.tv_dialog_description);
        btnCancel = findViewById(R.id.btn_cancel);
        btnConfirm = findViewById(R.id.btn_confirm);
    }
}