package pers.liyi.bullet.utils.box;


import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;


public class SPUtils {
    private static final String DEF_FILENAME = "Bullet-SP";
    private static final int DEF_MODE = Context.MODE_PRIVATE;

    private static Map<String, SPUtils> sInstanceMap;
    private static SharedPreferences sSp;
    private static SharedPreferences.Editor sEditor;

    private SPUtils(@NonNull Context context, @NonNull String fileName, int mode) {
        super();
        sSp = context.getSharedPreferences(fileName, mode);
        sEditor = sSp.edit();
    }

    public static SPUtils getInstance(@NonNull Context context) {
        return getInstance(context, DEF_FILENAME, DEF_MODE);
    }

    public static SPUtils getInstance(@NonNull Context context, @NonNull String fileName) {
        return getInstance(context, fileName, DEF_MODE);
    }

    public static SPUtils getInstance(@NonNull Context context, @NonNull String fileName, @NonNull int mode) {
        if (sInstanceMap == null) {
            sInstanceMap = new HashMap<String, SPUtils>();
        }
        // 先尝试获取 sharedpreferences 对象
        SPUtils manager = sInstanceMap.get(fileName + "_" + mode);
        // 如果没有获取到 sharedpreferences 对象，就重新创建一个实例
        if (manager == null) {
            manager = new SPUtils(context, fileName, mode);
            sInstanceMap.put(fileName + "_" + mode, manager);
        }
        return manager;
    }

    public void applyPut(@NonNull String key, Object object) {
        add(key, object);
        sEditor.apply();
    }

    public void applyPut(Map<String, Object> map) {
        Set<String> set = map.keySet();
        for (String key : set) {
            Object object = map.get(key);
            add(key, object);
        }
        sEditor.apply();
    }

    /**
     * 保存数据
     *
     * @param key
     * @param object
     */
    public boolean put(@NonNull String key, Object object) {
        add(key, object);
        return sEditor.commit();
    }

    /**
     * 同时保存多条数据
     *
     * @param map
     */
    public boolean put(Map<String, Object> map) {
        Set<String> set = map.keySet();
        for (String key : set) {
            Object object = map.get(key);
            add(key, object);
        }
        return sEditor.commit();
    }

    private void add(@NonNull String key, Object object) {
        if (object instanceof String) {
            sEditor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            sEditor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            sEditor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            sEditor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            sEditor.putLong(key, (Long) object);
        } else {
            sEditor.putString(key, object.toString());
        }
    }

    /**
     * 获取保存的数据
     *
     * @param key
     * @param object 默认返回值
     * @return 保存的数据
     */
    public Object get(String key, Object object) {
        if (object instanceof String) {
            return sSp.getString(key, (String) object);
        } else if (object instanceof Integer) {
            return sSp.getInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            return sSp.getBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            return sSp.getFloat(key, (Float) object);
        } else if (object instanceof Long) {
            return sSp.getLong(key, (Long) object);
        }
        return null;
    }


    /**
     * 通过 key 删除数据
     */
    public void applyRemove(String key) {
        sEditor.remove(key)
                .apply();
    }

    /**
     * 通过 key 删除数据
     */
    public boolean remove(String key) {
        return sEditor.remove(key)
                .commit();
    }

    /**
     * 清除所有的数据
     */
    public void applyClear() {
        sEditor.clear().apply();
    }

    /**
     * 清除所有的数据
     */
    public boolean clear() {
        return sEditor.clear().commit();
    }

    public SharedPreferences.Editor getEditor() {
        return sEditor;
    }

    public void free() {
        free(DEF_FILENAME, DEF_MODE);
    }

    public void free(@NonNull String fileName) {
        free(fileName, DEF_MODE);
    }

    /**
     * 释放指定的 sharedpreferences 对象
     *
     * @param fileName
     * @param mode
     */
    public void free(@NonNull String fileName, @NonNull int mode) {
        if (sInstanceMap != null) {
            String key = fileName + "_" + mode;
            if (sInstanceMap.containsKey(key)) {
                sInstanceMap.remove(key);
            }
        }
    }

    /**
     * 释放所有的 sharedpreferences 对象
     */
    public void freeAll() {
        if (sInstanceMap != null) {
            sInstanceMap.clear();
            sInstanceMap = null;
        }
    }
}
