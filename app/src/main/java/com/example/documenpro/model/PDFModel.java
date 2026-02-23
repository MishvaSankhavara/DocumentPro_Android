package com.example.documenpro.model;

import java.io.Serializable;
import java.util.Comparator;

public class PDFModel implements Comparable<PDFModel>,Serializable {
    private String absolutePath;
    private String createAt;
    private boolean isDirectory;
    private boolean isStarred;
    private Long lastModified;
    private Long length;
    private String name;
    private String fileUri;

    private boolean isProtected;
    private String urlThumbnail;

    private boolean isChecked;


    public PDFModel() {
    }

    public PDFModel(String absolutePath, String createAt, boolean isDirectory, boolean isStarred, Long lastModified, Long length, String name, String fileUri, boolean isProtected, String urlThumbnail) {
        this.absolutePath = absolutePath;
        this.createAt = createAt;
        this.isDirectory = isDirectory;
        this.isStarred = isStarred;
        this.lastModified = lastModified;
        this.length = length;
        this.name = name;
        this.fileUri = fileUri;
        this.isProtected = isProtected;
        this.urlThumbnail = urlThumbnail;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isProtected() {
        return isProtected;
    }

    public void setProtected(boolean aProtected) {
        isProtected = aProtected;
    }

    public String getUrlThumbnail() {
        return urlThumbnail;
    }

    public void setUrlThumbnail(String urlThumbnail) {
        this.urlThumbnail = urlThumbnail;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public boolean isStarred() {
        return isStarred;
    }

    public void setStarred(boolean starred) {
        isStarred = starred;
    }

    public Long getLastModified() {
        return lastModified;
    }

    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    @Override
    public int compareTo(PDFModel pdfModel) {
        return 0;
    }

    public static Comparator<PDFModel> sortNameAZComparator = Comparator.comparing(PDFModel::getName);
    public static Comparator<PDFModel> sortNameZAComparator = sortNameAZComparator.reversed();
    public static Comparator<PDFModel> sortFileSizeAscendingComparator = Comparator.comparingLong(PDFModel::getLength);
    public static Comparator<PDFModel> sortFileSizeDescendingComparator = sortFileSizeAscendingComparator.reversed();
    public static Comparator<PDFModel> sortDateAscendingComparator = Comparator.comparingLong(PDFModel::getLastModified);
    public static Comparator<PDFModel> sortDateDescendingComparator = sortDateAscendingComparator.reversed();
}
