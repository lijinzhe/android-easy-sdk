package com.ueueo.pulltorefresh.base;

import android.view.View;

public interface LoadMoreHandler {

    /**
     * Check can do load more or not. For example the content is empty or the first child is in view.
     * <p/>
     * {@link DefaultPullToRefreshHandler#checkContentCanBePulledDown}
     */
    boolean checkCanDoLoadMore(final PullToRefreshLayout frame, final View content, final View footer);

}