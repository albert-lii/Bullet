package pers.liyi.bullet.utils.box;


import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;

public class SDCardUtils {

    /**
     * 根据 environment 判断 SD 卡是否可用
     *<p>在通过 environment 判断 SD 卡是否可用时，通常是这样写的。因为 SD 卡只能在 SD 卡状态为“media_mounted”时可读写</p>
     *
     * @return {@code true}: 可用 <br> {@code false}: 不可用
     */
    public static boolean isSDCardEnableByEnvironment() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 根据 environment 获取 SD 卡的路径
     *
     * @return SD 卡的路径
     */
    public static String getSDCardPathByEnvironment() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return "";
    }

    /**
     * 判断 SD 卡是否可用
     *
     * @param context
     * @return {@code true}: 可用 <br> {@code false}: 不可用
     */
    public static boolean isSDCardEnabled(@NonNull Context context) {
        return !getSDCardPaths(context).isEmpty();
    }

    /**
     * 获取所有 SD 卡的路径
     *
     * @param context
     * @param removable 是否返回可移除的的 SD 卡的路径
     * @return 获取所有 SD 卡的路径列表
     */
    public static List<String> getSDCardPaths(@NonNull Context context, boolean removable) {
        List<String> paths = new ArrayList<>();
        StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        try {
            Class<?> storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            // noinspection JavaReflectionMemberAccess
            Method getVolumeList = StorageManager.class.getMethod("getVolumeList");
            // noinspection JavaReflectionMemberAccess
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(sm);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean res = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (removable == res) {
                    paths.add(path);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return paths;
    }

    /**
     * 获取所有的存储节点
     *
     * @param context
     * @return 所有存储节点路径列表
     */
    public static List<String> getSDCardPaths(@NonNull Context context) {
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        List<String> paths = new ArrayList<>();
        try {
            Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths");
            getVolumePathsMethod.setAccessible(true);
            Object invoke = getVolumePathsMethod.invoke(storageManager);
            paths = Arrays.asList((String[]) invoke);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return paths;
    }
}
