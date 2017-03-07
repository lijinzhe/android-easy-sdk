package com.ueueo.log;

public final class Settings {

    private int methodCount = 1;
    private boolean showThreadInfo = true;
    private int methodOffset = 0;
    private LogTool logTool;

    /**
     * 日志级别，只有大于等于logLevel的日志才会打印
     * <p/>
     * 参考：{@link LogLevel}
     */
    private int logLevel = LogLevel.VERBOSE;

    public Settings hideThreadInfo() {
        showThreadInfo = false;
        return this;
    }

    public Settings methodCount(int methodCount) {
        if (methodCount < 0) {
            methodCount = 0;
        }
        this.methodCount = methodCount;
        return this;
    }

    public Settings methodOffset(int offset) {
        this.methodOffset = offset;
        return this;
    }

    public Settings logTool(LogTool logTool) {
        this.logTool = logTool;
        return this;
    }

    public int getMethodCount() {
        return methodCount;
    }

    /**
     * Use {@link #methodCount}
     */
    @Deprecated
    public Settings setMethodCount(int methodCount) {
        return methodCount(methodCount);
    }

    public boolean isShowThreadInfo() {
        return showThreadInfo;
    }

    public int getLogLevel() {
        return logLevel;
    }

    /**
     * 设置日志级别，只有大于等于logLevel的日志才会打印
     * <p/>
     * 参考：{@link LogLevel}
     *
     * @param logLevel
     * @return
     */
    public Settings setLogLevel(int logLevel) {
        this.logLevel = logLevel;
        return this;
    }

    public int getMethodOffset() {
        return methodOffset;
    }

    /**
     * Use {@link #methodOffset}
     */
    @Deprecated
    public Settings setMethodOffset(int offset) {
        return methodOffset(offset);
    }

    public LogTool getLogTool() {
        if (logTool == null) {
            logTool = new AndroidLogTool();
        }
        return logTool;
    }
}
