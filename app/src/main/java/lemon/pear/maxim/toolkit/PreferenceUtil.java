package lemon.pear.maxim.toolkit;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import java.util.Map;
import java.util.Set;

import lemon.pear.maxim.Config;

/**
 * SharePreference工具类
 */
@SuppressWarnings("unchecked")
public class PreferenceUtil {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private final int ZERO = 0;

    public PreferenceUtil(Context context) {
        this(context, Config.SHARE_PREFERENCES, Context.MODE_PRIVATE);
    }

    public PreferenceUtil(Context context, String preferenceName) {
        this(context, preferenceName, Context.MODE_PRIVATE);
    }

    public PreferenceUtil(Context context, String preferenceName, int mode) {
        sharedPreferences = context.getSharedPreferences(preferenceName, mode);
        editor = sharedPreferences.edit();
    }

    public void putString(String key, String value) {
        if (!StringUtil.isEmpty(key) && !StringUtil.isEmpty(value)) {
            editor.putString(key, value);
            editor.commit();
        }
    }

    public String getString(String key) {
        String value = null;
        if (!TextUtils.isEmpty(key)) {
            value = sharedPreferences.getString(key, null);
        }
        return value;
    }

    public String getString(String key, String defaultValue) {
        String value = null;
        if (!TextUtils.isEmpty(key)) {
            value = sharedPreferences.getString(key, defaultValue);
        }
        return value;
    }

    public void putInt(String key, int value) {
        if (!TextUtils.isEmpty(key)) {
            editor.putInt(key, value);
            editor.commit();
        }
    }

    public int getInt(String key) {
        int value = ZERO;
        if (!TextUtils.isEmpty(key)) {
            value = sharedPreferences.getInt(key, ZERO);
        }
        return value;
    }

    public int getInt(String key, int defaultValue) {
        int value = defaultValue;
        if (!TextUtils.isEmpty(key)) {
            value = sharedPreferences.getInt(key, defaultValue);
        }
        return value;
    }

    public void putBoolean(String key, boolean value) {
        if (!TextUtils.isEmpty(key)) {
            editor.putBoolean(key, value);
            editor.commit();
        }
    }

    public boolean getBoolean(String key) {
        boolean result = false;
        if (!TextUtils.isEmpty(key)) {
            result = sharedPreferences.getBoolean(key, false);
        }
        return result;
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        boolean result = defaultValue;
        if (!TextUtils.isEmpty(key)) {
            result = sharedPreferences.getBoolean(key, defaultValue);
        }
        return result;
    }

    public void putLong(String key, long value) {
        if (!TextUtils.isEmpty(key)) {
            editor.putLong(key, value);
            editor.commit();
        }
    }

    public long getLong(String key) {
        long value = ZERO;
        if (!TextUtils.isEmpty(key)) {
            value = sharedPreferences.getLong(key, ZERO);
        }
        return value;
    }

    public long getLong(String key, long defaultValue) {
        long value = defaultValue;
        if (!TextUtils.isEmpty(key)) {
            value = sharedPreferences.getLong(key, defaultValue);
        }
        return value;
    }

    public void putFloat(String key, float value) {
        if (!TextUtils.isEmpty(key)) {
            editor.putFloat(key, value);
            editor.commit();
        }
    }

    public float getFloat(String key) {
        float value = ZERO;
        if (!TextUtils.isEmpty(key)) {
            value = sharedPreferences.getFloat(key, ZERO);
        }
        return value;
    }

    public float getFloat(String key, float defaultValue) {
        float value = defaultValue;
        if (!TextUtils.isEmpty(key)) {
            value = sharedPreferences.getFloat(key, defaultValue);
        }
        return value;
    }

    public void putStringSet(String key, Set<String> value) {
        if (!TextUtils.isEmpty(key)) {
            editor.putStringSet(key, value);
            editor.commit();
        }
    }

    public Set<String> getStringSet(String key) {
        Set<String> set = null;
        if (!TextUtils.isEmpty(key)) {
            set = sharedPreferences.getStringSet(key, null);
        }
        return set;
    }

    public Set<String> getStringSet(String key, Set<String> defaultValue) {
        Set<String> set = defaultValue;
        if (!TextUtils.isEmpty(key)) {
            set = sharedPreferences.getStringSet(key, defaultValue);
        }
        return set;
    }

    /**
     * 查询某个key是否已经存在
     *
     * @param key
     * @return
     */
    public boolean contains(String key) {
        return sharedPreferences.contains(key);
    }

    /**
     * 移除某个key值已经对应的值
     *
     * @param key
     */
    public void remove(String key) {
        editor.remove(key);
        editor.commit();
    }

    /**
     * 清除所有数据
     */
    public void clear() {
        editor.clear();
        editor.commit();
    }

    /**
     * 返回所有的键值对
     *
     * @return
     */
    public Map<String, ?> getAll() {
        return sharedPreferences.getAll();
    }

    /**
     * 日志输出所有键值对
     */
    public void selectKeyAll() {
        Map<String, Object> map = (Map<String, Object>) sharedPreferences.getAll();
        for (String key : map.keySet()) {
            Logger.d("key= " + key + " and value= " + map.get(key));
        }
    }
}