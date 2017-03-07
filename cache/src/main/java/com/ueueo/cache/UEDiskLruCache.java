package com.ueueo.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ueueo.cache.util.DiskLruCache;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by lijinzhe on 16/3/18.
 * <p/>
 * 使用文件进行数据缓存，采用LruCache算法，
 * 当存储的文件大小超过最大字节数时，会删除优先级最低的文件，保存当前最新插入的数据
 * 推荐所有操作方法不要在主线程中调用，因为会操作文件，比较耗时
 */
public class UEDiskLruCache {
    /**
     * 默认存储文件夹名称
     */
    private static final String DEFAULT_NAME = "local_disk_lrucache_default_9X3Df";
    /**
     * 默认的最大存储文件字节数
     */
    private static final long DEFAULT_MAX_SIZE = 1024 * 1024 * 100;//100M

    private static final String CACHE_PREFIX = "local_disk_cache_";

    private DiskLruCache mDiskLruCache = null;

    private UEDiskLruCache() {

    }

    /**
     * 初始化
     *
     * @param context
     */
    public UEDiskLruCache(Context context) throws IOException {
        this(context, DEFAULT_NAME, DEFAULT_MAX_SIZE);
    }

    public UEDiskLruCache(@Nullable Context context, @Nullable String name) throws IOException {
        this(context, name, DEFAULT_MAX_SIZE);
    }

    /**
     * @param context
     * @param name
     * @param maxSize 指定最大存储字节数，当存储的数据超出最大字节数时，会自动删除最老的数据，清理出空间用来存储最新数据
     */
    public UEDiskLruCache(@Nullable Context context, @Nullable String name, long maxSize) throws IOException {
        if (TextUtils.isEmpty(name)) {
            throw new RuntimeException("Name must not null!");
        }
        if (context == null) {
            throw new RuntimeException("Context must not null!");
        }
        if (maxSize <= 0) {
            throw new RuntimeException("Max size must bigger than 0!");
        }

        File cacheFile = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            cacheFile = new File(context.getExternalCacheDir(), CACHE_PREFIX + name);
        } else {
            cacheFile = new File(context.getCacheDir(), CACHE_PREFIX + name);
        }
        if (!cacheFile.exists()) {
            cacheFile.mkdirs();
        }
        mDiskLruCache = DiskLruCache.open(cacheFile, 1, 1, maxSize);
    }

    public void putLong(String key, long value) {
        putString(key, String.valueOf(value));
    }

    public long getLong(String key, long defaultValue) {
        try {
            return Long.parseLong(getString(key, String.valueOf(defaultValue)));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public void putBoolean(@Nullable String key, boolean value) {
        putString(key, String.valueOf(value));
    }

    public boolean getBoolean(@Nullable String key, boolean defaultValue) {
        try {
            return Boolean.parseBoolean(getString(key, String.valueOf(defaultValue)));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public void putFloat(@Nullable String key, float value) {
        putString(key, String.valueOf(value));
    }

    public float getFloat(@Nullable String key, float defaultValue) {
        try {
            return Float.parseFloat(getString(key, String.valueOf(defaultValue)));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public void putInt(String key, int value) {
        putString(key, String.valueOf(value));
    }

    public int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(getString(key, String.valueOf(defaultValue)));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 保存String类型数据
     *
     * @param key
     * @param value
     * @return
     */

    public void putString(@Nullable String key, @Nullable String value) {
        if (mDiskLruCache != null && !TextUtils.isEmpty(key)) {
            if (value == null) {
                remove(key);
                return;
            }
            OutputStream os = null;
            try {
                DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                os = editor.newOutputStream(0);
                os.write(value.getBytes());
                editor.commit();
                mDiskLruCache.flush();
            } catch (IOException e) {
            } finally {
                if (os != null) {
                    try {
                        os.close();
                        os.flush();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }

    /**
     * 获取存储的String类型数据
     *
     * @param key          key不能为null，且不能等于“”，否则返回defaultValue
     * @param defaultValue 默认的返回值
     * @return 当key对应的存储数据不存在，或者key无效时，返回默认返回值defaultValue
     */

    public String getString(@Nullable String key, String defaultValue) {
        if (TextUtils.isEmpty(key)) {
            return defaultValue;
        }
        if (mDiskLruCache != null) {
            InputStream is = null;
            BufferedReader reader = null;
            InputStreamReader isReader = null;
            try {
                DiskLruCache.Snapshot snapShot = mDiskLruCache.get(key);
                if (snapShot != null) {
                    is = snapShot.getInputStream(0);
                    isReader = new InputStreamReader(is);
                    reader = new BufferedReader(isReader);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    return sb.toString();
                }
            } catch (IOException e) {
            } finally {
                try {
                    if (isReader != null) {
                        isReader.close();
                    }
                } catch (IOException e) {
                }
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                }
            }
        }
        return defaultValue;
    }

    public void putBytes(@Nullable String key, @Nullable byte[] value) {
        if (mDiskLruCache != null && !TextUtils.isEmpty(key)) {
            if (value == null) {
                remove(key);
                return;
            }
            OutputStream os = null;
            try {
                DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                os = editor.newOutputStream(0);
                os.write(value);
                editor.commit();
                mDiskLruCache.flush();
            } catch (IOException e) {
            } finally {
                try {
                    if (os != null) {
                        os.close();
                        os.flush();
                    }
                } catch (IOException e) {
                }
            }
        }
    }

    public byte[] getBytes(@Nullable String key, byte[] defaultValue) {
        if (TextUtils.isEmpty(key)) {
            return defaultValue;
        }
        if (mDiskLruCache != null) {
            InputStream is = null;
            ByteArrayOutputStream baos = null;
            try {
                DiskLruCache.Snapshot snapShot = mDiskLruCache.get(key);
                if (snapShot != null) {
                    is = snapShot.getInputStream(0);
                    baos = new ByteArrayOutputStream();
                    byte[] buff = new byte[1024];
                    int rc = 0;
                    while ((rc = is.read(buff, 0, 1024)) > 0) {
                        baos.write(buff, 0, rc);
                    }
                    byte[] bytes = baos.toByteArray();

                    return bytes;
                }
            } catch (IOException e) {
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                }
                try {
                    if (baos != null) {
                        baos.close();
                        baos.flush();
                    }
                } catch (IOException e) {
                }
            }
        }
        return defaultValue;
    }

    public void putInputStream(@Nullable String key, @Nullable InputStream inputstream) {
        if (mDiskLruCache != null && !TextUtils.isEmpty(key)) {
            if (inputstream == null) {
                remove(key);
            }
            OutputStream os = null;
            try {
                DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                os = editor.newOutputStream(0);
                byte[] buff = new byte[1024];
                int size = 0;
                while ((size = inputstream.read(buff)) != -1) {
                    os.write(buff, 0, size);
                }
                editor.commit();
                mDiskLruCache.flush();
            } catch (IOException e) {
            } finally {
                try {
                    if (os != null) {
                        os.close();
                        os.flush();
                    }
                } catch (IOException e) {
                }
                try {
                    if (inputstream != null) {
                        inputstream.close();
                    }
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 返回InputStream 如果没有则返回null
     * <p/>
     * 返回的InputStream使用完之后必须调用close方法关闭
     *
     * @param key
     * @return
     */
    public InputStream getInputStream(@Nullable String key) {
        if (mDiskLruCache != null) {
            InputStream is = null;
            try {
                DiskLruCache.Snapshot snapShot = mDiskLruCache.get(key);
                if (snapShot != null) {
                    is = snapShot.getInputStream(0);
                    return is;
                }
            } catch (IOException e) {
            }
        }
        return null;
    }

    public void putBitmap(@Nullable String key, @Nullable Bitmap bitmap) {
        if (mDiskLruCache != null && !TextUtils.isEmpty(key)) {
            if (bitmap == null) {
                remove(key);
            }
            OutputStream os = null;
            try {
                DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                os = editor.newOutputStream(0);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                editor.commit();
                mDiskLruCache.flush();
            } catch (IOException e) {
            } finally {
                try {
                    if (os != null) {
                        os.close();
                        os.flush();
                    }
                } catch (IOException e) {
                }
            }
        }
    }

    public Bitmap getBitmap(@Nullable String key) {
        if (mDiskLruCache != null) {
            InputStream is = null;
            try {
                DiskLruCache.Snapshot snapShot = mDiskLruCache.get(key);
                if (snapShot != null) {
                    is = snapShot.getInputStream(0);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    return bitmap;
                }
            } catch (IOException e) {
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    public void putObject(@Nullable String key, @Nullable Object value) {
        if (value == null) {
            remove(key);
            return;
        }
        putString(key, new Gson().toJson(value));
    }

    public <T> T getObject(@Nullable String key, T defaultValue, Class<T> classOfT) {
        if (TextUtils.isEmpty(key)) {
            return defaultValue;
        }
        String value = getString(key, null);
        if (value != null) {
            return new Gson().fromJson(value, classOfT);
        } else {
            return defaultValue;
        }
    }

    public void putObjectArray(@Nullable String key, @Nullable List value) {
        if (value == null) {
            remove(key);
            return;
        }
        putString(key, new Gson().toJson(value));
    }

    public <T> List<T> getObjectArray(@Nullable String key, @Nullable List<T> defaultValue, TypeToken typeToken) {
        if (TextUtils.isEmpty(key)) {
            return defaultValue;
        }
        String value = getString(key, null);
        if (value != null) {
            return new Gson().fromJson(value, typeToken.getType());
        } else {
            return defaultValue;
        }
    }

    public void putObjectMap(@Nullable String key, @Nullable Map value) {
        if (value == null) {
            remove(key);
            return;
        }
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        putString(key, gson.toJson(value));
    }

    public <K, V> Map<K, V> getObjectMap(@Nullable String key, @Nullable Map<K, V> defaultValue, TypeToken typeToken) {
        if (TextUtils.isEmpty(key)) {
            return defaultValue;
        }
        String value = getString(key, null);
        if (value != null) {
            Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
            return gson.fromJson(value, typeToken.getType());
        } else {
            return defaultValue;
        }
    }

    public void putJSONObject(@Nullable String key, @Nullable JSONObject value) {
        if (value == null) {
            remove(key);
            return;
        }
        putString(key, value.toString());
    }

    public JSONObject getJSONObject(@Nullable String key, JSONObject defaultValue) {
        if (TextUtils.isEmpty(key)) {
            return defaultValue;
        }
        String js = getString(key, null);
        if (js != null) {
            try {
                return new JSONObject(js);
            } catch (JSONException e) {
            }
        }
        return defaultValue;
    }

    public void putJSONArray(@Nullable String key, @Nullable JSONArray value) {
        if (value == null) {
            remove(key);
            return;
        }
        putString(key, value.toString());
    }

    public JSONArray getJSONArray(@Nullable String key, JSONArray defaultValue) {
        if (TextUtils.isEmpty(key)) {
            return defaultValue;
        }
        String js = getString(key, null);
        if (js != null) {
            try {
                return new JSONArray(js);
            } catch (JSONException e) {
            }
        }
        return defaultValue;
    }

    public boolean contains(@Nullable String key) {
        return getString(key, null) != null;
    }

    public void remove(@Nullable String key) {
        if (mDiskLruCache != null && !TextUtils.isEmpty(key)) {
            try {
                mDiskLruCache.remove(key);
            } catch (IOException e) {
            }
        }
    }

    public void clear() {
        if (mDiskLruCache != null) {
            try {
                mDiskLruCache.delete();
            } catch (IOException e) {
            }
        }
    }
}
