package com.arkay.gkinhindi.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.ViewGroup;
import android.view.Window;

import com.arkay.gkinhindi.clickListener.OnConfirmClickListener;
import com.arkay.gkinhindi.clickListener.PasswordClickListener;
import com.arkay.gkinhindi.clickListener.RenameDialogClickListener;
import com.arkay.gkinhindi.model_reader.DocumentModel;
import com.arkay.gkinhindi.model_reader.PDFReaderModel;
import com.arkay.gkinhindi.ui.dialog.ActionConfirmDialog;
import com.arkay.gkinhindi.ui.dialog.ProtectedFileDialog;
import com.arkay.gkinhindi.ui.dialog.DocumentInfoDialog;
import com.arkay.gkinhindi.ui.dialog.LanguageSelectionDialog;
import com.arkay.gkinhindi.ui.dialog.RemovePdfPasswordDialog;
import com.arkay.gkinhindi.ui.dialog.FileRenameDialog;
import com.arkay.gkinhindi.ui.dialog.PasswordSetupDialog;

public class DialogManagerUtils {
    public static PasswordSetupDialog showPasswordSetup(Activity mContext, PasswordClickListener listener) {
        if (mContext == null) {
            return null;
        }
        PasswordSetupDialog renameDialog = new PasswordSetupDialog(mContext, listener);
        Window window = renameDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        renameDialog.show();
        return renameDialog;
    }

    public static void showPdfPasswordRemoval(Activity mContext, PDFReaderModel pdfModel, PasswordClickListener listener) {
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

    public static void showLanguageSelection(Context mContext) {
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

    public static void showProtectedFileDialog(Activity mContext, int idTvDes) {
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

    public static void showInfoDialog(Activity mContext, DocumentModel document) {
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

    public static void showRenameDialog(Activity mContext, String oldName, RenameDialogClickListener listener) {
        if (mContext == null) {
            return;
        }
        FileRenameDialog renameDialog = new FileRenameDialog(mContext, oldName, listener);
        Window window = renameDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        renameDialog.show();
    }

    public static void showConfirmationDialog(Activity mContext, int typeConfirm, OnConfirmClickListener listener) {
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
