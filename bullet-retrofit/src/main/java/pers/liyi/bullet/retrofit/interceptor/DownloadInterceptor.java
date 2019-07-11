package pers.liyi.bullet.retrofit.interceptor;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import pers.liyi.bullet.retrofit.ApiClient;
import pers.liyi.bullet.retrofit.listener.OnProgressListener;
import pers.liyi.bullet.retrofit.response.ProgressResponseBody;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 带进度的下载拦截器
 */
public class DownloadInterceptor implements Interceptor {
    private String progressListenerKey;

    public DownloadInterceptor(String key) {
        this.progressListenerKey = key;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Response response = chain.proceed(originalRequest);
        // 判断是否是下载响应体
        if (response.body() instanceof ProgressResponseBody) {
            // 获取指定的头信息的集合
            List<String> progressListenerTagList = originalRequest.headers(progressListenerKey);
            // 获取监听器集合
            HashMap<String, OnProgressListener> progressListenerSite = ApiClient.getInstance().getDownloadProgressSite();
            if (progressListenerSite != null) {
                if (progressListenerSite.size() == 1) {
                    return response.newBuilder().body(
                            new ProgressResponseBody(response.body(),
                                    (progressListenerTagList != null && !progressListenerTagList.isEmpty()) ? progressListenerTagList.get(0) : null,
                                    progressListenerSite.get(0))).build();
                } else if (progressListenerSite.size() > 1) {
                    // 获取头信息中配置的 value
                    String progressListenerTag = progressListenerTagList.get(0);
                    OnProgressListener progressListener = progressListenerSite.get(progressListenerTag);
                    if (progressListener != null) {
                        return response.newBuilder().body(
                                new ProgressResponseBody(response.body(), progressListenerTag, progressListener)).build();
                    }
                }
            }
        }
        return response;
    }
}
