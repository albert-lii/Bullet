package pers.liyi.bullet.retrofit;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import pers.liyi.bullet.retrofit.listener.ApiResponseListener;
import pers.liyi.bullet.retrofit.listener.OnProgressListener;
import pers.liyi.bullet.retrofit.listener.OnSerialAcceptListener;
import pers.liyi.bullet.retrofit.response.ApiResponse;
import retrofit2.Retrofit;


public class ApiClient {
    private static volatile ApiClient INSTANCE;
    // 订阅统一管理器
    private static HashMap<String, ApiResponse> sDisposableSite;
    // 下载进度监听管理器
    private static HashMap<String, OnProgressListener> sDownloadProgressSite;
    // Retrofit2.x 管理器
    private static RetrofitManager sRetrofitManager;

    private ApiClient() {
        sDisposableSite = new HashMap<>();
    }

    public static ApiClient getInstance() {
        if (INSTANCE == null) {
            synchronized (ApiClient.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ApiClient();
                }
            }
        }
        return INSTANCE;
    }

    public void init(@NonNull Context context) {
        init(context, new ApiClientConfig());
    }

    public void init(@NonNull Context context, @NonNull ApiClientConfig config) {
        sRetrofitManager = new RetrofitManager(context, config);
    }

    public void init(@NonNull Context context, @NonNull Retrofit retrofit) {
        sRetrofitManager = new RetrofitManager(context, null);
        sRetrofitManager.setRetrofit(retrofit);
    }

    /**
     * 创建服务
     *
     * @param clz
     * @param <T>
     * @return
     */
    public <T> T createService(@NonNull Class<T> clz) {
        if (sRetrofitManager == null) {
            throw new NullPointerException("Please initialize RetrofitManager first...");
        }
        return sRetrofitManager.createService(clz);
    }

    /**
     * 订阅 Api 请求
     *
     * @param observable 被观察者
     * @param listener   结果回调
     */
    public <T> ApiResponse call(@NonNull Observable<T> observable, ApiResponseListener<T> listener) {
        return add(observable
                // 在 io 线程中进行网络请求
                .subscribeOn(Schedulers.io())
                // 回到主线程处理返回结果
                .observeOn(AndroidSchedulers.mainThread())
                // 订阅
                .subscribeWith(new ApiResponse<T>(listener))
        );
    }


    /**
     * 订阅 Api 请求
     *
     * @param observable 被观察者
     * @param observer   观察者
     */
    public <T> ApiResponse call(@NonNull Observable<T> observable, ApiResponse<T> observer) {
        return add(observable
                // 在 io 线程中进行网络请求
                .subscribeOn(Schedulers.io())
                // 回到主线程处理返回结果
                .observeOn(AndroidSchedulers.mainThread())
                // 订阅
                .subscribeWith(observer)
        );
    }

    /**
     * 串行执行请求
     *
     * @param observable1   第一个请求
     * @param firstListener 第一个请求返回结果后的回调
     * @param observable2   第二个请求
     * @param response      第二个请求结果响应回调
     * @param <F>           第一个请求的参数类型
     * @param <S>           第二个请求的参数类型
     * @return 订阅关系
     */
    public <F, S> ApiResponse callSerially(@NonNull Observable<F> observable1, final OnSerialAcceptListener<F> firstListener,
                                           @NonNull final Observable<S> observable2, ApiResponse response) {
        return add(observable1
                // 在 io 线程中进行网络请求
                .subscribeOn(Schedulers.io())
                // 回到主线程处理返回结果
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<F, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(F f) throws Exception {
                        if (firstListener != null) {
                            firstListener.accept(f);
                        }
                        return observable2;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(response));
    }


    /**
     * 将请求添加到管理类中
     *
     * @param observer
     */
    private <T> ApiResponse add(ApiResponse<T> observer) {
        if (observer == null) return null;
        if (sDisposableSite == null) {
            sDisposableSite = new HashMap<>();
        }
        String tag = observer.getTag();
        if (tag == null) {
            tag = new Random().nextInt(1000) + "";
        } else {
            // 如果已经存在相同的请求，则先取消原来的请求，再将新的请求加入管理类
            if (sDisposableSite.containsKey(tag)) {
                cancel(tag);
            }
        }
        sDisposableSite.put(tag, observer);
        return observer;
    }

    /**
     * 取消指定的请求
     *
     * @param tag 请求标记
     */
    public void cancel(@NonNull String tag) {
        if (sDisposableSite != null && !sDisposableSite.isEmpty()) {
            ApiResponse disposable = sDisposableSite.get(tag);
            if (disposable != null) {
                disposable.cancel();
            }
            sDisposableSite.remove(tag);
        }
    }

    /**
     * 取消所有的请求
     */
    public void cancelAll() {
        if (sDisposableSite != null && !sDisposableSite.isEmpty()) {
            for (Map.Entry<String, ApiResponse> entry : sDisposableSite.entrySet()) {
                if (entry.getValue() != null) {
                    entry.getValue().cancel();
                }
            }
            sDisposableSite.clear();
        }
        removeAllDownloadProgressListener();
    }

    /**
     * 添加下载进度监听
     *
     * @param tag      监听器的标记
     * @param listener 下载监听
     */
    public void addDownloadProgressListener(@NonNull String tag, OnProgressListener listener) {
        if (tag != null && listener != null) {
            if (sDownloadProgressSite == null) {
                sDownloadProgressSite = new HashMap<>();
            }
            sDownloadProgressSite.put(tag, listener);
        }
    }

    /**
     * 获取下载进度监听集合
     */
    public HashMap<String, OnProgressListener> getDownloadProgressSite() {
        return sDownloadProgressSite;
    }

    /**
     * 移除指定的下载进度监听
     */
    public void removeDownloadProgressListener(String tag) {
        if (tag != null
                && sDownloadProgressSite != null
                && !sDownloadProgressSite.isEmpty()) {
            sDownloadProgressSite.remove(tag);
        }
    }

    public void removeAllDownloadProgressListener() {
        if (sDownloadProgressSite != null && !sDownloadProgressSite.isEmpty()) {
            sDownloadProgressSite.clear();
        }
    }

    public void clear() {
        cancelAll();
        if (sDisposableSite != null) {
            sDisposableSite.clear();
            sDisposableSite = null;
        }
        if (sDownloadProgressSite != null) {
            sDownloadProgressSite.clear();
            ;
            sDownloadProgressSite = null;
        }
        sRetrofitManager = null;
    }
}
