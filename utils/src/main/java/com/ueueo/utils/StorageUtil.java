package com.ueueo.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;

/**
 * 存储空间工具<br>
 * 需要开启权限：<br>
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 *
 * @author Lee
 * @date 2014-3-19
 */
public class StorageUtil {

    /**
     * 是否有SD卡
     *
     * @return
     */
    public static boolean hasSDCard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取SD卡根目录文件夹
     *
     * @return
     */
    public static File getSDCardRootDir() {
        return Environment.getExternalStorageDirectory();
    }

    /**
     * 获取SD卡空间大小 单位：byte
     *
     * @return
     */
    public static long getSDCardSpace() {
        File dir = Environment.getExternalStorageDirectory();
        if (dir != null) {
            StatFs sf = new StatFs(dir.getAbsolutePath());
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                return (long) sf.getBlockCount() * (long) sf.getBlockSize();
            } else {
                return sf.getBlockCountLong() * sf.getBlockSizeLong();
            }
        } else {
            return 0;
        }
    }

    /**
     * 获取SD卡可用存储空间 单位：byte
     *
     * @return
     */
    public static long getSDCardAvailableSpace() {
        File dir = Environment.getExternalStorageDirectory();
        if (dir != null) {
            StatFs sf = new StatFs(dir.getAbsolutePath());
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                return (long) sf.getAvailableBlocks() * (long) sf.getBlockSize();
            } else {
                return sf.getAvailableBlocksLong() * sf.getBlockSizeLong();
            }
        } else {
            return 0;
        }
    }

    /**
     * 获取SD卡已用空间大小 单位：byte
     *
     * @return
     */
    public static long getSDCardUsedSpace() {
        File dir = Environment.getExternalStorageDirectory();
        if (dir != null) {
            StatFs sf = new StatFs(dir.getAbsolutePath());
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                return (long) (sf.getBlockCount() - sf.getAvailableBlocks()) * (long) sf.getBlockSize();
            } else {
                return (sf.getBlockCountLong() - sf.getAvailableBlocksLong()) * sf.getBlockSizeLong();
            }
        } else {
            return 0;
        }
    }

    /**
     * 获取手机内部存储空间大小 单位：byte
     *
     * @return
     */
    public static long getInternalSpace() {
        StatFs sf = new StatFs("/data");
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return (long) sf.getBlockCount() * (long) sf.getBlockSize();
        } else {
            return sf.getBlockCountLong() * sf.getBlockSizeLong();
        }
    }

    /**
     * 获取手机内部存储可用空间 单位：byte
     *
     * @return
     */
    public static long getInternalAvailableSpace() {
        StatFs sf = new StatFs("/data");
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return (long) sf.getAvailableBlocks() * (long) sf.getBlockSize();
        } else {
            return sf.getAvailableBlocksLong() * sf.getBlockSizeLong();
        }
    }

    /**
     * 获取手机内部存储已用空间大小 单位：byte
     *
     * @return
     */
    public static long getInternalUsedSpace() {
        StatFs sf = new StatFs("/data");
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return (long) (sf.getBlockCount() - sf.getAvailableBlocks()) * (long) sf.getBlockSize();
        } else {
            return (sf.getBlockCountLong() - sf.getAvailableBlocksLong()) * sf.getBlockSizeLong();
        }
    }

    /**
     * 获取手机SD卡当前应用程序根目录文件夹
     *
     * @return
     */
    public static File getAppExternalRootDir(Context context) {
        if (context == null) {
            throw new RuntimeException("context must not null!");
        }
        File dir = context.getExternalFilesDir(null);
        if (dir != null && !dir.exists()) {
            dir.mkdirs();
        }
        if (dir != null && dir.exists()) {
            return dir;
        }
        return null;
    }

    /**
     * 获取手机SD卡当前应用程序根目录下指定名字的文件夹
     *
     * @param name
     * @return
     */
    public static File getAppExternalDir(Context context, String name) {
        if (context == null) {
            throw new RuntimeException("context must not null!");
        }
        File dir = context.getExternalFilesDir(name);
        if (dir != null && !dir.exists()) {
            dir.mkdirs();
        }
        if (dir != null && dir.exists()) {
            return dir;
        }
        return null;
    }

    /**
     * 获取手机内部存储空间当前应用程序根目录文件夹
     *
     * @return
     */
    public static File getAppInternalRootDir(Context context) {
        if (context == null) {
            throw new RuntimeException("context must not null!");
        }
        File dir = context.getDir("", Context.MODE_PRIVATE);
        if (dir != null && !dir.exists()) {
            dir.mkdirs();
        }
        if (dir != null && dir.exists()) {
            return dir;
        }
        return null;
    }

    /**
     * 获取手机内部存储空间当前应用程序下指定名字的文件夹
     *
     * @param name
     * @return
     */
    public static File getAppInternalDir(Context context, String name) {
        if (context == null) {
            throw new RuntimeException("context must not null!");
        }
        File dir = context.getDir(name, Context.MODE_PRIVATE);
        if (dir != null && !dir.exists()) {
            dir.mkdirs();
        }
        if (dir != null && dir.exists()) {
            return dir;
        }
        return null;
    }
}
