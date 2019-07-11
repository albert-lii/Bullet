package pers.liyi.bullet.utils.box.pinlifecycle;

import android.app.Application;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

public abstract class PinLifeCycle {
    private Application app;
    // 优先级，priority 越大，优先级越高
    @IntRange(from = 0)
    private int priority = 0;

    public PinLifeCycle(@NonNull Application application) {
        this.app = application;
    }

    public Application getApp() {
        return app;
    }

    /**
     * 获取优先级，priority 越大，优先级越高
     */
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public abstract void onCreate();

    public abstract void onTerminate();
}
