package pers.liyi.bullet.retrofit.constant;

/**
 * HTTP 状态码
 */
public final class HttpStatusCode {
    /**
     * 错误请求
     * {@link pers.liyi.bullet.retrofit.R.string#bullet_http_status_err_request_error}
     */
    public static final int ERROR_REQUEST = 400;
    /**
     * 服务器未授权访问
     * {@link pers.liyi.bullet.retrofit.R.string#bullet_http_status_err_unauthorized}
     */
    public static final int UNAUTHORIZED = 401;
    /**
     * 服务器拒绝请求
     * {@link pers.liyi.bullet.retrofit.R.string#bullet_http_status_err_forbidden}
     */
    public static final int FORBIDDEN = 403;
    /**
     * 服务器找不到请求的资源
     * {@link pers.liyi.bullet.retrofit.R.string#bullet_http_status_err_not_found}
     */
    public static final int NOT_FOUND = 404;
    /**
     * 请求方法不支持
     * {@link pers.liyi.bullet.retrofit.R.string#bullet_http_status_err_method_not_support}
     */
    public static final int METHOD_NOT_SUPPORT = 405;
    /**
     * 请求超时，网络信号不稳定
     * {@link pers.liyi.bullet.retrofit.R.string#bullet_http_status_err_timeout}
     */
    public static final int REQUEST_TIMEOUT = 408;
    /**
     * 请求实体过大
     * {@link pers.liyi.bullet.retrofit.R.string#bullet_http_status_err_request_large}
     */
    public static final int REQUEST_LARGE = 413;
    /**
     * 请求的 URI 过长
     * {@link pers.liyi.bullet.retrofit.R.string#bullet_http_status_err_uri_long}
     */
    public static final int REQUEST_URI_LONG = 414;
    /**
     * 服务器错误
     * {@link pers.liyi.bullet.retrofit.R.string#bullet_http_status_err_server_error}
     */
    public static final int SERVER_ERROR = 500;
    /**
     * 网关错误
     * {@link pers.liyi.bullet.retrofit.R.string#bullet_http_status_err_gateway_error}
     */
    public static final int GATEWAY_ERROR = 502;
    /**
     * 服务暂不可用
     * {@link pers.liyi.bullet.retrofit.R.string#bullet_http_status_err_service_unavailable}
     */
    public static final int SERVICE_UNAVAILABLE = 503;
    /**
     * 网关超时
     * {@link pers.liyi.bullet.retrofit.R.string#bullet_http_status_err_gateway_timeout}
     */
    public static final int GATEWAY_TIMEOUT = 504;
    /**
     * HTTP 协议版本不支持
     * {@link pers.liyi.bullet.retrofit.R.string#bullet_http_status_err_http_not_support}
     */
    public static final int HTTP_NOT_SUPPORT = 505;
}
