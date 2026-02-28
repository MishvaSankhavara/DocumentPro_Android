package com.example.documenpro.ui.customviews.smartrefresh.constant;

public class RefreshSpinnerStyle {

    public static final RefreshSpinnerStyle TRANSLATE = new RefreshSpinnerStyle(0, true, false);
    public static final RefreshSpinnerStyle FIXED_BEHIND = new RefreshSpinnerStyle(2, false, false);
    public static final RefreshSpinnerStyle FIXED_FRONT = new RefreshSpinnerStyle(3, true, false);
    public static final RefreshSpinnerStyle MATCH_LAYOUT = new RefreshSpinnerStyle(4, true, false);

    @Deprecated
    public static final RefreshSpinnerStyle SCALE = new RefreshSpinnerStyle(1, true, true);
    public static final RefreshSpinnerStyle[] STYLES = new RefreshSpinnerStyle[] {
            TRANSLATE,
            SCALE,
            FIXED_BEHIND,
            FIXED_FRONT,
            MATCH_LAYOUT
    };

    public final int ordinal;
    public final boolean front;
    public final boolean scale;

    private RefreshSpinnerStyle(int ordinal, boolean front, boolean scale) {
        this.ordinal = ordinal;
        this.front = front;
        this.scale = scale;
    }
}
