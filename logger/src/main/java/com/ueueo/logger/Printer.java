package com.ueueo.logger;

import org.json.JSONObject;

public interface Printer {

    Printer tag(String tag);

    Printer method(int methodCount);

    Printer header(String message, Object... args);

    Printer headerJson(String json);

    Printer headerXml(String xml);

    Printer headerObject(Object obj);

    Printer footer(String message, Object... args);

    Printer footerJson(String json);

    Printer footerXml(String xml);

    Printer footerObject(Object obj);

    Settings init(String tag);

    Settings getSettings();

    void d(String message, Object... args);

    void e(String message, Object... args);

    void e(Throwable throwable, String message, Object... args);

    void w(String message, Object... args);

    void i(String message, Object... args);

    void v(String message, Object... args);

    void wtf(String message, Object... args);

    void json(String json);

    void xml(String xml);

    void object(Object obj);

    void clear();
}
