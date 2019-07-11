package pers.liyi.bullet.retrofit;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import pers.liyi.bullet.retrofit.interceptor.BaseUrlInterceptor;
import pers.liyi.bullet.retrofit.interceptor.DownloadInterceptor;
import pers.liyi.bullet.retrofit.interceptor.LogInterceptor;
import pers.liyi.bullet.retrofit.interceptor.OfflineCacheControlInterceptor;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Retrofit2.0 配置类
 */
public class RetrofitManager {
    private ApiClientConfig mApiClientConfig;
    private OkHttpClient mOkHttpClient;
    private Retrofit mRetrofit;
    private Context mContext;

    public RetrofitManager(@NonNull Context context, ApiClientConfig config) {
        this.mContext = context.getApplicationContext();
        this.mApiClientConfig = config;
    }

    public void setOkHttpClient(OkHttpClient okHttpClient) {
        this.mOkHttpClient = okHttpClient;
    }

    public void setRetrofit(Retrofit retrofit) {
        this.mRetrofit = retrofit;
    }

    /**
     * 获取 OkHttpClient 实例
     */
    public OkHttpClient getOkHttpClient() {
        if (mOkHttpClient == null) {
            mOkHttpClient = createDefaultOkHttpClient();
        }
        return mOkHttpClient;
    }

    /**
     * 获取 Retrofit 实例
     */
    public Retrofit getRetrofit() {
        if (mRetrofit == null) {
            mRetrofit = createDefaultRetrofit();
        }
        return mRetrofit;
    }

    /**
     * 创建服务
     */
    public <T> T createService(@NonNull Class<T> clz) {
        return getRetrofit().create(clz);
    }

    /**
     * 获取默认的 Retrofit
     */
    private Retrofit createDefaultRetrofit() {
        if (mOkHttpClient == null) {
            mOkHttpClient = createDefaultOkHttpClient();
        }
        return new Retrofit.Builder()
                .baseUrl(mApiClientConfig.getHost())
                .client(mOkHttpClient)
                // 添加 Gson 转化器
                .addConverterFactory(GsonConverterFactory.create())
                // 添加 String 转化器
//                .addConverterFactory(ScalarsConverterFactory.create())
                // 配合 RxJava2 使用
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public OkHttpClient createDefaultOkHttpClient() {
        OkHttpClient.Builder builder = createDefaultOkBuilder();
        builder = setOkCache(builder);
        builder = setHostnameVerifier(builder);
        builder = setBaseUrlInterceptor(builder);
        builder = setDownloadInterceptor(builder);
        // 调试模式下，添加日志打印
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(new LogInterceptor());
        }
        return builder.build();
    }

    /**
     * 创建默认的 OkHttp 的 builder
     */
    private OkHttpClient.Builder createDefaultOkBuilder() {
        return new OkHttpClient.Builder()
                // 超时时间
                .connectTimeout(mApiClientConfig.getConnectTimeout(), TimeUnit.SECONDS)
                .readTimeout(mApiClientConfig.getReadTimeout(), TimeUnit.SECONDS)
                .writeTimeout(mApiClientConfig.getWriteTimeout(), TimeUnit.SECONDS)
                // 失败重连
                .retryOnConnectionFailure(true);
    }

    /**
     * 设置 OkHttp 的缓存配置
     */
    private OkHttpClient.Builder setOkCache(@NonNull OkHttpClient.Builder builder) {
        if (mApiClientConfig.isCacheEnable()) {
            // 缓存目录
            File cacheDir = new File(mContext.getCacheDir(), mApiClientConfig.getCacheName());
            Cache cache = new Cache(cacheDir, mApiClientConfig.getMaxCacheSize());
            // 添加离线缓存
            builder.addNetworkInterceptor(new OfflineCacheControlInterceptor(mContext))
                    // 设置缓存路径
                    .cache(cache);
        }
        return builder;
    }

    /**
     * 设置服务器域名验证
     */
    private OkHttpClient.Builder setHostnameVerifier(@NonNull OkHttpClient.Builder builder) {
        if (mApiClientConfig.getHostnameVerifier() != null) {
            // 验证服务器域名是否合法
            builder.hostnameVerifier(mApiClientConfig.getHostnameVerifier());
        }
        return builder;
    }

    /**
     * 设置多个域名
     */
    private OkHttpClient.Builder setBaseUrlInterceptor(@NonNull OkHttpClient.Builder builder) {
        if (!TextUtils.isEmpty(mApiClientConfig.getHostKeyInHeader())
                && mApiClientConfig.getHostMap() != null
                && !mApiClientConfig.getHostMap().isEmpty()) {
            builder.addInterceptor(new BaseUrlInterceptor(mApiClientConfig.getHostKeyInHeader(), mApiClientConfig.getHostMap()));
        }
        return builder;
    }

    /**
     * 当多个下载并发执行时，需要监听每个下载进度时执行
     */
    private OkHttpClient.Builder setDownloadInterceptor(@NonNull OkHttpClient.Builder builder) {
        if (!TextUtils.isEmpty(mApiClientConfig.getDownloadKeyInHeader())) {
            builder.addInterceptor(new DownloadInterceptor(mApiClientConfig.getDownloadKeyInHeader()));
        }
        return builder;
    }
}
