package pers.liyi.bullet.retrofit.response;


import pers.liyi.bullet.retrofit.ApiClient;
import pers.liyi.bullet.retrofit.listener.ApiResponseListener;
import pers.liyi.bullet.retrofit.listener.OnProgressListener;

/**
 * Retrofit2.0 中的下载请求响应基类
 */
public class ApiDownloadResponse<T> extends ApiResponse<T> {
    private OnProgressListener progressListener;

    /**
     * @param listener            响应回调
     * @param progressListenerTag 必须与 @Header("key:value") 中设置的 value 对应，例如 Header 配置为 @Header("downloadKey","apk")，则 listenerTag="apk"
     * @param progressListener    进度监听
     */
    public ApiDownloadResponse(ApiResponseListener<T> listener, String progressListenerTag, OnProgressListener progressListener) {
        super(listener);
        this.progressListener = progressListener;
        if (this.progressListener != null) {
            ApiClient.getInstance().addDownloadProgressListener(progressListenerTag, progressListener);
        }
    }
}
