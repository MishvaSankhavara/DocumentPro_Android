package com.artifex.sonui.editor;

import android.content.Context;
import android.supportv1.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.artifex.solib.SODoc;
import com.example.documenpro.R;

public class NUIDocViewDoc extends NUIDocView {

    public NUIDocViewDoc(Context var1) {
        super(var1);
        this.a();
    }

    public NUIDocViewDoc(Context var1, AttributeSet var2) {
        super(var1, var2);
        this.a();
    }

    public NUIDocViewDoc(Context var1, AttributeSet var2, int var3) {
        super(var1, var2, var3);
        this.a();
    }

    private void a() {
    }
    public int getBorderColor() {
        return ContextCompat.getColor(this.getContext(), R.color.editor_header_doc_color);
    }

    protected int getLayoutId() {
        return R.layout.document_view_doc;
    }

    public void onClick(View var1) {
        super.onClick(var1);
    }
    protected void prepareToGoBack() {
        DocView var1 = this.getDocView();
        if (var1 != null) {
            var1.preNextPrevTrackedChange();
        }

    }

    @Override
    protected void updateReviewUIAppearance() {
        SODoc var1 = this.getDocView().getDoc();
        var1.selectionIsReviewable();
    }

}
