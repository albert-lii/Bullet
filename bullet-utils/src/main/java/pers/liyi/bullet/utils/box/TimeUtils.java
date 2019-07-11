package pers.liyi.bullet.utils.box;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class TimeUtils {
    private static final String TAG = "Bullet-" + TimeUtils.class.getClass().getSimpleName();
    private static final String DEF_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 将时间戳转换为时间字符串
     *
     * @param millis 时间戳
     * @return 时间字符串
     */
    public static String millisToStr(long millis) {
        return millisToStr(millis, DEF_FORMAT);
    }

    /**
     * 将时间戳转换为时间字符串
     *
     * @param millis 时间戳
     * @param format 时间格式
     * @return 时间字符串
     */
    public static String millisToStr(long millis, @Nullable String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(millis));
    }

    /**
     * 将时间字符串转换为时间戳
     *
     * @param timeStr 时间字符串
     * @return 时间戳
     */
    public static long strToMillis(String timeStr) {
        return TextUtils.isEmpty(timeStr) ? 0 : strToMillis(timeStr, DEF_FORMAT);
    }

    /**
     * 将时间字符串转换为时间戳
     *
     * @param timeStr 时间字符串
     * @param format  时间格式
     * @return 时间戳
     */
    public static long strToMillis(String timeStr, @Nullable String format) {
        if (TextUtils.isEmpty(timeStr)) return 0;
        long millis = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date date = sdf.parse(timeStr);
            millis = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            LogUtils.e(TAG, "StringToMillis Error ===> timeStr: " + timeStr);
        }
        return millis;
    }

    /**
     * 将时间字符串转换为 date
     *
     * @param timeStr 时间字符串
     * @return date
     */
    public static Date strToDate(String timeStr) {
        return TextUtils.isEmpty(timeStr) ? null : strToDate(timeStr, DEF_FORMAT);
    }

    /**
     * 将时间字符串转换为 date
     *
     * @param timeStr 时间字符串
     * @param format  时间格式
     * @return date
     */
    public static Date strToDate(String timeStr, @Nullable String format) {
        if (TextUtils.isEmpty(timeStr)) return null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将 date 转换为时间字符串
     *
     * @param date date
     * @return 时间字符串
     */
    public static String dateToStr(Date date) {
        return date == null ? null : dateToStr(date, DEF_FORMAT);
    }

    /**
     * 将 date 转换为时间字符串
     *
     * @param date   date
     * @param format 时间格式
     * @return 时间字符串
     */
    public static String dateToStr(Date date, @Nullable String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 将 date 转换为时间戳
     *
     * @param date date
     * @return 时间戳
     */
    public static long dateToMillis(Date date) {
        return date == null ? null : date.getTime();
    }

    /**
     * 将时间戳转换为 date
     *
     * @param millis The timestamp
     * @return the date
     */
    public static Date millisToDate(long millis) {
        return new Date(millis);
    }

    /**
     * 将时间字符串转换为天数、小时数、分钟数、秒数
     *
     * @param timeStr 时间字符串
     * @return int[0]: 天数 <br> int[1]: 小时数 <br> int[2]: 分钟数 <br> int[3]: 秒数
     */
    public static int[] strToArray(String timeStr) {
        return strToArray(timeStr, DEF_FORMAT);
    }

    /**
     * 将时间字符串转换为天数、小时数、分钟数、秒数
     *
     * @param timeStr 时间字符串
     * @param format  时间格式
     * @return int[0]: 天数 <br> int[1]: 小时数 <br> int[2]: 分钟数 <br> int[3]: 秒数
     */
    public static int[] strToArray(String timeStr, @Nullable String format) {
        return TextUtils.isEmpty(timeStr) ? null : millisToArray(strToMillis(timeStr, format));
    }

    /**
     * 将 date 转换为天数、小时数、分钟数、秒数
     *
     * @param date date
     * @return int[0]: 天数 <br> int[1]: 小时数 <br> int[2]: 分钟数 <br> int[3]: 秒数
     */
    public static int[] dateToArray(Date date) {
        return date == null ? null : millisToArray(date.getTime());
    }

    /**
     * 将时间戳转换为天数、小时数、分钟数、秒数
     *
     * @param millis 时间戳
     * @return int[0]: 天数 <br> int[1]: 小时数 <br> int[2]: 分钟数 <br> int[3]: 秒数
     */
    public static int[] millisToArray(long millis) {
        long secondDiff = millis / 1000;
        int days = (int) (secondDiff / (60 * 60 * 24));
        int hours = (int) ((secondDiff - days * (60 * 60 * 24)) / (60 * 60));
        int minutes = (int) ((secondDiff - days * (60 * 60 * 24) - hours * (60 * 60)) / 60);
        int seconds = (int) ((secondDiff - days * (60 * 60 * 24) - hours * (60 * 60) - minutes * 60));
        return new int[]{days, hours, minutes, seconds};
    }

    /**
     * 计算两个时间之间的时间差
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 时间差（单位：毫秒）
     */
    public static long calcTimeDiff(@NonNull Object startTime, @NonNull Object endTime) {
        return calcTimeDiff(startTime, endTime, DEF_FORMAT);
    }

    /**
     * 计算两个时间之间的时间差
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param foramt    时间格式
     * @return 时间差（单位：毫秒）
     */
    public static long calcTimeDiff(@NonNull Object startTime, @NonNull Object endTime, @Nullable String foramt) {
        long milliStart, milliEnd;
        if (startTime instanceof String) {
            milliStart = strToMillis((String) startTime, foramt);
        } else if (startTime instanceof Long || startTime instanceof Integer) {
            milliStart = (long) startTime;
        } else if (startTime instanceof Date) {
            milliStart = ((Date) startTime).getTime();
        } else {
            LogUtils.e(TAG, "Error startTime in the calcTimeDiff() method ===> startTime: " + startTime);
            throw new UnsupportedOperationException("startTime foramt error");
        }
        if (endTime instanceof String) {
            milliEnd = strToMillis((String) endTime, foramt);
        } else if (endTime instanceof Long || startTime instanceof Integer) {
            milliEnd = (long) endTime;
        } else if (endTime instanceof Date) {
            milliEnd = ((Date) endTime).getTime();
        } else {
            LogUtils.e(TAG, "Error endTime in the calcTimeDiff() method ===> endTime: " + endTime);
            throw new UnsupportedOperationException("endTime foramt error");
        }
        return (milliEnd - milliStart);
    }

    /**
     * 计算两个时间之间的时间差
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return int[0]: 天数 <br> int[1]: 小时数 <br> int[2]: 分钟数 <br> int[3]: 秒数
     */
    public static int[] calcTimeDiffArray(@NonNull Object startTime, @NonNull Object endTime) {
        return calcTimeDiffArray(startTime, endTime, DEF_FORMAT);
    }

    /**
     * 计算两个时间之间的时间差
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param format    时间格式
     * @return int[0]: 天数 <br> int[1]: 小时数 <br> int[2]: 分钟数 <br> int[3]: 秒数
     */
    public static int[] calcTimeDiffArray(@NonNull Object startTime, @NonNull Object endTime, @Nullable String format) {
        return millisToArray(calcTimeDiff(startTime, endTime, format));
    }

    /**
     * 对比两个时间的大小
     *
     * @param t1
     * @param t2
     * @return {@code true}: t1 >= t2 <br> {@code false}: t1 < t2
     */
    public static boolean compareTime(@NonNull Object t1, @NonNull Object t2) {
        return calcTimeDiff(t2, t1) >= 0;
    }
}
