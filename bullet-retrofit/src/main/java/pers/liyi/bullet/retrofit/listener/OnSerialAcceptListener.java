package pers.liyi.bullet.retrofit.listener;

/**
 * 串行执行请求时的响应回调
 *
 * @param <T>
 */
public interface OnSerialAcceptListener<T> {

    void accept(T t);
}
