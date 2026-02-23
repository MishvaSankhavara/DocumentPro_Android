package com.example.documenpro.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.documenpro.model_reader.DocumentModel;

import java.util.ArrayList;

public class RecentDataSingleton {
    private static RecentDataSingleton instance;
    private final MutableLiveData<ArrayList<DocumentModel>> recentLiveData = new MutableLiveData<>();
    private final ArrayList<DocumentModel> recentList = new ArrayList<>();

    private RecentDataSingleton() {
    }

    public static RecentDataSingleton getInstance() {
        if (instance == null) {
            instance = new RecentDataSingleton();
        }
        return instance;
    }
    public LiveData<ArrayList<DocumentModel>> getRecentLiveData() {
        return recentLiveData;
    }

    public void addRecentDocument(DocumentModel document) {
        recentList.add(document);
        recentLiveData.setValue(recentList);
    }

    public void removeRecentDocument(DocumentModel document) {
        recentList.remove(document);
        recentLiveData.setValue(recentList);
    }
}
