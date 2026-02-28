package com.example.documenpro.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.ViewGroup;
import android.view.Window;

import com.example.documenpro.clickListener.OnConfirmClickListener;
import com.example.documenpro.clickListener.PasswordClickListener;
import com.example.documenpro.clickListener.RenameDialogClickListener;
import com.example.documenpro.model_reader.DocumentModel;
import com.example.documenpro.model_reader.PDFReaderModel;
import com.example.documenpro.ui.dialog.ActionConfirmDialog;
import com.example.documenpro.ui.dialog.ProtectedFileDialog;
import com.example.documenpro.ui.dialog.DocumentInfoDialog;
import com.example.documenpro.ui.dialog.LanguageSelectionDialog;
import com.example.documenpro.ui.dialog.RemovePdfPasswordDialog;
import com.example.documenpro.ui.dialog.RenameDialog;
import com.example.documenpro.ui.dialog.SetPasswordDialog;

public class DialogUtils {
    public static void showSetPasswordDialog(Activity mContext, PasswordClickListener listener) {
        if (mContext == null) {
            return;
        }
        SetPasswordDialog renameDialog = new SetPasswordDialog(mContext, listener);
        Window window = renameDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        renameDialog.show();
    }

    public static void showLanguageDialog(Context mContext) {
        if (mContext == null) {
            return;
        }
        LanguageSelectionDialog informationDialog = new LanguageSelectionDialog(mContext);
        Window window = informationDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        informationDialog.show();
    }

    public static void showRemovePasswordDialog(Activity mContext, PDFReaderModel pdfModel, PasswordClickListener listener) {
        if (mContext == null) {
            return;
        }
        RemovePdfPasswordDialog renameDialog = new RemovePdfPasswordDialog(mContext, pdfModel, listener);
        Window window = renameDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        renameDialog.show();
    }

    public static void showFileProtectedDialog(Activity mContext, int idTvDes) {
        if (mContext == null) {
            return;
        }
        ProtectedFileDialog dialog = new ProtectedFileDialog(mContext, idTvDes);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        dialog.show();
    }

    public static void showRenameDialog(Activity mContext, String oldName, RenameDialogClickListener listener) {
        if (mContext == null) {
            return;
        }
        RenameDialog renameDialog = new RenameDialog(mContext, oldName, listener);
        Window window = renameDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        renameDialog.show();
    }

    public static void showInformationDialog(Activity mContext, DocumentModel document) {
        if (mContext == null) {
            return;
        }
        DocumentInfoDialog informationDialog = new DocumentInfoDialog(mContext, document);
        Window window = informationDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        informationDialog.show();
    }

    public static void showConfirmDialog(Activity mContext, int typeConfirm, OnConfirmClickListener listener) {
        if (mContext == null) {
            return;
        }
        ActionConfirmDialog dialog = new ActionConfirmDialog(mContext, typeConfirm, listener);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        dialog.show();

    }
}
