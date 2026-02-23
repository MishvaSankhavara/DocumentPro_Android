package com.example.documenpro.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.documenpro.model_reader.DocumentModel;

import java.util.ArrayList;

public class FavoriteDataSingleton {
    private static FavoriteDataSingleton instance;
    private final MutableLiveData<ArrayList<DocumentModel>> favoriteLiveData = new MutableLiveData<>();
    private final ArrayList<DocumentModel> favoriteList = new ArrayList<>();
    private FavoriteDataSingleton() {}
    public static FavoriteDataSingleton getInstance() {
        if (instance == null) {
            instance = new FavoriteDataSingleton();
        }
        return instance;
    }

    public LiveData<ArrayList<DocumentModel>> getFavoriteLiveData() {
        return favoriteLiveData;
    }

    public void addFavoriteDocument(DocumentModel document) {
        favoriteList.add(document);
        favoriteLiveData.setValue(favoriteList);
    }

    public void removeFavoriteDocument(DocumentModel document) {
        favoriteList.remove(document);
        favoriteLiveData.setValue(favoriteList);
    }
}
