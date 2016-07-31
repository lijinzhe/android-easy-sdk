package com.ueueo.logger;

public interface Printer {

    Printer tag(String tag);

    Printer method(int methodCount);

    Printer header(String message, Object... args);

    Printer footer(String message, Object... args);

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
