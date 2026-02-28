package com.example.documenpro.ui.customviews.smartrefresh.api;

import android.content.Context;

import androidx.annotation.NonNull;



public interface RefreshFooterCreatorFactory {
    @NonNull
    RefreshFooterComponent createDefaultRefreshFooter(@NonNull Context context, @NonNull SmartRefreshLayout layout);
}
