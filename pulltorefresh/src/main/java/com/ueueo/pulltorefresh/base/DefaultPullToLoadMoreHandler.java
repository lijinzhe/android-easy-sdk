package com.ueueo.pulltorefresh.base;

import android.view.View;
import android.widget.AbsListView;
import android.widget.ScrollView;

import com.ueueo.pulltorefresh.base.LoadMoreHandler;
import com.ueueo.pulltorefresh.base.PullToRefreshLayout;

public class DefaultPullToLoadMoreHandler implements LoadMoreHandler {

    public static boolean canChildScrollDown(View view) {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (view instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) view;
                return absListView.getChildCount() > 0
                        && (absListView.getLastVisiblePosition() < absListView.getChildCount() - 1
                        || absListView.getChildAt(absListView.getChildCount() - 1).getBottom() > absListView.getPaddingBottom());
            } else if (view instanceof ScrollView) {
                ScrollView scrollView = (ScrollView) view;
                if (scrollView.getChildCount() == 0) {
                    return false;
                } else {
                    return scrollView.getScrollY() < scrollView.getChildAt(0).getHeight() - scrollView.getHeight();
                }
            } else {
                return false;
            }
        } else {
            return view.canScrollVertically(1);
        }
    }

    /**
     * Default implement for check can perform pull to refresh
     *
     * @param frame
     * @param content
     * @param header
     * @return
     */
    public static boolean checkContentCanBePulledUp(PullToRefreshLayout frame, View content, View header) {
        return !canChildScrollDown(content);
    }

    @Override
    public boolean checkCanDoLoadMore(PullToRefreshLayout frame, View content, View footer) {
        return checkContentCanBePulledUp(frame, content, footer);
    }
}