package pers.liyi.bullet.retrofit.interceptor;

import android.util.Log;

import java.io.IOException;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 日志拦截器
 */
public class LogInterceptor implements Interceptor {
    private final String TAG = "Bullet-" + this.getClass().getSimpleName();

    @Override
    public Response intercept(Chain chain) throws IOException {
        // 这个 chain 里面包含了 request 和 response，所以你要什么都可以从这里拿
        // =========== 发送 ===========
        Request originalRequest = chain.request();
        // 请求发起的时间
        long requestTime = System.currentTimeMillis();
        HttpUrl requestUrl = originalRequest.url();
        Connection requestConnection = chain.connection();
        Headers requestHeaders = originalRequest.headers();
        // 打印发送信息
        Log.d(TAG, "\n===============================================================" +
                "\n== LogInterceptor===发送===requestUrl= " + requestUrl +
                "\n== LogInterceptor===发送===requestConnection= " + requestConnection +
                "\n== LogInterceptor===发送===requestHeaders= " + requestHeaders +
                "\n===============================================================");

        // =========== 接收 ===========
        // 收到响应的时间
        long responseTime = System.currentTimeMillis();
        Response response = chain.proceed(chain.request());
        MediaType mediaType = response.body().contentType();
//        ResponseBody responseBody = response.peekBody(1024 * 1024);
        HttpUrl responseUrl = response.request().url();
        Headers responseHeaders = response.headers();
        String content = response.body().string();
        // 延迟时间
        long delayTime = responseTime - requestTime;
        // 打印接收信息
        Log.d(TAG, "\n===============================================================" +
                "\n== LogInterceptor===接收===requestUrl= " + responseUrl +
                "\n== LogInterceptor===接收===responseHeaders= " + responseHeaders +
                "\n== LogInterceptor===接收===delayTime= " + delayTime +
                "\n== LogInterceptor===接收===content= " + content +
                "\n===============================================================");
        // 因为 OkHttp 请求回调中 response.body().string() 只能有效调用一次，调用后就会被关闭，
        // 那么在返回结果时就会报错：java.lang.IllegalStateException: closed
        // 所以需要重构一个新的有效的 response
        return response.newBuilder()
                .body(ResponseBody.create(mediaType, content))
                .build();
    }
}
