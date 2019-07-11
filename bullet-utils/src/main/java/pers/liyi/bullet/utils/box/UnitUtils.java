package pers.liyi.bullet.utils.box;

import android.content.Context;
import android.util.TypedValue;

import androidx.annotation.NonNull;


public class UnitUtils {

    /**
     * dp 转 px
     *
     * @param dpVal dp 值
     * @return px 值
     */
    public static float dpToPx(@NonNull Context context, float dpVal) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, context.getResources().getDisplayMetrics());
    }

    /**
     * sp 转 px
     *
     * @param spVal sp 值
     * @return px 值
     */
    public static float spToPx(@NonNull Context context, float spVal) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, context.getResources().getDisplayMetrics());
    }

    /**
     * pt 转 px
     *
     * @param ptVal pt 值
     * @return px 值
     */
    public static float ptToPx(@NonNull Context context, float ptVal) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PT, ptVal, context.getResources().getDisplayMetrics());
    }


    /**
     * px 转 dp
     *
     * @param pxVal px 值
     * @return dp 值
     */
    public static float pxToDp(@NonNull Context context, float pxVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return pxVal / scale + 0.5f;
    }

    /**
     * px 转 sp
     *
     * @param pxVal px 值
     * @return sp 值
     */
    public static float pxToSp(@NonNull Context context, float pxVal) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return pxVal / scale + 0.5f;
    }

    /**
     * px 转 pt
     *
     * @param pxVal px 值
     * @return pt 值
     */
    public static float pxToPt(@NonNull Context context, float pxVal) {
        final float scale = context.getResources().getDisplayMetrics().xdpi;
        return pxVal * 72f / scale + 0.5f;
    }
}
