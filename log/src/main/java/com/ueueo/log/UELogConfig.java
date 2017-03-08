package com.ueueo.log;

import java.util.ArrayList;
import java.util.List;

/**
 * Log打印默认配置
 */
public final class UELogConfig {

    public static final String DEFAULT_TAG = "UEUEO";
    /**
     * tag is used for the Log, the name is a little different
     * in order to differentiate the logs easily with the filter
     */
    private String tag;
    private int methodCount = 1;
    private boolean showThreadInfo = true;
    private boolean printToFile = false;

    private List<UELogTool> logToolList = new ArrayList<>();
    /**
     * 日志级别，只有大于等于logLevel的日志才会打印
     * <p/>
     * 参考：{@link UELogLevel}
     */
    private int logLevel = UELogLevel.VERBOSE;

    public UELogConfig() {
        logToolList.add(new UEAndroidLogTool());
        logToolList.add(new UEFileLogTool());
    }

    public UELogConfig tag(String tag) {
        this.tag = tag;
        return this;
    }

    public UELogConfig showThreadInfo(boolean isShow) {
        showThreadInfo = isShow;
        return this;
    }

    public UELogConfig methodCount(int methodCount) {
        if (methodCount < 0) {
            methodCount = 0;
        }
        this.methodCount = methodCount;
        return this;
    }

    public UELogConfig printToFile(boolean printToFile) {
        this.printToFile = printToFile;
        return this;
    }

    public UELogConfig addLogTool(UELogTool logTool) {
        if (logTool != null && !logToolList.contains(logTool)) {
            logToolList.add(logTool);
        }
        return this;
    }

    int getMethodCount() {
        return methodCount;
    }

    boolean isShowThreadInfo() {
        return showThreadInfo;
    }

    int getLogLevel() {
        return logLevel;
    }

    public UELogConfig setLogLevel(int logLevel) {
        this.logLevel = logLevel;
        return this;
    }

    String getTag() {
        return tag;
    }

    boolean isPrintToFile() {
        return printToFile;
    }

    List<UELogTool> getLogToolList() {
        return logToolList;
    }

}
