package com.example.documenpro.model;

import android.net.Uri;

public class PDFPage {
    private int pageNumber;
    private Uri thumbnailUri;


    private boolean isChecked;

    public PDFPage(int i, Uri uri) {
        this.pageNumber = i;
        this.thumbnailUri = uri;
    }


    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    public void setPageNumber(int i) {
        this.pageNumber = i;
    }

    public Uri getThumbnailUri() {
        return this.thumbnailUri;
    }

    public void setThumbnailUri(Uri uri) {
        this.thumbnailUri = uri;
    }
}
