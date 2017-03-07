package com.ueueo.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 应用程序工具类
 * <p/>
 * 例如：安装／卸载／打开等操作
 * Created by Lee on 16/7/20.
 */
public class UEAppUtil {

    /**
     * 安装指定路径下的Apk
     * <p>根据路径名是否符合和文件是否存在判断是否安装成功
     * <p>更好的做法应该是startActivityForResult回调判断是否安装成功比较妥当
     * <p>这里做不了回调，后续自己做处理
     */
    public static boolean installApp(Context context, String filePath) {
        if (filePath != null && filePath.length() > 4 && filePath.toLowerCase().substring(filePath.length() - 4).equals(".apk")) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            File file = new File(filePath);
            if (file.exists() && file.isFile() && file.length() > 0) {
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return true;
            }
        }
        return false;
    }

    /**
     * 卸载指定包名的App
     * <p>这里卸载成不成功只判断了packageName是否为空
     * <p>如果要根据是否卸载成功应该用startActivityForResult回调判断是否还存在比较妥当
     * <p>这里做不了回调，后续自己做处理
     */
    public static boolean uninstallApp(Context context, String packageName) {
        if (!TextUtils.isEmpty(packageName)) {
            Intent intent = new Intent(Intent.ACTION_DELETE);
            intent.setData(Uri.parse("package:" + packageName));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        }
        return false;
    }

    /**
     * 打开指定包名的App
     */
    public static boolean openApp(Context context, String packageName) {
        if (!TextUtils.isEmpty(packageName)) {
            PackageManager pm = context.getPackageManager();
            Intent launchIntentForPackage = pm.getLaunchIntentForPackage(packageName);
            if (launchIntentForPackage != null) {
                context.startActivity(launchIntentForPackage);
                return true;
            }
        }
        return false;
    }

    /**
     * 获取当前App信息
     *
     * @param context
     * @return
     */
    public static AppInfo getAppInfo(Context context) {
        return getAppInfo(context, context.getPackageName());
    }

    /**
     * 获取指定包名的App信息
     *
     * @param context
     * @param packageName
     * @return
     */
    public static AppInfo getAppInfo(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pi != null ? getApkInfo(pm, pi) : null;
    }

    /**
     * 获取所有已安装App信息
     */
    public static List<AppInfo> getAllAppInfo(Context context) {
        List<AppInfo> list = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        // 获取系统中安装的所有软件信息
        List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
        for (PackageInfo pi : installedPackages) {
            if (pi != null) {
                list.add(getApkInfo(pm, pi));
            }
        }
        return list;
    }

    /**
     * 打开指定包名的App应用信息界面
     *
     * @param context
     * @param packageName 启动的App的包名
     * @return
     */
    public static boolean openAppSetting(Context context, String packageName) {
        if (!TextUtils.isEmpty(packageName)) {
            Intent intent = new Intent();
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + packageName));
            context.startActivity(intent);
            return true;
        }
        return false;
    }

    /**
     * 判断当前App处于前台还是后台
     * <p/>
     * 需添加权限
     * <uses-permission android:name="android.permission.GET_TASKS"/>
     * <p/>
     * 并且必须是系统应用该方法才有效
     */
    public static boolean isBackground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        @SuppressWarnings("deprecation")
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    private static AppInfo getApkInfo(PackageManager packageManager, PackageInfo packageInfo) {
        ApplicationInfo ai = packageInfo.applicationInfo;
        String name = ai.loadLabel(packageManager).toString();
        Drawable icon = ai.loadIcon(packageManager);
        String packageName = packageInfo.packageName;
        String versionName = packageInfo.versionName;
        int versionCode = packageInfo.versionCode;
        boolean isSD = (ApplicationInfo.FLAG_SYSTEM & ai.flags) != ApplicationInfo.FLAG_SYSTEM;
        boolean isUser = (ApplicationInfo.FLAG_SYSTEM & ai.flags) != ApplicationInfo.FLAG_SYSTEM;

        AppInfo apkInfo = new AppInfo();
        apkInfo.name = name;
        apkInfo.icon = icon;
        apkInfo.packagName = packageName;
        apkInfo.versionCode = versionCode;
        apkInfo.versionName = versionName;
        apkInfo.isSD = isSD;
        apkInfo.isUser = isUser;
        return apkInfo;
    }

    /**
     * 封装App信息的Bean类
     */
    public static class AppInfo {
        //名称
        public String name;
        //图标
        public Drawable icon;
        //包名
        public String packagName;
        //版本号
        public String versionName;
        //版本Code
        public int versionCode;
        //是否安装在SD卡
        public boolean isSD;
        //是否是用户程序
        public boolean isUser;
    }
}

