package com.ueueo.cache;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Created by Lee on 16/7/11.
 */
public interface Cache {

    public void putString(@Nullable String key, @Nullable String value);

    public void putString(@Nullable String key, @Nullable String value, long expiresIn);

    public String getString(@Nullable String key, String defaultValue);

    public void putInt(@Nullable String key, int value);

    public void putInt(@Nullable String key, int value, long expiresIn);

    public int getInt(@Nullable String key, int defaultValue);

    public void putFloat(@Nullable String key, float value);

    public void putFloat(@Nullable String key, float value, long expiresIn);

    public float getFloat(@Nullable String key, float defaultValue);

    public void putLong(@Nullable String key, long value);

    public void putLong(@Nullable String key, long value, long expiresIn);

    public long getLong(@Nullable String key, long defaultValue);

    public void putBoolean(@Nullable String key, boolean value);

    public void putBoolean(@Nullable String key, boolean value, long expiresIn);

    public boolean getBoolean(@Nullable String key, boolean defaultValue);

    public void putBytes(@Nullable String key, @Nullable byte[] value);

    public void putBytes(@Nullable String key, @Nullable byte[] value, long expiresIn);

    public byte[] getBytes(@Nullable String key, byte[] defaultValue);

    public void putJSONObject(@Nullable String key, @Nullable JSONObject value);

    public void putJSONObject(@Nullable String key, @Nullable JSONObject value, long expiresIn);

    public JSONObject getJSONObject(@Nullable String key, JSONObject defaultValue);

    public void putJSONArray(@Nullable String key, @Nullable JSONArray value);

    public void putJSONArray(@Nullable String key, @Nullable JSONArray value, long expiresIn);

    public JSONArray getJSONArray(@Nullable String key, JSONArray defaultValue);

    public void putObject(@Nullable String key, @Nullable Object value);

    public void putObject(@Nullable String key, @Nullable Object value, long expiresIn);

    public <T> T getObject(@Nullable String key, T defaultValue, Class<T> classOfT);

    public void putObjectArray(@Nullable String key, @Nullable List value);

    public void putObjectArray(@Nullable String key, @Nullable List value, long expiresIn);

    public <T> List<T> getObjectArray(@Nullable String key, @Nullable List<T> defaultValue, Type type);

    public void putObjectMap(@Nullable String key, @Nullable Map value);

    public void putObjectMap(@Nullable String key, @Nullable Map value, long expiresIn);

    public <K, V> Map<K, V> getObjectMap(@Nullable String key, @Nullable Map<K, V> defaultValue, Type type);

    public void remove(@Nullable String key);

    public void clear();

    public boolean contains(@Nullable String key);
}
