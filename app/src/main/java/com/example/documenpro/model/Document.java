package com.example.documenpro.model;

import java.util.Comparator;

public class Document implements Comparable<Document> {
    private String fileName;
    private Long lastModified;
    private Long length;
    private boolean isStarred;
    private String fileUri;

    private int srcImage;

    private boolean isChecked;

    public Document() {
    }

    public Document(String fileName, Long lastModified, Long length, boolean isStarred, String fileUri, int srcImage) {
        this.fileName = fileName;
        this.lastModified = lastModified;
        this.length = length;
        this.isStarred = isStarred;
        this.fileUri = fileUri;
        this.srcImage = srcImage;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getSrcImage() {
        return srcImage;
    }

    public void setSrcImage(int srcImage) {
        this.srcImage = srcImage;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    public boolean isStarred() {
        return isStarred;
    }

    public void setStarred(boolean starred) {
        isStarred = starred;
    }

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    @Override
    public int compareTo(Document o) {
        return Long.compare(this.getLastModified(), o.getLastModified());

    }

    public static Comparator<Document> sortNameAZComparator = Comparator.comparing(Document::getFileName);
    public static Comparator<Document> sortNameZAComparator = sortNameAZComparator.reversed();
    public static Comparator<Document> sortFileSizeAscendingComparator = Comparator.comparingLong(Document::getLength);
    public static Comparator<Document> sortFileSizeDescendingComparator = sortFileSizeAscendingComparator.reversed();
    public static Comparator<Document> sortDateAscendingComparator = Comparator.comparingLong(Document::getLastModified);
    public static Comparator<Document> sortDateDescendingComparator = sortDateAscendingComparator.reversed();

}