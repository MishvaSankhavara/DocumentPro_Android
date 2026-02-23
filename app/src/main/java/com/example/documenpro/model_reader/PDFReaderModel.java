package com.example.documenpro.model_reader;

import java.io.Serializable;
import java.util.Comparator;

public class PDFReaderModel implements Comparable<PDFReaderModel>, Serializable {

    private boolean isChecked_PDFModel;
    private String urlThumbnail_PDFModel;
    private boolean isProtected_PDFModel;
    private String fileUri_PDFModel;
    private String name_PDFModel;
    private Long length_PDFModel;
    private Long lastModified_PDFModel;
    private boolean isStarred_PDFModel;
    private boolean isDirectory_PDFModel;
    private String createAt_PDFModel;
    private String absolutePath_PDFModel;

    @Override
    public int compareTo(PDFReaderModel pdfModel) {
        return 0;
    }

    public void setFileUri_PDFModel(String fileUri_PDFModel) {
        this.fileUri_PDFModel = fileUri_PDFModel;
    }

    public String getFileUri_PDFModel() {
        return fileUri_PDFModel;
    }

    public void setName_PDFModel(String name_PDFModel) {
        this.name_PDFModel = name_PDFModel;
    }

    public String getName_PDFModel() {
        return name_PDFModel;
    }

    public void setLength_PDFModel(Long length_PDFModel) {
        this.length_PDFModel = length_PDFModel;
    }

    public Long getLength_PDFModel() {
        return length_PDFModel;
    }

    public void setLastModified_PDFModel(Long lastModified_PDFModel) {
        this.lastModified_PDFModel = lastModified_PDFModel;
    }

    public Long getLastModified_PDFModel() {
        return lastModified_PDFModel;
    }

    public void setDirectory_PDFModel(boolean directory_PDFModel) {
        isDirectory_PDFModel = directory_PDFModel;
    }

    public void setAbsolutePath_PDFModel(String absolutePath_PDFModel) {
        this.absolutePath_PDFModel = absolutePath_PDFModel;
    }

    public String getAbsolutePath_PDFModel() {
        return absolutePath_PDFModel;
    }

    public void setProtected_PDFModel(boolean protected_PDFModel) {
        isProtected_PDFModel = protected_PDFModel;
    }

    public boolean isProtected_PDFModel() {
        return isProtected_PDFModel;
    }

    public void setChecked_PDFModel(boolean checked_PDFModel) {
        isChecked_PDFModel = checked_PDFModel;
    }

    public PDFReaderModel(String absolutePath_PDFModel,
                          String createAt_PDFModel,
                          boolean isDirectory_PDFModel,
                          boolean isStarred_PDFModel,
                          Long lastModified_PDFModel,
                          Long length_PDFModel,
                          String name_PDFModel,
                          String fileUri_PDFModel,
                          boolean isProtected_PDFModel,
                          String urlThumbnail_PDFModel) {

        this.absolutePath_PDFModel = absolutePath_PDFModel;
        this.createAt_PDFModel = createAt_PDFModel;
        this.isDirectory_PDFModel = isDirectory_PDFModel;
        this.isStarred_PDFModel = isStarred_PDFModel;
        this.lastModified_PDFModel = lastModified_PDFModel;
        this.length_PDFModel = length_PDFModel;
        this.name_PDFModel = name_PDFModel;
        this.fileUri_PDFModel = fileUri_PDFModel;
        this.isProtected_PDFModel = isProtected_PDFModel;
        this.urlThumbnail_PDFModel = urlThumbnail_PDFModel;
    }

    public PDFReaderModel() {
    }

    public String getUrlThumbnail_PDFModel() {
        return urlThumbnail_PDFModel;
    }

    public boolean isChecked_PDFModel() {
        return isChecked_PDFModel;
    }

    public boolean isStarred_PDFModel() {
        return isStarred_PDFModel;
    }

    public boolean isDirectory_PDFModel() {
        return isDirectory_PDFModel;
    }

    public String getCreateAt_PDFModel() {
        return createAt_PDFModel;
    }
}