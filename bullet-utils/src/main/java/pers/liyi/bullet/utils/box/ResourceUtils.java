package pers.liyi.bullet.utils.box;


import android.content.Context;

import androidx.annotation.NonNull;

public class ResourceUtils {

    public static int getLayoutIdByName(@NonNull Context context, @NonNull String resName) {
        return getIdByName(context, "layout", resName);
    }

    public static int getLayoutIdByName(@NonNull String packageName, @NonNull String resName) {
        return getIdByName(packageName, "layout", resName);
    }

    public static int getViewIdByName(@NonNull Context context, @NonNull String resName) {
        return getIdByName(context, "id", resName);
    }

    public static int getViewIdByName(@NonNull String packageName, @NonNull String resName) {
        return getIdByName(packageName, "id", resName);
    }

    public static int getDrawableIdByName(@NonNull Context context, @NonNull String resName) {
        return getIdByName(context, "drawable", resName);
    }

    public static int getDrawableIdByName(@NonNull String packageName, @NonNull String resName) {
        return getIdByName(packageName, "drawable", resName);
    }

    public static int getColorIdByName(@NonNull Context context, @NonNull String resName) {
        return getIdByName(context, "color", resName);
    }

    public static int getColorIdByName(@NonNull String packageName, @NonNull String resName) {
        return getIdByName(packageName, "color", resName);
    }

    public static int getDimenIdByName(@NonNull Context context, @NonNull String resName) {
        return getIdByName(context, "dimen", resName);
    }

    public static int getDimenIdByName(@NonNull String packageName, @NonNull String resName) {
        return getIdByName(packageName, "dimen", resName);
    }

    public static int getStringIdByName(@NonNull Context context, @NonNull String resName) {
        return getIdByName(context, "string", resName);
    }

    public static int getStringIdByName(@NonNull String packageName, @NonNull String resName) {
        return getIdByName(packageName, "string", resName);
    }

    public static int getStringArrayIdByName(@NonNull Context context, @NonNull String resName) {
        return getIdByName(context, "array", resName);
    }

    public static int getStringArrayIdByName(@NonNull String packageName, @NonNull String resName) {
        return getIdByName(packageName, "array", resName);
    }

    public static int getStyleIdByName(@NonNull Context context, @NonNull String resName) {
        return getIdByName(context, "style", resName);
    }

    public static int getStyleIdByName(@NonNull String packageName, @NonNull String resName) {
        return getIdByName(packageName, "style", resName);
    }

    /**
     * 根据资源名获取资源 id
     *
     * @param context
     * @param className 资源类型
     * @param resName   资源名
     * @return 资源 id
     */
    public static int getIdByName(@NonNull Context context, @NonNull String className, @NonNull String resName) {
        return context.getResources().getIdentifier(resName, className, context.getPackageName());
    }

    /**
     * 根据资源名获取资源 id
     *
     * @param packageName 包名
     * @param className   资源类型
     * @param resName     资源名
     * @return 资源 id
     */
    public static int getIdByName(@NonNull String packageName, @NonNull String className, @NonNull String resName) {
        int id = 0;
        try {
            Class r = Class.forName(packageName + ".R");
            Class[] classes = r.getClasses();
            Class desireClass = null;
            for (Class cls : classes) {
                if (cls.getName().split("\\$")[1].equals(className)) {
                    desireClass = cls;
                    break;
                }
            }
            if (desireClass != null) {
                id = desireClass.getField(resName).getInt(desireClass);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }
}
