package com.ueueo.cache;

import android.support.annotation.Nullable;
import android.util.LruCache;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by Lee on 16/7/28.
 */
public class MemoryLruCache {

    //使用1/8系统分配内存作为缓存上限
    private static final int MAX_CACHE_SIZE = (int) (Runtime.getRuntime().maxMemory() / 8);
    private LruCache<String, Object> mLruCache = null;
    private MemOccupyUtil memOccupyUtil = MemOccupyUtil.forSun32BitsVM();

    public MemoryLruCache() {
        this(MAX_CACHE_SIZE);
    }

    public MemoryLruCache(int maxSize) {
        mLruCache = new LruCache<String, Object>(maxSize) {
            @Override
            protected int sizeOf(String key, Object value) {
                return memOccupyUtil.occupyof(value);
            }
        };
    }

    public void putString(@Nullable String key, @Nullable String value) {
        mLruCache.put(key, value);
    }

    public String getString(@Nullable String key, String defaultValue) {
        Object obj = mLruCache.get(key);
        if (obj != null && obj instanceof String) {
            return String.valueOf(obj);
        } else {
            return defaultValue;
        }
    }

    public void putInt(@Nullable String key, int value) {
        mLruCache.put(key, value);
    }

    public int getInt(@Nullable String key, int defaultValue) {
        Object obj = mLruCache.get(key);
        if (obj != null && obj instanceof Integer) {
            return Integer.parseInt(obj.toString());
        } else {
            return defaultValue;
        }
    }

    public void putFloat(@Nullable String key, float value) {
        mLruCache.put(key, value);
    }

    public float getFloat(@Nullable String key, float defaultValue) {
        Object obj = mLruCache.get(key);
        if (obj != null && obj instanceof Float) {
            return Float.parseFloat(obj.toString());
        } else {
            return defaultValue;
        }
    }

    public void putLong(@Nullable String key, long value) {
        mLruCache.put(key, value);
    }

    public long getLong(@Nullable String key, long defaultValue) {
        Object obj = mLruCache.get(key);
        if (obj != null && obj instanceof Long) {
            return Long.parseLong(obj.toString());
        } else {
            return defaultValue;
        }
    }

    public void putBoolean(@Nullable String key, boolean value) {
        mLruCache.put(key, value);
    }

    public boolean getBoolean(@Nullable String key, boolean defaultValue) {
        Object obj = mLruCache.get(key);
        if (obj != null && obj instanceof Boolean) {
            return Boolean.parseBoolean(obj.toString());
        } else {
            return defaultValue;
        }
    }

    public void putBytes(@Nullable String key, @Nullable byte[] value) {
        mLruCache.put(key, value);
    }

    public byte[] getBytes(@Nullable String key, byte[] defaultValue) {
        Object obj = mLruCache.get(key);
        if (obj != null && obj instanceof byte[]) {
            return (byte[]) obj;
        } else {
            return defaultValue;
        }
    }

    public void putJSONObject(@Nullable String key, @Nullable JSONObject value) {
        mLruCache.put(key, value);
    }

    public JSONObject getJSONObject(@Nullable String key, JSONObject defaultValue) {
        Object obj = mLruCache.get(key);
        if (obj != null && obj instanceof JSONObject) {
            return (JSONObject) obj;
        } else {
            return defaultValue;
        }
    }

    public void putJSONArray(@Nullable String key, @Nullable JSONArray value) {
        mLruCache.put(key, value);
    }

    public JSONArray getJSONArray(@Nullable String key, JSONArray defaultValue) {
        Object obj = mLruCache.get(key);
        if (obj != null && obj instanceof JSONArray) {
            return (JSONArray) obj;
        } else {
            return defaultValue;
        }
    }

    public void putObject(@Nullable String key, @Nullable Object value) {
        mLruCache.put(key, value);
    }

    public <T> T getObject(@Nullable String key, T defaultValue) {
        Object obj = mLruCache.get(key);
        if (obj != null) {
            return (T) obj;
        } else {
            return defaultValue;
        }
    }

    public void putObjectArray(@Nullable String key, @Nullable List value) {
        mLruCache.put(key, value);
    }

    public <T> List<T> getObjectArray(@Nullable String key, @Nullable List<T> defaultValue) {
        Object obj = mLruCache.get(key);
        if (obj != null) {
            return (List<T>) obj;
        } else {
            return defaultValue;
        }
    }

    public void putObjectMap(@Nullable String key, @Nullable Map value) {
        mLruCache.put(key, value);
    }

    public <K, V> Map<K, V> getObjectMap(@Nullable String key, @Nullable Map<K, V> defaultValue) {
        Object obj = mLruCache.get(key);
        if (obj != null) {
            return (Map<K, V>) obj;
        } else {
            return defaultValue;
        }
    }

    public void remove(@Nullable String key) {
        mLruCache.remove(key);
    }

    public void clear() {
        mLruCache.evictAll();
    }

    public boolean contains(@Nullable String key) {
        return mLruCache.get(key) != null;
    }
}
