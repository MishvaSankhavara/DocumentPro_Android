package com.artifex.sonui.editor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.supportv1.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.artifex.solib.SOSelectionLimits;
import com.artifex.sonui.editor.ShapeDialog.Shape;
import com.artifex.sonui.editor.ShapeDialog.onSelectShapeListener;
import com.example.documenpro.R;
import com.example.documenpro.ui.customviews.BottomButtonView;

public class NUIDocViewPpt extends NUIDocView {
    boolean b = false;
    private BottomButtonView btnSlideshow;

    public NUIDocViewPpt(Context context) {
        super(context);
        this.a();
    }

    public NUIDocViewPpt(Context var1, AttributeSet var2) {
        super(var1, var2);
        this.a();
    }

    public NUIDocViewPpt(Context var1, AttributeSet var2, int var3) {
        super(var1, var2, var3);
        this.a();
    }

    private void a() {
    }

    @Override
    protected void afterFirstLayoutComplete() {
        super.afterFirstLayoutComplete();

//        this.btnInsertShape = (LinearLayout) this.createToolbarButton(R.id.insert_shape_button);
        this.btnSlideshow = (BottomButtonView) this.createToolbarButton(R.id.btnSlideShowTab);
    }


    public boolean canCanManipulatePages() {
        return this.mConfigOptions.c();
    }

    protected void createEditButtons() {
        super.createEditButtons();
    }

    protected DocView createMainView(Activity var1) {
        DocPowerPointView var2 = new DocPowerPointView(var1);
        this.btnListBullets.setVisibility(GONE);
        this.btnListNumbers.setVisibility(GONE);
        return var2;
    }


    public void doInsertImage(String var1) {
        var1 = Utilities.preInsertImage(this.getContext(), var1);
        int var2 = this.getTargetPageNumber();
        this.getDoc().clearSelection();
        this.getDoc().a(var2, var1);
        this.getDocView().scrollToPage(var2, false);
    }

    public int getBorderColor() {
        return ContextCompat.getColor(this.getContext(), R.color.sodk_editor_header_ppt_color);
    }

    protected int getLayoutId() {
        return R.layout.document_view_ppt;
    }


    @Override
    public void onClick(View var1) {
        super.onClick(var1);
        if (var1 == this.btnSlideshow) {
            this.onClickSlideshow();
        }

    }


    public void onClickSlideshow() {
        this.getDoc().clearSelection();
        this.getDoc().o();
        if (mSession != null && mSession.getDoc() != null) {
            SlideShowActivity.setSession(this.mSession);
            Intent intent = new Intent(this.getContext(), SlideShowActivity.class);
            intent.setAction("android.intent.action.VIEW");
            this.activity().startActivity(intent);
        }

    }

    protected void onDocCompleted() {
        super.onDocCompleted();
    }

    public void onInsertShapeButton(View var1) {
        (new ShapeDialog(this.getContext(), var1, new onSelectShapeListener() {
            public void onSelectShape(Shape var1) {
                int var2 = NUIDocViewPpt.this.getTargetPageNumber();
                NUIDocViewPpt var3 = NUIDocViewPpt.this;
                var3.b = true;
                var3.getDoc().clearSelection();
                NUIDocViewPpt.this.getDoc().a(var2, var1.shape, var1.properties);
                NUIDocViewPpt.this.getDocView().scrollToPage(var2, false);
            }
        })).show();
    }

    public void onSelectionChanged() {
        super.onSelectionChanged();
        this.updateUIAppearance();
        if (this.b) {
            this.getDocView().scrollSelectionIntoView();
        }

        this.b = false;
    }

    public boolean showKeyboard() {
        SOSelectionLimits var1 = this.getDocView().getSelectionLimits();
        if (var1 != null && var1.getIsActive() && !this.getDoc().getSelectionCanBeAbsolutelyPositioned()) {
            super.showKeyboard();
            return true;
        } else {
            return false;
        }
    }

    protected void updateEditUIAppearance() {
        super.updateEditUIAppearance();
    }

    protected void updateInsertUIAppearance() {
        if (this.btnInsertImage != null && this.mConfigOptions.k()) {
            this.btnInsertImage.setEnabled(true);
        }

        if (this.btnInsertPhoto != null && this.mConfigOptions.l()) {
            this.btnInsertPhoto.setEnabled(true);
        }

    }

    protected void updateReviewUIAppearance() {
    }

    protected void updateUIAppearance() {
        super.updateUIAppearance();
        boolean var1 = this.getDoc().selectionIsAutoshapeOrImage();
    }
}
