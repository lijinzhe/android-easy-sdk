package com.ueueo.log;

/**
 * Logger is a wrapper of {@link android.util.Log}
 * But more pretty, simple and powerful
 * <p/>
 * Verbose: 用来打印输出价值比较低的信息。
 * Debug: 用来打印调试信息，在Release版本不会输出。
 * Info: 用来打印一般提示信息。
 * Warn: 用来打印警告信息，这种信息一般是提示开发者需要注意，有可能会出现问题！
 * Error: 用来打印错误崩溃日志信息，例如在try-catch的catch中输出捕获的错误信息。
 * Assert: 在Log.wtf()作为参数，表面当前问题是个严重的等级。
 */
public final class Logger {
    private static final String DEFAULT_TAG = "logger";

    private static Printer printer = new LoggerPrinter();

    //no instance
    private Logger() {
    }

    /**
     * 判断是否可以打印Debug级别的日志
     *
     * @return
     */
    public static boolean isDebugEnabled() {
        return (printer.getSettings().getLogLevel() <= LogLevel.DEBUG);
    }

    /**
     * It is used to get the settings object in order to change settings
     *
     * @return the settings object
     */
    public static Settings init() {
        return init(DEFAULT_TAG);
    }

    /**
     * It is used to change the tag
     *
     * @param tag is the given string which will be used in Logger as TAG
     */
    public static Settings init(String tag) {
        printer = new LoggerPrinter();
        return printer.init(tag);
    }

    public static void clear() {
        printer.clear();
        printer = null;
    }

    public static Printer tag(String tag) {
        return printer.tag(tag);
    }

    public static Printer method(int methodCount) {
        return printer.method(methodCount);
    }

    public static Printer header(String message, Object... args) {
        printer.header(message, args);
        return printer;
    }

    public static Printer footer(String message, Object... args) {
        printer.footer(message, args);
        return printer;
    }

    public static Printer headerJson(String json) {
        printer.headerJson(json);
        return printer;
    }

    public static Printer headerXml(String xml) {
        printer.headerXml(xml);
        return printer;
    }

    public static Printer headerObject(Object obj) {
        printer.headerObject(obj);
        return printer;
    }

    public static Printer footerJson(String json) {
        printer.footerJson(json);
        return printer;
    }

    public static Printer footerXml(String xml) {
        printer.footerXml(xml);
        return printer;
    }

    public static Printer footerObject(Object obj) {
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
}
