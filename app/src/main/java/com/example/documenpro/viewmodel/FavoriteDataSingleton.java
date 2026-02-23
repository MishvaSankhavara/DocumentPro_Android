package com.example.documenpro.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.documenpro.model.Document;

import java.util.ArrayList;

public class FavoriteDataSingleton {
    private static FavoriteDataSingleton instance;
    private final MutableLiveData<ArrayList<Document>> favoriteLiveData = new MutableLiveData<>();
    private final ArrayList<Document> favoriteList = new ArrayList<>();
    private FavoriteDataSingleton() {}
    public static FavoriteDataSingleton getInstance() {
        if (instance == null) {
            instance = new FavoriteDataSingleton();
        }
        return instance;
    }

    public LiveData<ArrayList<Document>> getFavoriteLiveData() {
        return favoriteLiveData;
    }

    public void addFavoriteDocument(Document document) {
        favoriteList.add(document);
        favoriteLiveData.setValue(favoriteList);
    }

    public void removeFavoriteDocument(Document document) {
        favoriteList.remove(document);
        favoriteLiveData.setValue(favoriteList);
    }
}
