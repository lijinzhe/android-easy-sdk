package com.ueueo.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;

import com.ueueo.pulltorefresh.base.PullToRefreshLayout;

public class UEPullToRefreshLayout extends PullToRefreshLayout {

    private UEPullToRefreshHeader mPtrClassicHeader;
    private UEPullToLoadMoreFooter mPtrClassicFooter;

    public UEPullToRefreshLayout(Context context) {
        super(context);
        initViews();
    }

    public UEPullToRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public UEPullToRefreshLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews();
    }

    private void initViews() {
        mPtrClassicHeader = new UEPullToRefreshHeader(getContext());
        setHeaderView(mPtrClassicHeader);
        addUIListener(mPtrClassicHeader);
        mPtrClassicFooter = new UEPullToLoadMoreFooter(getContext());
        setFooterView(mPtrClassicFooter);
        addUIListener(mPtrClassicFooter);
    }

    public UEPullToRefreshHeader getHeader() {
        return mPtrClassicHeader;
    }

    /**
     * Specify the last update time by this key string
     *
     * @param key
     */
    public void setLastUpdateTimeKey(String key) {
        setLastUpdateTimeHeaderKey(key);
        setLastUpdateTimeFooterKey(key);
    }

    public void setLastUpdateTimeHeaderKey(String key) {
        if (mPtrClassicHeader != null) {
            mPtrClassicHeader.setLastUpdateTimeKey(key);
        }
    }

    public void setLastUpdateTimeFooterKey(String key) {
        if (mPtrClassicFooter != null) {
            mPtrClassicFooter.setLastUpdateTimeKey(key);
        }
    }

    /**
     * Using an object to specify the last update time.
     *
     * @param object
     */
    public void setLastUpdateTimeRelateObject(Object object) {
        setLastUpdateTimeHeaderRelateObject(object);
        setLastUpdateTimeFooterRelateObject(object);
    }

    public void setLastUpdateTimeHeaderRelateObject(Object object) {
        if (mPtrClassicHeader != null) {
            mPtrClassicHeader.setLastUpdateTimeRelateObject(object);
        }
    }

    public void setLastUpdateTimeFooterRelateObject(Object object) {
        if (mPtrClassicFooter != null) {
            mPtrClassicFooter.setLastUpdateTimeRelateObject(object);
        }
    }
}
