package com.example.documenpro.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.documenpro.model.Document;

import java.util.ArrayList;

public class RecentDataSingleton {
    private static RecentDataSingleton instance;
    private final MutableLiveData<ArrayList<Document>> recentLiveData = new MutableLiveData<>();
    private final ArrayList<Document> recentList = new ArrayList<>();

    private RecentDataSingleton() {
    }

    public static RecentDataSingleton getInstance() {
        if (instance == null) {
            instance = new RecentDataSingleton();
        }
        return instance;
    }
    public LiveData<ArrayList<Document>> getRecentLiveData() {
        return recentLiveData;
    }

    public void addRecentDocument(Document document) {
        recentList.add(document);
        recentLiveData.setValue(recentList);
    }

    public void removeRecentDocument(Document document) {
        recentList.remove(document);
        recentLiveData.setValue(recentList);
    }
}
