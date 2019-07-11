package pers.liyi.bullet.retrofit.listener;

/**
 * 进度监听接口
 */
public interface OnProgressListener {
    /**
     * 上传/下载进度监听
     *
     * @param progress  当前进度
     * @param totalSize 文件字节流的总大小
     * @param tag       多文件上传时，标记不同的请求
     */
    void onProgress(float progress, long totalSize, String tag);
}
