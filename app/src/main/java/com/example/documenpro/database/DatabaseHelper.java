package com.example.documenpro.database;

import static com.example.documenpro.utils.Utils.getDocumentSrc;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.widget.Toast;

import com.example.documenpro.utils.Utils;

import com.example.documenpro.R;
import com.example.documenpro.model_reader.DocumentModel;

import java.io.File;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private int mOpenCounter_DatabaseHelper;
    private SQLiteDatabase mDatabase_DatabaseHelper;
    public Context context_DatabaseHelper;
    private static DatabaseHelper sInstance_DatabaseHelper;
    private static final String DATABASE_NAME_DatabaseHelper = "pdf_history.db";

    public boolean isStared_DatabaseHelper(SQLiteDatabase sQLiteDatabase_DatabaseHelper, String str_DatabaseHelper) {
        Cursor query = sQLiteDatabase_DatabaseHelper.query(
                DatabaseContract.FavoriteEntry_DbContract.TABLE_NAME_DbContract,
                new String[] { "path" },
                "path =?",
                new String[] { str_DatabaseHelper },
                null,
                null,
                null);
        boolean valueOf = query.moveToFirst();
        query.close();
        return valueOf;
    }

    public boolean isStared_DatabaseHelper(String str_DatabaseHelper) {
        Cursor query = getReadableDb_DatabaseHelper().query(
                DatabaseContract.FavoriteEntry_DbContract.TABLE_NAME_DbContract,
                new String[] { "path" },
                "path =?",
                new String[] { str_DatabaseHelper },
                null,
                null,
                null);
        boolean valueOf = query.moveToFirst();
        query.close();
        closeDb_DatabaseHelper();
        return valueOf;
    }

    public void updateHistory_DatabaseHelper(String str_DatabaseHelper, String str2_DatabaseHelper) {
        try {
            SQLiteDatabase readableDb_DatabaseHelper = getReadableDb_DatabaseHelper();
            ContentValues contentValues_DatabaseHelper = new ContentValues();
            contentValues_DatabaseHelper.put("path", str2_DatabaseHelper);
            readableDb_DatabaseHelper.update(
                    DatabaseContract.RecentEntry_DbContract.TABLE_NAME_DbContract,
                    contentValues_DatabaseHelper,
                    "path=?",
                    new String[] { str_DatabaseHelper });
            closeDb_DatabaseHelper();
        } catch (Exception e) {
            Toast.makeText(this.context_DatabaseHelper, R.string.action_failed, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void updateStaredDocument_DatabaseHelper(String str_DatabaseHelper, String str2_DatabaseHelper) {
        try {
            SQLiteDatabase readableDb_DatabaseHelper = getReadableDb_DatabaseHelper();
            ContentValues contentValues_DatabaseHelper = new ContentValues();
            contentValues_DatabaseHelper.put("path", str2_DatabaseHelper);
            readableDb_DatabaseHelper.update(
                    DatabaseContract.FavoriteEntry_DbContract.TABLE_NAME_DbContract,
                    contentValues_DatabaseHelper,
                    "path =?",
                    new String[] { str_DatabaseHelper });
            closeDb_DatabaseHelper();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeStaredDocument_DatabaseHelper(String str_DatabaseHelper) {
        getReadableDb_DatabaseHelper().delete(
                DatabaseContract.FavoriteEntry_DbContract.TABLE_NAME_DbContract,
                "path =?",
                new String[] { str_DatabaseHelper });
        closeDb_DatabaseHelper();
    }

    public void addStaredDocument_DatabaseHelper(String str_DatabaseHelper) {
        SQLiteDatabase readableDb = getReadableDb_DatabaseHelper();
        ContentValues contentValues = new ContentValues();
        contentValues.put("path", str_DatabaseHelper);
        readableDb.replace(DatabaseContract.FavoriteEntry_DbContract.TABLE_NAME_DbContract, null, contentValues);
        closeDb_DatabaseHelper();
    }

    public ArrayList<DocumentModel> getStarredDocuments_DatabaseHelper() {
        ArrayList<DocumentModel> arrayList_DatabaseHelper = new ArrayList<>();
        Cursor rawQuery = getReadableDb_DatabaseHelper()
                .rawQuery("SELECT * FROM stared_pdfs ORDER BY created_at DESC", null);

        if (rawQuery.moveToFirst()) {
            do {
                @SuppressLint("Range")
                String string = rawQuery.getString(rawQuery.getColumnIndex("path"));
                File file = new File(string);
                if (file.exists()) {
                    DocumentModel document = new DocumentModel();
                    document.setFileName_DocModel(file.getName());
                    document.setFileUri_DocModel(file.getAbsolutePath());
                    document.setLength_DocModel(file.length());
                    document.setSrcImage_DocModel(getDocumentSrc(file));
                    document.setLastModified_DocModel(file.lastModified());
                    document.setStarred_DocModel(true);
                    arrayList_DatabaseHelper.add(document);
                } else {
                    removeStaredDocument_DatabaseHelper(string);
                }
            } while (rawQuery.moveToNext());
        }
        rawQuery.close();
        closeDb_DatabaseHelper();
        return arrayList_DatabaseHelper;
    }

    public void updateStarred_DatabaseHelper(String str_DatabaseHelper, String str2_DatabaseHelper) {
        SQLiteDatabase readableDb = getReadableDb_DatabaseHelper();
        ContentValues contentValues = new ContentValues();
        contentValues.put("path", str2_DatabaseHelper);
        readableDb.update(
                DatabaseContract.FavoriteEntry_DbContract.TABLE_NAME_DbContract,
                contentValues,
                "path=?",
                new String[] { str_DatabaseHelper });
        closeDb_DatabaseHelper();
    }

    public void deleteRecentPDF_DatabaseHelper(String str_DatabaseHelper) {
        getReadableDb_DatabaseHelper().delete(
                DatabaseContract.RecentEntry_DbContract.TABLE_NAME_DbContract,
                "path =?",
                new String[] { str_DatabaseHelper });
        closeDb_DatabaseHelper();
    }

    public ArrayList<DocumentModel> getRecentDocuments_DatabaseHelper() {
        ArrayList<DocumentModel> arrayList_DatabaseHelper = new ArrayList<>();
        SQLiteDatabase readableDb_DatabaseHelper = getReadableDb_DatabaseHelper();
        Cursor rawQuery = readableDb_DatabaseHelper
                .rawQuery("SELECT * FROM history_pdfs ORDER BY last_accessed_at DESC", null);

        if (rawQuery.moveToFirst()) {
            do {
                @SuppressLint("Range")
                String string_DatabaseHelper = rawQuery.getString(rawQuery.getColumnIndex("path"));
                File file = new File(string_DatabaseHelper);

                if (file.exists()) {
                    boolean isLockedPdf = false;
                    if (file.getName().toLowerCase().endsWith(".pdf")) {
                        isLockedPdf = Utils.checkHavePassword(context_DatabaseHelper, Uri.fromFile(file));
                    }

                    if (!isLockedPdf) {
                        DocumentModel document = new DocumentModel();
                        document.setFileName_DocModel(file.getName());
                        document.setFileUri_DocModel(file.getAbsolutePath());
                        document.setLength_DocModel(file.length());
                        document.setSrcImage_DocModel(getDocumentSrc(file));
                        document.setLastModified_DocModel(file.lastModified());
                        document.setStarred_DocModel(
                                isStared_DatabaseHelper(readableDb_DatabaseHelper,
                                        file.getAbsolutePath()));
                        arrayList_DatabaseHelper.add(document);
                    }
                } else {
                    deleteRecentPDF_DatabaseHelper(string_DatabaseHelper);
                }
            } while (rawQuery.moveToNext());
        }
        rawQuery.close();
        closeDb_DatabaseHelper();
        return arrayList_DatabaseHelper;
    }

    public void removeRecentDocument_DatabaseHelper(String str_DatabaseHelper) {
        getReadableDb_DatabaseHelper().delete(
                DatabaseContract.RecentEntry_DbContract.TABLE_NAME_DbContract,
                "path =?",
                new String[] { str_DatabaseHelper });
        closeDb_DatabaseHelper();
    }

    public void addRecentDocument_DatabaseHelper(String str_DatabaseHelper) {
        SQLiteDatabase readableDb = getReadableDb_DatabaseHelper();
        ContentValues contentValues = new ContentValues();
        contentValues.put("path", str_DatabaseHelper);
        readableDb.replace(DatabaseContract.RecentEntry_DbContract.TABLE_NAME_DbContract, null, contentValues);
        closeDb_DatabaseHelper();
    }

    public boolean isRecent_DatabaseHelper(String str_DatabaseHelper) {
        Cursor query = getReadableDb_DatabaseHelper().query(
                DatabaseContract.RecentEntry_DbContract.TABLE_NAME_DbContract,
                new String[] { "path" },
                "path =?",
                new String[] { str_DatabaseHelper },
                null,
                null,
                null);
        boolean valueOf = query.moveToFirst();
        query.close();
        closeDb_DatabaseHelper();
        return valueOf;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sQLiteDatabase_DatabaseHelper, int i, int i2) {
        if (i == 1) {
            sQLiteDatabase_DatabaseHelper.execSQL(
                    "CREATE TABLE IF NOT EXISTS last_opened_page ( _id INTEGER PRIMARY KEY AUTOINCREMENT, path TEXT UNIQUE, page_number INTEGER)");
            sQLiteDatabase_DatabaseHelper.execSQL(
                    "CREATE TABLE IF NOT EXISTS bookmarks ( _id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, path TEXT, page_number INTEGER UNIQUE, created_at DATETIME DEFAULT (DATETIME('now','localtime')))");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sQLiteDatabase_DatabaseHelper) {
        sQLiteDatabase_DatabaseHelper.execSQL(
                "CREATE TABLE IF NOT EXISTS history_pdfs ( _id INTEGER PRIMARY KEY AUTOINCREMENT, path TEXT UNIQUE, last_accessed_at DATETIME DEFAULT (DATETIME('now','localtime')))");
        sQLiteDatabase_DatabaseHelper.execSQL(
                "CREATE TABLE IF NOT EXISTS stared_pdfs ( _id INTEGER PRIMARY KEY AUTOINCREMENT, path TEXT UNIQUE, created_at DATETIME DEFAULT (DATETIME('now','localtime')))");
        sQLiteDatabase_DatabaseHelper.execSQL(
                "CREATE TABLE IF NOT EXISTS last_opened_page ( _id INTEGER PRIMARY KEY AUTOINCREMENT, path TEXT UNIQUE, page_number INTEGER)");
        sQLiteDatabase_DatabaseHelper.execSQL(
                "CREATE TABLE IF NOT EXISTS bookmarks ( _id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, path TEXT, page_number INTEGER UNIQUE, created_at DATETIME DEFAULT (DATETIME('now','localtime')))");
    }

    public synchronized void closeDb_DatabaseHelper() {
        this.mOpenCounter_DatabaseHelper--;
        if (this.mOpenCounter_DatabaseHelper <= 0) {
            this.mOpenCounter_DatabaseHelper = 0;
            if (this.mDatabase_DatabaseHelper != null && this.mDatabase_DatabaseHelper.isOpen()) {
                this.mDatabase_DatabaseHelper.close();
            }
            this.mDatabase_DatabaseHelper = null;
        }
    }

    public synchronized SQLiteDatabase getReadableDb_DatabaseHelper() {
        this.mOpenCounter_DatabaseHelper++;
        if (this.mOpenCounter_DatabaseHelper == 1 || this.mDatabase_DatabaseHelper == null || !this.mDatabase_DatabaseHelper.isOpen()) {
            this.mDatabase_DatabaseHelper = getWritableDatabase();
        }
        return this.mDatabase_DatabaseHelper;
    }

    public static synchronized DatabaseHelper getInstance(Context context2_DatabaseHelper) {
        DatabaseHelper databaseHelper_DatabaseHelper;
        synchronized (DatabaseHelper.class) {
            if (sInstance_DatabaseHelper == null) {
                sInstance_DatabaseHelper = new DatabaseHelper(context2_DatabaseHelper.getApplicationContext());
            }
            databaseHelper_DatabaseHelper = sInstance_DatabaseHelper;
        }
        return databaseHelper_DatabaseHelper;
    }

    public DatabaseHelper(Context context2_DatabaseHelper) {
        super(context2_DatabaseHelper, DATABASE_NAME_DatabaseHelper, null, 2);
        this.context_DatabaseHelper = context2_DatabaseHelper;
    }
}