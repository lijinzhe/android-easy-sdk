package com.ueueo.log;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;

/**
 * Logger is a wrapper of {@link android.util.Log}
 * But more pretty, simple and powerful
 * <p/>
 * Verbose: 用来打印输出价值比较低的信息。
 * Debug: 用来打印调试信息，在Release版本不会输出。
 * Info: 用来打印一般提示信息。
 * Warn: 用来打印警告信息，这种信息一般是提示开发者需要注意，有可能会出现问题！
 * Error: 用来打印错误崩溃日志信息，例如在try-catch的catch中输出捕获的错误信息。
 * Assert: 在Log.wtf()作为参数，表明当前问题是个严重的等级。
 */
public final class UELog {
    private static final String DEFAULT_TAG = "logger";

    private static UELogPrinter printer = new UELogPrinter();

    //no instance
    private UELog() {
    }

    /**
     * 判断是否可以打印Debug级别的日志
     *
     * @return
     */
    public static boolean isDebugEnabled() {
        return (printer.getSettings().getLogLevel() <= UELogLevel.DEBUG);
    }

    /**
     * It is used to get the settings object in order to change settings
     *
     * @return the settings object
     */
    public static UELogSetting init(Application application) {
        return init(DEFAULT_TAG,application);
    }

    /**
     * It is used to change the tag
     *
     * @param tag is the given string which will be used in Logger as TAG
     */
    public static UELogSetting init(String tag, Application application) {
        printer = new UELogPrinter();
        return printer.init(tag,application);
    }

    public static void clear() {
        printer.clear();
        printer = null;
    }

    public static UELogPrinter tag(String tag) {
        return printer.tag(tag);
    }

    public static UELogPrinter method(int methodCount) {
        return printer.method(methodCount);
    }

    public static UELogPrinter header(String message, Object... args) {
        printer.header(message, args);
        return printer;
    }

    public static UELogPrinter footer(String message, Object... args) {
        printer.footer(message, args);
        return printer;
    }

    public static UELogPrinter headerJson(String json) {
        printer.headerJson(json);
        return printer;
    }

    public static UELogPrinter headerXml(String xml) {
        printer.headerXml(xml);
        return printer;
    }

    public static UELogPrinter headerObject(Object obj) {
        printer.headerObject(obj);
        return printer;
    }

    public static UELogPrinter footerJson(String json) {
        printer.footerJson(json);
        return printer;
    }

    public static UELogPrinter footerXml(String xml) {
        printer.footerXml(xml);
        return printer;
    }

    public static UELogPrinter footerObject(Object obj) {
        printer.footerObject(obj);
        return printer;
    }

    public static void d(String message, Object... args) {
        printer.d(message, args);
    }

    public static void e(String message, Object... args) {
        printer.e(null, message, args);
    }

    public static void e(Throwable throwable, String message, Object... args) {
        printer.e(throwable, message, args);
    }

    public static void i(String message, Object... args) {
        printer.i(message, args);
    }

    public static void v(String message, Object... args) {
        printer.v(message, args);
    }

    public static void w(String message, Object... args) {
        printer.w(message, args);
    }

    public static void wtf(String message, Object... args) {
        printer.wtf(message, args);
    }

    /**
     * Formats the json content and print it
     *
     * @param json the json content
     */
    public static void json(String json) {
        printer.json(json);
    }

    /**
     * Formats the json content and print it
     *
     * @param xml the xml content
     */
    public static void xml(String xml) {
        printer.xml(xml);
    }

    /**
     * Formats the obj content and print it
     *
     * @param obj the xml content
     */
    public static void object(Object obj) {
        printer.object(obj);
    }

    /**
     * 打印手机状态信息
     * <p>需添加权限<uses-permission android:name="android.permission.READ_PHONE_STATE"/>
     */
    public static void printPhoneInfo(Context context) {
        if (context == null) {
            throw new RuntimeException("context must not null");
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (context.getPackageManager().checkPermission(Manifest.permission.READ_PHONE_STATE, context.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                d("Must have permission 'android.permission.READ_PHONE_STATE'");
                return;
            }
        } else {
            if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                d("Must have permission 'android.permission.READ_PHONE_STATE'");
                return;
            }
        }
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("DeviceId(IMEI) = ").append(tm.getDeviceId()).append("\n");
        stringBuilder.append("DeviceSoftwareVersion = ").append(tm.getDeviceSoftwareVersion()).append("\n");
        stringBuilder.append("Line1Number = ").append(tm.getLine1Number()).append("\n");
        stringBuilder.append("NetworkCountryIso = ").append(tm.getNetworkCountryIso()).append("\n");
        stringBuilder.append("NetworkOperator = ").append(tm.getNetworkOperator()).append("\n");
        stringBuilder.append("NetworkOperatorName = ").append(tm.getNetworkOperatorName()).append("\n");
        stringBuilder.append("NetworkType = ").append(tm.getNetworkType()).append("\n");
        stringBuilder.append("honeType = ").append(tm.getPhoneType()).append("\n");
        stringBuilder.append("SimCountryIso = ").append(tm.getSimCountryIso()).append("\n");
        stringBuilder.append("SimOperator = ").append(tm.getSimOperator()).append("\n");
        stringBuilder.append("SimOperatorName = ").append(tm.getSimOperatorName()).append("\n");
        stringBuilder.append("SimSerialNumber = ").append(tm.getSimSerialNumber()).append("\n");
        stringBuilder.append("SimState = ").append(tm.getSimState()).append("\n");
        stringBuilder.append("SubscriberId(IMSI) = ").append(tm.getSubscriberId()).append("\n");
        stringBuilder.append("VoiceMailNumber = ").append(tm.getVoiceMailNumber()).append("\n");
        d(stringBuilder.toString());
    }

    /**
     * 打印手机存储空间信息
     *
     * @param context
     */
    public static void printStorageInfo(Context context) {

    }

    /**
     * 打印手机内存信息
     *
     * @param context
     */
    public static void printMemoryInfo(Context context) {

    }

    /**
     * 打印网络信息
     *
     * @param context
     */
    public static void printNetworkInfo(Context context) {

    }

    /**
     * 显示日志悬浮窗
     *
     * @param context
     */
    public static void showFloatWidget(Context context) {

    }

    /**
     * 关闭日志悬浮窗
     *
     * @param context
     */
    public static void hideFloatWidget(Context context) {

    }
}
