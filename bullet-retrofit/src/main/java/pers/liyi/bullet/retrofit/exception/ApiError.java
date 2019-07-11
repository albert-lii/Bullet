package pers.liyi.bullet.retrofit.exception;

import android.content.Context;

import io.reactivex.annotations.NonNull;
import pers.liyi.bullet.retrofit.R;

/**
 * 网络请求异常类
 */
public class ApiError {
    // 错误码
    private int code;
    // 异常信息
    private String message;
    // 抛出的异常
    private Throwable throwable;
    // 自定义的错误信息资源 id
    private int cusErrId;


    public ApiError() {

    }

    public ApiError(int errorCode) {
        this.code = errorCode;
    }

    public ApiError(int errorCode, int cusErrId) {
        this.code = errorCode;
        this.cusErrId = cusErrId;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public int getCusErrId() {
        return cusErrId;
    }

    public void setCusErrId(int cusErrId) {
        this.cusErrId = cusErrId;
    }

    /**
     * 获取错误信息，当自定义错误信息为未知错误或者网络错误时，直接给出原始的异常信息
     */
    public String getError(@NonNull Context context) {
        if (cusErrId != 0
                && cusErrId != R.string.bullet_http_network_err_unknown
                && cusErrId != R.string.bullet_http_status_err_error) {
            return context.getResources().getString(cusErrId);
        } else {
            return message;
        }
    }
}
