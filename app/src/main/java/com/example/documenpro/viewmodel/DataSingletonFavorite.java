package com.example.documenpro.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.documenpro.model_reader.DocumentModel;

import java.util.ArrayList;

public class DataSingletonFavorite {
    private static DataSingletonFavorite instance;
    private final MutableLiveData<ArrayList<DocumentModel>> favoriteDocumentsLiveData = new MutableLiveData<>();
    private final ArrayList<DocumentModel> favoriteDocuments = new ArrayList<>();
    private DataSingletonFavorite() {}
    public static DataSingletonFavorite getInstance() {
        if (instance == null) {
            instance = new DataSingletonFavorite();
        }
        return instance;
    }

    public LiveData<ArrayList<DocumentModel>> getFavoriteDocumentsLiveData() {
        return favoriteDocumentsLiveData;
    }

    public void addToFavorites(DocumentModel document) {
        favoriteDocuments.add(document);
        favoriteDocumentsLiveData.setValue(favoriteDocuments);
    }

    public void removeFromFavorites(DocumentModel document) {
        favoriteDocuments.remove(document);
        favoriteDocumentsLiveData.setValue(favoriteDocuments);
    }
}
