package com.example.documenpro.ui.customviews.seekbar;

public class SeekBarState {
    public String indicatorDisplayText;
    public float progressValue; //now progress value
    public boolean isMinimum;
    public boolean isMaximum;

    @Override
    public String toString() {
        return "indicatorText: " + indicatorDisplayText + " ,isMin: " + isMinimum + " ,isMax: " + isMaximum;
    }
}