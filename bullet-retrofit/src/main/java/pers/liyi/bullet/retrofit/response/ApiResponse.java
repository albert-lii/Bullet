package pers.liyi.bullet.retrofit.response;

import io.reactivex.observers.DisposableObserver;
import pers.liyi.bullet.retrofit.exception.ExceptionHandler;
import pers.liyi.bullet.retrofit.listener.ApiResponseListener;

/**
 * Retrofit2.0 中的网络请求响应基类
 */
public class ApiResponse<T> extends DisposableObserver<T> {
    // 请求标记
    private String tag = null;
    // 网络请求响应监听
    private ApiResponseListener<T> responseListener;

    public ApiResponse(ApiResponseListener listener) {
        this.responseListener = listener;
    }

    public ApiResponse(String tag, ApiResponseListener listener) {
        this.tag = tag;
        this.responseListener = listener;
    }

    /**
     * 设置请求标记
     *
     * @param tag
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (responseListener != null) {
            responseListener.onStart(tag);
        }
    }

//    @Override
//    public void onSubscribe(Disposable d) {
//        this.mDisposable = d;
//    }

    @Override
    public void onNext(T t) {
        if (responseListener != null) {
            responseListener.onSuccess(tag, t);
        }
    }

    @Override
    public void onError(Throwable e) {
        if (responseListener != null) {
            responseListener.onError(tag, ExceptionHandler.parseException(e));
        }
    }

    @Override
    public void onComplete() {
        if (responseListener != null) {
            responseListener.onComplete(tag);
        }
    }

    /**
     * 取消请求
     */
    public void cancel() {
        if (responseListener != null) {
            responseListener.onCancel(tag);
        }
        // 如果处于订阅状态，则取消订阅
        if (!isDisposed()) {
            dispose();
        }
    }
}
