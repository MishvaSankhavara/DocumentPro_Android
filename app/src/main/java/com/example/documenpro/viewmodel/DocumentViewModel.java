package com.example.documenpro.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.documenpro.model.Document;

import java.util.ArrayList;

public class DocumentViewModel extends ViewModel {
    private final ArrayList<Document> allFilesLiveData = new ArrayList<>();
    private final ArrayList<Document> excelFilesLiveData = new ArrayList<>();
    private final ArrayList<Document> pdfFilesLiveData = new ArrayList<>();
    private final ArrayList<Document> wordFilesLiveData = new ArrayList<>();
    private final ArrayList<Document> pptFilesLiveData = new ArrayList<>();

    public void setAllFiles(ArrayList<Document> fileList) {
        allFilesLiveData.clear();
        allFilesLiveData.addAll(fileList);
    }

    public ArrayList<Document> getAllFiles() {
        return allFilesLiveData;
    }

    public void setExcelFiles(ArrayList<Document> fileList) {
        excelFilesLiveData.clear();
        excelFilesLiveData.addAll(fileList);
    }

    public ArrayList<Document> getExcelFiles() {
        return excelFilesLiveData;
    }

    public void setPdfFiles(ArrayList<Document> fileList) {
        pdfFilesLiveData.clear();
        pdfFilesLiveData.addAll(fileList);
    }

    public ArrayList<Document> getPdfFiles() {
        return pdfFilesLiveData;
    }

    public void setWordFiles(ArrayList<Document> fileList) {
        wordFilesLiveData.clear();
        wordFilesLiveData.addAll(fileList);
//        wordFilesLiveData = fileList;
    }

    public ArrayList<Document> getWordFiles() {
        return wordFilesLiveData;
    }

    public void setPptFiles(ArrayList<Document> fileList) {
        pptFilesLiveData.clear();
        pptFilesLiveData.addAll(fileList);
    }

    public ArrayList<Document> getPptFiles() {
        return pptFilesLiveData;
    }
}
