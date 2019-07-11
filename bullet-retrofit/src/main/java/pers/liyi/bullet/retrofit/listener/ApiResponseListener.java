package pers.liyi.bullet.retrofit.listener;

import pers.liyi.bullet.retrofit.exception.ApiError;

public abstract class ApiResponseListener<T> {

    /**
     * 开始网络请求
     *
     * @param tag 请求的标记
     */
    public void onStart(String tag){

    }

    /**
     * 网络请求成功
     *
     * @param tag  请求的标记
     * @param data 返回的数据
     */
    public void onSuccess(String tag, T data){

    }

    /**
     * 网络请求失败
     *
     * @param tag     请求的标记
     * @param error 请求失败时，返回的信息类
     */
    public void onError(String tag, ApiError error){

    }

    /**
     * 网络请求完成
     *
     * @param tag 请求的标记
     */
    public void onComplete(String tag){

    }

    /**
     * 取消网络请求
     *
     * @param tag 请求的标记
     */
    public void onCancel(String tag){

    }
}
