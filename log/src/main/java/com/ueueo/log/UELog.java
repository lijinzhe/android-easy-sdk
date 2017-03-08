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
 * Assert: 在Log.wtf()作为参数，表明当前问题是个严重的等级。
 */
public final class UELog {

    private static UELogPrinter printer = new UELogPrinter();

    //no instance
    private UELog() {
    }

    public static void init(String tag) {
        printer.getLogConfig().tag(tag);
    }

    public static void init(String tag, int methodCount) {
        printer.getLogConfig().tag(tag).methodCount(methodCount);
    }

    public static void init(String tag, int methodCount, boolean printToFile) {
        printer.getLogConfig().tag(tag).methodCount(methodCount).printToFile(printToFile);
    }

    public static void addLogTool(UELogTool logTool) {
        printer.getLogConfig().addLogTool(logTool);
    }

    public static UELogPrinter tag(String tag) {
        return printer.tag(tag);
    }

    public static UELogPrinter method(int methodCount) {
        return printer.method(methodCount);
    }

    public static UELogPrinter file(boolean file) {
        return printer.file(file);
    }

    public static UELogPrinter append(String message, Object... args) {
        printer.append(message, args);
        return printer;
    }

    public static UELogPrinter appendJson(String json) {
        printer.appendJson(json);
        return printer;
    }

    public static UELogPrinter appendXml(String xml) {
        printer.appendXml(xml);
        return printer;
    }

    public static UELogPrinter appendObject(Object object) {
        printer.appendObject(object);
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
