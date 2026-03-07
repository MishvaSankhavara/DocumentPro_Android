package com.example.documenpro.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.documenpro.model_reader.DocumentModel;

import java.util.ArrayList;

public class DataSingletonRecent {
    private static DataSingletonRecent instance;
    private final MutableLiveData<ArrayList<DocumentModel>> recentDocumentsLiveData = new MutableLiveData<>();
    private final ArrayList<DocumentModel> recentDocuments = new ArrayList<>();

    private DataSingletonRecent() {
    }

    public static DataSingletonRecent getInstance() {
        if (instance == null) {
            instance = new DataSingletonRecent();
        }
        return instance;
    }
    public LiveData<ArrayList<DocumentModel>> getRecentDocumentsLiveData() {
        return recentDocumentsLiveData;
    }

    public void removeFromRecent(DocumentModel document) {
        recentDocuments.remove(document);
        recentDocumentsLiveData.setValue(recentDocuments);
    }

    public void addToRecent(DocumentModel document) {
        recentDocuments.add(document);
        recentDocumentsLiveData.setValue(recentDocuments);
    }
}
