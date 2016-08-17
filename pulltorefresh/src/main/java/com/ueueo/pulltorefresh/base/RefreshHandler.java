package com.ueueo.pulltorefresh.base;

import android.view.View;

public interface RefreshHandler {

    /**
     * Check can do refresh or not. For example the content is empty or the first child is in view.
     * <p/>
     */
    boolean checkCanDoRefresh(final PullToRefreshLayout frame, final View content, final View header);

}