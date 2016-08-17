package com.ueueo.pulltorefresh.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;
import android.widget.TextView;

import com.ueueo.pulltorefresh.R;

import java.util.ArrayList;

/**
 * This layout view for "Pull to Refresh(Ptr)" support all of the view, you can contain everything you want.
 * support: pull to refresh / release to refresh / auto refresh / keep header view while refreshing / hide header view while refreshing
 * It defines PtrUIHandler, which allows you customize the UI easily.
 */
public class PullToRefreshLayout extends ViewGroup {

    // status enum
    public final static byte PTR_STATUS_INIT = 1;
    public final static byte PTR_STATUS_PREPARE = 2;
    public final static byte PTR_STATUS_LOADING = 3;
    public final static byte PTR_STATUS_COMPLETE = 4;
    private static final boolean DEBUG_LAYOUT = true;
    public static boolean DEBUG = true;
    private static int ID = 1;
    // auto refresh status
    private static byte FLAG_AUTO_REFRESH_AT_ONCE = 0x01;
    private static byte FLAG_AUTO_REFRESH_BUT_LATER = 0x01 << 1;
    private static byte FLAG_ENABLE_NEXT_PTR_AT_ONCE = 0x01 << 2;
    private static byte FLAG_PIN_CONTENT = 0x01 << 3;
    private static byte MASK_AUTO_REFRESH = 0x03;
    protected final String LOG_TAG = "ptr-frame-" + ++ID;
    protected View mContent;
    private byte mStatus = PTR_STATUS_INIT;
    // optional config for define header and content in xml file
    private int mHeaderId = 0;
    private int mContainerId = 0;
    private int mFooterId = 0;
    // config
    private int mDurationToClose = 200;
    private int mDurationToCloseHeader = 1000;
    private boolean mKeepHeaderWhenRefresh = true;
    private boolean mPullToRefreshEnable = false;
    private boolean mPullToLoadMoreEnable = false;
    private View mHeaderView;
    private View mFooterView;
    private UIListenerHolder mUIListenerHolder = UIListenerHolder.create();
    private RefreshHandler mPullToRefreshHandler;
    private LoadMoreHandler mPullToLoadMoreHandler;
    private OnRefreshListener mOnRefreshListener;
    private OnLoadMoreListener mOnLoadMoreListener;
    // working parameters
    private ScrollChecker mScrollChecker;
    private int mPagingTouchSlop;
    private int mHeaderHeight;
    private int mFooterHeight;
    private boolean mDisableWhenHorizontalMove = false;
    private int mFlag = 0x00;
    // disable when detect moving horizontally
    private boolean mPreventForHorizontal = false;
    private MotionEvent mLastMoveEvent;
    private UIListenerHook mRefreshCompleteHook;
    private int mLoadingMinTime = 500;
    private long mLoadingStartTime = 0;
    private Indicator mIndicator;
    private boolean mHasSendCancelEvent = false;
    private boolean isSuccess = true;

    private Runnable mPerformRefreshCompleteDelay = new Runnable() {
        @Override
        public void run() {
            performRefreshComplete();
        }
    };

    public PullToRefreshLayout(Context context) {
        this(context, null);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mIndicator = new Indicator();

        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.PullToRefreshLayout, 0, 0);
        if (arr != null) {
            mHeaderId = arr.getResourceId(R.styleable.PullToRefreshLayout_header, mHeaderId);
            mContainerId = arr.getResourceId(R.styleable.PullToRefreshLayout_content, mContainerId);
            mFooterId = arr.getResourceId(R.styleable.PullToRefreshLayout_footer, mFooterId);

            mIndicator.setResistance(arr.getFloat(R.styleable.PullToRefreshLayout_resistance, mIndicator.getResistance()));

            mDurationToClose = arr.getInt(R.styleable.PullToRefreshLayout_durationToClose, mDurationToClose);
            mDurationToCloseHeader = arr.getInt(R.styleable.PullToRefreshLayout_durationToCloseHeader, mDurationToCloseHeader);

            float ratio = mIndicator.getRatioOfHeaderToHeightRefresh();
            ratio = arr.getFloat(R.styleable.PullToRefreshLayout_ratioOfHeaderHeightToRefresh, ratio);
            mIndicator.setRatioOfHeaderHeightToRefresh(ratio);

            mKeepHeaderWhenRefresh = arr.getBoolean(R.styleable.PullToRefreshLayout_keepHeaderWhenRefresh, mKeepHeaderWhenRefresh);

            mPullToRefreshEnable = arr.getBoolean(R.styleable.PullToRefreshLayout_pullToRefreshEnable, mPullToRefreshEnable);
            mPullToLoadMoreEnable = arr.getBoolean(R.styleable.PullToRefreshLayout_pullToLoadMoreEnable, mPullToLoadMoreEnable);

            arr.recycle();
        }

        mScrollChecker = new ScrollChecker();

        final ViewConfiguration conf = ViewConfiguration.get(getContext());
        mPagingTouchSlop = conf.getScaledTouchSlop() * 2;

        mPullToRefreshHandler = new DefaultPullToRefreshHandler();
        mPullToLoadMoreHandler = new DefaultPullToLoadMoreHandler();
    }

    private Mode getModeFromIndex(int index) {
        switch (index) {
            case 0:
                return Mode.NONE;
            case 1:
                return Mode.REFRESH;
            case 2:
                return Mode.LOAD_MORE;
            case 3:
                return Mode.BOTH;
            default:
                return Mode.BOTH;
        }
    }

    @Override
    protected void onFinishInflate() {
        final int childCount = getChildCount();
        if (childCount > 3) {
            throw new IllegalStateException("PtrFrameLayout only can host 3 elements");
        } else if (childCount == 3) {
            if (mHeaderId != 0 && mHeaderView == null) {
                mHeaderView = findViewById(mHeaderId);
            }
            if (mContainerId != 0 && mContent == null) {
                mContent = findViewById(mContainerId);
            }
            if (mFooterId != 0 && mFooterView == null) {
                mFooterView = findViewById(mFooterId);
            }
            // not specify header or content or footer
            if (mContent == null || mHeaderView == null || mFooterView == null) {
                final View child1 = getChildAt(0);
                final View child2 = getChildAt(1);
                final View child3 = getChildAt(2);
                // all are not specified
                if (mContent == null && mHeaderView == null && mFooterView == null) {
                    mHeaderView = child1;
                    mContent = child2;
                    mFooterView = child3;
                }
                // only some are specified
                else {
                    ArrayList<View> view = new ArrayList<View>(3) {{
                        add(child1);
                        add(child2);
                        add(child3);
                    }};
                    if (mHeaderView != null) {
                        view.remove(mHeaderView);
                    }
                    if (mContent != null) {
                        view.remove(mContent);
                    }
                    if (mFooterView != null) {
                        view.remove(mFooterView);
                    }
                    if (mHeaderView == null && view.size() > 0) {
                        mHeaderView = view.get(0);
                        view.remove(0);
                    }
                    if (mContent == null && view.size() > 0) {
                        mContent = view.get(0);
                        view.remove(0);
                    }
                    if (mFooterView == null && view.size() > 0) {
                        mFooterView = view.get(0);
                        view.remove(0);
                    }
                }
            }
        } else if (childCount == 2) { // ignore the footer by default
            if (mHeaderId != 0 && mHeaderView == null) {
                mHeaderView = findViewById(mHeaderId);
            }
            if (mContainerId != 0 && mContent == null) {
                mContent = findViewById(mContainerId);
            }

            // not specify header or content
            if (mContent == null || mHeaderView == null) {

                View child1 = getChildAt(0);
                View child2 = getChildAt(1);
                if (child1 instanceof UIListener) {
                    mHeaderView = child1;
                    mContent = child2;
                } else if (child2 instanceof UIListener) {
                    mHeaderView = child2;
                    mContent = child1;
                } else {
                    // both are not specified
                    if (mContent == null && mHeaderView == null) {
                        mHeaderView = child1;
                        mContent = child2;
                    }
                    // only one is specified
                    else {
                        if (mHeaderView == null) {
                            mHeaderView = mContent == child1 ? child2 : child1;
                        } else {
                            mContent = mHeaderView == child1 ? child2 : child1;
                        }
                    }
                }
            }
        } else if (childCount == 1) {
            mContent = getChildAt(0);
        } else {
            TextView errorView = new TextView(getContext());
            errorView.setClickable(true);
            errorView.setTextColor(0xffff6600);
            errorView.setGravity(Gravity.CENTER);
            errorView.setTextSize(20);
            errorView.setText("The content view in PtrFrameLayout is empty. Do you forget to specify its id in xml layout file?");
            mContent = errorView;
            addView(mContent);
        }
        if (mHeaderView != null) {
            mHeaderView.bringToFront();
        }
        if (mFooterView != null) {
            mFooterView.bringToFront();
        }
        super.onFinishInflate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mScrollChecker != null) {
            mScrollChecker.destroy();
        }

        if (mPerformRefreshCompleteDelay != null) {
            removeCallbacks(mPerformRefreshCompleteDelay);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (DEBUG && DEBUG_LAYOUT) {
            Log.d(LOG_TAG, String.format("onMeasure frame: width: %s, height: %s, padding: %s %s %s %s",
                    getMeasuredHeight(), getMeasuredWidth(),
                    getPaddingLeft(), getPaddingRight(), getPaddingTop(), getPaddingBottom()));
        }

        if (mHeaderView != null) {
            measureChildWithMargins(mHeaderView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            MarginLayoutParams lp = (MarginLayoutParams) mHeaderView.getLayoutParams();
            mHeaderHeight = mHeaderView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            mIndicator.setHeaderHeight(mHeaderHeight);
        }

        if (mFooterView != null) {
            measureChildWithMargins(mFooterView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            MarginLayoutParams lp = (MarginLayoutParams) mFooterView.getLayoutParams();
            mFooterHeight = mFooterView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            mIndicator.setFooterHeight(mFooterHeight);
        }

        if (mContent != null) {
            measureContentView(mContent, widthMeasureSpec, heightMeasureSpec);
            if (DEBUG && DEBUG_LAYOUT) {
                ViewGroup.MarginLayoutParams lp = (MarginLayoutParams) mContent.getLayoutParams();
                Log.d(LOG_TAG, String.format("onMeasure content, width: %s, height: %s, margin: %s %s %s %s",
                        getMeasuredWidth(), getMeasuredHeight(),
                        lp.leftMargin, lp.topMargin, lp.rightMargin, lp.bottomMargin));
                Log.d(LOG_TAG, String.format("onMeasure, currentPos: %s, lastPos: %s, top: %s",
                        mIndicator.getCurrentPosY(), mIndicator.getLastPosY(), mContent.getTop()));
            }
        }
    }

    private void measureContentView(View child,
                                    int parentWidthMeasureSpec,
                                    int parentHeightMeasureSpec) {
        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin, lp.width);
        final int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
                getPaddingTop() + getPaddingBottom() + lp.topMargin, lp.height);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean flag, int i, int j, int k, int l) {
        layoutChildren();
    }

    private void layoutChildren() {
        // because the header and footer can not show at the same time, so when header has a offset, the footer's offset should be 0, vice versa..
        int offsetHeaderY;
        int offsetFooterY;
        if (mIndicator.isHeader()) {
            offsetHeaderY = mIndicator.getCurrentPosY();
            offsetFooterY = 0;
        } else {
            offsetHeaderY = 0;
            offsetFooterY = mIndicator.getCurrentPosY();
        }
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int contentBottom = 0;

        if (DEBUG && DEBUG_LAYOUT) {
            Log.d(LOG_TAG, String.format("onLayout offset: %s %s %s %s", offsetHeaderY, offsetFooterY, isPinContent(), mIndicator.isHeader()));
        }

        if (mHeaderView != null) {
            MarginLayoutParams lp = (MarginLayoutParams) mHeaderView.getLayoutParams();
            final int left = paddingLeft + lp.leftMargin;
            final int top = paddingTop + lp.topMargin + offsetHeaderY - mHeaderHeight;
            final int right = left + mHeaderView.getMeasuredWidth();
            final int bottom = top + mHeaderView.getMeasuredHeight();
            mHeaderView.layout(left, top, right, bottom);
            if (DEBUG && DEBUG_LAYOUT) {
                Log.d(LOG_TAG, String.format("onLayout header: %s %s %s %s %s", left, top, right, bottom, mHeaderView.getMeasuredHeight()));
            }
        }
        if (mContent != null) {
            MarginLayoutParams lp = (MarginLayoutParams) mContent.getLayoutParams();
            int left;
            int top;
            int right;
            int bottom;
            if (mIndicator.isHeader()) {
                left = paddingLeft + lp.leftMargin;
                top = paddingTop + lp.topMargin + (isPinContent() ? 0 : offsetHeaderY);
                right = left + mContent.getMeasuredWidth();
                bottom = top + mContent.getMeasuredHeight();
            } else {
                left = paddingLeft + lp.leftMargin;
                top = paddingTop + lp.topMargin - (isPinContent() ? 0 : offsetFooterY);
                right = left + mContent.getMeasuredWidth();
                bottom = top + mContent.getMeasuredHeight();
            }
            contentBottom = bottom;
            if (DEBUG && DEBUG_LAYOUT) {
                Log.d(LOG_TAG, String.format("onLayout content: %s %s %s %s %s", left, top, right, bottom, mContent.getMeasuredHeight()));
            }
            mContent.layout(left, top, right, bottom);
        }
        if (mFooterView != null) {
            MarginLayoutParams lp = (MarginLayoutParams) mFooterView.getLayoutParams();
            final int left = paddingLeft + lp.leftMargin;
            final int top = paddingTop + lp.topMargin + contentBottom - (isPinContent() ? offsetFooterY : 0);
            final int right = left + mFooterView.getMeasuredWidth();
            final int bottom = top + mFooterView.getMeasuredHeight();
            mFooterView.layout(left, top, right, bottom);
            if (DEBUG && DEBUG_LAYOUT) {
                Log.d(LOG_TAG, String.format("onLayout footer: %s %s %s %s %s", left, top, right, bottom, mFooterView.getMeasuredHeight()));
            }
        }
    }

    public boolean dispatchTouchEventSupper(MotionEvent e) {
        return super.dispatchTouchEvent(e);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        if (!isEnabled() || mContent == null || mHeaderView == null) {
            return dispatchTouchEventSupper(e);
        }
        int action = e.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIndicator.onRelease();
                if (mIndicator.hasLeftStartPosition()) {
                    if (DEBUG) {
                        Log.d(LOG_TAG, "call onRelease when user release");
                    }
                    onRelease(false);
                    if (mIndicator.hasMovedAfterPressedDown()) {
                        sendCancelEvent();
                        return true;
                    }
                    return dispatchTouchEventSupper(e);
                } else {
                    return dispatchTouchEventSupper(e);
                }

            case MotionEvent.ACTION_DOWN:
                mHasSendCancelEvent = false;
                mIndicator.onPressDown(e.getX(), e.getY());

                mScrollChecker.abortIfWorking();

                mPreventForHorizontal = false;
                // The cancel event will be sent once the position is moved.
                // So let the event pass to children.
                // fix #93, #102
                dispatchTouchEventSupper(e);
                return true;

            case MotionEvent.ACTION_MOVE:
                mLastMoveEvent = e;
                mIndicator.onMove(e.getX(), e.getY());
                float offsetX = mIndicator.getOffsetX();
                float offsetY = mIndicator.getOffsetY();

                if (mDisableWhenHorizontalMove && !mPreventForHorizontal && (Math.abs(offsetX) > mPagingTouchSlop && Math.abs(offsetX) > Math.abs(offsetY))) {
                    if (mIndicator.isInStartPosition()) {
                        mPreventForHorizontal = true;
                    }
                }
                if (mPreventForHorizontal) {
                    return dispatchTouchEventSupper(e);
                }

                boolean moveDown = offsetY > 0;
                boolean moveUp = !moveDown;

                boolean canMoveUp = mIndicator.isHeader() && mIndicator.hasLeftStartPosition(); // if the header is showing

                boolean canMoveDown = mFooterView != null && !mIndicator.isHeader() && mIndicator.hasLeftStartPosition(); // if the footer is showing

                boolean canHeaderMoveDown = mPullToRefreshEnable && mPullToRefreshHandler != null
                        && mPullToRefreshHandler.checkCanDoRefresh(this, mContent, mHeaderView);
                boolean canFooterMoveUp = mPullToLoadMoreEnable && mPullToLoadMoreHandler != null
                        && mFooterView != null // The footer view could be null, so need double check
                        && mPullToLoadMoreHandler.checkCanDoLoadMore(this, mContent, mFooterView);

                if (DEBUG) {
                    Log.v(LOG_TAG, String.format("ACTION_MOVE: offsetY:%s, currentPos: %s, moveUp: %s, canMoveUp: %s, moveDown: %s: canMoveDown: %s canHeaderMoveDown: %s canFooterMoveUp: %s", offsetY, mIndicator.getCurrentPosY(), moveUp, canMoveUp, moveDown, canMoveDown, canHeaderMoveDown, canFooterMoveUp));
                }

                // if either the header and footer are not showing
                if (!canMoveUp && !canMoveDown) {
                    // disable move when header not reach top
                    if (moveDown && !canHeaderMoveDown) {
                        return dispatchTouchEventSupper(e);
                    }
                    if (moveUp && !canFooterMoveUp) {
                        return dispatchTouchEventSupper(e);
                    }

                    // should show up header
                    if (moveDown) {
                        moveHeaderPos(offsetY);
                        return true;
                    }

                    // should show up footer
                    if (moveUp) {
                        moveFooterPos(offsetY);
                        return true;
                    }
                }

                // if header is showing, then no need to move footer
                if (canMoveUp) {
                    moveHeaderPos(offsetY);
                    return true;
                }

                // if footer is showing, then no need to move header
                // When status is completing, that is, the footer is hiding, disable pull up
                if (canMoveDown && mStatus != PTR_STATUS_COMPLETE) {
                    moveFooterPos(offsetY);
                    return true;
                }
        }
        return dispatchTouchEventSupper(e);
    }

    private void moveFooterPos(float deltaY) {
        mIndicator.setIsHeader(false);
        Log.i("lijinzhe", "moveFooterPos  deltaY:" + deltaY);
        // to keep the consistence with refresh, need to converse the deltaY
        movePos(-deltaY);
    }

    private void moveHeaderPos(float deltaY) {
        mIndicator.setIsHeader(true);
        movePos(deltaY);
    }

    /**
     * if deltaY > 0, move the content down
     *
     * @param deltaY
     */
    private void movePos(float deltaY) {
        // has reached the top
        if ((deltaY < 0 && mIndicator.isInStartPosition())) {
            if (DEBUG) {
                Log.e(LOG_TAG, String.format("has reached the top"));
            }
            return;
        }

        int to = mIndicator.getCurrentPosY() + (int) deltaY;

        // over top
        if (mIndicator.willOverTop(to)) {
            if (DEBUG) {
                Log.e(LOG_TAG, String.format("over top"));
            }
            to = Indicator.POS_START;
        }
        mIndicator.setCurrentPos(to);
        int change = to - mIndicator.getLastPosY();
        updatePos(mIndicator.isHeader() ? change : -change);
    }

    private void updatePos(int change) {
        if (change == 0) {
            return;
        }

        boolean isUnderTouch = mIndicator.isUnderTouch();

        // once moved, cancel event will be sent to child
        if (isUnderTouch && !mHasSendCancelEvent && mIndicator.hasMovedAfterPressedDown()) {
            mHasSendCancelEvent = true;
            sendCancelEvent();
        }

        // leave initiated position or just refresh complete
        if ((mIndicator.hasJustLeftStartPosition() && mStatus == PTR_STATUS_INIT) ||
                (mIndicator.goDownCrossFinishPosition() && mStatus == PTR_STATUS_COMPLETE && isEnabledNextPtrAtOnce())) {

            mStatus = PTR_STATUS_PREPARE;
            mUIListenerHolder.onUIRefreshPrepare(this);
            if (DEBUG) {
                Log.i(LOG_TAG, String.format("PtrUIHandler: onUIRefreshPrepare, mFlag %s", mFlag));
            }
        }

        // back to initiated position
        if (mIndicator.hasJustBackToStartPosition()) {
            tryToNotifyReset();

            // recover event to children
            if (isUnderTouch) {
                sendDownEvent();
            }
        }

        // Pull to Refresh
        if (mStatus == PTR_STATUS_PREPARE) {
            // reach fresh height while moving from top to bottom
//            if (isUnderTouch && !isAutoRefresh() && (mPullToRefreshEnable || mPullToLoadMoreEnable)
//                    && mIndicator.crossRefreshLineFromTopToBottom()) {
//                tryToPerformRefresh();
//            }
            // reach header height while auto refresh
            if (performAutoRefreshButLater() && mIndicator.hasJustReachedHeaderHeightFromTopToBottom()) {
                tryToPerformRefresh();
            }
        }

        if (DEBUG) {
            Log.v(LOG_TAG, String.format("updatePos: change: %s, current: %s last: %s, top: %s, headerHeight: %s",
                    change, mIndicator.getCurrentPosY(), mIndicator.getLastPosY(), mContent.getTop(), mHeaderHeight));
        }

        if (mIndicator.isHeader()) {
            mHeaderView.offsetTopAndBottom(change);
        } else {
            mFooterView.offsetTopAndBottom(change);
        }
        if (!isPinContent()) {
            mContent.offsetTopAndBottom(change);
        }
        invalidate();

        if (mUIListenerHolder.hasHandler()) {
            mUIListenerHolder.onUIPositionChange(this, isUnderTouch, mStatus, mIndicator);
        }
        onPositionChange(isUnderTouch, mStatus, mIndicator);
    }

    protected void onPositionChange(boolean isInTouching, byte status, Indicator mPtrIndicator) {
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mOnRefreshListener = listener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mOnLoadMoreListener = listener;
    }

    @SuppressWarnings("unused")
    public int getHeaderHeight() {
        return mHeaderHeight;
    }

    public int getFooterHeight() {
        return mFooterHeight;
    }

    private void onRelease(boolean stayForLoading) {

        tryToPerformRefresh();

        if (mStatus == PTR_STATUS_LOADING) {
            // keep header for fresh
            if (mKeepHeaderWhenRefresh) {
                // scroll header back
                if (mIndicator.isOverOffsetToKeepHeaderWhileLoading() && !stayForLoading) {
                    mScrollChecker.tryToScrollTo(mIndicator.getOffsetToKeepHeaderWhileLoading(), mDurationToClose);
                } else {
                    // do nothing
                }
            } else {
                tryScrollBackToTopWhileLoading();
            }
        } else {
            if (mStatus == PTR_STATUS_COMPLETE) {
                notifyUIRefreshComplete(false);
            } else {
                tryScrollBackToTopAbortRefresh();
            }
        }
    }

    /**
     * please DO REMEMBER resume the hook
     *
     * @param hook
     */

    public void setRefreshCompleteHook(UIListenerHook hook) {
        mRefreshCompleteHook = hook;
        hook.setResumeAction(new Runnable() {
            @Override
            public void run() {
                if (DEBUG) {
                    Log.d(LOG_TAG, "mRefreshCompleteHook resume.");
                }
                notifyUIRefreshComplete(true);
            }
        });
    }

    /**
     * Scroll back to to if is not under touch
     */
    private void tryScrollBackToTop() {
        if (!mIndicator.isUnderTouch() && mIndicator.hasLeftStartPosition()) {
            mScrollChecker.tryToScrollTo(Indicator.POS_START, mDurationToCloseHeader);
        }
    }

    /**
     * just make easier to understand
     */
    private void tryScrollBackToTopWhileLoading() {
        tryScrollBackToTop();
    }

    /**
     * just make easier to understand
     */
    private void tryScrollBackToTopAfterComplete() {
        tryScrollBackToTop();
    }

    /**
     * just make easier to understand
     */
    private void tryScrollBackToTopAbortRefresh() {
        tryScrollBackToTop();
    }

    private boolean tryToPerformRefresh() {
        if (mStatus != PTR_STATUS_PREPARE) {
            return false;
        }

        //
        if ((mIndicator.isOverOffsetToKeepHeaderWhileLoading() && isAutoRefresh()) || mIndicator.isOverOffsetToRefresh()) {
            mStatus = PTR_STATUS_LOADING;
            performRefresh();
        }
        return false;
    }

    private void performRefresh() {
        mLoadingStartTime = System.currentTimeMillis();
        if (mUIListenerHolder.hasHandler()) {
            mUIListenerHolder.onUIRefreshBegin(this);
            if (DEBUG) {
                Log.i(LOG_TAG, "PtrUIHandler: onUIRefreshBegin");
            }
        }
//        if (mPullToRefreshHandler != null) {
        if (mIndicator.isHeader()) {
//                mPullToRefreshHandler.onRefreshBegin(this);
            if (mOnRefreshListener != null) {
                mOnRefreshListener.onRefreshBegin();
            }
        } else {
//            if (mPullToRefreshHandler instanceof PtrHandler2) {
//                    ((PtrHandler2) mPullToRefreshHandler).onLoadMoreBegin(this);
            if (mOnLoadMoreListener != null) {
                mOnLoadMoreListener.onLoadMoreBegin();
            }
//            }
        }
//        }
    }

    /**
     * If at the top and not in loading, reset
     */
    private boolean tryToNotifyReset() {
        if ((mStatus == PTR_STATUS_COMPLETE || mStatus == PTR_STATUS_PREPARE) && mIndicator.isInStartPosition()) {
            if (mUIListenerHolder.hasHandler()) {
                mUIListenerHolder.onUIReset(this);
                if (DEBUG) {
                    Log.i(LOG_TAG, "PtrUIHandler: onUIReset");
                }
            }
            mStatus = PTR_STATUS_INIT;
            clearFlag();
            return true;
        }
        return false;
    }

    protected void onScrollAbort() {
        if (mIndicator.hasLeftStartPosition() && isAutoRefresh()) {
            if (DEBUG) {
                Log.d(LOG_TAG, "call onRelease after scroll abort");
            }
            onRelease(true);
        }
    }

    protected void onScrollFinish() {
        if (mIndicator.hasLeftStartPosition() && isAutoRefresh()) {
            if (DEBUG) {
                Log.d(LOG_TAG, "call onRelease after scroll finish");
            }
            onRelease(true);
        }
    }

    /**
     * Detect whether is refreshing.
     *
     * @return
     */
    public boolean isRefreshing() {
        return mStatus == PTR_STATUS_LOADING;
    }

    public void refreshSuccess() {
        refreshComplete(true);
    }

    public void refreshFailed() {
        refreshComplete(false);
    }

    /**
     * Call this when data is loaded.
     * The UI will perform complete at once or after a delay, depends on the time elapsed is greater then {@link #mLoadingMinTime} or not.
     */
    final public void refreshComplete(boolean isSuccess) {
        if (DEBUG) {
            Log.i(LOG_TAG, "refreshComplete");
        }
        this.isSuccess = isSuccess;
        if (mRefreshCompleteHook != null) {
            mRefreshCompleteHook.reset();
        }

        int delay = (int) (mLoadingMinTime - (System.currentTimeMillis() - mLoadingStartTime));
        if (delay <= 0) {
            if (DEBUG) {
                Log.d(LOG_TAG, "performRefreshComplete at once");
            }
            performRefreshComplete();
        } else {
            postDelayed(mPerformRefreshCompleteDelay, delay);
            if (DEBUG) {
                Log.d(LOG_TAG, String.format("performRefreshComplete after delay: %s", delay));
            }
        }
    }

    /**
     * Do refresh complete work when time elapsed is greater than {@link #mLoadingMinTime}
     */
    private void performRefreshComplete() {
        mStatus = PTR_STATUS_COMPLETE;

        // if is auto refresh do nothing, wait scroller stop
        if (mScrollChecker.mIsRunning && isAutoRefresh()) {
            // do nothing
            if (DEBUG) {
                Log.d(LOG_TAG, String.format("performRefreshComplete do nothing, scrolling: %s, auto refresh: %s",
                        mScrollChecker.mIsRunning, mFlag));
            }
            return;
        }

        notifyUIRefreshComplete(false);
    }

    /**
     * Do real refresh work. If there is a hook, execute the hook first.
     *
     * @param ignoreHook
     */
    private void notifyUIRefreshComplete(boolean ignoreHook) {
        /**
         * After hook operation is done, {@link #notifyUIRefreshComplete} will be call in resume action to ignore hook.
         */
        if (mIndicator.hasLeftStartPosition() && !ignoreHook && mRefreshCompleteHook != null) {
            if (DEBUG) {
                Log.d(LOG_TAG, "notifyUIRefreshComplete mRefreshCompleteHook run.");
            }

            mRefreshCompleteHook.takeOver();
            return;
        }
        if (mUIListenerHolder.hasHandler()) {
            if (DEBUG) {
                Log.i(LOG_TAG, "PtrUIHandler: onUIRefreshComplete");
            }
            mUIListenerHolder.onUIRefreshComplete(this, this.isSuccess);
        }
        mIndicator.onUIRefreshComplete();
        tryScrollBackToTopAfterComplete();
        tryToNotifyReset();
    }

    public void autoRefresh() {
        autoRefresh(true, mDurationToCloseHeader);
    }

    public void autoRefresh(boolean atOnce) {
        autoRefresh(atOnce, mDurationToCloseHeader);
    }

    private void clearFlag() {
        // remove auto fresh flag
        mFlag = mFlag & ~MASK_AUTO_REFRESH;
    }

    public void autoLoadMore() {
        autoRefresh(true, mDurationToCloseHeader, false);
    }

    public void autoLoadMore(boolean atOnce) {
        autoRefresh(atOnce, mDurationToCloseHeader, false);
    }

    public void autoRefresh(boolean atOnce, int duration) {
        autoRefresh(atOnce, duration, true);
    }

    public void autoRefresh(boolean atOnce, int duration, boolean isHeader) {

        if (mStatus != PTR_STATUS_INIT) {
            return;
        }

        mFlag |= atOnce ? FLAG_AUTO_REFRESH_AT_ONCE : FLAG_AUTO_REFRESH_BUT_LATER;

        mStatus = PTR_STATUS_PREPARE;
        if (mUIListenerHolder.hasHandler()) {
            mUIListenerHolder.onUIRefreshPrepare(this);
            if (DEBUG) {
                Log.i(LOG_TAG, String.format("PtrUIHandler: onUIRefreshPrepare, mFlag %s", mFlag));
            }
        }
        mIndicator.setIsHeader(isHeader);
        mScrollChecker.tryToScrollTo(mIndicator.getOffsetToRefresh(), duration);
        if (atOnce) {
            mStatus = PTR_STATUS_LOADING;
            performRefresh();
        }
    }

    public boolean isAutoRefresh() {
        return (mFlag & MASK_AUTO_REFRESH) > 0;
    }

    private boolean performAutoRefreshButLater() {
        return (mFlag & MASK_AUTO_REFRESH) == FLAG_AUTO_REFRESH_BUT_LATER;
    }

    public boolean isEnabledNextPtrAtOnce() {
        return (mFlag & FLAG_ENABLE_NEXT_PTR_AT_ONCE) > 0;
    }

    /**
     * If @param enable has been set to true. The user can perform next PTR at once.
     *
     * @param enable
     */
    public void setEnabledNextPtrAtOnce(boolean enable) {
        if (enable) {
            mFlag = mFlag | FLAG_ENABLE_NEXT_PTR_AT_ONCE;
        } else {
            mFlag = mFlag & ~FLAG_ENABLE_NEXT_PTR_AT_ONCE;
        }
    }

    public boolean isPinContent() {
        return (mFlag & FLAG_PIN_CONTENT) > 0;
    }

    /**
     * The content view will now move when {@param pinContent} set to true.
     *
     * @param pinContent
     */
    public void setPinContent(boolean pinContent) {
        if (pinContent) {
            mFlag = mFlag | FLAG_PIN_CONTENT;
        } else {
            mFlag = mFlag & ~FLAG_PIN_CONTENT;
        }
    }

    /**
     * It's useful when working with viewpager.
     *
     * @param disable
     */
    public void disableWhenHorizontalMove(boolean disable) {
        mDisableWhenHorizontalMove = disable;
    }

    /**
     * loading will last at least for so long
     *
     * @param time
     */
    public void setLoadingMinTime(int time) {
        mLoadingMinTime = time;
    }

    @SuppressWarnings({"unused"})
    public View getContentView() {
        return mContent;
    }

    public void setPullToRefreshHandler(RefreshHandler pullToRefreshHandler) {
        mPullToRefreshHandler = pullToRefreshHandler;
    }

    public void setPullToLoadMoreHandler(LoadMoreHandler pullToLoadMoreHandler) {
        mPullToLoadMoreHandler = pullToLoadMoreHandler;
    }

    public void addUIListener(UIListener uiListener) {
        UIListenerHolder.addHandler(mUIListenerHolder, uiListener);
    }

    @SuppressWarnings({"unused"})
    public void removeUIListener(UIListener uiListener) {
        mUIListenerHolder = UIListenerHolder.removeHandler(mUIListenerHolder, uiListener);
    }

    public void setIndicator(Indicator slider) {
        if (mIndicator != null && mIndicator != slider) {
            slider.convertFrom(mIndicator);
        }
        mIndicator = slider;
    }

    @SuppressWarnings({"unused"})
    public float getResistance() {
        return mIndicator.getResistance();
    }

    public void setResistance(float resistance) {
        mIndicator.setResistance(resistance);
    }

    @SuppressWarnings({"unused"})
    public float getDurationToClose() {
        return mDurationToClose;
    }

    /**
     * The duration to return back to the refresh position
     *
     * @param duration
     */
    public void setDurationToClose(int duration) {
        mDurationToClose = duration;
    }

    @SuppressWarnings({"unused"})
    public long getDurationToCloseHeader() {
        return mDurationToCloseHeader;
    }

    /**
     * The duration to close time
     *
     * @param duration
     */
    public void setDurationToCloseHeader(int duration) {
        mDurationToCloseHeader = duration;
    }

    public void setRatioOfHeaderHeightToRefresh(float ratio) {
        mIndicator.setRatioOfHeaderHeightToRefresh(ratio);
    }

    public int getOffsetToRefresh() {
        return mIndicator.getOffsetToRefresh();
    }

    @SuppressWarnings({"unused"})
    public void setOffsetToRefresh(int offset) {
        mIndicator.setOffsetToRefresh(offset);
    }

    @SuppressWarnings({"unused"})
    public float getRatioOfHeaderToHeightRefresh() {
        return mIndicator.getRatioOfHeaderToHeightRefresh();
    }

    @SuppressWarnings({"unused"})
    public int getOffsetToKeepHeaderWhileLoading() {
        return mIndicator.getOffsetToKeepHeaderWhileLoading();
    }

    @SuppressWarnings({"unused"})
    public void setOffsetToKeepHeaderWhileLoading(int offset) {
        mIndicator.setOffsetToKeepHeaderWhileLoading(offset);
    }

    @SuppressWarnings({"unused"})
    public boolean isKeepHeaderWhenRefresh() {
        return mKeepHeaderWhenRefresh;
    }

    public void setKeepHeaderWhenRefresh(boolean keepOrNot) {
        mKeepHeaderWhenRefresh = keepOrNot;
    }

    public boolean isPullToRefreshEnable() {
        return mPullToRefreshEnable;
    }

    public void setPullToRefreshEnable(boolean pullToRefreshEnable) {
        mPullToRefreshEnable = pullToRefreshEnable;
    }

    public boolean isPullToLoadMoreEnable() {
        return mPullToLoadMoreEnable;
    }

    public void setPullToLoadMoreEnable(boolean pullToLoadMoreEnable) {
        mPullToLoadMoreEnable = pullToLoadMoreEnable;
    }

    @SuppressWarnings({"unused"})
    public View getHeaderView() {
        return mHeaderView;
    }

    public void setHeaderView(View header) {
        if (mHeaderView != null && header != null && mHeaderView != header) {
            removeView(mHeaderView);
        }
        ViewGroup.LayoutParams lp = header.getLayoutParams();
        if (lp == null) {
            lp = new LayoutParams(-1, -2);
            header.setLayoutParams(lp);
        }
        mHeaderView = header;
        addView(header);
    }

    public void setFooterView(View footer) {
        if (mFooterView != null && footer != null && mHeaderView != footer) {
            removeView(mHeaderView);
        }
        ViewGroup.LayoutParams lp = footer.getLayoutParams();
        if (lp == null) {
            lp = new LayoutParams(-1, -2);
            footer.setLayoutParams(lp);
        }
        mFooterView = footer;
        addView(footer);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p != null && p instanceof LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    private void sendCancelEvent() {
        if (DEBUG) {
            Log.d(LOG_TAG, "send cancel event");
        }
        // The ScrollChecker will update position and lead to send cancel event when mLastMoveEvent is null.
        // fix #104, #80, #92
        if (mLastMoveEvent == null) {
            return;
        }
        MotionEvent last = mLastMoveEvent;
        MotionEvent e = MotionEvent.obtain(last.getDownTime(), last.getEventTime() + ViewConfiguration.getLongPressTimeout(), MotionEvent.ACTION_CANCEL, last.getX(), last.getY(), last.getMetaState());
        dispatchTouchEventSupper(e);
    }

    private void sendDownEvent() {
        if (DEBUG) {
            Log.d(LOG_TAG, "send down event");
        }
        final MotionEvent last = mLastMoveEvent;
        MotionEvent e = MotionEvent.obtain(last.getDownTime(), last.getEventTime(), MotionEvent.ACTION_DOWN, last.getX(), last.getY(), last.getMetaState());
        dispatchTouchEventSupper(e);
    }

    public enum Mode {
        NONE, REFRESH, LOAD_MORE, BOTH
    }

    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        @SuppressWarnings({"unused"})
        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    class ScrollChecker implements Runnable {

        private int mLastFlingY;
        private Scroller mScroller;
        private boolean mIsRunning = false;
        private int mStart;
        private int mTo;

        public ScrollChecker() {
            mScroller = new Scroller(getContext());
        }

        public void run() {
            boolean finish = !mScroller.computeScrollOffset() || mScroller.isFinished();
            int curY = mScroller.getCurrY();
            int deltaY = curY - mLastFlingY;
            if (DEBUG) {
                if (deltaY != 0) {
                    Log.v(LOG_TAG,
                            String.format("scroll: %s, start: %s, to: %s, currentPos: %s, current :%s, last: %s, delta: %s",
                                    finish, mStart, mTo, mIndicator.getCurrentPosY(), curY, mLastFlingY, deltaY));
                }
            }
            if (!finish) {
                mLastFlingY = curY;
                if (mIndicator.isHeader()) {
                    moveHeaderPos(deltaY);
                } else {
                    moveFooterPos(-deltaY);
                }
                post(this);
            } else {
                finish();
            }
        }

        public boolean isRunning() {
            return mScroller.isFinished();
        }

        private void finish() {
            if (DEBUG) {
                Log.v(LOG_TAG, String.format("finish, currentPos:%s", mIndicator.getCurrentPosY()));
            }
            reset();
            onScrollFinish();
        }

        private void reset() {
            mIsRunning = false;
            mLastFlingY = 0;
            removeCallbacks(this);
        }

        private void destroy() {
            reset();
            if (!mScroller.isFinished()) {
                mScroller.forceFinished(true);
            }
        }

        public void abortIfWorking() {
            if (mIsRunning) {
                if (!mScroller.isFinished()) {
                    mScroller.forceFinished(true);
                }
                onScrollAbort();
                reset();
            }
        }

        public void tryToScrollTo(int to, int duration) {
            if (mIndicator.isAlreadyHere(to)) {
                return;
            }
            mStart = mIndicator.getCurrentPosY();
            mTo = to;
            int distance = to - mStart;
            if (DEBUG) {
                Log.d(LOG_TAG, String.format("tryToScrollTo: start: %s, distance:%s, to:%s", mStart, distance, to));
            }
            removeCallbacks(this);

            mLastFlingY = 0;

            // fix #47: Scroller should be reused, https://github.com/liaohuqiu/android-Ultra-Pull-To-Refresh/issues/47
            if (!mScroller.isFinished()) {
                mScroller.forceFinished(true);
            }
            mScroller.startScroll(0, 0, 0, distance, duration);
            post(this);
            mIsRunning = true;
        }
    }
}
