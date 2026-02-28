package com.example.documenpro.ui.customviews.smartrefresh.api;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;
import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP;
import static androidx.annotation.RestrictTo.Scope.SUBCLASSES;

import androidx.annotation.RestrictTo;

public interface RefreshFooterComponent extends RefreshComponent {

    @RestrictTo({LIBRARY,LIBRARY_GROUP,SUBCLASSES})
    boolean setNoMoreDataAvailable(boolean noMoreData);
}
