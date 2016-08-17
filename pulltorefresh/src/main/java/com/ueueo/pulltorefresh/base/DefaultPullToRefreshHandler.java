package com.ueueo.pulltorefresh.base;

import android.view.View;
import android.widget.AbsListView;

import com.ueueo.pulltorefresh.base.PullToRefreshLayout;
import com.ueueo.pulltorefresh.base.RefreshHandler;

public class DefaultPullToRefreshHandler implements RefreshHandler {

    public static boolean canChildScrollUp(View view) {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (view instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) view;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return view.getScrollY() > 0;
            }
        } else {
            return view.canScrollVertically(-1);
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
    public static boolean checkContentCanBePulledDown(PullToRefreshLayout frame, View content, View header) {
        return !canChildScrollUp(content);
    }

    @Override
    public boolean checkCanDoRefresh(PullToRefreshLayout frame, View content, View header) {
        return checkContentCanBePulledDown(frame, content, header);
    }

}