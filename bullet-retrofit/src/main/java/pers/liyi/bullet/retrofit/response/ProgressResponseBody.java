package pers.liyi.bullet.retrofit.response;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;

import pers.liyi.bullet.retrofit.listener.OnProgressListener;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * 带进度的响应体
 */
public class ProgressResponseBody extends ResponseBody {
    private ResponseBody mDelegate;
    //  BufferedSource 是 okio 库中的输入流，这里就当作 inputStream 来使用。
    private BufferedSource mBufferedSource;
    // 下载进度监听的标记
    private String mTag;
    // 下载进度监听
    private OnProgressListener mProgressListener;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public ProgressResponseBody(ResponseBody responseBody, String progressListenerTag, OnProgressListener listener) {
        this.mDelegate = responseBody;
        this.mTag = progressListenerTag;
        this.mProgressListener = listener;
    }

    @Override
    public MediaType contentType() {
        return mDelegate.contentType();
    }

    @Override
    public long contentLength() {
        return mDelegate.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (mBufferedSource == null) {
            mBufferedSource = Okio.buffer(new ProgressSource(mDelegate.source()));
        }
        return mBufferedSource;
    }

    private class ProgressSource extends ForwardingSource {
        private long totalBytesRead = 0;
        private long totalSize = 0;

        public ProgressSource(Source delegate) {
            super(delegate);
        }

        @Override
        public long read(Buffer sink, long byteCount) throws IOException {
            long bytesRead = super.read(sink, byteCount);
            if (totalSize == 0) {
                totalSize = contentLength();
            }
            // 增加当前读取的字节数，如果读取完成了 bytesRead 会返回-1
            this.totalBytesRead += (bytesRead != -1 ? bytesRead : 0);
            // 实时发送读取进度、当前已读取的字节和总字节
            if (mProgressListener != null) {
                mHandler.post(new ProgressUpdater(totalBytesRead, totalSize));
            }
            return bytesRead;
        }
    }

    private class ProgressUpdater implements Runnable {
        private long readed;
        private long totalSize;

        public ProgressUpdater(long uploaded, long total) {
            this.readed = uploaded;
            this.totalSize = total;
        }

        @Override
        public void run() {
            mProgressListener.onProgress(readed / totalSize * 100f, totalSize, mTag);
        }
    }
}
