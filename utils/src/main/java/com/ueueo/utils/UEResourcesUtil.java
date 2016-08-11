package com.ueueo.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;

/**
 * 读取资源文件工具
 * <p/>
 * 主要用于resources中sdk版本适配问题
 * Created by Lee on 16/8/9.
 */
public class UEResourcesUtil {
    public static int getColor(Context context, int resId) {
        if (android.os.Build.VERSION.SDK_INT < 23) {
            return context.getResources().getColor(resId);
        } else {
            return context.getResources().getColor(resId, context.getTheme());
        }
    }

    public static ColorStateList getColorStateList(Context context, int resId) {
        if (android.os.Build.VERSION.SDK_INT < 23) {
            return context.getResources().getColorStateList(resId);
        } else {
            return context.getResources().getColorStateList(resId, context.getTheme());
        }
    }

    public static Drawable getDrawable(Context context, int resId) {
        if (android.os.Build.VERSION.SDK_INT < 21) {
            return context.getResources().getDrawable(resId);
        } else {
            return context.getResources().getDrawable(resId, context.getTheme());
        }
    }

    public static Drawable getDrawableForDensity(Context context, int resId, int density) {
        if (android.os.Build.VERSION.SDK_INT >= 15 && android.os.Build.VERSION.SDK_INT < 21) {
            return context.getResources().getDrawableForDensity(resId, density);
        } else if (android.os.Build.VERSION.SDK_INT >= 21) {
            return context.getResources().getDrawableForDensity(resId, density, context.getTheme());
        }
        return null;
    }
}
