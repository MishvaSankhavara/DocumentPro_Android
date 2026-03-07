package com.artifex.sonui.editor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.supportv1.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.artifex.solib.ConfigOptions;
import com.artifex.solib.SOSelectionLimits;
import com.artifex.solib.c;

import com.example.documenpro.AppGlobalConstants;
import com.example.documenpro.R;
import com.example.documenpro.adapter_reader.ColorSelectionAdapter;
import com.example.documenpro.ui.customviews.EditBtn;
import com.example.documenpro.ui.customviews.seekbar.RangeSeekBarChangeListener;
import com.example.documenpro.ui.customviews.seekbar.RangeSeekBar;
import com.example.documenpro.utils.Utils;

public class NUIDocViewPdf extends NUIDocView {
    private EditBtn btnHighLight;
    private
    EditBtn btnDeleteNote;
    FrameLayout drawSizeOut;
    View drawSizeIn;

    private EditBtn btnDrawNew;
    private LinearLayout llBottomDraw;

    private RecyclerView recyclerViewColor;
    private RangeSeekBar seekBarThickness;

    private TextView tvSize;


    private boolean q = false;

    public NUIDocViewPdf(Context var1) {
        super(var1);
        this.a(var1);
    }

    public NUIDocViewPdf(Context var1, AttributeSet var2) {
        super(var1, var2);
        this.a(var1);
    }

    public NUIDocViewPdf(Context var1, AttributeSet var2, int var3) {
        super(var1, var2, var3);
        this.a(var1);
    }


    private void a(Context var1) {
    }

    private void b() {
        if (this.btnSaveAsPdf != null) {
            this.btnSaveAsPdf.setVisibility(GONE);
        }
    }

    protected void afterFirstLayoutComplete() {
        super.afterFirstLayoutComplete();
        this.btnHighLight = (EditBtn) this.createToolbarButton(R.id.btnHighlight);
        this.drawSizeOut = this.findViewById(R.id.paint_size_container);
        this.drawSizeIn = this.findViewById(R.id.paint_size);
        this.btnDeleteNote = (EditBtn) this.createToolbarButton(R.id.btnDelete);
        this.btnDrawNew = (EditBtn) this.createToolbarButton(R.id.btnDrawNew);

        this.llBottomDraw = this.findViewById(R.id.pdf_bottom_draw);
        this.recyclerViewColor = this.findViewById(R.id.recyclerColor);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewColor.setLayoutManager(layoutManager);
        recyclerViewColor.setHasFixedSize(true);

        tvSize = this.findViewById(R.id.sizeTv);

        ColorSelectionAdapter adapter = new ColorSelectionAdapter(AppGlobalConstants.getColorDrawList(), 3, new ColorSelectionAdapter.ColorChangedListener_ColorSelection() {
            @Override
            public void onColorChanged(String var1) {
                DocPdfView var2 = NUIDocViewPdf.this.getPdfDocView();
                int var2x = Color.parseColor(var1);
                var2.setInkLineColor(var2x);
            }
        });
        recyclerViewColor.setAdapter(adapter);


        this.b();
        seekBarThickness = this.findViewById(R.id.thickness_seekBar);

        seekBarThickness.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    NUIDocViewPdf.this.drawSizeOut.setVisibility(VISIBLE);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    NUIDocViewPdf.this.drawSizeOut.setVisibility(GONE);
                }
                return false;
            }
        });
        seekBarThickness.setOnRangeChangedListener(new RangeSeekBarChangeListener() {
            @Override
            public void onRangeValueChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                activity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int value = (int) leftValue;
                        tvSize.setText(String.valueOf(value));
                        DocPdfView docPdfView = NUIDocViewPdf.this.getPdfDocView();
                        if (docPdfView.getDrawMode()) {
                            float var2 = docPdfView.getInkLineThickness();
                            docPdfView.setInkLineThickness(value);
                        }
                        LayoutParams layoutParams = (LayoutParams) drawSizeIn.getLayoutParams();
                        layoutParams.width = value;
                        layoutParams.height = value;
                        layoutParams.gravity = 17;
                        drawSizeIn.setLayoutParams(layoutParams);

                    }
                });
            }

            @Override
            public void onTrackingStarted(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onTrackingStopped(RangeSeekBar view, boolean isLeft) {

            }
        });

    }

    protected void checkXFA() {
        if (ConfigOptions.a().x()) {
            boolean var1;
            var1 = this.mPageCount == 0;

            if (var1) {
                boolean var2 = this.getDoc().x();
                boolean var3 = this.getDoc().y();
                if (var2 && !var3) {
                    Utilities.showMessage((Activity) this.getContext(), this.getContext().getString(R.string.editor_xfa_title), this.getContext().getString(R.string.editor_xfa_body));
                }
            }
        }

    }

    protected PageAdapter createAdapter() {
        return new PageAdapter(this.activity(), this, 2);
    }

    protected void createEditButtons() {
    }

    protected void createEditButtons2() {
    }

    @Override
    protected void createRecyclerColor() {

    }

    protected void createInputView() {
    }

    protected void createInsertButtons() {
    }

    protected DocView createMainView(Activity var1) {
        return new DocPdfView(var1);
    }


    public int getBorderColor() {
        return ContextCompat.getColor(this.getContext(), R.color.editor_header_pdf_color);
    }

    public InputView getInputView() {
        return null;
    }

    protected int getLayoutId() {
        return R.layout.document_view_pdf;
    }

    public DocPdfView getPdfDocView() {
        return (DocPdfView) this.getDocView();
    }


    protected void goBack() {
        super.goBack();
    }

    protected boolean inputViewHasFocus() {
        return false;
    }

//    @Override
//    protected boolean isRedactionMode() {
//        String var1 = this.getCurrentTab();
//        return var1 != null && var1.equals("REDACT");
//    }

    protected void layoutAfterPageLoad() {
    }

    public void onClick(View var1) {
        super.onClick(var1);

        if (var1 == this.btnEditTab) {
            if (this.getPdfDocView().getDrawMode()) {
                this.getPdfDocView().onDrawMode();
            }
            try {
                this.getDoc().clearSelection();
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        if (var1 == this.btnDrawNew) {
            if (llBottomDraw.getVisibility() == VISIBLE) {
                Utils.showHideView(getContext(), llBottomDraw, false, R.dimen.cm_dp_102);
                this.onDrawButton();
                btnDrawNew.setChoose(false);


            } else if (llBottomDraw.getVisibility() == GONE) {
                Utils.showHideView(getContext(), llBottomDraw, true, R.dimen.cm_dp_102);
                this.onDrawButton();
                btnDrawNew.setChoose(true);
            }
        }
        if (var1 == this.btnHighLight) {
            this.onHighlightButton();
        }

        if (var1 == this.btnDeleteNote) {
            this.onDeleteButton();
        }

        if (var1 == this.btnCloseEdit) {
            this.getPdfDocView().saveNoteData();


            if (llBottomDraw.getVisibility() == VISIBLE) {
                this.onDrawButton();

                Utils.showHideView(getContext(), llBottomDraw, false, R.dimen.cm_dp_102);

            }
        }

    }


    public void onDeleteButton() {
        DocPdfView var2 = this.getPdfDocView();
        if (this.getDoc().getSelectionCanBeDeleted()) {
            try {
                this.getDoc().selectionDelete();
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        } else {
            if (!var2.getDrawMode()) {
                return;
            }

            var2.clearInk();
        }

        this.updateUIAppearance();
    }

    protected void onDeviceSizeChange() {
        super.onDeviceSizeChange();
        a.b();
    }

    protected void onDocCompleted() {
        if (!this.mFinished) {
            try {
                if (!this.q) {
                    this.mSession.getDoc().clearSelection();
                    this.q = true;
                }
                this.mPageCount = this.mSession.getDoc().r();
                String var1;
                if (this.mPageCount <= 0) {
                    var1 = Utilities.getOpenErrorDescription(this.getContext(), 17);
                    Utilities.showMessage((Activity) this.getContext(), this.getContext().getString(R.string.editor_error), var1);
                } else {
                    this.mAdapter.setCount(this.mPageCount);
                    this.layoutNow();
                    com.artifex.solib.k.a(this.getDoc(), new com.artifex.solib.k.a() {
                        public void a(int var1, int var2, int var3, String var4, String var5, float var6, float var7) {

                        }
                    });
                    if (this.mSession.getDoc().getAuthor() == null) {
                        var1 = Utilities.getApplicationName(this.activity());
                        var1 = Utilities.getStringPreference(Utilities.getPreferencesObject(this.activity(), "general"), "DocAuthKey", var1);
                        this.mSession.getDoc().setAuthor(var1);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void onDrawButton() {
        try {
            this.getPdfDocView().onDrawMode();
            this.updateUIAppearance();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

    }

    public void onHighlightButton() {
        try {
            this.getDoc().addHighlightAnnotation();
            this.getDoc().clearSelection();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

    }

//    public void onNoteButton() {
//        this.getPdfDocView().onNoteMode();
//        this.updateUIAppearance();
//    }

    protected void onPageLoaded(int var1) {
        this.checkXFA();
        super.onPageLoaded(var1);
    }

//    public void onRedactApply() {
//        Utilities.yesNoMessage((Activity) this.getContext(), "", this.getContext().getString(R.string.sodk_editor_redact_confirm_apply_body), this.getContext().getString(R.string.sodk_editor_yes), this.getContext().getString(R.string.sodk_editor_no), new Runnable() {
//            public void run() {
//                c var1 = (c) NUIDocViewPdf.this.getDoc();
//                var1.u();
//                var1.clearSelection();
//                NUIDocViewPdf.this.updateUIAppearance();
//            }
//        }, new Runnable() {
//            public void run() {
//            }
//        });
//    }

//    public void onRedactMark(View var1) {
//        c var2 = (c) this.getDoc();
//        var2.s();
//        var2.clearSelection();
//        this.updateUIAppearance();
//    }

//    public void onRedactRemove(View var1) {
//        if (this.getDoc().getSelectionCanBeDeleted()) {
//            this.getDoc().selectionDelete();
//            this.updateUIAppearance();
//        }
//
//    }

    public void onRedoButton(View var1) {
        super.onRedoButton(var1);
    }


    protected void onSearch() {
        super.onSearch();
    }


    public void onUndoButton(View var1) {
        super.onUndoButton(var1);
    }

    protected void preSaveQuestion(final Runnable var1, final Runnable var2) {
        if (!((c) this.getDoc()).t()) {
            if (var1 != null) {
                var1.run();
            }

        } else {
            Utilities.yesNoMessage((Activity) this.getContext(), "", this.getContext().getString(R.string.editor_redact_confirm_save), this.getContext().getString(R.string.editor_yes), this.getContext().getString(R.string.editor_no), new Runnable() {
                public void run() {
                    if (var1 != null) {
                        var1.run();
                    }

                }
            }, new Runnable() {
                public void run() {
                    if (var2 != null) {
                        var2.run();
                    }

                }
            });
        }
    }

    protected void prepareToGoBack() {
        if (this.mSession == null || this.mSession.getDoc() != null) {
            if (this.getPdfDocView() != null) {
                this.getPdfDocView().resetModes();
            }

        }
    }

    public void reloadFile() {
        c cVar = (c) getDoc();
        String openedPath = this.mSession.getFileState().getOpenedPath();

        if (cVar != null && openedPath != null) {
            if (!cVar.h()) {
                if (!cVar.f()) {
                    if (com.artifex.solib.a.a(openedPath) < cVar.a()) {
                        return;
                    }
                } else {
                    return;
                }
            }
            cVar.a(false);
            cVar.a(openedPath);
            final ProgressDialog createAndShowWaitSpinner = Utilities.createAndShowWaitSpinner(getContext());
            cVar.a(new c.c() {
                public void a() {
                    if (NUIDocViewPdf.this.getDocView() != null) {
                        NUIDocViewPdf.this.getDocView().onReloadFile();
                    }
                    if (NUIDocViewPdf.this.usePagesView() && NUIDocViewPdf.this.getDocListPagesView() != null) {
                        NUIDocViewPdf.this.getDocListPagesView().onReloadFile();
                    }
                    createAndShowWaitSpinner.dismiss();
                }
            });
        } else {
            Log.i("Thangfix", "loi null roi");
        }


    }


    public void setConfigurableButtons() {
        super.setConfigurableButtons();
        this.b();
    }

    protected boolean shouldConfigureSaveAsPDFButton() {
        return false;
    }

    protected void updateUIAppearance() {
        DocPdfView var1 = this.getPdfDocView();
        this.updateSaveUIAppearance();
        this.updateUndoUIAppearance();
        SOSelectionLimits var2 = this.getDocView().getSelectionLimits();
        if (var2 != null && var2.getIsActive()) {
            var2.getIsCaret();
        }

        boolean var3 = this.getDoc().getSelectionCanBeDeleted();
        boolean var4 = this.getDoc().getSelectionIsAlterableTextSelection();
        this.btnHighLight.setEnable(var4);


        boolean var6 = var1.getDrawMode();
        this.btnDrawNew.setChoose(var6);
        boolean var5 = ((DocPdfView) this.getDocView()).hasNotSavedInk();
        EditBtn var11 = this.btnDeleteNote;
        var5 = (var6 && var5) || var3;

        var11.setEnable(var5);
        var5 = !var6;
        var11.setEnable(var5);
        this.btnHighLight.setEnable(var5);

        c var12 = (c) this.getDoc();
        if (var3) {
            var12.n();
        }

        this.getPdfDocView().onSelectionChanged();
    }

    protected void updateUndoUIAppearance() {
        this.btnUndo.setVisibility(GONE);
        this.btnRedo.setVisibility(GONE);
    }
}
