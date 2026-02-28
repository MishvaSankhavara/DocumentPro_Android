package com.example.documenpro.ui.customviews.smartrefresh.constant;


public class RefreshDimensionStatus {

    public static final RefreshDimensionStatus DEFAULT_UNNOTIFIED = new RefreshDimensionStatus(0,false);
    public static final RefreshDimensionStatus DEFAULT = new RefreshDimensionStatus(1,true);
    public static final RefreshDimensionStatus XML_WRAP_UNNOTIFIED = new RefreshDimensionStatus(2,false);
    public static final RefreshDimensionStatus XML_WRAP = new RefreshDimensionStatus(3,true);
    public static final RefreshDimensionStatus XML_EXACT_UNNOTIFIED = new RefreshDimensionStatus(4,false);
    public static final RefreshDimensionStatus XML_EXACT = new RefreshDimensionStatus(5,true);
    public static final RefreshDimensionStatus XML_LAYOUT_UNNOTIFIED = new RefreshDimensionStatus(6,false);
    public static final RefreshDimensionStatus XML_LAYOUT = new RefreshDimensionStatus(7,true);
    public static final RefreshDimensionStatus CODE_EXACT_UNNOTIFIED = new RefreshDimensionStatus(8,false);
    public static final RefreshDimensionStatus CODE_EXACT = new RefreshDimensionStatus(9,true);
    public static final RefreshDimensionStatus DEADLOCK_UNNOTIFIED = new RefreshDimensionStatus(10,false);
    public static final RefreshDimensionStatus DEADLOCK = new RefreshDimensionStatus(10,true);

    public final int ordinal;
    public final boolean isNotified;

    public static final RefreshDimensionStatus[] STATUSES = new RefreshDimensionStatus[]{
            DEFAULT_UNNOTIFIED,
            DEFAULT,
            XML_WRAP_UNNOTIFIED,
            XML_WRAP,
            XML_EXACT_UNNOTIFIED,
            XML_EXACT,
            XML_LAYOUT_UNNOTIFIED,
            XML_LAYOUT,
            CODE_EXACT_UNNOTIFIED,
            CODE_EXACT,
            DEADLOCK_UNNOTIFIED,
            DEADLOCK
    };

    private RefreshDimensionStatus(int ordinal, boolean notified) {
        this.ordinal = ordinal;
        this.isNotified = notified;
    }

    public RefreshDimensionStatus toUnnotified() {
        if (isNotified) {
            RefreshDimensionStatus prev = STATUSES[ordinal - 1];
            if (!prev.isNotified) {
                return prev;
            }
            return DEFAULT_UNNOTIFIED;
        }
        return this;
    }

    public RefreshDimensionStatus notified() {
        if (!isNotified) {
            return STATUSES[ordinal + 1];
        }
        return this;
    }

    public boolean canBeReplacedBy(RefreshDimensionStatus status) {
        return ordinal < status.ordinal || ((!isNotified || CODE_EXACT == this) && ordinal == status.ordinal);
    }

}