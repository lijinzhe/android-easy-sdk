package com.ueueo.utils;

import android.content.Context;
import android.content.res.Resources;

/**
 * 手机屏幕信息工具类<br>
 *
 * @author Lee
 * @date 2014-3-19
 */
public class ScreenUtil {

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static float getStatusBarHeight(Context context) {
        if (context == null) {
            throw new RuntimeException("context must not null");
        }
        return context.getResources().getDimensionPixelSize(Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android"));
    }

    /**
     * 获取屏幕像素密度比
     *
     * @param context
     * @return
     */
    public static float getDensity(Context context) {
        if (context == null) {
            throw new RuntimeException("context must not null");
        }
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        if (context == null) {
            throw new RuntimeException("context must not null");
        }
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        if (context == null) {
            throw new RuntimeException("context must not null");
        }
        return context.getResources().getDisplayMetrics().widthPixels;
    }
}
