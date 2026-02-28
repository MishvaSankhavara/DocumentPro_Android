package com.example.documenpro.ui.customviews.seekbar;

public interface RangeSeekBarChangeListener {
    void onRangeValueChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser);
    void onTrackingStopped(RangeSeekBar view, boolean isLeft);
    void onTrackingStarted(RangeSeekBar view, boolean isLeft);
}