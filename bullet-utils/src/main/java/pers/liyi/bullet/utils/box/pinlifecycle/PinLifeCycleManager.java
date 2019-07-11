package pers.liyi.bullet.utils.box.pinlifecycle;

import java.util.ArrayList;

import androidx.annotation.NonNull;


/**
 * 组件生命周期管理器
 */
public class PinLifeCycleManager {
    private static ArrayList<PinLifeCycle> sPinLifeCycleList;

    /**
     * 注册组件的生命周期
     */
    public static <T extends PinLifeCycle> void register(@NonNull T lifeCycle) {
        if (sPinLifeCycleList == null) {
            sPinLifeCycleList = new ArrayList();
        }
        if (!sPinLifeCycleList.contains(lifeCycle)) {
            sPinLifeCycleList.add(lifeCycle);
        }
    }

    /**
     * 执行组件生命周期
     */
    public static void execute() {
        if (sPinLifeCycleList != null && !sPinLifeCycleList.isEmpty()) {
            // 冒泡算法排序，按优先级从高到低重新排列组件生命周期
            PinLifeCycle temp = null;
            for (int i = 0, len = sPinLifeCycleList.size() - 1; i < len; i++) {
                for (int j = 0; j < len - i; j++) {
                    if (sPinLifeCycleList.get(j).getPriority() < sPinLifeCycleList.get(j + 1).getPriority()) {
                        temp = sPinLifeCycleList.get(j);
                        sPinLifeCycleList.set(j, temp);
                        sPinLifeCycleList.set(j + 1, temp);
                    }
                }
            }
            for (PinLifeCycle lifeCycle : sPinLifeCycleList) {
                lifeCycle.onCreate();
            }
        }
    }

    /**
     * 解除组件生命周期
     */
    public static <T extends PinLifeCycle> void unregister(@NonNull T lifeCycle) {
        if (sPinLifeCycleList != null) {
            if (sPinLifeCycleList.contains(lifeCycle)) {
                lifeCycle.onTerminate();
                sPinLifeCycleList.remove(lifeCycle);
            }
        }
    }

    /**
     * 清除所有的组件生命周期
     */
    public static void clear() {
        if (sPinLifeCycleList != null) {
            if (!sPinLifeCycleList.isEmpty()) {
                for (PinLifeCycle lifeCycle : sPinLifeCycleList) {
                    lifeCycle.onTerminate();
                }
            }
            sPinLifeCycleList.clear();
        }
    }
}
