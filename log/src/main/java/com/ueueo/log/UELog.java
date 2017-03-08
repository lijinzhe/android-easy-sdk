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

    //日志打印机
    private static UELogPrinter printer = new UELogPrinter();

    private UELog() {
    }

    /**
     * 初始化全局配置
     *
     * @param tag 全局标签（默认为UEUEO）
     */
    public static void init(String tag) {
        printer.getLogConfig().tag(tag);
    }

    /**
     * 初始化全局配置
     *
     * @param tag         全局标签（默认为UEUEO）
     * @param methodCount 全局显示方法调用栈数量（默认为1）
     */
    public static void init(String tag, int methodCount) {
        printer.getLogConfig().tag(tag).methodCount(methodCount);
    }

    /**
     * 初始化全局配置
     *
     * @param tag         全局Tag（默认为UEUEO）
     * @param methodCount 全局显示方法调用栈数量（默认为1）
     * @param printToFile 全局是否输出到文件中（默认为否）
     */
    public static void init(String tag, int methodCount, boolean printToFile) {
        printer.getLogConfig().tag(tag).methodCount(methodCount).printToFile(printToFile);
    }

    /**
     * 添加新的日志输入工具
     *
     * @param logTool
     */
    public static void addLogTool(UELogTool logTool) {
        printer.getLogConfig().addLogTool(logTool);
    }

    /**
     * 指定当前这条Log信息打印的tag，不受全局配置影响
     *
     * @param tag
     * @return
     */
    public static UELogPrinter tag(String tag) {
        return printer.tag(tag);
    }

    /**
     * 指定当前这条Log信息打印的方法调用栈数量，不受全局配置影响
     *
     * @param methodCount
     * @return
     */
    public static UELogPrinter method(int methodCount) {
        return printer.method(methodCount);
    }

    /**
     * 指定当前这条Log信息是否打印到文件，不受全局配置影响
     *
     * @param file
     * @return
     */
    public static UELogPrinter file(boolean file) {
        return printer.file(file);
    }

    /**
     * 拼接日志，每条日志之间会有分割线分割
     *
     * @param message
     * @param args
     * @return
     */
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

    public static void json(String json) {
        printer.json(json);
    }

    public static void xml(String xml) {
        printer.xml(xml);
    }

    public static void object(Object obj) {
        printer.object(obj);
    }

}
