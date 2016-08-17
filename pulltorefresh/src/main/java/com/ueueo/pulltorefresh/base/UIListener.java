package com.ueueo.pulltorefresh.base;

/**
 *
 */
public interface UIListener {

    /**
     * When the content view has reached top and refresh has been completed, view will be reset.
     *
     * @param frame
     */
    void onUIReset(PullToRefreshLayout frame);

    /**
     * prepare for loading
     *
     * @param frame
     */
    void onUIRefreshPrepare(PullToRefreshLayout frame);

    /**
     * perform refreshing UI
     */
    void onUIRefreshBegin(PullToRefreshLayout frame);

    /**
     * perform UI after refresh
     */
    void onUIRefreshComplete(PullToRefreshLayout frame);

    void onUIPositionChange(PullToRefreshLayout frame, boolean isUnderTouch, byte status, Indicator ptrIndicator);
}
