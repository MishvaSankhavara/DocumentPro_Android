package com.example.documenpro.ui.customviews.smartrefresh.api;

import android.content.Context;

import androidx.annotation.NonNull;


public interface RefreshLayoutInitializer {
    void initializeRefreshLayout(@NonNull Context context, @NonNull SmartRefreshLayout layout);
}
