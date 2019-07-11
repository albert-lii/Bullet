package pers.liyi.bullet.utils.box;


import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolUtils {
    private static final TimeUnit UNIT = TimeUnit.SECONDS;
    private volatile static ThreadPoolUtils INSTANCE;
    private ThreadPoolExecutor mPoolExecutor;

    public static ThreadPoolUtils getInstance() {
        int size = Runtime.getRuntime().availableProcessors() * 2 + 1;
        return getInstance(size, size, 60 * 60);
    }

    public static ThreadPoolUtils getInstance(int corePoolSize, int maxPoolSize, long aliveTime) {
        if (INSTANCE == null) {
            synchronized (ThreadPoolUtils.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ThreadPoolUtils(corePoolSize, maxPoolSize, aliveTime,10);
                }
            }
        }
        return INSTANCE;
    }

    public static ThreadPoolUtils getInstance(int corePoolSize, int maxPoolSize, long aliveTime, int maxWaitingTasks) {
        if (INSTANCE == null) {
            synchronized (ThreadPoolUtils.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ThreadPoolUtils(corePoolSize, maxPoolSize, aliveTime,maxWaitingTasks);
                }
            }
        }
        return INSTANCE;
    }

    private ThreadPoolUtils(int corePoolSize, int maxPoolSize, long aliveTime, int maxWaitingTasks) {
        mPoolExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                aliveTime,
                UNIT,
                // 存储等待任务的缓冲队列
                new LinkedBlockingQueue<Runnable>(maxWaitingTasks),
                // 创建线程的工厂
                Executors.defaultThreadFactory(),
                // 针对超出 maximumPoolSize 的任务的处理策略
                new ThreadPoolExecutor.DiscardPolicy()
        );
    }

    public Future<?> submit(Runnable task) {
        return mPoolExecutor.submit(task);
    }

    /**
     * 执行任务
     */
    public void execute(Runnable task) {
        if (task == null) return;
        mPoolExecutor.execute(task);
    }

    /**
     * 从线程池中移除任务
     */
    public void remove(Runnable task) {
        if (task == null) return;
        mPoolExecutor.remove(task);
    }

    /**
     * 关闭线程池，不可以向线程池中提交新的 task，并且会尝试去关闭正在执行的线程，并返回尚未执行的线程
     * @return 尚未执行的线程列表
     */
    public List<Runnable> closeNow() {
        return mPoolExecutor.shutdownNow();
    }

    /**
     * 关闭线程池，不可以向线程池中提交新的 task，但是不会影响到正在执行的线程
     */
    public void close() {
        mPoolExecutor.shutdown();
    }

    public boolean isClosed() {
        return mPoolExecutor.isShutdown();
    }

    /**
     * 获取线程池对象
     *
     * @return
     */
    public ThreadPoolExecutor getThreadPoolExecutor() {
        return mPoolExecutor;
    }
}
