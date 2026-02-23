package com.example.documenpro.listener;

public interface SortByListener {
    void onSortByDateOldest();

    void onSortByDateNewest();

    void onSortAtoZ();

    void onSortZtoA();

    void onSortFileSizeUp();

    void onSortFileSizeDown();
}
