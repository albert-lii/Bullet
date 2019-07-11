package pers.liyi.bullet.utils.box;


import androidx.annotation.IntRange;


public class StringUtils {

    /**
     * 判断字符串是否为空
     */
    public static boolean isEmpty(CharSequence s) {
        return s == null || s.length() == 0;
    }

    /**
     * 去除左右两边空格后，判断字符串是否为空
     */
    public static boolean isEmptyByTrim(CharSequence s) {
        return s == null || s.toString().trim().length() == 0;
    }

    /**
     * 截取指定位置的字符串
     */
    public static String cut(String s, @IntRange(from = 0) int start) {
        if (isEmpty(s)) {
            return null;
        } else {
            return s.substring(start);
        }
    }

    /**
     * 截取指定位置的字符串
     *
     * @param s
     * @param start 被截取的字符的开始位置
     * @param count 被截取的字符个数
     * @return
     */
    public static String cut(String s, @IntRange(from = 0) int start, @IntRange(from = 0) int count) {
        if (isEmpty(s) || count == 0) {
            return null;
        } else {
            return s.substring(start, start + count);
        }
    }

    /**
     * 移除指定位置的字符串
     *
     * @param s
     * @param start 要移除的字符串的开始位置
     * @param count 移除的字符的个数
     * @return
     */
    public static String remove(String s, @IntRange(from = 0) int start, @IntRange(from = 0) int count) {
        if (isEmpty(s) || count == 0) {
            return s;
        } else {
            int len = s.length();
            if (len <= start) {
                throw new StringIndexOutOfBoundsException(start);
            }
            if (len < (start + count)) {
                throw new StringIndexOutOfBoundsException(start + count);
            }
            String str1 = (start == 0 ? "" : s.substring(0, start));
            String str2 = ((start + count) == len ? "" : s.substring(start + count, s.length()));
            return str1 + str2;
        }
    }

    /**
     * 替换指定位置的字符串
     *
     * @param s
     * @param start      被替换的字符的开始位置
     * @param count      被替换的字符的个数
     * @param replacement 替换的字符串
     * @return
     */
    public static String replace(String s, @IntRange(from = 0) int start, @IntRange(from = 0) int count, CharSequence replacement) {
        if (isEmpty(s) || count == 0) {
            return s;
        } else {
            if (replacement == null) {
                throw new NullPointerException("replacement == null");
            }
            int len = s.length();
            if (len <= start) {
                throw new StringIndexOutOfBoundsException(start);
            }
            if (len < (start + count)) {
                throw new StringIndexOutOfBoundsException(start + count);
            }
            String str1 = (start == 0 ? "" : s.substring(0, start));
            String str2 = ((start + count) == len ? "" : s.substring(start + count, s.length()));
            return str1 + replacement.toString() + str2;
        }
    }
}
