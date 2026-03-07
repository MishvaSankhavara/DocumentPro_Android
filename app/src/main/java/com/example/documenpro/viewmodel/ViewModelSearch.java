package com.example.documenpro.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ViewModelSearch extends ViewModel {

    private final MutableLiveData<String> searchQueryLiveData = new MutableLiveData<>();

    public void setSearchQueryLiveData(String queryData) {
        searchQueryLiveData.setValue(queryData);
    }

    public LiveData<String> getSearchQueryLiveData() {
        return searchQueryLiveData;
    }
}
