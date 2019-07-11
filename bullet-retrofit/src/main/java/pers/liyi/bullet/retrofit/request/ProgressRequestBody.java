package pers.liyi.bullet.retrofit.request;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;

import pers.liyi.bullet.retrofit.listener.OnProgressListener;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * 带进度的上传请求体
 */
public class ProgressRequestBody extends RequestBody {
    private RequestBody delegate;
    private OnProgressListener progressListener;
    private BufferedSink bufferedSink;
    private Handler handler = new Handler(Looper.getMainLooper());
    private String tag;

    public ProgressRequestBody(RequestBody delegate, OnProgressListener progressListener) {
        this.delegate = delegate;
        this.progressListener = progressListener;
    }

    public ProgressRequestBody(RequestBody requestBody, OnProgressListener progressListener, String tag) {
        this.delegate = requestBody;
        this.progressListener = progressListener;
        this.tag = tag;
    }

    @Override
    public MediaType contentType() {
        return delegate.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        try {
            return delegate.contentLength();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        // 判断是否是 Log 拦截器，因为 Log 拦截器的调用，会使本方法被重复调用
        if (sink instanceof Buffer) {
            delegate.writeTo(sink);
            return;
        }
        if (bufferedSink == null) {
            bufferedSink = Okio.buffer(new WrapSink(sink));
        }
        // 写入数据
        delegate.writeTo(bufferedSink);
        // 刷新，必须调用 flush，否则最后一部分数据可能不会被写入
        bufferedSink.flush();
    }

    private final class WrapSink extends ForwardingSink {
        private long bytesWriten = 0;
        private long totalSize = 0;

        public WrapSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            if (totalSize == 0) {
                totalSize = contentLength();
            }
            bytesWriten += byteCount;
            if (progressListener != null) {
                handler.post(new ProgressUpdater(bytesWriten, totalSize));
            }
        }
    }

    private class ProgressUpdater implements Runnable {
        private long uploaded;
        private long totalSize;

        public ProgressUpdater(long uploaded, long total) {
            this.uploaded = uploaded;
            this.totalSize = total;
        }

        @Override
        public void run() {
            progressListener.onProgress(uploaded * 1f / totalSize, totalSize, tag);
        }
    }
}
