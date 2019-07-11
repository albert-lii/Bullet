package pers.liyi.bullet.retrofit;

import java.util.Map;

import javax.net.ssl.HostnameVerifier;


public class ApiClientConfig {
    /**
     * 当前域名
     */
    private String host;

    /**
     * 超时时间
     */
    private int connectTimeout;
    private int readTimeout;
    private int writeTimeout;

    /**
     * 缓存相关
     */
    // 是否设置缓存
    private boolean cacheEnable;
    // 默认的缓存文件夹的名字
    private String cacheName;
    // 最大缓存空间
    private int maxCacheSize;

    /**
     * 验证服务器域名是否合法
     */
    private HostnameVerifier hostnameVerifier;

    /**
     * 多域名相关
     */
    // @Header("key:value") 中的 key
    private String hostKeyInHeader;
    // BaseUrl 存储器
    private Map<String, String> hostMap;

    /**
     * 多下载相关
     */
    // @Header("key:value") 中的 key
    private String downloadKeyInHeader;


    public ApiClientConfig() {
        this.connectTimeout = 25;
        this.readTimeout = 25;
        this.writeTimeout = 25;
        this.cacheEnable = true;
        this.cacheName = "apiCache";
        this.maxCacheSize = 10 * 1024 * 1024;
    }

    public String getHost() {
        return host;
    }

    public ApiClientConfig setHost(String host) {
        this.host = host;
        return this;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public ApiClientConfig setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public ApiClientConfig setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public int getWriteTimeout() {
        return writeTimeout;
    }

    public ApiClientConfig setWriteTimeout(int writeTimeout) {
        this.writeTimeout = writeTimeout;
        return this;
    }

    public boolean isCacheEnable() {
        return cacheEnable;
    }

    public ApiClientConfig setCacheEnable(boolean cacheEnable) {
        this.cacheEnable = cacheEnable;
        return this;
    }

    public String getCacheName() {
        return cacheName;
    }

    public ApiClientConfig setCacheName(String cacheName) {
        this.cacheName = cacheName;
        return this;
    }

    public int getMaxCacheSize() {
        return maxCacheSize;
    }

    public ApiClientConfig setMaxCacheSize(int maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
        return this;
    }

    public HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }

    public ApiClientConfig setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }

    public String getHostKeyInHeader() {
        return hostKeyInHeader;
    }

    public ApiClientConfig setHostKeyInHeader(String hostKeyInHeader) {
        this.hostKeyInHeader = hostKeyInHeader;
        return this;
    }

    public Map<String, String> getHostMap() {
        return hostMap;
    }

    public ApiClientConfig setHostMap(Map<String, String> hostMap) {
        this.hostMap = hostMap;
        return this;
    }

    public String getDownloadKeyInHeader() {
        return downloadKeyInHeader;
    }

    public ApiClientConfig setDownloadKeyInHeader(String downloadKeyInHeader) {
        this.downloadKeyInHeader = downloadKeyInHeader;
        return this;
    }
}
