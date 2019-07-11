package pers.liyi.bullet.retrofit.request;

import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pers.liyi.bullet.retrofit.listener.OnProgressListener;


public class RequestCreator {
    private static Gson sGson;
    // 文本类型
    public static final MediaType MEDIATYPE_TEXT = MediaType.parse("text/plain");
    // 表单类型
    public static final MediaType MEDIATYPE_FORM = MediaType.parse("multipart/form-data");
    // json 类型
    public static final MediaType MEDIATYPE_JSON = MediaType.parse("application/json;charset=utf-8");
    // 文件类型
    public static final MediaType MEDIATYPE_FILE = MediaType.parse("application/octet-stream");
    // JPG 图片类型
    public static final MediaType MEDIATYPE_JPG = MediaType.parse("image/jpg");
    // PNG 图片类型
    public static final MediaType MEDIATYPE_PNG = MediaType.parse("image/png");


    /**
     * 创建表单类型请求体
     *
     * @param object 参数对象
     * @return {@link RequestBody}
     */
    public static RequestBody createFormBody(Object object) {
        if (object == null) return null;
        return RequestBody.create(obj2Str(object),MEDIATYPE_FORM);
    }

    /**
     * 创建 json 类型请求体
     *
     * @param object 参数对象
     * @return {@link RequestBody}
     */
    public static RequestBody createJsonBody(Object object) {
        if (object == null) return null;
        return RequestBody.create(obj2Str(object),MEDIATYPE_JSON);
    }

    /**
     * 创建文件类型请求体
     *
     * @param mediaType 文件类型
     * @param file      文件
     * @return {@link RequestBody}
     */
    public static RequestBody createFileBody(@NonNull MediaType mediaType, File file) {
        if (file == null || !file.exists()) return null;
        return RequestBody.create(file,mediaType);
    }

    /**
     * 创建带进度的上传文件类型请求体
     *
     * @param mediaType
     * @param file
     * @param listener
     * @return {@link ProgressRequestBody}
     */
    public static ProgressRequestBody createFileBody(@NonNull MediaType mediaType, File file, OnProgressListener listener) {
        if (file == null || !file.exists()) return null;
        return new ProgressRequestBody(createFileBody(mediaType, file), listener);
    }

    /**
     * 创建带进度的上传文件类型请求体
     *
     * @param mediaType
     * @param file
     * @param listener
     * @param tag       请求标记
     * @return {@link ProgressRequestBody}
     */
    public static ProgressRequestBody createFileBody(@NonNull MediaType mediaType, File file, OnProgressListener listener, String tag) {
        if (file == null || !file.exists()) return null;
        return new ProgressRequestBody(createFileBody(mediaType, file), listener, tag);
    }

    /**
     * 创建单个文件的请求体
     *
     * @param mediaType 文件类型
     * @param name      与服务器约定的 key
     * @param file      文件
     * @return {@link MultipartBody.Part}
     */
    public static MultipartBody.Part createMultipartBodyPart(@NonNull MediaType mediaType, @NonNull String name, File file) {
        if (file == null || !file.exists()) return null;
        RequestBody requestFile = createFileBody(mediaType, file);
        return MultipartBody.Part.createFormData(name, file.getName(), requestFile);
    }

    /**
     * 创建带进度的单个文件的请求体
     *
     * @param mediaType
     * @param name
     * @param file
     * @param listener
     * @return {@link MultipartBody.Part}
     */
    public static MultipartBody.Part createMultipartBodyPart(@NonNull MediaType mediaType, @NonNull String name, File file,
                                                             OnProgressListener listener) {
        if (file == null || !file.exists()) return null;
        ProgressRequestBody requestFile = createFileBody(mediaType, file, listener);
        return MultipartBody.Part.createFormData(name, file.getName(), requestFile);
    }

    /**
     * 创建带进度的单个文件的请求体
     * @param mediaType
     * @param name
     * @param file
     * @param listener
     * @param tag
     * @return
     */
    public static MultipartBody.Part createMultipartBodyPart(@NonNull MediaType mediaType, @NonNull String name, File file,
                                                             OnProgressListener listener, String tag) {
        if (file == null || !file.exists()) return null;
        ProgressRequestBody requestFile = createFileBody(mediaType, file, listener, tag);
        return MultipartBody.Part.createFormData(name, file.getName(), requestFile);
    }

    /**
     * 创建多文件上传请求体
     *
     * @param mediaType 文件类型
     * @param name      与服务器约定的 key
     * @param files     文件集合
     * @return {@link List<MultipartBody.Part>}
     */
    public static List<MultipartBody.Part> createMultipartBodyParts(@NonNull MediaType mediaType, @NonNull String name, List<File> files) {
        if (files == null || files.isEmpty()) return null;
        List<MultipartBody.Part> parts = new ArrayList<>(files.size());
        for (File file : files) {
            MultipartBody.Part part = createMultipartBodyPart(mediaType, name, file);
            parts.add(part);
        }
        return parts;
    }

    /**
     * 创建多文件上传请求体
     *
     * @param mediaType
     * @param name
     * @param files     Object[] 中有两个元素，第一个元素为 OnProgressListener ,第二个元素为 OnProgressListener 的tag
     * @return
     */
    public static List<MultipartBody.Part> createMultipartBodyParts(@NonNull MediaType mediaType, @NonNull String name,
                                                                    LinkedHashMap<File, Object[]> files) {
        if (files == null || files.isEmpty()) return null;
        List<MultipartBody.Part> parts = new ArrayList<>(files.size());
        for (Map.Entry<File, Object[]> entry : files.entrySet()) {
            OnProgressListener progressListener = (OnProgressListener) entry.getValue()[0];
            String tag = (String) entry.getValue()[1];
            MultipartBody.Part part = createMultipartBodyPart(mediaType, name, entry.getKey(), progressListener, tag);
            parts.add(part);
        }
        return parts;
    }

    /**
     * 带参数上传文件
     *
     * @param mediaType 文件的 MediaType
     * @param params 需要上传的参数
     * @param fileKey 与服务器约定的 key
     * @param file 需要上传的文件
     * @return
     */
    public static MultipartBody createMultipartBody(@NonNull MediaType mediaType, Map<String, String> params,
                                                    @NonNull String fileKey, File file) {
        if (file == null || !file.exists()) return null;
        MultipartBody.Builder builder = new MultipartBody.Builder();
        // 携带参数
        if(params!=null&&!params.isEmpty()){
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }
        builder.addFormDataPart(fileKey, file.getName(), createFileBody(mediaType, file));
        builder.setType(MultipartBody.FORM);
        return builder.build();
    }

    /**
     * 带参数和进度条上传文件
     * @param mediaType
     * @param params
     * @param fileKey
     * @param file
     * @param listener
     * @param tag
     * @return
     */
    public static MultipartBody createMultipartBody(@NonNull MediaType mediaType, Map<String, String> params,
                                                    @NonNull String fileKey, File file, OnProgressListener listener, String tag) {
        if (file == null || !file.exists()) return null;
        MultipartBody.Builder builder = new MultipartBody.Builder();
        // 携带参数
        if(params!=null&&!params.isEmpty()){
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }
        builder.addFormDataPart(fileKey, file.getName(), createFileBody(mediaType, file, listener, tag));
        builder.setType(MultipartBody.FORM);
        return builder.build();
    }

    /**
     * 创建带参数多文件上传请求体
     *
     * @param mediaType 文件类型
     * @param params    与文件一起上传的参数
     * @param fileKey   与服务器约定的 key
     * @param files     文件集合
     * @return {@link MultipartBody}
     */
    public static MultipartBody createMultipartBody(@NonNull MediaType mediaType, Map<String, String> params,
                                                    @NonNull String fileKey, List<File> files) {
        if (files == null || files.isEmpty()) return null;
        MultipartBody.Builder builder = new MultipartBody.Builder();
        // 携带参数
        if(params!=null&&!params.isEmpty()){
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }
        for (File file : files) {
            builder.addFormDataPart(fileKey, file.getName(), createFileBody(mediaType, file));
        }
        builder.setType(MultipartBody.FORM);
        return builder.build();
    }

    /**
     * 创建带参数多文件上传请求体
     *
     * @param mediaType
     * @param params
     * @param fileKey
     * @param files     Object[] 中有两个元素，第一个元素为 OnProgressListener ,第二个元素为 OnProgressListener 的tag
     * @return
     */
    public static MultipartBody createMultipartBody(@NonNull MediaType mediaType, Map<String, String> params,
                                                    @NonNull String fileKey, LinkedHashMap<File, Object[]> files) {
        if (files == null || files.isEmpty()) return null;
        MultipartBody.Builder builder = new MultipartBody.Builder();
        // 携带参数
        if(params!=null&&!params.isEmpty()){
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }
        for (Map.Entry<File, Object[]> entry : files.entrySet()) {
            File file = entry.getKey();
            OnProgressListener progressListener = (OnProgressListener) entry.getValue()[0];
            String tag = (String) entry.getValue()[1];
            builder.addFormDataPart(fileKey, file.getName(), createFileBody(mediaType, file, progressListener, tag));
        }
        builder.setType(MultipartBody.FORM);
        return builder.build();
    }

    private static String obj2Str(Object obj) {
        if (sGson == null) {
            sGson = new Gson();
        }
        return sGson.toJson(obj);
    }
}
