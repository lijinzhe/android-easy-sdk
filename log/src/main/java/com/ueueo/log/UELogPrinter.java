package com.ueueo.log;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

/**
 * Logger is a wrapper for logging utils
 * But more pretty, simple and powerful
 */
final class UELogPrinter {

    /**
     * Android's max limit for a log entry is ~4076 bytes,
     * so 4000 bytes is used as chunk size since default charset
     * is UTF-8
     */
//    private static final int CHUNK_SIZE = 4000;

    /**
     * It is used for json pretty print
     */
    private static final int JSON_INDENT = 4;

    /**
     * The minimum stack trace index, starts at this class after two native calls.
     */
    private static final int MIN_STACK_OFFSET = 3;

    /**
     * Drawing toolbox
     */
    private static final char TOP_LEFT_CORNER = '╔';
    private static final char BOTTOM_LEFT_CORNER = '╚';
    private static final char MIDDLE_CORNER = '╟';
    private static final char HORIZONTAL_DOUBLE_LINE = '║';
    private static final String DOUBLE_DIVIDER = "════════════════════════════════════════════";
    private static final String SINGLE_DIVIDER = "────────────────────────────────────────────";
    private static final String TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String MIDDLE_BORDER = MIDDLE_CORNER + SINGLE_DIVIDER + SINGLE_DIVIDER;
    private static final String HEADER_FOOTER_SEPARATOR = "3k3o9dwq";
    /**
     * Localize single tag and method count for each thread
     */
    private final ThreadLocal<String> localTag = new ThreadLocal<>();
    private final ThreadLocal<Integer> localMethodCount = new ThreadLocal<>();
    private final ThreadLocal<Boolean> localIsSaveFile = new ThreadLocal<>();
    /**
     * tag is used for the Log, the name is a little different
     * in order to differentiate the logs easily with the filter
     */
    private String tag;
    /**
     * It is used to determine log settings such as method count, thread info visibility
     */
    private UELogSetting settings;
    private StringBuffer headerMessage = new StringBuffer();
    private StringBuffer footerMessage = new StringBuffer();

    /**
     * It is used to change the tag
     *
     * @param tag is the given string which will be used in Logger
     */
    public UELogSetting init(String tag, Application application) {
        if (tag == null) {
            throw new NullPointerException("tag may not be null");
        }
        if (tag.trim().length() == 0) {
            throw new IllegalStateException("tag may not be empty");
        }
        this.tag = tag;
        this.settings = new UELogSetting(application);
        return settings;
    }

    public UELogSetting getSettings() {
        return settings;
    }

    public UELogPrinter tag(String tag) {
        if (tag != null) {
            localTag.set(tag);
        }
        return this;
    }

    public UELogPrinter method(int methodCount) {
        localMethodCount.set(methodCount);
        return this;
    }

    public UELogPrinter save(boolean save){
        localIsSaveFile.set(save);
        return this;
    }

    public UELogPrinter header(String message, Object... args) {
        String header = createMessage(message, args);
        if (!TextUtils.isEmpty(header)) {
            headerMessage.append(header).append(HEADER_FOOTER_SEPARATOR);
        }
        return this;
    }

    public UELogPrinter footer(String message, Object... args) {
        String footer = createMessage(message, args);
        if (!TextUtils.isEmpty(footer)) {
            footerMessage.append(footer).append(HEADER_FOOTER_SEPARATOR);
        }
        return this;
    }

    public UELogPrinter headerJson(String json) {
        headerMessage.append(parseJsonMessage(json)).append(HEADER_FOOTER_SEPARATOR);
        return this;
    }

    public UELogPrinter headerXml(String xml) {
        headerMessage.append(parseXmlMessage(xml)).append(HEADER_FOOTER_SEPARATOR);
        return this;
    }

    public UELogPrinter headerObject(Object obj) {
        headerMessage.append(parseObjectMessage(obj)).append(HEADER_FOOTER_SEPARATOR);
        return this;
    }

    public UELogPrinter footerJson(String json) {
        footerMessage.append(parseJsonMessage(json)).append(HEADER_FOOTER_SEPARATOR);
        return this;
    }

    public UELogPrinter footerXml(String xml) {
        footerMessage.append(parseXmlMessage(xml)).append(HEADER_FOOTER_SEPARATOR);
        return this;
    }

    public UELogPrinter footerObject(Object obj) {
        footerMessage.append(parseObjectMessage(obj)).append(HEADER_FOOTER_SEPARATOR);
        return this;
    }

    public void d(String message, Object... args) {
        log(UELogLevel.DEBUG, message, args);
    }

    public void e(String message, Object... args) {
        e(null, message, args);
    }

    public void e(Throwable throwable, String message, Object... args) {
        if (throwable != null && message != null) {
            message += " : " + Log.getStackTraceString(throwable);
        }
        if (throwable != null && message == null) {
            message = throwable.toString();
        }
        if (message == null) {
            message = "No message/exception is set";
        }
        log(UELogLevel.ERROR, message, args);
    }

    public void w(String message, Object... args) {
        log(UELogLevel.WARN, message, args);
    }

    public void i(String message, Object... args) {
        log(UELogLevel.INFO, message, args);
    }

    public void v(String message, Object... args) {
        log(UELogLevel.VERBOSE, message, args);
    }

    public void wtf(String message, Object... args) {
        log(UELogLevel.ASSERT, message, args);
    }

    /**
     * Formats the json content and print it
     *
     * @param json the json content
     */
    public void json(String json) {
        d(parseJsonMessage(json));
    }

    /**
     * Formats the json content and print it
     *
     * @param xml the xml content
     */
    public void xml(String xml) {
        d(parseXmlMessage(xml));
    }

    /**
     * Formats the obj content and print it
     *
     * @param obj the xml content
     */
    public void object(Object obj) {
        d(parseObjectMessage(obj));
    }

    public void clear() {
        settings = null;
    }

    /**
     * This method is synchronized in order to avoid messy of logs' order.
     */
    private synchronized void log(int logType, String msg, Object... args) {
        if (logType < settings.getLogLevel()) {
            return;
        }
        String tag = getTag();
        if (settings.isShowThreadInfo()) {
            tag += "[" + Thread.currentThread().getName() + "]";
        }
        String message = createMessage(msg, args);
        int methodCount = getMethodCount();

        if (TextUtils.isEmpty(message)) {
            message = "Empty/NULL log message";
        }

        logTopBorder(logType, tag);
        logHeaderContent(logType, tag, methodCount);

        //get bytes of message with system's default charset (which is UTF-8 for Android)
//        byte[] bytes = message.getBytes();
//        int length = bytes.length;
        if (methodCount > 0) {
            logDivider(logType, tag);
        }
        String headerString = headerMessage.toString();
        headerMessage.setLength(0);
        if (!TextUtils.isEmpty(headerString)) {
            String[] headers = headerString.split(HEADER_FOOTER_SEPARATOR);
            for (String header : headers) {
                String[] lines = header.split(System.getProperty("line.separator"));
                for (String line : lines) {
                    logChunk(logType, tag, HORIZONTAL_DOUBLE_LINE + " " + line);
                }
                logDivider(logType, tag);
            }
        }
//        if (length <= CHUNK_SIZE) {
        logContent(logType, tag, message);
//        }else{
//            for (int i = 0; i < length; i += CHUNK_SIZE) {
//                int count = Math.min(length - i, CHUNK_SIZE);
//                //create a new String with system's default charset (which is UTF-8 for Android)
//                logContent(logType, tag, new String(bytes, i, count));
//            }
//        }
        String footerString = footerMessage.toString();
        footerMessage.setLength(0);
        if (!TextUtils.isEmpty(footerString)) {
            String[] footers = footerString.split(HEADER_FOOTER_SEPARATOR);
            for (String footer : footers) {
                logDivider(logType, tag);
                String[] lines = footer.split(System.getProperty("line.separator"));
                for (String line : lines) {
                    logChunk(logType, tag, HORIZONTAL_DOUBLE_LINE + " " + line);
                }
            }
        }
        logBottomBorder(logType, tag);
    }

    private void logTopBorder(int logType, String tag) {
        logChunk(logType, tag, TOP_BORDER);
    }

    private void logHeaderContent(int logType, String tag, int methodCount) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
//        if (settings.isShowThreadInfo()) {
//            logChunk(logType, tag, HORIZONTAL_DOUBLE_LINE + " Thread: " + Thread.currentThread().getName());
//            logDivider(logType, tag);
//        }
        String level = "";

        int stackOffset = getStackOffset(trace) + settings.getMethodOffset();

        //corresponding method count with the current stack may exceeds the stack trace. Trims the count
        if (methodCount + stackOffset > trace.length) {
            methodCount = trace.length - stackOffset - 1;
        }

        for (int i = methodCount; i > 0; i--) {
            int stackIndex = i + stackOffset;
            if (stackIndex >= trace.length) {
                continue;
            }
            StringBuilder builder = new StringBuilder();
            builder.append("║ ")
                    .append(level)
                    .append(getSimpleClassName(trace[stackIndex].getClassName()))
                    .append(".")
                    .append(trace[stackIndex].getMethodName())
                    .append(" ")
                    .append(" (")
                    .append(trace[stackIndex].getFileName())
                    .append(":")
                    .append(trace[stackIndex].getLineNumber())
                    .append(")");
            level += "   ";
            logChunk(logType, tag, builder.toString());
        }
    }

    private void logBottomBorder(int logType, String tag) {
        logChunk(logType, tag, BOTTOM_BORDER);
    }

    private void logDivider(int logType, String tag) {
        logChunk(logType, tag, MIDDLE_BORDER);
    }

    private void logContent(int logType, String tag, String chunk) {
        String[] lines = chunk.split(System.getProperty("line.separator"));
        for (String line : lines) {
            logChunk(logType, tag, HORIZONTAL_DOUBLE_LINE + " " + line);
        }
    }

    private void logChunk(int logType, String tag, String chunk) {
        String finalTag = formatTag(tag);
        switch (logType) {
            case UELogLevel.ERROR:
                settings.getAndroidLogTool().e(finalTag, chunk);
                if (settings.isSavaFile()) {
                    settings.getFileLogTool().e(finalTag, chunk);
                }
                break;
            case UELogLevel.INFO:
                settings.getAndroidLogTool().i(finalTag, chunk);
                if (settings.isSavaFile()) {
                    settings.getFileLogTool().i(finalTag, chunk);
                }
                break;
            case UELogLevel.VERBOSE:
                settings.getAndroidLogTool().v(finalTag, chunk);
                if (settings.isSavaFile()) {
                    settings.getFileLogTool().v(finalTag, chunk);
                }
                break;
            case UELogLevel.WARN:
                settings.getAndroidLogTool().w(finalTag, chunk);
                if (settings.isSavaFile()) {
                    settings.getFileLogTool().w(finalTag, chunk);
                }
                break;
            case UELogLevel.ASSERT:
                settings.getAndroidLogTool().wtf(finalTag, chunk);
                if (settings.isSavaFile()) {
                    settings.getFileLogTool().wtf(finalTag, chunk);
                }
                break;
            case UELogLevel.DEBUG:
                settings.getAndroidLogTool().d(finalTag, chunk);
                if (settings.isSavaFile()) {
                    settings.getFileLogTool().d(finalTag, chunk);
                }
                break;
            default:
                settings.getAndroidLogTool().v(finalTag, chunk);
                if (settings.isSavaFile()) {
                    settings.getFileLogTool().v(finalTag, chunk);
                }
                break;
        }
    }

    private String getSimpleClassName(String name) {
        int lastIndex = name.lastIndexOf(".");
        return name.substring(lastIndex + 1);
    }

    private String formatTag(String tag) {
        if (!TextUtils.isEmpty(tag)) {
            return tag;
        }
        return this.tag;
    }

    /**
     * @return the appropriate tag based on local or global
     */
    private String getTag() {
        String tag = localTag.get();
        if (tag != null) {
            localTag.remove();
            return tag;
        }
        return this.tag;
    }

    private String createMessage(String message, Object... args) {
        return args.length == 0 ? message : String.format(message, args);
    }

    private int getMethodCount() {
        Integer count = localMethodCount.get();
        int result = settings.getMethodCount();
        if (count != null) {
            localMethodCount.remove();
            result = count;
        }
        if (result < 0) {
            throw new IllegalStateException("methodCount cannot be negative");
        }
        return result;
    }

    /**
     * Determines the starting index of the stack trace, after method calls made by this class.
     *
     * @param trace the stack trace
     * @return the stack offset
     */
    private int getStackOffset(StackTraceElement[] trace) {
        for (int i = MIN_STACK_OFFSET; i < trace.length; i++) {
            StackTraceElement e = trace[i];
            String name = e.getClassName();
            if (!name.equals(UELogPrinter.class.getName()) && !name.equals(UELog.class.getName())) {
                return --i;
            }
        }
        return -1;
    }

    private String parseXmlMessage(String xml) {
        if (!TextUtils.isEmpty(xml)) {
            try {
                Source xmlInput = new StreamSource(new StringReader(xml));
                StreamResult xmlOutput = new StreamResult(new StringWriter());
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
                transformer.transform(xmlInput, xmlOutput);
                return xmlOutput.getWriter().toString().replaceFirst(">", ">\n");
            } catch (TransformerException e) {
                return "Invalid xml content";
            }
        } else {
            return "Empty/Null xml content";
        }
    }

    private String parseJsonMessage(String json) {
        if (!TextUtils.isEmpty(json)) {
            try {
                json = json.trim();
                if (json.startsWith("{")) {
                    JSONObject jsonObject = new JSONObject(json);
                    String message = jsonObject.toString(JSON_INDENT);
                    return message;
                } else if (json.startsWith("[")) {
                    JSONArray jsonArray = new JSONArray(json);
                    String message = jsonArray.toString(JSON_INDENT);
                    return message;
                } else {
                    return "Invalid json content";
                }
            } catch (JSONException e) {
                return "Invalid json content";
            }
        } else {
            return "Empty/Null json content";
        }
    }

    private String parseObjectMessage(Object obj) {
        if (obj != null) {
            try {
                if (obj instanceof List) {
                    JSONArray jsonArray = new JSONArray();
                    for (Object o : (List) obj) {
                        JSONObject jo = new JSONObject(new Gson().toJson(o));
                        jsonArray.put(jo);
                    }
                    String message = jsonArray.toString(JSON_INDENT);
                    return message;
                } else if (obj instanceof Map) {
                    Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
                    JSONObject jsonObject = new JSONObject(gson.toJson(obj));
                    String message = jsonObject.toString(JSON_INDENT);
                    return message;
                } else {
                    JSONObject jsonObject = new JSONObject(new Gson().toJson(obj));
                    String message = jsonObject.toString(JSON_INDENT);
                    return message;
                }
            } catch (JSONException e) {
                return "Invalid object content";
            }
        } else {
            return "Null object content";
        }
    }
}
