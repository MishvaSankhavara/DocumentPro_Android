package com.example.documenpro.model_reader;

import java.util.Comparator;

public class DocumentModel implements Comparable<DocumentModel> {

    private boolean isChecked_DocModel;
    private int srcImage_DocModel;
    private String fileUri_DocModel;
    private boolean isStarred_DocModel;
    private Long length_DocModel;
    private Long lastModified_DocModel;
    private String fileName_DocModel;

    public static Comparator<DocumentModel> sortDateDescendingComparator_DocModel = Comparator.comparingLong(DocumentModel::getLastModified_DocModel).reversed();

    public static Comparator<DocumentModel> sortDateAscendingComparator_DocModel = Comparator.comparingLong(DocumentModel::getLastModified_DocModel);

    public static Comparator<DocumentModel> sortFileSizeDescendingComparator_DocModel = Comparator.comparingLong(DocumentModel::getLength_DocModel).reversed();

    public static Comparator<DocumentModel> sortFileSizeAscendingComparator_DocModel = Comparator.comparingLong(DocumentModel::getLength_DocModel);

    public static Comparator<DocumentModel> sortNameZAComparator_DocModel = Comparator.comparing(DocumentModel::getFileName_DocModel).reversed();

    public static Comparator<DocumentModel> sortNameAZComparator_DocModel = Comparator.comparing(DocumentModel::getFileName_DocModel);

    @Override
    public int compareTo(DocumentModel o_DocModel) {
        return Long.compare(this.getLastModified_DocModel(), o_DocModel.getLastModified_DocModel());
    }

    public void setFileUri_DocModel(String fileUri_DocModel) {
        this.fileUri_DocModel = fileUri_DocModel;
    }

    public String getFileUri_DocModel() {
        return fileUri_DocModel;
    }

    public void setStarred_DocModel(boolean starred_DocModel) {
        isStarred_DocModel = starred_DocModel;
    }

    public void setLength_DocModel(Long length_DocModel) {
        this.length_DocModel = length_DocModel;
    }

    public Long getLength_DocModel() {
        return length_DocModel;
    }

    public void setLastModified_DocModel(Long lastModified_DocModel) {
        this.lastModified_DocModel = lastModified_DocModel;
    }

    public Long getLastModified_DocModel() {
        return lastModified_DocModel;
    }

    public void setFileName_DocModel(String fileName_DocModel) {
        this.fileName_DocModel = fileName_DocModel;
    }

    public String getFileName_DocModel() {
        return fileName_DocModel;
    }

    public void setSrcImage_DocModel(int srcImage_DocModel) {
        this.srcImage_DocModel = srcImage_DocModel;
    }

    public int getSrcImage_DocModel() {
        return srcImage_DocModel;
    }

    public void setChecked_DocModel(boolean checked_DocModel) {
        isChecked_DocModel = checked_DocModel;
    }

    public DocumentModel(String fileName_DocModel, Long lastModified_DocModel, Long length_DocModel, boolean isStarred_DocModel, String fileUri_DocModel, int srcImage_DocModel) {

        this.fileName_DocModel = fileName_DocModel;
        this.lastModified_DocModel = lastModified_DocModel;
        this.length_DocModel = length_DocModel;
        this.isStarred_DocModel = isStarred_DocModel;
        this.fileUri_DocModel = fileUri_DocModel;
        this.srcImage_DocModel = srcImage_DocModel;
    }

    public DocumentModel() {
    }

    public boolean isChecked_DocModel() {
        return isChecked_DocModel;
    }
}