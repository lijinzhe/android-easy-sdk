package com.ueueo.log;

import android.app.Application;

public final class UELogSetting {

    private int methodCount = 1;
    private boolean showThreadInfo = true;
    private int methodOffset = 0;
    private boolean savaFile = false;

    private UEAndroidLogTool androidLogTool = null;
    private UEFileLogTool fileLogTool = null;
    private Application application = null;

    public UELogSetting(Application application){
        this.androidLogTool = new UEAndroidLogTool();
        this.application = application;
    }

    /**
     * 日志级别，只有大于等于logLevel的日志才会打印
     * <p/>
     * 参考：{@link UELogLevel}
     */
    private int logLevel = UELogLevel.VERBOSE;

    public UELogSetting hideThreadInfo() {
        showThreadInfo = false;
        return this;
    }

    public UELogSetting methodCount(int methodCount) {
        if (methodCount < 0) {
            methodCount = 0;
        }
        this.methodCount = methodCount;
        return this;
    }

    public UELogSetting methodOffset(int offset) {
        this.methodOffset = offset;
        return this;
    }

    public int getMethodCount() {
        return methodCount;
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
     * 参考：{@link UELogLevel}
     *
     * @param logLevel
     * @return
     */
    public UELogSetting setLogLevel(int logLevel) {
        this.logLevel = logLevel;
        return this;
    }

    public int getMethodOffset() {
        return methodOffset;
    }

    public boolean isSavaFile() {
        return savaFile;
    }

    public void setSavaFile(boolean savaFile) {
        this.savaFile = savaFile;
    }

    public UEAndroidLogTool getAndroidLogTool() {
        return androidLogTool;
    }

    public UEFileLogTool getFileLogTool() {
        if(fileLogTool == null){
            fileLogTool = new UEFileLogTool(application);
        }
        return fileLogTool;
    }
}
