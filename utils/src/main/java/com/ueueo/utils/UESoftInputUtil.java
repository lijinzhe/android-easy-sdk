package com.ueueo.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * 软键盘工具类
 *
 * @author Lee
 * @date 16/5/28
 */
public class UESoftInputUtil {

    /**
     * 隐藏软键盘
     *
     * @param activity
     */
    public static void hideSoftInput(Activity activity) {
        if (activity == null) {
            throw new RuntimeException("activity must not null!");
        }
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception e) {
        }
    }

    /**
     * 打开软键盘
     *
     * @param activity
     * @param view      需要获取软键盘焦点的view
     * @param delayTime 延迟时间，在Fragment的onCreate等生命周期直接弹出软键盘无效，需要延迟弹出
     */
    public static void showSoftInput(final Activity activity, final View view, int delayTime) {
        if (activity == null) {
            throw new RuntimeException("activity must not null!");
        }
        if (view == null) {
            throw new RuntimeException("view must not null!");
        }
        if (delayTime > 0) {
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showSoftInput(activity, view);
                }
            }, delayTime);
        } else {
            showSoftInput(activity, view);
        }
    }

    public static void showSoftInput(final Activity activity, final View view) {
        if (activity == null) {
            throw new RuntimeException("activity must not null!");
        }
        if (view == null) {
            throw new RuntimeException("view must not null!");
        }
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
            }
        } catch (Exception e) {
        }
    }

    /**
     * 切换软键盘的显示和隐藏
     *
     * @param activity
     */
    public static void toggleSoftInput(Activity activity) {
        if (activity == null) {
            throw new RuntimeException("activity must not null!");
        }
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception e) {
        }
    }

    /**
     * 获取软键盘是否打开
     *
     * @param activity
     * @return true:打开，false:关闭
     */
    public static boolean isSoftInputOpen(Activity activity) {
        if (activity == null) {
            throw new RuntimeException("activity must not null!");
        }
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            return imm.isActive();
        } catch (Exception e) {
        }
        return false;
    }
}
