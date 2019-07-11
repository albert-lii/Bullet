package pers.liyi.bullet.arms.utils.eventbus;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.meta.SubscriberInfoIndex;

import androidx.annotation.NonNull;

public class EventUtil {
    private static final String TAG = "Bullet-" + EventUtil.class.getSimpleName();

    /**
     * 安装 EventBus 索引
     * @param index
     */
    public static void installIndex(SubscriberInfoIndex index) {
        EventBus.builder().addIndex(index).installDefaultEventBus();
    }

    public static boolean isRegistered(@NonNull Object subscriber) {
        return EventBus.getDefault().isRegistered(subscriber);
    }

    /**
     * 注册 EventBus
     *
     * @param subscriber
     */
    public static void register(@NonNull Object subscriber) {
        if (!EventBus.getDefault().isRegistered(subscriber)) {
            EventBus.getDefault().register(subscriber);
        } else {
            Log.e(TAG, "Failed to register eventbus");
        }
    }

    /**
     * 取消 EventBus 注册
     *
     * @param subscriber
     */
    public static void unregister(@NonNull Object subscriber) {
        if (EventBus.getDefault().isRegistered(subscriber)) {
            EventBus.getDefault().unregister(subscriber);
        }
    }

    /**
     * 发布一个订阅事件
     */
    public static void post(@NonNull Object event) {
        EventBus.getDefault().post(event);
    }

    /**
     * 发布一个粘性的订阅事件
     * <p>
     * 粘性事件将最新的信息保存在内存中，取消原始消息，执行最新的消息；
     * 只有注册后，才能接收消息，如果没有注册，消息将保留在内存中。
     */
    public static void postSticky(@NonNull Object event) {
        EventBus.getDefault().postSticky(event);
    }

    /**
     *  移除指定的粘性订阅事件
     *
     * @param eventType 事件类型
     */
    public static <T> T removeStickyEvent(Class<T> eventType) {
        return EventBus.getDefault().removeStickyEvent(eventType);
    }

    public static boolean removeStickyEvent(@NonNull Object event) {
        return EventBus.getDefault().removeStickyEvent(event);
    }

    /**
     * 移除所有的粘性订阅事件
     */
    public static void removeAllStickyEvents() {
        EventBus.getDefault().removeAllStickyEvents();
    }

    /**
     * 高优先级的订阅者可以中断事件往下继续传递
     * <p>
     * 此方法只能在事件通过时调用（ps: 表示只能在事件接收方法中调用此方法）
     *
     * @param event
     */
    public static void cancelEventDelivery(Object event) {
        EventBus.getDefault().cancelEventDelivery(event);
    }

    /**
     * 获取 EventBus 单例
     */
    public static EventBus getEventBus() {
        return EventBus.getDefault();
    }
}
