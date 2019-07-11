package pers.liyi.bullet.utils.box;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class ImageUtils {

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////  drawable, bitmap, byte 之间相互转换
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * drawable 转 bitmap
     *
     * @param drawable The drawable.
     * @return bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) return null;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        Bitmap bitmap;
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1,
                    drawable.getOpacity() != PixelFormat.OPAQUE
                            ? Bitmap.Config.ARGB_8888
                            : Bitmap.Config.RGB_565);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    drawable.getOpacity() != PixelFormat.OPAQUE
                            ? Bitmap.Config.ARGB_8888
                            : Bitmap.Config.RGB_565);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * bitmap 转 drawable
     *
     * @param context
     * @param bitmap  The bitmap
     * @return drawable
     */
    public static Drawable bitmapToDrawable(@NonNull Context context, Bitmap bitmap) {
        return bitmap == null ? null : new BitmapDrawable(context.getResources(), bitmap);
    }

    /**
     * drawable 转 bytes
     *
     * @param drawable The drawable.
     * @param format   bitmap 的压缩格式
     * @return bytes
     */
    public static byte[] drawableToBytes(Drawable drawable, @NonNull Bitmap.CompressFormat format) {
        return drawable == null ? null : bitmapToBytes(drawableToBitmap(drawable), format);
    }

    /**
     * bytes 转 drawable
     *
     * @param context
     * @param bytes   The bytes.
     * @return drawable
     */
    public static Drawable bytesToDrawable(@NonNull Context context, byte[] bytes) {
        return bitmapToDrawable(context, bytesToBitmap(bytes));
    }

    /**
     * bitmap 转 bytes
     *
     * @param bitmap The bitmap.
     * @param format bitmap 的压缩格式
     * @return bytes
     */
    public static byte[] bitmapToBytes(Bitmap bitmap, @NonNull Bitmap.CompressFormat format) {
        if (bitmap == null) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(format, 100, baos);
        return baos.toByteArray();
    }

    /**
     * bytes 转 bitmap.
     *
     * @param bytes The bytes.
     * @return bitmap
     */
    public static Bitmap bytesToBitmap(byte[] bytes) {
        return (bytes == null || bytes.length == 0) ?
                null : BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////   获取 bitmap
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 获取 bitmap
     *
     * @param file bitmap 文件
     * @return bitmap
     */
    public static Bitmap getBitmap(File file) {
        if (file == null) return null;
        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }

    /**
     * 获取 bitmap
     *
     * @param file       bitmap 文件
     * @param maxWidth  bitmap 的最大宽度
     * @param maxHeight bitmap 的最大高度
     * @return bitmap
     */
    public static Bitmap getBitmap(File file, int maxWidth, int maxHeight) {
        if (file == null) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
    }

    /**
     * 获取 bitmap
     *
     * @param filePath bitmap 文件的路径
     * @return bitmap
     */
    public static Bitmap getBitmap(String filePath) {
        return BitmapFactory.decodeFile(filePath);
    }

    /**
     * 获取 bitmap
     *
     * @param filePath  bitmap 文件的路径
     * @param maxWidth  bitmap 的最大宽度
     * @param maxHeight bitmap 的最大高度
     * @return bitmap
     */
    public static Bitmap getBitmap(String filePath, int maxWidth, int maxHeight) {
        if (TextUtils.isEmpty(filePath)) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 获取 bitmap
     *
     * @param is 数据流
     * @return bitmap
     */
    public static Bitmap getBitmap(InputStream is) {
        return BitmapFactory.decodeStream(is);
    }

    /**
     * 获取 bitmap
     *
     * @param is        数据流
     * @param maxWidth  bitmap 的最大宽度
     * @param maxHeight bitmap 的最大高度
     * @return bitmap
     */
    public static Bitmap getBitmap(InputStream is, int maxWidth, int maxHeight) {
        if (is == null) return null;
        byte[] bytes = input2Byte(is);
        return getBitmap(bytes, 0, maxWidth, maxHeight);
    }

    /**
     * 获取 bitmap
     *
     * @param data   字节流
     * @param offset 字节流解码的起始位置
     * @return bitmap
     */
    public static Bitmap getBitmap(final byte[] data, final int offset) {
        if (data.length == 0) return null;
        return BitmapFactory.decodeByteArray(data, offset, data.length);
    }

    /**
     * 获取 bitmap
     *
     * @param data      字节流
     * @param offset    字节流解码的起始位置
     * @param maxWidth  bitmap 的最大宽度
     * @param maxHeight bitmap 的最大高度
     * @return bitmap
     */
    public static Bitmap getBitmap(final byte[] data,
                                   final int offset,
                                   final int maxWidth,
                                   final int maxHeight) {
        if (data.length == 0) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, offset, data.length, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, offset, data.length, options);
    }

    /**
     * 获取 bitmap
     *
     * @param resId 资源 id
     * @return bitmap
     */
    public static Bitmap getBitmap(@NonNull Context context, @DrawableRes int resId) {
        Drawable drawable = ContextCompat.getDrawable(context, resId);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 获取 bitmap
     *
     * @param resId     资源 id
     * @param maxWidth  bitmap 的最大宽度
     * @param maxHeight bitmap 的最大高度
     * @return bitmap
     */
    public static Bitmap getBitmap(@NonNull Context context, @DrawableRes int resId,
                                   int maxWidth, int maxHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        final Resources resources = context.getResources();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, resId, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(resources, resId, options);
    }

    /**
     * 获取 bitmap
     *
     * @param fd 文件描述符
     * @return bitmap
     */
    public static Bitmap getBitmap(FileDescriptor fd) {
        if (fd == null) return null;
        return BitmapFactory.decodeFileDescriptor(fd);
    }

    /**
     * 获取 bitmap
     *
     * @param fd        文件描述符
     * @param maxWidth  bitmap 的最大宽度
     * @param maxHeight bitmap 的最大高度
     * @return bitmap
     */
    public static Bitmap getBitmap(FileDescriptor fd, int maxWidth, int maxHeight) {
        if (fd == null) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, null, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fd, null, options);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////  对图片的操作
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 缩放图片
     *
     * @param src       原始的 bitmap
     * @param newWidth  缩放后的宽
     * @param newHeight 缩放后的高
     * @return 缩放后的 bitmap
     */
    public static Bitmap scale(Bitmap src, int newWidth, int newHeight) {
        return scale(src, newWidth, newHeight, false);
    }

    /**
     * 缩放图片
     *
     * @param src       原始的 bitmap
     * @param newWidth  缩放后的宽
     * @param newHeight 缩放后的高
     * @param recycle   是否回收原始的 bitmap
     * @return 缩放后的 bitmap
     */
    public static Bitmap scale(Bitmap src,int newWidth, int newHeight, boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        Bitmap ret = Bitmap.createScaledBitmap(src, newWidth, newHeight, true);
        if (recycle && !src.isRecycled()) src.recycle();
        return ret;
    }

    /**
     * 缩放图片
     *
     * @param src         原始的 bitmap
     * @param wScale  宽的缩放比例
     * @param hScale 高的缩放比例
     * @return 缩放后的 bitmap
     */
    public static Bitmap scale(Bitmap src, float wScale, float hScale) {
        return scale(src, wScale, hScale, false);
    }

    /**
     * 缩放图片
     *
     * @param src     原始的 bitmap
     * @param wScale  宽的缩放比例
     * @param hScale  高的缩放比例
     * @param recycle 是否回收原始的 bitmap
     * @return 缩放后的 bitmap
     */
    public static Bitmap scale(Bitmap src, float wScale, float hScale, boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        Matrix matrix = new Matrix();
        matrix.setScale(wScale, hScale);
        Bitmap ret = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        if (recycle && !src.isRecycled()) src.recycle();
        return ret;
    }

    /**
     * 剪裁图片
     *
     * @param src    原始的 bitmap
     * @param x      剪裁起点的 x 轴坐标
     * @param y      剪裁起点的 y 轴坐标
     * @param width  剪裁后的图片的宽
     * @param height 剪裁后的图片的高
     * @return 剪裁后的 bitmap
     */
    public static Bitmap clip(Bitmap src,
                              int x, int y,
                              int width, int height) {
        return clip(src, x, y, width, height, false);
    }

    /**
     * 剪裁图片
     *
     * @param src     原始的 bitmap
     * @param x       剪裁起点的 x 轴坐标
     * @param y       剪裁起点的 y 轴坐标
     * @param width   剪裁后的图片的宽
     * @param height  剪裁后的图片的高
     * @param recycle 是否回收原始的 bitmap
     * @return 剪裁后的 bitmap
     */
    public static Bitmap clip(Bitmap src,
                              int x, int y,
                              int width, int height,
                              boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        Bitmap ret = Bitmap.createBitmap(src, x, y, width, height);
        if (recycle && !src.isRecycled()) src.recycle();
        return ret;
    }

    /**
     * 旋转图片
     *
     * @param src     原始的 bitmap
     * @param degrees 旋转角度
     * @param px      旋转中心的 x 轴坐标
     * @param py      旋转中心的 y 轴坐标
     * @return 旋转后的 bitmap
     */
    public static Bitmap rotate(Bitmap src,
                                int degrees,
                                float px, float py) {
        return rotate(src, degrees, px, py, false);
    }

    /**
     * 旋转图片
     *
     * @param src     原始的 bitmap
     * @param degrees 旋转角度
     * @param px      旋转中心的 x 轴坐标
     * @param py      旋转中心的 y 轴坐标
     * @param recycle 是否回收原始图片
     * @return 旋转后的 bitmap
     */
    public static Bitmap rotate(Bitmap src,
                                int degrees,
                                float px, float py,
                                boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        if (degrees == 0) return src;
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees, px, py);
        Bitmap ret = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        if (recycle && !src.isRecycled()) src.recycle();
        return ret;
    }

    /**
     * 获取图片旋转的角度
     *
     * @param filePath 图片文件路径
     * @return 旋转的角度
     */
    public static int getRotateDegree(@NonNull String filePath) {
        try {
            ExifInterface exifInterface = new ExifInterface(filePath);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
            );
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
                default:
                    return 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 将图片转化为圆形图
     * 
     * @param src 原始 bitmap
     * @return 圆形 bitmap
     */
    public static Bitmap toCircle(Bitmap src) {
        return toCircle(src, 0, 0, false);
    }

    /**
     * 将图片转化为圆形图
     *
     * @param src     原始 bitmap
     * @param recycle 是否回收原始 bitmap
     * @return 圆形 bitmap
     */
    public static Bitmap toCircle(Bitmap src, final boolean recycle) {
        return toCircle(src, 0, 0, recycle);
    }

    /**
     * 将图片转化为圆形图
     *
     * @param src         原始 bitmap
     * @param borderSize  图片的边的宽度
     * @param borderColor 图片的边的颜色
     * @return 圆形 bitmap
     */
    public static Bitmap toCircle(Bitmap src, @IntRange(from = 0) int borderSize, @ColorInt int borderColor) {
        return toCircle(src, borderSize, borderColor, false);
    }

    /**
     * 将图片转化为圆形图
     *
     * @param src         原始 bitmap
     * @param borderSize  图片的边的宽度
     * @param borderColor 图片的边的颜色
     * @param recycle    是否回收原始 bitmap
     * @return 圆形 bitmap
     */
    public static Bitmap toCircle(Bitmap src,
                                 @IntRange(from = 0) int borderSize,
                                 @ColorInt int borderColor,
                                 boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        int width = src.getWidth();
        int height = src.getHeight();
        int size = Math.min(width, height);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Bitmap ret = Bitmap.createBitmap(width, height, src.getConfig());
        float center = size / 2f;
        RectF rectF = new RectF(0, 0, width, height);
        rectF.inset((width - size) / 2f, (height - size) / 2f);
        Matrix matrix = new Matrix();
        matrix.setTranslate(rectF.left, rectF.top);
        matrix.preScale((float) size / width, (float) size / height);
        BitmapShader shader = new BitmapShader(src, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        shader.setLocalMatrix(matrix);
        paint.setShader(shader);
        Canvas canvas = new Canvas(ret);
        canvas.drawRoundRect(rectF, center, center, paint);
        if (borderSize > 0) {
            paint.setShader(null);
            paint.setColor(borderColor);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(borderSize);
            float radius = center - borderSize / 2f;
            canvas.drawCircle(width / 2f, height / 2f, radius, paint);
        }
        if (recycle && !src.isRecycled()) src.recycle();
        return ret;
    }

    /**
     * 将图片转为圆角图片
     *
     * @param src    原始 bitmap
     * @param radius 圆角半径
     * @return 圆角 bitmap
     */
    public static Bitmap toRound(Bitmap src, float radius) {
        return toRound(src, radius, 0, 0, false);
    }

    /**
     * 将图片转为圆角图片
     *
     * @param src     原始 bitmap
     * @param radius  圆角半径
     * @param recycle 是否回收原始 bitmap
     * @return 圆角 bitmap
     */
    public static Bitmap toRound(Bitmap src,
                                       float radius,
                                       boolean recycle) {
        return toRound(src, radius, 0, 0, recycle);
    }

    /**
     * 将图片转为圆角图片
     *
     * @param src         原始 bitmap
     * @param radius      圆角半径
     * @param borderSize  图片的边的宽度
     * @param borderColor 图片的边的颜色
     * @return 圆角 bitmap
     */
    public static Bitmap toRound(Bitmap src,
                                       float radius,
                                       @IntRange(from = 0) int borderSize,
                                       @ColorInt int borderColor) {
        return toRound(src, radius, borderSize, borderColor, false);
    }

    /**
     * 将图片转为圆角图片
     *
     * @param src         原始 bitmap
     * @param radius       圆角半径
     * @param borderSize   图片的边的宽度
     * @param borderColor 图片的边的颜色
     * @param recycle     是否回收原始 bitmap
     * @return 圆角 bitmap
     */
    public static Bitmap toRound(Bitmap src,
                                       float radius,
                                       @IntRange(from = 0) int borderSize,
                                       @ColorInt int borderColor,
                                       boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        int width = src.getWidth();
        int height = src.getHeight();
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Bitmap ret = Bitmap.createBitmap(width, height, src.getConfig());
        BitmapShader shader = new BitmapShader(src, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        Canvas canvas = new Canvas(ret);
        RectF rectF = new RectF(0, 0, width, height);
        float halfBorderSize = borderSize / 2f;
        rectF.inset(halfBorderSize, halfBorderSize);
        canvas.drawRoundRect(rectF, radius, radius, paint);
        if (borderSize > 0) {
            paint.setShader(null);
            paint.setColor(borderColor);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(borderSize);
            paint.setStrokeCap(Paint.Cap.ROUND);
            canvas.drawRoundRect(rectF, radius, radius, paint);
        }
        if (recycle && !src.isRecycled()) src.recycle();
        return ret;
    }
    
    /**
     * 从图片中提取出只包含 alpha 的位图（其实提取出来的就是一个图片的轮廓，没有了原图中的填充色）
     *
     * @param src 原始 bitmap
     * @return alpha 位图
     */
    public static Bitmap toAlpha(Bitmap src) {
        return toAlpha(src, false);
    }

    /**
     * 从图片中提取出只包含 alpha 的位图（其实提取出来的就是一个图片的轮廓，没有了原图中的填充色）
     *
     * @param src     原始 bitmap
     * @param recycle 是否回收原始 bitmap
     * @return talpha 位图
     */
    public static Bitmap toAlpha(Bitmap src, Boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        Bitmap ret = src.extractAlpha();
        if (recycle && !src.isRecycled()) src.recycle();
        return ret;
    }

    /**
     * 图片灰化
     *
     * @param src 原始 bitmap
     * @return 灰化后的图片
     */
    public static Bitmap toGray(Bitmap src) {
        return toGray(src, false);
    }

    /**
     * 图片灰化
     *
     * @param src     原始 bitmap
     * @param recycle 是否回收原 bitmap
     * @return 灰化后的图片
     */
    public static Bitmap toGray(Bitmap src, boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        Bitmap ret = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        Canvas canvas = new Canvas(ret);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter colorMatrixColorFilter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(colorMatrixColorFilter);
        canvas.drawBitmap(src, 0, 0, paint);
        if (recycle && !src.isRecycled()) src.recycle();
        return ret;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////  图片压缩相关
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 使用缩放来压缩图片
     *
     * @param src       原始图片
     * @param newWidth  压缩后的图片宽度
     * @param newHeight 压缩后的图片高度
     * @return 压缩后的图片
     */
    public static Bitmap compressByScale(Bitmap src, int newWidth, int newHeight) {
        return scale(src, newWidth, newHeight, false);
    }

    /**
     * 使用缩放来压缩图片
     *
     * @param src       原始图片
     * @param newWidth  压缩后的图片宽度
     * @param newHeight 压缩后的图片高度
     * @param recycle   是否回收原始的图片
     * @return 压缩后的图片
     */
    public static Bitmap compressByScale(Bitmap src, int newWidth, int newHeight, boolean recycle) {
        return scale(src, newWidth, newHeight, recycle);
    }

    /**
     * 使用缩放来压缩图片
     *
     * @param src     原始图片
     * @param wScale  图片宽度的缩放比例
     * @param hScale  图片高度的缩放比例
     * @return 压缩后的图片
     */
    public static Bitmap compressByScale(Bitmap src, float wScale, float hScale) {
        return scale(src, wScale, hScale, false);
    }

    /**
     * 使用缩放来压缩图片
     *
     * @param src     原始图片
     * @param wScale  图片宽度的缩放比例
     * @param hScale 图片高度的缩放比例
     * @param recycle  是否回收原始的图片
     * @return 压缩后的图片
     */
    public static Bitmap compressByScale(Bitmap src, float wScale, float hScale, boolean recycle) {
        return scale(src, wScale, hScale, recycle);
    }

    /**
     * 图片质量压缩
     *
     * @param src     原始图片
     * @param quality 图片质量
     * @return 压缩后的图片
     */
    public static Bitmap compressByQuality(Bitmap src, @IntRange(from = 0, to = 100) int quality) {
        return compressByQuality(src, quality, false);
    }

    /**
     * 图片质量压缩
     *
     * @param src     原始图片
     * @param quality 图片质量
     * @param recycle 是否回收原始图片
     * @return 压缩后的图片
     */
    public static Bitmap compressByQuality(Bitmap src, @IntRange(from = 0, to = 100) int quality,
                                           boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        byte[] bytes = baos.toByteArray();
        if (recycle && !src.isRecycled()) src.recycle();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * 图片质量压缩
     *
     * @param src         原始图片
     * @param maxByteSize 图片的最大大小
     * @return 压缩后的图片
     */
    public static Bitmap compressByQuality(Bitmap src, long maxByteSize) {
        return compressByQuality(src, maxByteSize, false);
    }

    /**
     * 图片质量压缩
     *
     * @param src         原始图片
     * @param maxByteSize 图片的最大大小
     * @param recycle    是否回收原始图片
     * @return 压缩后的图片
     */
    public static Bitmap compressByQuality(Bitmap src, long maxByteSize, boolean recycle) {
        if (isEmptyBitmap(src) || maxByteSize <= 0) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes;
        if (baos.size() <= maxByteSize) {
            bytes = baos.toByteArray();
        } else {
            baos.reset();
            src.compress(Bitmap.CompressFormat.JPEG, 0, baos);
            if (baos.size() >= maxByteSize) {
                bytes = baos.toByteArray();
            } else {
                // 找寻最符合期望的 quality 值
                int st = 0;
                int end = 100;
                int mid = 0;
                while (st < end) {
                    mid = (st + end) / 2;
                    baos.reset();
                    src.compress(Bitmap.CompressFormat.JPEG, mid, baos);
                    int len = baos.size();
                    if (len == maxByteSize) {
                        break;
                    } else if (len > maxByteSize) {
                        end = mid - 1;
                    } else {
                        st = mid + 1;
                    }
                }
                if (end == mid - 1) {
                    baos.reset();
                    src.compress(Bitmap.CompressFormat.JPEG, st, baos);
                }
                bytes = baos.toByteArray();
            }
        }
        if (recycle && !src.isRecycled()) src.recycle();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * 通过采样率压缩图片
     *
     * @param src        原始图片
     * @param sampleSize 采样率
     * @return 压缩后的图片
     */

    public static Bitmap compressBySampleSize(Bitmap src, int sampleSize) {
        return compressBySampleSize(src, sampleSize, false);
    }

    /**
     * 通过采样率压缩图片
     *
     * @param src        原始图片
     * @param sampleSize 采样率
     * @param recycle    是否回收原图
     * @return 压缩后的图片
     */
    public static Bitmap compressBySampleSize(Bitmap src, int sampleSize, boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        if (recycle && !src.isRecycled()) src.recycle();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }

    /**
     * 通过采样率压缩图片
     *
     * @param src       原始图片
     * @param maxWidth  压缩后图片的最大宽度
     * @param maxHeight 压缩后图片的最大高度
     * @return 压缩后的图片
     */
    public static Bitmap compressBySampleSize(Bitmap src, int maxWidth, int maxHeight) {
        return compressBySampleSize(src, maxWidth, maxHeight, false);
    }

    /**
     * 通过采样率压缩图片
     *
     * @param src       原始图片
     * @param maxWidth  压缩后图片的最大宽度
     * @param maxHeight 压缩后图片的最大高度
     * @param recycle   是否回收原图
     * @return 压缩后的图片
     */
    public static Bitmap compressBySampleSize(Bitmap src, int maxWidth, int maxHeight, boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        if (recycle && !src.isRecycled()) src.recycle();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////  私有方法
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static boolean isEmptyBitmap(Bitmap src) {
        return src == null || src.getWidth() == 0 || src.getHeight() == 0;
    }

    private static int calculateInSampleSize(@NonNull BitmapFactory.Options options, int maxWidth, int maxHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        while ((width >>= 1) >= maxWidth && (height >>= 1) >= maxHeight) {
            inSampleSize <<= 1;
        }
        return inSampleSize;
    }

    private static byte[] input2Byte(InputStream is) {
        if (is == null) return null;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int len;
            while ((len = is.read(b, 0, 1024)) != -1) {
                os.write(b, 0, len);
            }
            return os.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static File getFileByPath(String filePath) {
        return TextUtils.isEmpty(filePath) ? null : new File(filePath);
    }

    private static boolean createFileByDeleteOldFile(File file) {
        if (file == null) return false;
        if (file.exists() && !file.delete()) return false;
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean createOrExistsDir(File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }
}
