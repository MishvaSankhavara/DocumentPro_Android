package com.artifex.sonui.editor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.supportv1.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.artifex.solib.ConfigOptions;
import com.artifex.solib.SOSelectionLimits;
import com.artifex.solib.c;
import com.artifex.mupdf.fitz.PDFAnnotation;

import com.example.documenpro.AppGlobalConstants;
import com.example.documenpro.R;
import com.example.documenpro.adapter_reader.ColorSelectionAdapter;
import com.example.documenpro.ui.customviews.EditBtn;
import com.example.documenpro.ui.customviews.seekbar.RangeSeekBarChangeListener;
import com.example.documenpro.ui.customviews.seekbar.RangeSeekBar;
import com.example.documenpro.utils.Utils;

public class NUIDocViewPdf extends NUIDocView {
    private EditBtn btnHighLight;
    private EditBtn btnText;
    private EditBtn btnDeleteNote;
    FrameLayout drawSizeOut;
    View drawSizeIn;

    private EditBtn btnDrawNew;
    private LinearLayout llBottomDraw;

    // For scroll synchronization
    private ArrayList<View> mAnnotationViews = new ArrayList<>();

    private RecyclerView recyclerViewColor;
    private RangeSeekBar seekBarThickness;

    private TextView tvSize;

    private boolean q = false;
    private View mActiveAnnotationView;

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

    private void repositionAnnotations() {
        int[] rootLocation = new int[2];
        this.getLocationOnScreen(rootLocation);

        for (View container : mAnnotationViews) {
            Object[] tag = (Object[]) container.getTag();
            if (tag != null && tag.length >= 2) {
                DocPageView pageView = (DocPageView) tag[0];
                Point pagePoint = (Point) tag[1];

                Point screenPoint = pageView.pageToScreen(pagePoint);
                container.setX(screenPoint.x - rootLocation[0] - (container.getWidth() / 2.0f));
                container.setY(screenPoint.y - rootLocation[1] - (container.getHeight() / 2.0f));
            }
        }
    }

    private void showTextBoxAt(float rawX, float rawY) {
        DocView docView = getPdfDocView();
        if (docView == null)
            return;

        DocPageView targetPage = null;
        for (int i = 0; i < docView.getChildCount(); i++) {
            View child = docView.getChildAt(i);
            if (child instanceof DocPageView) {
                DocPageView page = (DocPageView) child;
                Rect r = page.screenRect();
                if (r.contains((int) rawX, (int) rawY)) {
                    targetPage = page;
                    break;
                }
            }
        }

        if (targetPage == null)
            return;

        int[] rootLocation = new int[2];
        this.getLocationOnScreen(rootLocation);
        float x = rawX - rootLocation[0];
        float y = rawY - rootLocation[1];

        // Map screen point to page point
        Point pagePoint = targetPage.screenToPage((int) rawX, (int) rawY);

        // Inflate the advanced textbox layout
        final View container = LayoutInflater.from(getContext()).inflate(R.layout.layout_text_annotation, this, false);
        container.setTag(new Object[] { targetPage, pagePoint }); // Store anchor info

        final EditText editText = container.findViewById(R.id.annotation_edit_text);
        View btnCancel = container.findViewById(R.id.btn_cancel);
        View btnFlip = container.findViewById(R.id.btn_flip);
        View btnResize = container.findViewById(R.id.btn_resize);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        container.setLayoutParams(lp);
        container.setX(x - Utilities.convertDpToPixel(50)); // Center it roughly
        container.setY(y - Utilities.convertDpToPixel(20));

        this.mActiveAnnotationView = container;
        this.mAnnotationViews.add(container);

        // Cancel/Delete logic
        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                removeView(container);
                mAnnotationViews.remove(container);
                if (mActiveAnnotationView == container)
                    mActiveAnnotationView = null;
            }
        });

        // Flip logic
        btnFlip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                float currentScale = editText.getScaleX();
                editText.setScaleX(currentScale * -1);
            }
        });

        // Resize logic
        btnResize.setOnTouchListener(new OnTouchListener() {
            private float lastX, lastY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = event.getRawX();
                        lastY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        float dx = event.getRawX() - lastX;
                        float dy = event.getRawY() - lastY;

                        ViewGroup.LayoutParams elp = editText.getLayoutParams();
                        elp.width = (int) Math.max(Utilities.convertDpToPixel(50), editText.getWidth() + dx);
                        elp.height = (int) Math.max(Utilities.convertDpToPixel(30), editText.getHeight() + dy);
                        editText.setLayoutParams(elp);

                        lastX = event.getRawX();
                        lastY = event.getRawY();
                        return true;
                }
                return false;
            }
        });

        // Movement logic (Drag the whole container)
        container.setOnTouchListener(new OnTouchListener() {
            private float lastX, lastY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = event.getRawX();
                        lastY = event.getRawY();
                        editText.requestFocus();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        float dx = event.getRawX() - lastX;
                        float dy = event.getRawY() - lastY;
                        v.setX(v.getX() + dx);
                        v.setY(v.getY() + dy);

                        // Update the anchored page point when moved manually
                        Object[] tag = (Object[]) v.getTag();
                        if (tag != null && tag.length >= 2) {
                            DocPageView pageView = (DocPageView) tag[0];
                            // Use container's center for anchoring
                            int[] loc = new int[2];
                            v.getLocationOnScreen(loc);
                            int centerX = loc[0] + v.getWidth() / 2;
                            int centerY = loc[1] + v.getHeight() / 2;

                            Point newPagePoint = pageView.screenToPage(centerX, centerY);
                            tag[1] = newPagePoint;
                        }

                        lastX = event.getRawX();
                        lastY = event.getRawY();
                        return true;
                }
                return false;
            }
        });

        // Focus behavior
        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                editText.setCursorVisible(hasFocus);
                // Optionally hide/show handles based on focus
                int visibility = hasFocus ? VISIBLE : GONE;
                container.findViewById(R.id.btn_cancel).setVisibility(visibility);
                container.findViewById(R.id.btn_flip).setVisibility(visibility);
                container.findViewById(R.id.btn_resize).setVisibility(visibility);

                // Also update the border visibility or style
                android.graphics.drawable.GradientDrawable gd = (android.graphics.drawable.GradientDrawable) editText
                        .getBackground();
                if (gd != null) {
                    gd.setStroke(hasFocus ? 3 : 1, hasFocus ? Color.BLACK : Color.TRANSPARENT);
                }
            }
        });

        this.addView(container);
        editText.requestFocus();

        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }

        setIsAddTextMode(false);
        this.updateUIAppearance();
    }

    protected void afterFirstLayoutComplete() {
        super.afterFirstLayoutComplete();

        // Ensure we listen for scroll/zoom changes to reposition
        DocView docView = getPdfDocView();
        if (docView != null) {
            docView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    repositionAnnotations();
                }
            });
        }
        this.btnHighLight = (EditBtn) this.createToolbarButton(R.id.btnHighlight);
        this.drawSizeOut = this.findViewById(R.id.paint_size_container);
        this.drawSizeIn = this.findViewById(R.id.paint_size);
        this.btnDeleteNote = (EditBtn) this.createToolbarButton(R.id.btnDelete);
        this.btnDrawNew = (EditBtn) this.createToolbarButton(R.id.btnDrawNew);
        this.btnText = (EditBtn) this.createToolbarButton(R.id.btnText);

        this.llBottomDraw = this.findViewById(R.id.pdf_bottom_draw);
        this.recyclerViewColor = this.findViewById(R.id.recyclerColor);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,
                false);
        recyclerViewColor.setLayoutManager(layoutManager);
        recyclerViewColor.setHasFixedSize(true);

        tvSize = this.findViewById(R.id.sizeTv);

        ColorSelectionAdapter adapter = new ColorSelectionAdapter(AppGlobalConstants.getColorDrawList(), 3,
                new ColorSelectionAdapter.ColorChangedListener_ColorSelection() {
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

        // Handle tap-to-add-text for PDFs when Text tool is active.
        if (getPdfDocView() != null) {
            getPdfDocView().setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (!getIsAddTextMode()) {
                        if (mActiveAnnotationView != null && event.getAction() == MotionEvent.ACTION_DOWN) {
                            EditText et = mActiveAnnotationView.findViewById(R.id.annotation_edit_text);
                            if (et != null)
                                et.clearFocus();
                            Utilities.hideKeyboard(getContext());
                        }
                        return false;
                    }

                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        float rawX = event.getRawX();
                        float rawY = event.getRawY();
                        showTextBoxAt(rawX, rawY);
                        return true;
                    }
                    return true;
                }
            });
        }

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
                    Utilities.showMessage((Activity) this.getContext(),
                            this.getContext().getString(R.string.editor_xfa_title),
                            this.getContext().getString(R.string.editor_xfa_body));
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

    @Override
    protected void createInputView() {
        mInputView = new InputView(this.getContext(), null, this);
        this.addView(mInputView);
    }

    protected void createInsertButtons() {
    }

    protected DocView createMainView(Activity var1) {
        return new DocPdfView(var1);
    }

    public int getBorderColor() {
        return ContextCompat.getColor(this.getContext(), R.color.editor_header_pdf_color);
    }

    private InputView mInputView;

    @Override
    public InputView getInputView() {
        return mInputView;
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

    // @Override
    // protected boolean isRedactionMode() {
    // String var1 = this.getCurrentTab();
    // return var1 != null && var1.equals("REDACT");
    // }

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
            if (this.getPdfDocView().getDrawMode()) {
                if (llBottomDraw.getVisibility() == VISIBLE) {
                    Utils.showHideView(getContext(), llBottomDraw, false, R.dimen.cm_dp_102);
                }
                this.onDrawButton();
                btnDrawNew.setChoose(false);
            }
            this.onHighlightButton();
        }

        if (var1 == this.btnText) {
            this.onTextButton();
        }

        if (var1 == this.btnDrawNew || var1 == this.btnHighLight || var1 == this.btnDeleteNote) {
            this.mIsAddTextMode = false;
            if (mActiveAnnotationView != null) {
                EditText et = mActiveAnnotationView.findViewById(R.id.annotation_edit_text);
                if (et != null)
                    et.clearFocus();
                Utilities.hideKeyboard(getContext());
            }
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
        // Handle deletion of our custom annotation view first
        if (mActiveAnnotationView != null) {
            EditText et = mActiveAnnotationView.findViewById(R.id.annotation_edit_text);
            if (et != null && et.isFocused()) {
                this.removeView(mActiveAnnotationView);
                mActiveAnnotationView = null;
                this.updateUIAppearance();
                return;
            }
        }

        DocPdfView var2 = this.getPdfDocView();
        c var12 = (c) this.getDoc();
        SOSelectionLimits var13 = var2.getSelectionLimits();
        boolean hasSelection = var13 != null && var13.getIsActive();

        // 1. Try deleting the currently selected annotation
        PDFAnnotation ann = var12.m();
        if (ann != null) {
            try {
                ann.destroy();
                ann.update();
            } catch (Exception e) {
                e.printStackTrace();
            }

            var12.clearSelection();
            this.updateUIAppearance();
            return;
        }

        // 2. Try deleting highlight annotation explicitly
        try {
            var12.deleteHighlightAnnotation();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 3. If text selection can be deleted
        if (this.getDoc().getSelectionCanBeDeleted()) {
            try {
                this.getDoc().selectionDelete();
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        // 4. If selection exists but highlight still not removed
        if (hasSelection) {
            var12.clearSelection();
        } else {
            // 5. Handle drawing erase
            if (var2.getDrawMode()) {
                var2.clearInk();
            }
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
                    Utilities.showMessage((Activity) this.getContext(),
                            this.getContext().getString(R.string.editor_error), var1);
                } else {
                    this.mAdapter.setCount(this.mPageCount);
                    this.layoutNow();
                    com.artifex.solib.k.a(this.getDoc(), new com.artifex.solib.k.a() {
                        public void a(int var1, int var2, int var3, String var4, String var5, float var6, float var7) {

                        }
                    });
                    if (this.mSession.getDoc().getAuthor() == null) {
                        var1 = Utilities.getApplicationName(this.activity());
                        var1 = Utilities.getStringPreference(Utilities.getPreferencesObject(this.activity(), "general"),
                                "DocAuthKey", var1);
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
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    public void onTextButton() {
        this.mIsAddTextMode = !this.mIsAddTextMode;
        Log.d("TEXT_TOOL_DEBUG", "onTextButton called, mIsAddTextMode: " + this.mIsAddTextMode);
        if (this.mIsAddTextMode) {
            this.getPdfDocView().resetModes();
        }
        this.updateUIAppearance();
    }

    protected void onPageLoaded(int var1) {
        this.checkXFA();
        super.onPageLoaded(var1);
    }

    // public void onRedactApply() {
    // Utilities.yesNoMessage((Activity) this.getContext(), "",
    // this.getContext().getString(R.string.sodk_editor_redact_confirm_apply_body),
    // this.getContext().getString(R.string.sodk_editor_yes),
    // this.getContext().getString(R.string.sodk_editor_no), new Runnable() {
    // public void run() {
    // c var1 = (c) NUIDocViewPdf.this.getDoc();
    // var1.u();
    // var1.clearSelection();
    // NUIDocViewPdf.this.updateUIAppearance();
    // }
    // }, new Runnable() {
    // public void run() {
    // }
    // });
    // }

    // public void onRedactMark(View var1) {
    // c var2 = (c) this.getDoc();
    // var2.s();
    // var2.clearSelection();
    // this.updateUIAppearance();
    // }

    // public void onRedactRemove(View var1) {
    // if (this.getDoc().getSelectionCanBeDeleted()) {
    // this.getDoc().selectionDelete();
    // this.updateUIAppearance();
    // }
    //
    // }

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
            Utilities.yesNoMessage((Activity) this.getContext(), "",
                    this.getContext().getString(R.string.editor_redact_confirm_save),
                    this.getContext().getString(R.string.editor_yes), this.getContext().getString(R.string.editor_no),
                    new Runnable() {
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
        boolean isActive = var2 != null && var2.getIsActive();

        boolean var6 = var1.getDrawMode();
        this.btnDrawNew.setChoose(var6);
        boolean var5 = ((DocPdfView) this.getDocView()).hasNotSavedInk();
        EditBtn var11 = this.btnDeleteNote;
        c var12 = (c) this.getDoc();
        boolean isAnnotation = var12.n();
        var5 = (var6 && var5) || var3 || isAnnotation || var4 || isActive;

        var11.setEnable(var5);
        this.btnHighLight.setEnable(var4 || var6);
        this.btnText.setChoose(this.mIsAddTextMode);

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
