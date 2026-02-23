package com.example.documenpro.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.documenpro.model_reader.DocumentModel;

import java.util.ArrayList;

public class DocumentViewModel extends ViewModel {
    private final ArrayList<DocumentModel> allFilesLiveData = new ArrayList<>();
    private final ArrayList<DocumentModel> excelFilesLiveData = new ArrayList<>();
    private final ArrayList<DocumentModel> pdfFilesLiveData = new ArrayList<>();
    private final ArrayList<DocumentModel> wordFilesLiveData = new ArrayList<>();
    private final ArrayList<DocumentModel> pptFilesLiveData = new ArrayList<>();

    public void setAllFiles(ArrayList<DocumentModel> fileList) {
        allFilesLiveData.clear();
        allFilesLiveData.addAll(fileList);
    }

    public ArrayList<DocumentModel> getAllFiles() {
        return allFilesLiveData;
    }

    public void setExcelFiles(ArrayList<DocumentModel> fileList) {
        excelFilesLiveData.clear();
        excelFilesLiveData.addAll(fileList);
    }

    public ArrayList<DocumentModel> getExcelFiles() {
        return excelFilesLiveData;
    }

    public void setPdfFiles(ArrayList<DocumentModel> fileList) {
        pdfFilesLiveData.clear();
        pdfFilesLiveData.addAll(fileList);
    }

    public ArrayList<DocumentModel> getPdfFiles() {
        return pdfFilesLiveData;
    }

    public void setWordFiles(ArrayList<DocumentModel> fileList) {
        wordFilesLiveData.clear();
        wordFilesLiveData.addAll(fileList);
//        wordFilesLiveData = fileList;
    }

    public ArrayList<DocumentModel> getWordFiles() {
        return wordFilesLiveData;
    }

    public void setPptFiles(ArrayList<DocumentModel> fileList) {
        pptFilesLiveData.clear();
        pptFilesLiveData.addAll(fileList);
    }

    public ArrayList<DocumentModel> getPptFiles() {
        return pptFilesLiveData;
    }
}
