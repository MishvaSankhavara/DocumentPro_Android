package com.example.documenpro.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.ViewGroup;
import android.view.Window;

import com.example.documenpro.clickListener.OnConfirmClickListener;
import com.example.documenpro.clickListener.PasswordClickListener;
import com.example.documenpro.clickListener.RenameDialogClickListener;
import com.example.documenpro.model.Document;
import com.example.documenpro.model.PDFModel;
import com.example.documenpro.ui.dialog.ConfirmDialog;
import com.example.documenpro.ui.dialog.FileProtectedDialog;
import com.example.documenpro.ui.dialog.InformationDialog;
import com.example.documenpro.ui.dialog.LanguageDialog;
import com.example.documenpro.ui.dialog.RemovePasswordDialog;
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
        LanguageDialog informationDialog = new LanguageDialog(mContext);
        Window window = informationDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        informationDialog.show();
    }

    public static void showRemovePasswordDialog(Activity mContext, PDFModel pdfModel, PasswordClickListener listener) {
        if (mContext == null) {
            return;
        }
        RemovePasswordDialog renameDialog = new RemovePasswordDialog(mContext, pdfModel, listener);
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
        FileProtectedDialog dialog = new FileProtectedDialog(mContext, idTvDes);
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

    public static void showInformationDialog(Activity mContext, Document document) {
        if (mContext == null) {
            return;
        }
        InformationDialog informationDialog = new InformationDialog(mContext, document);
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
        ConfirmDialog dialog = new ConfirmDialog(mContext, typeConfirm, listener);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        dialog.show();

    }
}
