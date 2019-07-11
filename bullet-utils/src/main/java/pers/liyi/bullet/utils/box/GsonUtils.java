package pers.liyi.bullet.utils.box;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;


public class GsonUtils {
    private static Gson sGson;

    private static void checkNotNull(){
        if (sGson == null) {
            sGson = new Gson();
        }
    }

    /**
     * json 转 bean
     *
     * @param jsonStr
     * @param cls
     * @param <T>
     * @return bean
     */
    public static <T> T strToBean(String jsonStr, Class<T> cls) {
        checkNotNull();
        T t = sGson.fromJson(jsonStr, cls);
        return t;
    }

    /**
     * json 转 list
     *
     * @param jsonStr
     * @param type
     * @param <T>
     * @return List<?>
     */
    public static <T> List<T> strToList(String jsonStr, Type type) {
        checkNotNull();
        List<T> list = sGson.fromJson(jsonStr, type);
        return list;
    }

    /**
     * json 转 map
     *
     * @param jsonStr
     * @param <T>     Map<String,?>
     * @return Map<String,?>
     */
    public static <T> Map<String, T> strToMap(String jsonStr) {
        checkNotNull();
        Map<String, T> map = sGson.fromJson(jsonStr, new TypeToken<Map<String, T>>() {
        }.getType());
        return map;
    }

    /**
     * json 转成元素为 map 的 list
     *
     * @param jsonStr
     * @param <T>
     * @return List<Map>
     */
    public static <T> List<Map<String, T>> strToListMap(String jsonStr) {
        checkNotNull();
        List<Map<String, T>> list = sGson.fromJson(jsonStr, new TypeToken<List<Map<String, T>>>() {
        }.getType());
        return list;
    }

    /**
     * object 转 string
     *
     * @param obj
     * @return String
     */
    public static String objToStr(Object obj) {
        checkNotNull();
        return sGson.toJson(obj);
    }
}
