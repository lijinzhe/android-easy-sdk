package com.ueueo.cache;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * Created by Lee on 16/3/18.
 * <p/>
 * SharedPreferences 存储数据
 */
public class UESharedCache {
    /**
     * 默认存储文件名称
     */
    private static final String DEFAULT_NAME = "default_shared_preferences";
    //用来存储数据有效时长的key后缀
    private final String EXPIRES_IN_SUFFIX = "_expires_in_10X27Pd31";

    private SharedPreferences mSP = null;
    private SharedPreferences.Editor mEditor = null;

    private UESharedCache() {
    }

    public UESharedCache(@Nullable Context context) {
        this(context, DEFAULT_NAME);
    }

    public UESharedCache(@Nullable Context context, @Nullable String name) {
        if (context == null) {
            throw new RuntimeException("Context must not null!");
        }
        if (TextUtils.isEmpty(name)) {
            throw new RuntimeException("Name must not null or ''");
        }
        mSP = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        mEditor = mSP.edit();
    }

    /**
     * 缓存String类型数据
     *
     * @param key   key不能为null，也不能为“”，否则存储失败
     * @param value 如果value等于null，则会清除当前key对应的数据，与remove(key)方法相同
     */

    public void putString(@Nullable String key, @Nullable String value) {
        this.putString(key, value, 0);
    }

    /**
     * 缓存String类型数据
     *
     * @param key       key不能为null，也不能为“”，否则存储失败
     * @param value     如果value等于null，则会清除当前key对应的数据，与remove(key)方法相同
     * @param expiresIn 有效时间，单位秒，当超出这个时间则会自动清除数据，如果expiresIn<=0则永久有效，只要手动删除数据
     */

    public void putString(@Nullable String key, @Nullable String value, long expiresIn) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        if (value == null) {
            remove(key);
            return;
        }
        mEditor.putString(key, value);
        if (expiresIn > 0) {
            mEditor.putLong(key + EXPIRES_IN_SUFFIX, System.currentTimeMillis() + expiresIn);
        } else {
            mEditor.remove(key + EXPIRES_IN_SUFFIX);
        }
        mEditor.commit();
    }

    /**
     * 获取key对应存储的String类型数据
     *
     * @param key          key不能null，也不能等于“”，否则返回defaultValue
     * @param defaultValue 当key对应的数据不存在时，返回defaultValue
     * @return 如果key对应数据存储在返回存储的数据，否则返回defaultValue
     */

    public String getString(@Nullable String key, String defaultValue) {
        if (TextUtils.isEmpty(key)) {
            return defaultValue;
        }
        if (mSP.contains(key + EXPIRES_IN_SUFFIX)) {
            long expiresIn = mSP.getLong(key + EXPIRES_IN_SUFFIX, 0);
            if (System.currentTimeMillis() <= expiresIn) {
                return mSP.getString(key, defaultValue);
            } else {
                mEditor.remove(key).remove(key + EXPIRES_IN_SUFFIX).commit();
                return defaultValue;
            }
        } else {
            return mSP.getString(key, defaultValue);
        }
    }

    public void putInt(@Nullable String key, int value) {
        this.putInt(key, value, 0);
    }

    public void putInt(@Nullable String key, int value, long expiresIn) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        mEditor.putInt(key, value);
        if (expiresIn > 0) {
            mEditor.putLong(key + EXPIRES_IN_SUFFIX, System.currentTimeMillis() + expiresIn);
        } else {
            mEditor.remove(key + EXPIRES_IN_SUFFIX);
        }
        mEditor.commit();
    }

    public int getInt(@Nullable String key, int defaultValue) {
        if (TextUtils.isEmpty(key)) {
            return defaultValue;
        }
        if (mSP.contains(key + EXPIRES_IN_SUFFIX)) {
            long expiresIn = mSP.getLong(key + EXPIRES_IN_SUFFIX, 0);
            if (System.currentTimeMillis() <= expiresIn) {
                return mSP.getInt(key, defaultValue);
            } else {
                mEditor.remove(key).remove(key + EXPIRES_IN_SUFFIX).commit();
                return defaultValue;
            }
        } else {
            return mSP.getInt(key, defaultValue);
        }
    }

    public void putFloat(@Nullable String key, float value) {
        this.putFloat(key, value, 0);
    }

    public void putFloat(@Nullable String key, float value, long expiresIn) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        mEditor.putFloat(key, value);
        if (expiresIn > 0) {
            mEditor.putLong(key + EXPIRES_IN_SUFFIX, System.currentTimeMillis() + expiresIn);
        } else {
            mEditor.remove(key + EXPIRES_IN_SUFFIX);
        }
        mEditor.commit();
    }

    public float getFloat(@Nullable String key, float defaultValue) {
        if (TextUtils.isEmpty(key)) {
            return defaultValue;
        }
        if (mSP.contains(key + EXPIRES_IN_SUFFIX)) {
            long expiresIn = mSP.getLong(key + EXPIRES_IN_SUFFIX, 0);
            if (System.currentTimeMillis() <= expiresIn) {
                return mSP.getFloat(key, defaultValue);
            } else {
                mEditor.remove(key).remove(key + EXPIRES_IN_SUFFIX).commit();
                return defaultValue;
            }
        } else {
            return mSP.getFloat(key, defaultValue);
        }
    }

    public void putLong(@Nullable String key, long value) {
        this.putLong(key, value, 0);
    }

    public void putLong(@Nullable String key, long value, long expiresIn) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        mEditor.putLong(key, value);
        if (expiresIn > 0) {
            mEditor.putLong(key + EXPIRES_IN_SUFFIX, System.currentTimeMillis() + expiresIn);
        } else {
            mEditor.remove(key + EXPIRES_IN_SUFFIX);
        }
        mEditor.commit();
    }

    public long getLong(@Nullable String key, long defaultValue) {
        if (TextUtils.isEmpty(key)) {
            return defaultValue;
        }
        if (mSP.contains(key + EXPIRES_IN_SUFFIX)) {
            long expiresIn = mSP.getLong(key + EXPIRES_IN_SUFFIX, 0);
            if (System.currentTimeMillis() <= expiresIn) {
                return mSP.getLong(key, defaultValue);
            } else {
                mEditor.remove(key).remove(key + EXPIRES_IN_SUFFIX).commit();
                return defaultValue;
            }
        } else {
            return mSP.getLong(key, defaultValue);
        }
    }

    public void putBoolean(@Nullable String key, boolean value) {
        this.putBoolean(key, value, 0);
    }

    public void putBoolean(@Nullable String key, boolean value, long expiresIn) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        mEditor.putBoolean(key, value);
        if (expiresIn > 0) {
            mEditor.putLong(key + EXPIRES_IN_SUFFIX, System.currentTimeMillis() + expiresIn);
        } else {
            mEditor.remove(key + EXPIRES_IN_SUFFIX);
        }
        mEditor.commit();
    }

    public boolean getBoolean(@Nullable String key, boolean defaultValue) {
        if (TextUtils.isEmpty(key)) {
            return defaultValue;
        }
        if (mSP.contains(key + EXPIRES_IN_SUFFIX)) {
            long expiresIn = mSP.getLong(key + EXPIRES_IN_SUFFIX, 0);
            if (System.currentTimeMillis() <= expiresIn) {
                return mSP.getBoolean(key, defaultValue);
            } else {
                mEditor.remove(key).remove(key + EXPIRES_IN_SUFFIX).commit();
                return defaultValue;
            }
        } else {
            return mSP.getBoolean(key, defaultValue);
        }
    }

    public void putBytes(@Nullable String key, @Nullable byte[] value) {
        this.putBytes(key, value, 0);
    }

    public void putBytes(@Nullable String key, @Nullable byte[] value, long expiresIn) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        if (value == null) {
            remove(key);
            return;
        }
        try {
            mEditor.putString(key, new String(value, "UTF-8"));
            if (expiresIn > 0) {
                mEditor.putLong(key + EXPIRES_IN_SUFFIX, System.currentTimeMillis() + expiresIn);
            } else {
                mEditor.remove(key + EXPIRES_IN_SUFFIX);
            }
            mEditor.commit();
        } catch (UnsupportedEncodingException e) {
        }
    }

    public byte[] getBytes(@Nullable String key, byte[] defaultValue) {
        if (TextUtils.isEmpty(key)) {
            return defaultValue;
        }
        if (mSP.contains(key + EXPIRES_IN_SUFFIX)) {
            long expiresIn = mSP.getLong(key + EXPIRES_IN_SUFFIX, 0);
            if (System.currentTimeMillis() <= expiresIn) {
                try {
                    String value = mSP.getString(key, null);
                    if (value != null) {
                        return value.getBytes("UTF-8");
                    }
                } catch (UnsupportedEncodingException e) {
                }
            } else {
                mEditor.remove(key).remove(key + EXPIRES_IN_SUFFIX).commit();
                return defaultValue;
            }
        } else {
            try {
                String value = mSP.getString(key, null);
                if (value != null) {
                    return value.getBytes("UTF-8");
                }
            } catch (UnsupportedEncodingException e) {
            }
        }
        return defaultValue;
    }

    public void putJSONObject(@Nullable String key, @Nullable JSONObject value) {
        this.putJSONObject(key, value, 0);
    }

    public void putJSONObject(@Nullable String key, @Nullable JSONObject value, long expiresIn) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        if (value == null) {
            remove(key);
            return;
        }
        mEditor.putString(key, value.toString());
        if (expiresIn > 0) {
            mEditor.putLong(key + EXPIRES_IN_SUFFIX, System.currentTimeMillis() + expiresIn);
        } else {
            mEditor.remove(key + EXPIRES_IN_SUFFIX);
        }
        mEditor.commit();
    }

    public JSONObject getJSONObject(@Nullable String key, JSONObject defaultValue) {
        if (TextUtils.isEmpty(key)) {
            return defaultValue;
        }
        if (mSP.contains(key + EXPIRES_IN_SUFFIX)) {
            long expiresIn = mSP.getLong(key + EXPIRES_IN_SUFFIX, 0);
            if (System.currentTimeMillis() <= expiresIn) {
                String value = mSP.getString(key, null);
                if (value != null) {
                    try {
                        return new JSONObject(value);
                    } catch (JSONException e) {
                    }
                }
            } else {
                mEditor.remove(key).remove(key + EXPIRES_IN_SUFFIX).commit();
                return defaultValue;
            }
        } else {
            String value = mSP.getString(key, null);
            if (value != null) {
                try {
                    return new JSONObject(value);
                } catch (JSONException e) {
                }
            }
        }
        return defaultValue;
    }

    public void putJSONArray(@Nullable String key, @Nullable JSONArray value) {
        this.putJSONArray(key, value, 0);
    }

    public void putJSONArray(@Nullable String key, @Nullable JSONArray value, long expiresIn) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        if (value == null) {
            remove(key);
            return;
        }
        mEditor.putString(key, value.toString());
        if (expiresIn > 0) {
            mEditor.putLong(key + EXPIRES_IN_SUFFIX, System.currentTimeMillis() + expiresIn);
        } else {
            mEditor.remove(key + EXPIRES_IN_SUFFIX);
        }
        mEditor.commit();
    }

    public JSONArray getJSONArray(@Nullable String key, JSONArray defaultValue) {
        if (TextUtils.isEmpty(key)) {
            return defaultValue;
        }
        if (mSP.contains(key + EXPIRES_IN_SUFFIX)) {
            long expiresIn = mSP.getLong(key + EXPIRES_IN_SUFFIX, 0);
            if (System.currentTimeMillis() <= expiresIn) {
                String value = mSP.getString(key, null);
                if (value != null) {
                    try {
                        return new JSONArray(value);
                    } catch (JSONException e) {
                    }
                }
            } else {
                mEditor.remove(key).remove(key + EXPIRES_IN_SUFFIX).commit();
                return defaultValue;
            }
        } else {
            String value = mSP.getString(key, null);
            if (value != null) {
                try {
                    return new JSONArray(value);
                } catch (JSONException e) {
                }
            }
        }
        return defaultValue;
    }

    public void putObject(@Nullable String key, @Nullable Object value) {
        this.putObject(key, value, 0);
    }

    public void putObject(@Nullable String key, @Nullable Object value, long expiresIn) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        if (value == null) {
            remove(key);
            return;
        }
        mEditor.putString(key, new Gson().toJson(value));
        if (expiresIn > 0) {
            mEditor.putLong(key + EXPIRES_IN_SUFFIX, System.currentTimeMillis() + expiresIn);
        } else {
            mEditor.remove(key + EXPIRES_IN_SUFFIX);
        }
        mEditor.commit();
    }

    public <T> T getObject(@Nullable String key, T defaultValue, Class<T> classOfT) {
        if (TextUtils.isEmpty(key)) {
            return defaultValue;
        }
        if (mSP.contains(key + EXPIRES_IN_SUFFIX)) {
            long expiresIn = mSP.getLong(key + EXPIRES_IN_SUFFIX, 0);
            if (System.currentTimeMillis() > expiresIn) {
                mEditor.remove(key).remove(key + EXPIRES_IN_SUFFIX).commit();
                return defaultValue;
            }
        }
        String value = mSP.getString(key, null);
        if (value != null) {
            return new Gson().fromJson(value, classOfT);
        } else {
            return defaultValue;
        }
    }

    public void putObjectArray(@Nullable String key, @Nullable List value) {
        this.putObjectArray(key, value, 0);
    }

    public void putObjectArray(@Nullable String key, @Nullable List value, long expiresIn) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        if (value == null) {
            remove(key);
            return;
        }
        mEditor.putString(key, new Gson().toJson(value));
        if (expiresIn > 0) {
            mEditor.putLong(key + EXPIRES_IN_SUFFIX, System.currentTimeMillis() + expiresIn);
        } else {
            mEditor.remove(key + EXPIRES_IN_SUFFIX);
        }
        mEditor.commit();
    }

    public <T> List<T> getObjectArray(@Nullable String key, @Nullable List<T> defaultValue, TypeToken typeToken) {
        if (TextUtils.isEmpty(key)) {
            return defaultValue;
        }
        if (mSP.contains(key + EXPIRES_IN_SUFFIX)) {
            long expiresIn = mSP.getLong(key + EXPIRES_IN_SUFFIX, 0);
            if (System.currentTimeMillis() > expiresIn) {
                mEditor.remove(key).remove(key + EXPIRES_IN_SUFFIX).commit();
                return defaultValue;
            }
        }
        String value = mSP.getString(key, null);
        if (value != null) {
            return new Gson().fromJson(value, typeToken.getType());
        } else {
            return defaultValue;
        }
    }

    public void putObjectMap(@Nullable String key, @Nullable Map value) {
        this.putObjectMap(key, value, 0);
    }

    public void putObjectMap(@Nullable String key, @Nullable Map value, long expiresIn) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        if (value == null) {
            remove(key);
            return;
        }
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        mEditor.putString(key, gson.toJson(value));
        if (expiresIn > 0) {
            mEditor.putLong(key + EXPIRES_IN_SUFFIX, System.currentTimeMillis() + expiresIn);
        } else {
            mEditor.remove(key + EXPIRES_IN_SUFFIX);
        }
        mEditor.commit();
    }

    public <K, V> Map<K, V> getObjectMap(@Nullable String key, @Nullable Map<K, V> defaultValue, TypeToken typeToken) {
        if (TextUtils.isEmpty(key)) {
            return defaultValue;
        }
        if (mSP.contains(key + EXPIRES_IN_SUFFIX)) {
            long expiresIn = mSP.getLong(key + EXPIRES_IN_SUFFIX, 0);
            if (System.currentTimeMillis() > expiresIn) {
                mEditor.remove(key).remove(key + EXPIRES_IN_SUFFIX).commit();
                return defaultValue;
            }
        }
        String value = mSP.getString(key, null);
        if (value != null) {
            Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
            return gson.fromJson(value, typeToken.getType());
        } else {
            return defaultValue;
        }
    }

    public void remove(@Nullable String key) {
        mEditor.remove(key);
        mEditor.remove(key + EXPIRES_IN_SUFFIX);
        mEditor.commit();
    }

    public void clear() {
        mEditor.clear().commit();
    }

    public boolean contains(@Nullable String key) {
        return mSP.contains(key);
    }
}
