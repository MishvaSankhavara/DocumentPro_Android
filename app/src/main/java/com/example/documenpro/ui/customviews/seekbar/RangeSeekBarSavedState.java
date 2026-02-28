package com.example.documenpro.ui.customviews.seekbar;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

public class RangeSeekBarSavedState extends View.BaseSavedState {
    public float minimumRangeValue;
    public float maximumRangeValue;
    public float rangeStepInterval;
    public int tickCount;
    public float currentSelectedMinValue;
    public float currentSelectedMaxValue;

    public RangeSeekBarSavedState(Parcelable superState) {
        super(superState);
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeFloat(minimumRangeValue);
        out.writeFloat(maximumRangeValue);
        out.writeFloat(rangeStepInterval);
        out.writeInt(tickCount);
        out.writeFloat(currentSelectedMinValue);
        out.writeFloat(currentSelectedMaxValue);
    }

    private RangeSeekBarSavedState(Parcel in) {
        super(in);
        minimumRangeValue = in.readFloat();
        maximumRangeValue = in.readFloat();
        rangeStepInterval = in.readFloat();
        tickCount = in.readInt();
        currentSelectedMinValue = in.readFloat();
        currentSelectedMaxValue = in.readFloat();
    }

    public static final Creator<RangeSeekBarSavedState> CREATOR = new Creator<RangeSeekBarSavedState>() {
        public RangeSeekBarSavedState createFromParcel(Parcel in) {
            return new RangeSeekBarSavedState(in);
        }

        public RangeSeekBarSavedState[] newArray(int size) {
            return new RangeSeekBarSavedState[size];
        }
    };
}