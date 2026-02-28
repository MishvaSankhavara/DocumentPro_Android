package com.example.documenpro.ui.customviews.smartrefresh.constant;


public enum RefreshLayoutState {
    IDLE(0,false,false,false,false,false),
    PULL_DOWN_TO_REFRESH(1,true,false,false,false,false), PullUpToLoad(2,true,false,false,false,false),
    PULL_DOWN_CANCELLED(1,false,false,false,false,false), PullUpCanceled(2,false,false,false,false,false),
    RELEASE_TO_REFRESH(1,true,false,false,false,true), ReleaseToLoad(2,true,false,false,false,true),
    RELEASE_TO_TWO_LEVEL(1,true,false,false,true,true), TwoLevelReleased(1,false,false,false,true,false),
    REFRESH_RELEASED(1,false,false,false,false,false), LoadReleased(2,false,false,false,false,false),
    REFRESHING(1,false,true,false,false,false), Loading(2,false,true,false,false,false), TwoLevel(1, false, true,false,true,false),
    REFRESH_FINISHED(1,false,false,true,false,false), LoadFinish(2,false,false,true,false,false), TwoLevelFinish(1,false,false,true,true,false);

    public final boolean isHeaderState;
    public final boolean isFooterState;
    public final boolean isTwoLevelState;
    public final boolean isDraggingState;
    public final boolean isOpeningState;
    public final boolean isFinishingState;
    public final boolean isReleaseToOpeningState;

    RefreshLayoutState(int role, boolean dragging, boolean opening, boolean finishing, boolean twoLevel, boolean releaseToOpening) {
        this.isHeaderState = role == 1;
        this.isFooterState = role == 2;
        this.isDraggingState = dragging;
        this.isOpeningState = opening;
        this.isFinishingState = finishing;
        this.isTwoLevelState = twoLevel;
        this.isReleaseToOpeningState = releaseToOpening;
    }

    public RefreshLayoutState convertToHeaderState() {
        if (isFooterState && !isTwoLevelState) {
            return values()[ordinal()-1];
        }
        return this;
    }

    public RefreshLayoutState convertToFooterState() {
        if (isHeaderState && !isTwoLevelState) {
            return values()[ordinal() + 1];
        }
        return this;
    }
}