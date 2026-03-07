package com.example.documenpro.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.documenpro.model_reader.DocumentModel;

import java.util.ArrayList;

public class ViewModelDocument extends ViewModel {
    private final ArrayList<DocumentModel> allDocuments = new ArrayList<>();
    private final ArrayList<DocumentModel> excelDocuments = new ArrayList<>();
    private final ArrayList<DocumentModel> pdfDocuments = new ArrayList<>();
    private final ArrayList<DocumentModel> wordDocuments = new ArrayList<>();
    private final ArrayList<DocumentModel> pptDocuments = new ArrayList<>();

    public void setAllDocuments(ArrayList<DocumentModel> fileList) {
        allDocuments.clear();
        allDocuments.addAll(fileList);
    }

    public ArrayList<DocumentModel> getAllDocuments() {
        return allDocuments;
    }

    public void setExcelFiles(ArrayList<DocumentModel> fileList) {
        excelDocuments.clear();
        excelDocuments.addAll(fileList);
    }

    public ArrayList<DocumentModel> getExcelFiles() {
        return excelDocuments;
    }

    public void setPdfFiles(ArrayList<DocumentModel> fileList) {
        pdfDocuments.clear();
        pdfDocuments.addAll(fileList);
    }

    public ArrayList<DocumentModel> getPdfFiles() {
        return pdfDocuments;
    }

    public void setWordFiles(ArrayList<DocumentModel> fileList) {
        wordDocuments.clear();
        wordDocuments.addAll(fileList);
    }

    public ArrayList<DocumentModel> getWordFiles() {
        return wordDocuments;
    }

    public void setPptFiles(ArrayList<DocumentModel> fileList) {
        pptDocuments.clear();
        pptDocuments.addAll(fileList);
    }

    public ArrayList<DocumentModel> getPptFiles() {
        return pptDocuments;
    }
}
