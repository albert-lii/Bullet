package pers.liyi.bullet.utils.box;


import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

import androidx.annotation.NonNull;


public class FileUtils {
    private final static String TAG = "Bullet-" + FileUtils.class.getSimpleName();

    public static FileUtils getInstance() {
        return FileUtilHolder.INSTANCE;
    }

    private static class FileUtilHolder {
        private static final FileUtils INSTANCE = new FileUtils();
    }

    /**
     * 判断是否是一个文件
     *
     * @param filePath 路径
     * @return {@code true}: 是 <br> {@code false}: 否
     */
    public static boolean isFile(String filePath) {
        return isFile(new File(filePath));
    }

    /**
     * 判断是否是一个文件
     *
     * @param file File
     * @return {@code true}: 是 <br> {@code false}: 否
     */
    public static boolean isFile(File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        return file.isFile();
    }

    /**
     * 判断是否是一个文件夹
     *
     * @param dirPath 路径
     * @return {@code true}: 是 <br>{ @code false}: 否
     */
    public static boolean isDir(String dirPath) {
        return isDir(new File(dirPath));
    }

    /**
     * 判断是否是一个文件夹
     *
     * @param dir File
     * @return {@code true}: 是 <br> {@code false}: 否
     */
    public static boolean isDir(File dir) {
        if (dir == null || !dir.exists()) {
            return false;
        }
        return dir.isDirectory();
    }

    /**
     * 创建一个文件夹
     *
     * @param path
     * @return {@code true}: 创建成功 <br> {@code false}: 创建失败
     */
    public boolean createDir(String path) {
        if (TextUtils.isEmpty(path)) return false;
        boolean isSuccess;
        File file = new File(path);
        if (!file.exists()) {
            // 如果文件夹已经存在，执行 mkdirs() 方法会返回 false，所以前面需要加个文件夹是否已经存在的判断
            isSuccess = file.mkdirs();
        } else {
            isSuccess = true;
        }
        return isSuccess;
    }

    /**
     * 创建一个文件
     * <br>
     * 在创建一个文件之前，必须先创建一个文件夹。否则会报“不能找到路径”
     *
     * @param path
     * @return {@code true}: 创建成功 <br> {@code false}: 创建失败
     */
    public boolean createFile(String path) {
        if (TextUtils.isEmpty(path)) return false;
        boolean isSuccess = false;
        File file = new File(path);
        if (!file.exists()) {
            try {
                // 如果文件已经存在，执行 createNewFile() 方法会返回 false，所以前面需要加个文件是否已经存在的判断
                isSuccess = file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            isSuccess = true;
        }
        return isSuccess;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////  IO 操作
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 保存 string 数据
     *
     * @param key   存储 string 数据的 file 的 key
     * @param value string 数据
     * @throws IOException
     */
    public void put(@NonNull String key, String value) {
        File file = new File(key);
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(file), 1024);
            out.write(value);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 读取 string 数据
     *
     * @param key 存储 string 数据的 file 的 key
     * @return 存储的 string 数据
     */
    public String getAsString(@NonNull String key) {
        File file = new File(key);
        if (!file.exists()) {
            return null;
        }
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            String readString = "";
            String currentLine;
            while ((currentLine = in.readLine()) != null) {
                readString += currentLine;
            }
            return readString;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 存储 byte 数据
     *
     * @param key 存储 byte 数据的 file 的 key
     * @param value byte 数据
     */
    public void put(@NonNull String key, byte[] value) {
        File file = new File(key);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(value);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 读取 byte 数据
     *
     * @param key 存储 byte 数据的 file 的 key
     * @return byte 数据
     */
    public byte[] getAsBinary(@NonNull String key) {
        File file = new File(key);
        if (!file.exists()) {
            return null;
        }
        byte[] byteArray = null;
        FileInputStream fis = null;
        ByteArrayOutputStream baos = null;
        try {
            fis = new FileInputStream(file);
            baos = new ByteArrayOutputStream(1024);
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                baos.write(b, 0, n);
            }
            byteArray = baos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (baos != null) {
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return byteArray;
    }

    /**
     * 保存序列化数据
     *
     * @param key 存储序列化数据的 file 的 key
     * @param value 序列化数据
     */
    public void put(@NonNull String key, Serializable value) {
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(value);
            byte[] data = baos.toByteArray();
            put(key, data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取序列化数据
     *
     * @param key 存储序列化数据的 file 的 key
     * @return 序列化数据
     */
    public Object getAsObject(@NonNull String key) {
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;
        try {
            byte[] data = getAsBinary(key);
            if (data == null || data.length == 0) return null;
            bais = new ByteArrayInputStream(data);
            ois = new ObjectInputStream(bais);
            Object obj = ois.readObject();
            return obj;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (bais != null) {
                try {
                    bais.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 保存 bitmap
     *
     * @param key
     * @param value
     */
    public void put(@NonNull String key, Bitmap value) {
        put(key, bitmap2Byte(value));
    }

    /**
     * 读取 bitmap
     *
     * @param key
     * @return
     */
    public Bitmap getAsBitmap(@NonNull String key) {
        if (getAsBinary(key) == null) return null;
        return byte2Bitmap(getAsBinary(key));
    }

    private byte[] bitmap2Byte(Bitmap bmp) {
        if (bmp == null) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    private Bitmap byte2Bitmap(byte[] bytes) {
        if (bytes.length == 0) {
            return null;
        }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////  拷贝文件和文件夹到指定路径
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 拷贝文件到指定路径
     *
     * @param oldPath 被拷贝文件的路径
     * @param newPath 指定路径
     */
    public void copyFile(@NonNull String oldPath, @NonNull String newPath) {
        int bytesum = 0;
        int byteread = 0;
        File oldfile = new File(oldPath);
        FileInputStream fis = null;
        FileOutputStream fos = null;
        if (oldfile.exists()) {
            try {
                fis = new FileInputStream(oldPath);
                fos = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024];
                while ((byteread = fis.read(buffer)) != -1) {
                    bytesum += byteread;
                    fos.write(buffer, 0, byteread);
                }
                LogUtils.d(TAG, "Copy file success, the total size of the file is ===> " + bytesum + " byte");
            } catch (IOException e) {
                LogUtils.e(TAG, "Copy file error ===> " + e.toString());
                e.printStackTrace();
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fos != null) {
                    try {
                        fos.flush();
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            LogUtils.e(TAG, "Copy file error ===> source file does not exist!");
        }
    }

    /**
     * 拷贝文件夹到指定路径
     *
     * @param oldPath 被拷贝文件夹的路径
     * @param newPath 指定路径
     */
    public void copyDir(@NonNull String oldPath, @NonNull String newPath) {
        try {
            (new File(newPath)).mkdirs();
            File oldDir = new File(oldPath);
            String[] fileNameList = oldDir.list();
            File temp = null;
            for (int i = 0; i < fileNameList.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + fileNameList[i]);
                } else {
                    temp = new File(oldPath + File.separator + fileNameList[i]);
                }
                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = null;
                    if (newPath.endsWith(File.separator)) {
                        output = new FileOutputStream(newPath + (temp.getName()).toString());
                    } else {
                        output = new FileOutputStream(newPath + File.separator + (temp.getName()).toString());
                    }
                    byte[] b = new byte[1024];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                } else {
                    String op = null, np = null;
                    if (oldPath.endsWith(File.separator)) {
                        op = oldPath + fileNameList[i];
                    } else {
                        op = oldPath + File.separator + fileNameList[i];
                    }
                    if (newPath.endsWith(File.separator)) {
                        np = newPath + fileNameList[i];
                    } else {
                        np = newPath + File.separator + fileNameList[i];
                    }
                    copyDir(op, np);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取指定文件夹中的文件个数
     *
     * @param dir   文件夹的路径
     * @param isAll {@code true}: 获取所有的文件个数 <br> {@code false}: 仅仅获取第一级文件夹中的文件个数
     * @return 文件个数
     */
    public int getFileCount(String dir, boolean isAll) {
        if (TextUtils.isEmpty(dir)) return 0;
        int count = 0;
        if (!dir.endsWith(File.separator)) {
            dir = dir + File.separator;
        }
        File dirFile = new File(dir);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return count;
        }
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                count += 1;
            } else {
                if (isAll) {
                    getFileCount(files[i].getAbsolutePath(), true);
                }
            }
        }
        return count;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////  删除文件
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 删除一个文件和文件夹
     *
     * @param path 文件或者文件夹的路径
     * @return {@code true}: 删除成功 <br>{ @code false}: 删除失败
     */
    public boolean delete(String path) {
        return TextUtils.isEmpty(path) ? true : delete(new File(path));
    }

    /**
     * 删除一个文件和文件夹
     *
     * @param file 文件或者文件夹
     * @return {@code true}: 删除成功 <br> {@code false}: 删除失败
     */
    public boolean delete(File file) {
        if (file == null) return true;
        try {
            if (file.exists()) {
                if (file.isFile()) {
                    return deleteFile(file);
                } else {
                    return deleteDir(file);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, "Failed to delete file ===> " + e.getMessage());
        }
        return false;
    }

    /**
     * 删除一个文件
     *
     * @param path
     * @return {@code true}: 删除成功 <br> {@code false}: 删除失败
     */
    public boolean deleteFile(String path) {
        return TextUtils.isEmpty(path) ? true : deleteFile(new File(path));
    }

    /**
     * 删除一个文件
     *
     * @param file
     * @return {@code true}: 删除成功 <br> {@code false}: 删除失败
     */
    public boolean deleteFile(File file) {
        if (file != null && file.exists() && file.isFile()) {
            return file.delete();
        } else {
            return true;
        }
    }

    /**
     * 删除一个文件夹
     *
     * @param dirPath
     * @return {@code true}: 删除成功 <br> {@code false}: 删除失败
     */
    public boolean deleteDir(String dirPath) {
        if (TextUtils.isEmpty(dirPath)) return true;
        // 如果文件夹路径不是以文件分隔符结尾，请在文件夹路径末尾自动添加文件分隔符
        if (!dirPath.endsWith(File.separator)) {
            dirPath = dirPath + File.separator;
        }
        return deleteDir(new File(dirPath));
    }

    /**
     * 删除一个文件夹
     *
     * @param fileDir 文件夹
     * @return {@code true}: 删除成功 <br> {@code false}: 删除失败
     */
    public boolean deleteDir(File fileDir) {
        if (fileDir == null || !fileDir.exists() || !fileDir.isDirectory()) {
            return true;
        }
        boolean isSuccess = true;
        File[] files = fileDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 如果是个文件，就删除文件
            if (files[i].isFile()) {
                isSuccess = deleteFile(files[i]);
                if (!isSuccess) break;
            }
            // 如果是个文件夹，就删除文件夹
            else if (files[i].isDirectory()) {
                isSuccess = deleteDir(files[i]);
                if (!isSuccess) break;
            }
        }
        if (!isSuccess) return false;
        return fileDir.delete();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////  获取文件或者文件夹的大小
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 获取指定文件或者文件夹的大小
     */
    public long getFileSize(String path) {
        return TextUtils.isEmpty(path) ? 0 : getFileSize(new File(path));
    }

    /**
     * 获取指定文件或者文件夹的大小
     */
    public long getFileSize(File file) {
        if (file == null) return 0;
        long size = 0;
        try {
            if (file.exists()) {
                if (file.isFile()) {
                    size += getSingleFileSize(file);
                } else {
                    size += getFileDirSize(file);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, "Failed to get the specified file size ===> " + e.getMessage());
        }
        return size;
    }

    /**
     * 获取指定文件的大小
     */
    public long getSingleFileSize(String path) {
        return TextUtils.isEmpty(path) ? 0 : getSingleFileSize(new File(path));
    }

    /**
     * 获取指定文件的大小
     */
    public long getSingleFileSize(File file) {
        if (file == null) return 0;
        long size = 0;
        try {
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                size = fis.available();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 获取指定文件夹的大小
     */
    public long getFileDirSize(String dir) {
        return TextUtils.isEmpty(dir) ? 0 : getFileDirSize(new File(dir));
    }

    /**
     * 获取指定文件夹的大小
     */
    public long getFileDirSize(File dir) {
        if (dir == null) return 0;
        long size = 0;
        File flist[] = dir.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileDirSize(flist[i]);
            } else {
                size = size + getSingleFileSize(flist[i]);
            }
        }
        return size;
    }

    /**
     * 转化文件大小
     *
     * @param size
     * @return 指定单位的文件大小
     */
    public static String convertFileSize(final long size) {
        final long kb = 1024;
        final long mb = kb * 1024;
        final long gb = mb * 1024;

        if (size >= gb) {
            return String.format("%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else
            return String.format("%d B", size);
    }

    /**
     * 重命名文件
     *
     * @param filePath 被重命名的文件路径
     * @param newName  文件的新名字
     * @return {@code true}: 重命名成功 <br> {@code false}: 重命名失败
     */
    public static boolean rename(final String filePath, final String newName) {
        if (TextUtils.isEmpty(filePath)) return false;
        final File file = new File(filePath);
        return rename(file, newName);
    }

    /**
     * 重命名文件
     *
     * @param file    被重命名的文件
     * @param newName 文件的新名字
     * @return {@code true}: 重命名成功 <br>{@code false}: 重命名失败
     */
    public static boolean rename(final File file, final String newName) {
        if (file == null) return false;
        if (!file.exists()) return false;
        if (TextUtils.isEmpty(newName)) return false;
        if (newName.equals(file.getName())) return true;
        File newFile = new File(file.getParent() + File.separator + newName);
        return !newFile.exists() && file.renameTo(newFile);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////  获取文件的真实路径
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 从 uri 中获取文件的真实路径
     */
    public String getRealPath(@NonNull Context context, @NonNull Uri uri) {
        final String path = getFilePathFromUri(context, uri);
        if (TextUtils.isEmpty(path)) {
            return getFilePathFromUriByCopy(context, uri);
        } else {
            return path;
        }
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.<br>
     * <br>
     * Callers should check whether the path is local before assuming it
     * represents a local file.
     *
     * @param uri The Uri to query.l
     */
    public String getFilePathFromUri(@NonNull Context context, @NonNull Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                try {
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    return getDataColumn(context, contentUri, null, null);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
//                final Uri contentUri = ContentUris.withAppendedId(
//                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
//                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public String getDataColumn(@NonNull Context context, Uri uri, String selection, String[] selectionArgs) {
        String value = null;
        Cursor cursor = null;
        final String column = MediaStore.Images.Media.DATA;
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                value = cursor.getString(column_index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            return value;
        }
    }

    /**
     * 通过拷贝文件到指定路径的方式，从而获取 file 的真实路径
     * 此方法是当 {@link #getFilePathFromUri } 方法获取不到真实路径时，所执行的补充方案
     */
    public static String getFilePathFromUriByCopy(@NonNull Context context, @NonNull Uri contentUri) {
        File rootDataDir = context.getFilesDir();
        String fileName = getFileName(contentUri);
        if (!TextUtils.isEmpty(fileName)) {
            File copyFile = new File(rootDataDir + File.separator + fileName);
            copyFile(context, contentUri, copyFile);
            return copyFile.getAbsolutePath();
        }
        return null;
    }

    public static String getFileName(Uri uri) {
        if (uri == null) return null;
        String fileName = null;
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
        return fileName;
    }

    private static void copyFile(@NonNull Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(dstFile);
            copyStream(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int copyStream(InputStream input, OutputStream output) throws Exception, IOException {
        final int BUFFER_SIZE = 1024 * 2;
        byte[] buffer = new byte[BUFFER_SIZE];
        BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
        BufferedOutputStream out = new BufferedOutputStream(output, BUFFER_SIZE);
        int count = 0, n = 0;
        try {
            while ((n = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
                out.write(buffer, 0, n);
                count += n;
            }
            out.flush();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
            }
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return count;
    }
}
