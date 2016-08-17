package com.ueueo.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.RotateAnimation;

import com.ueueo.pulltorefresh.base.PullToRefreshLayout;

public class UEPullToLoadMoreFooter extends UEPullToRefreshHeader {

    public UEPullToLoadMoreFooter(Context context) {
        super(context);
    }

    public UEPullToLoadMoreFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UEPullToLoadMoreFooter(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void buildAnimation() {
        super.buildAnimation();
        RotateAnimation tmp = mFlipAnimation;
        mFlipAnimation = mReverseFlipAnimation;
        mReverseFlipAnimation = tmp;
    }

    @Override
    public void onUIRefreshPrepare(PullToRefreshLayout frame) {
        super.onUIRefreshPrepare(frame);
        mTitleTextView.setText(getResources().getString(R.string.ue_ptr_pull_up_to_load));
    }
}
