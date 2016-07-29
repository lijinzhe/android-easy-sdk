package com.ueueo.logger;

import android.content.Context;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class AndroidFileLogTool implements LogTool {

    // 日志文件保存的文件夹目录
    private static String LOG_DIR_PATH;

    private static HashMap<String, File> mLogFiles = new HashMap<>();

    public AndroidFileLogTool(Context context) {
        LOG_DIR_PATH = context.getExternalFilesDir("LOG").getAbsolutePath();
    }

    @Override
    public void d(String tag, String message) {
        Log.d(tag, message);
        writeToFile(Log.DEBUG, tag, message);
    }

    @Override
    public void e(String tag, String message) {
        Log.e(tag, message);
        writeToFile(Log.ERROR, tag, message);
    }

    @Override
    public void w(String tag, String message) {
        Log.w(tag, message);
        writeToFile(Log.WARN, tag, message);
    }

    @Override
    public void i(String tag, String message) {
        Log.i(tag, message);
        writeToFile(Log.INFO, tag, message);
    }

    @Override
    public void v(String tag, String message) {
        Log.v(tag, message);
        writeToFile(Log.VERBOSE, tag, message);
    }

    @Override
    public void wtf(String tag, String message) {
        Log.wtf(tag, message);
        writeToFile(Log.ERROR, tag, message);
    }

    /**
     * 将日志写入文件中
     *
     * @param priority
     * @param tag
     * @param msg
     */
    private synchronized void writeToFile(int priority, String tag, String msg) {
        File logFile = mLogFiles.get(tag);
        if (logFile == null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = new Date(System.currentTimeMillis());
            String fileName = format.format(date) + ".log";
            File logDir = new File(LOG_DIR_PATH, tag);
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
            logFile = new File(logDir, fileName);
            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                } catch (IOException e) {
                }
            }
        }
        BufferedWriter bufWriter = null;
        OutputStreamWriter out = null;
        try {
            out = new OutputStreamWriter(new FileOutputStream(logFile, true), "UTF-8");
            bufWriter = new BufferedWriter(out);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Date date = new Date(System.currentTimeMillis());
            String priorityName = null;
            if (priority == Log.VERBOSE) {
                priorityName = "V";
            } else if (priority == Log.INFO) {
                priorityName = "I";
            } else if (priority == Log.DEBUG) {
                priorityName = "D";
            } else if (priority == Log.WARN) {
                priorityName = "W";
            } else if (priority == Log.ERROR) {
                priorityName = "E";
            } else {
                priorityName = "V";
            }
            bufWriter.write(format.format(date) + ": " + priorityName + "/" + tag + ": " + msg + "\r\n");
        } catch (Exception e) {
        } catch (Error error) {
        } finally {
            if (bufWriter != null) {
                try {
                    bufWriter.close();
                } catch (Exception e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                }
            }
        }
    }
}
