package com.example.documenpro;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.hjq.language.MultiLanguages;
import com.example.documenpro.model_reader.PDFReaderModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class DocumentMyApplication extends Application
        implements Application.ActivityLifecycleCallbacks, DefaultLifecycleObserver {
    private Activity foregroundActivity;
    private static DocumentMyApplication mInstance;
    private static ArrayList<PDFReaderModel> arrayListAll;
    private static ArrayList<PDFReaderModel> arrayListMerge;
    private static ArrayList<Integer> arraySplit;
    private static ArrayList<String> arrayPhoto;

    public static DocumentMyApplication getInstance() {
        return mInstance;
    }

    private static synchronized void setInstance(DocumentMyApplication myApplication) {
        synchronized (DocumentMyApplication.class) {
            mInstance = myApplication;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (mInstance == null) {
            setInstance(this);
        }
        MultiLanguages.init(this);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        arrayListAll = new ArrayList<>();
        arrayListMerge = new ArrayList<>();
        arraySplit = new ArrayList<>();
        arrayPhoto = new ArrayList<>();

        this.registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

    }

    public void updateAllPdfList(ArrayList<PDFReaderModel> arrayList) {
        arrayListAll = arrayList;
    }

    public ArrayList<PDFReaderModel> getAllPdfList() {
        return arrayListAll;
    }

    public void updateMergedPdfList(ArrayList<PDFReaderModel> arrayList) {
        arrayListMerge = arrayList;
    }

    public ArrayList<PDFReaderModel> getMergedPdfList() {
        return arrayListMerge;
    }

    public void clearArrayListMerge() {
        arrayListMerge.clear();
    }

    public void clearArrayListSplit() {
        arraySplit.clear();
    }

    public void updateSplitIndices(ArrayList<Integer> arrayList) {
        arraySplit = arrayList;
    }

    public ArrayList<Integer> getArrayListSplit() {
        return arraySplit;
    }

    public ArrayList<String> getSplitIndices() {
        return arrayPhoto;
    }

    public void updateSelectedImages(ArrayList<String> array) {
        arrayPhoto = array;
    }

    public void clearSelectedImageList() {
        arrayPhoto.clear();
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onStart(owner);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        foregroundActivity = activity;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }

}
