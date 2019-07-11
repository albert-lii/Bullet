package pers.liyi.bullet.retrofit.exception;

import android.net.ParseException;
import android.util.Log;

import com.google.gson.JsonParseException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import pers.liyi.bullet.retrofit.R;
import pers.liyi.bullet.retrofit.constant.HttpStatusCode;
import pers.liyi.bullet.retrofit.constant.NetworkErrorType;
import retrofit2.HttpException;

/**
 * 网络请求异常处理类
 */
public class ExceptionHandler {

    /**
     * 异常解析
     *
     * @param e
     * @return {@link ApiError}
     */
    public static ApiError parseException(Throwable e) {
        ApiError error = new ApiError();
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            int msgResId;
            switch (httpException.code()) {
                case HttpStatusCode.ERROR_REQUEST:
                    msgResId = R.string.bullet_http_status_err_request_error;
                    break;
                case HttpStatusCode.UNAUTHORIZED:
                    msgResId = R.string.bullet_http_status_err_unauthorized;
                    break;
                case HttpStatusCode.FORBIDDEN:
                    msgResId = R.string.bullet_http_status_err_forbidden;
                    break;
                case HttpStatusCode.NOT_FOUND:
                    msgResId = R.string.bullet_http_status_err_not_found;
                    break;
                case HttpStatusCode.METHOD_NOT_SUPPORT:
                    msgResId = R.string.bullet_http_status_err_method_not_support;
                    break;
                case HttpStatusCode.REQUEST_TIMEOUT:
                    msgResId = R.string.bullet_http_status_err_timeout;
                    break;
                case HttpStatusCode.REQUEST_LARGE:
                    msgResId = R.string.bullet_http_status_err_request_large;
                    break;
                case HttpStatusCode.REQUEST_URI_LONG:
                    msgResId = R.string.bullet_http_status_err_uri_long;
                    break;
                case HttpStatusCode.SERVER_ERROR:
                    msgResId = R.string.bullet_http_status_err_server_error;
                    break;
                case HttpStatusCode.GATEWAY_ERROR:
                    msgResId = R.string.bullet_http_status_err_gateway_error;
                    break;
                case HttpStatusCode.SERVICE_UNAVAILABLE:
                    msgResId = R.string.bullet_http_status_err_service_unavailable;
                    break;
                case HttpStatusCode.GATEWAY_TIMEOUT:
                    msgResId = R.string.bullet_http_status_err_gateway_timeout;
                    break;
                case HttpStatusCode.HTTP_NOT_SUPPORT:
                    msgResId = R.string.bullet_http_status_err_http_not_support;
                    break;
                default:
                    msgResId = R.string.bullet_http_status_err_error;
            }
            error.setCode(httpException.code());
            error.setCusErrId(msgResId);
        } else if (e instanceof ConnectException || e instanceof SocketTimeoutException || e instanceof ConnectTimeoutException) {
            error.setCode(NetworkErrorType.UNCONNECT_ERROR);
            error.setCusErrId(R.string.bullet_http_network_err_unconnect);
        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            error.setCode(NetworkErrorType.SSL_ERROR);
            error.setCusErrId(R.string.bullet_http_network_err_ssl_error);
        } else if (e instanceof JsonParseException || e instanceof JSONException || e instanceof ParseException) {
            error.setCode(NetworkErrorType.PARSE_ERROR);
            error.setCusErrId(R.string.bullet_http_network_err_parse_error);
        } else {
            error.setCode(NetworkErrorType.UNKNOWN_ERROR);
            error.setCusErrId(R.string.bullet_http_network_err_unknown);
        }
        error.setMessage(e.getMessage());
        error.setThrowable(e);
        Log.e("Bullet-Http-Exception",
                "\n==================================================================" +
                        "\n== Http Error Code >>> " + error.getCode() +
                        "\n== Http Error Cause >>> " + e.getCause() +
                        "\n== Http Error Message >>> " + e.getMessage() +
                        "\n==================================================================");
        return error;
    }
}
