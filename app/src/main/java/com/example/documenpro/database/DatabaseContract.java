package com.example.documenpro.database;

import android.provider.BaseColumns;

public class DatabaseContract {

    public static class RecentEntry_DbContract implements BaseColumns {
        public static final String TABLE_NAME_DbContract = "history_pdfs";
    }

    public static class FavoriteEntry_DbContract implements BaseColumns {
        public static final String TABLE_NAME_DbContract = "stared_pdfs";
    }

}
