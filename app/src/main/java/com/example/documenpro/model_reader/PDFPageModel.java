package com.example.documenpro.model_reader;

import android.net.Uri;

public class PDFPageModel {
    private int pageNumber_PDFPageModel;
    private Uri thumbnailUri_PDFPageModel;


    private boolean isChecked_PDFPageModel;

    public PDFPageModel(int i_PDFPageModel, Uri uri_PDFPageModel) {
        this.pageNumber_PDFPageModel = i_PDFPageModel;
        this.thumbnailUri_PDFPageModel = uri_PDFPageModel;
    }



    public void setChecked_PDFPageModel(boolean checked_PDFPageModel) {
        isChecked_PDFPageModel = checked_PDFPageModel;
    }

    public int getPageNumber_PDFPageModel() {
        return this.pageNumber_PDFPageModel;
    }


    public Uri getThumbnailUri_PDFPageModel() {
        return this.thumbnailUri_PDFPageModel;
    }

    public boolean isChecked_PDFPageModel() {
        return isChecked_PDFPageModel;
    }
}
