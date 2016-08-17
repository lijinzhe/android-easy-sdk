package com.ueueo.pulltorefresh.base;

/**
 * A single linked list to wrap PtrUIHandler
 */
class UIListenerHolder implements UIListener {

    private UIListener mHandler;
    private UIListenerHolder mNext;

    private boolean contains(UIListener handler) {
        return mHandler != null && mHandler == handler;
    }

    private UIListenerHolder() {

    }

    public boolean hasHandler() {
        return mHandler != null;
    }

    private UIListener getHandler() {
        return mHandler;
    }

    public static void addHandler(UIListenerHolder head, UIListener handler) {

        if (null == handler) {
            return;
        }
        if (head == null) {
            return;
        }
        if (null == head.mHandler) {
            head.mHandler = handler;
            return;
        }

        UIListenerHolder current = head;
        for (; ; current = current.mNext) {

            // duplicated
            if (current.contains(handler)) {
                return;
            }
            if (current.mNext == null) {
                break;
            }
        }

        UIListenerHolder newHolder = new UIListenerHolder();
        newHolder.mHandler = handler;
        current.mNext = newHolder;
    }

    public static UIListenerHolder create() {
        return new UIListenerHolder();
    }

    public static UIListenerHolder removeHandler(UIListenerHolder head, UIListener handler) {
        if (head == null || handler == null || null == head.mHandler) {
            return head;
        }

        UIListenerHolder current = head;
        UIListenerHolder pre = null;
        do {

            // delete current: link pre to next, unlink next from current;
            // pre will no change, current move to next element;
            if (current.contains(handler)) {

                // current is head
                if (pre == null) {

                    head = current.mNext;
                    current.mNext = null;

                    current = head;
                } else {

                    pre.mNext = current.mNext;
                    current.mNext = null;
                    current = pre.mNext;
                }
            } else {
                pre = current;
                current = current.mNext;
            }

        } while (current != null);

        if (head == null) {
            head = new UIListenerHolder();
        }
        return head;
    }

    @Override
    public void onUIReset(PullToRefreshLayout frame) {
        UIListenerHolder current = this;
        do {
            final UIListener handler = current.getHandler();
            if (null != handler) {
                handler.onUIReset(frame);
            }
        } while ((current = current.mNext) != null);
    }

    @Override
    public void onUIRefreshPrepare(PullToRefreshLayout frame) {
        if (!hasHandler()) {
            return;
        }
        UIListenerHolder current = this;
        do {
            final UIListener handler = current.getHandler();
            if (null != handler) {
                handler.onUIRefreshPrepare(frame);
            }
        } while ((current = current.mNext) != null);
    }

    @Override
    public void onUIRefreshBegin(PullToRefreshLayout frame) {
        UIListenerHolder current = this;
        do {
            final UIListener handler = current.getHandler();
            if (null != handler) {
                handler.onUIRefreshBegin(frame);
            }
        } while ((current = current.mNext) != null);
    }

    @Override
    public void onUIRefreshComplete(PullToRefreshLayout frame,boolean isSuccess) {
        UIListenerHolder current = this;
        do {
            final UIListener handler = current.getHandler();
            if (null != handler) {
                handler.onUIRefreshComplete(frame,isSuccess);
            }
        } while ((current = current.mNext) != null);
    }

    @Override
    public void onUIPositionChange(PullToRefreshLayout frame, boolean isUnderTouch, byte status, Indicator ptrIndicator) {
        UIListenerHolder current = this;
        do {
            final UIListener handler = current.getHandler();
            if (null != handler) {
                handler.onUIPositionChange(frame, isUnderTouch, status, ptrIndicator);
            }
        } while ((current = current.mNext) != null);
    }
}
