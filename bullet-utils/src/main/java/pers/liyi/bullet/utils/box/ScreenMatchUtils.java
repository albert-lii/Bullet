package pers.liyi.bullet.utils.box;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;

import java.lang.reflect.Field;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

/**
 * 屏幕适配解决方案
 * <p>
 * <p>PS: 提供 dp、sp 以及 pt 作为适配单位，建议开发中以 dp、sp 适配为主，pt 可作为 dp、sp 的适配补充</p>
 */
public class ScreenMatchUtils {
    /**
     * 适配基准
     */
    // 以宽度为基准
    public static final int MATCH_BASE_WIDTH = 0;
    // 以高度为基准
    public static final int MATCH_BASE_HEIGHT = 1;
    /**
     * 适配单位
     */
    public static final int MATCH_UNIT_DP = 0;
    public static final int MATCH_UNIT_PT = 1;

    // 原始的设配适配信息
    private static DeviceInfo sOriginalDeviceInfo;
    // 专门存储 Miui 的设备信息
    private static DeviceInfo sOriginalDeviceInfoOnMiui;
    // Activity 的生命周期监控
    private static Application.ActivityLifecycleCallbacks mActivityLifecycleCallback;


    public static void setup(@NonNull Context context) {
        final DisplayMetrics displayMetrics = context.getApplicationContext().getResources().getDisplayMetrics();
        if (displayMetrics != null && sOriginalDeviceInfo == null) {
            // 保存系统原始的信息
            sOriginalDeviceInfo = new DeviceInfo();
            sOriginalDeviceInfo.setScreenWidth(displayMetrics.widthPixels);
            sOriginalDeviceInfo.setScreenHeight(displayMetrics.heightPixels);
            sOriginalDeviceInfo.setAppDensity(displayMetrics.density);
            sOriginalDeviceInfo.setAppDensityDpi(displayMetrics.densityDpi);
            sOriginalDeviceInfo.setAppScaledDensity(displayMetrics.scaledDensity);
            sOriginalDeviceInfo.setAppXdpi(displayMetrics.xdpi);
        }
        // 兼容小米手机适配
        final DisplayMetrics displayMetricsOnMiui = getMetricsOnMiui(context.getApplicationContext().getResources());
        if (displayMetricsOnMiui != null && sOriginalDeviceInfoOnMiui == null) {
            sOriginalDeviceInfoOnMiui = new DeviceInfo();
            sOriginalDeviceInfoOnMiui.setScreenWidth(displayMetricsOnMiui.widthPixels);
            sOriginalDeviceInfoOnMiui.setScreenHeight(displayMetricsOnMiui.heightPixels);
            sOriginalDeviceInfoOnMiui.setAppDensity(displayMetricsOnMiui.density);
            sOriginalDeviceInfoOnMiui.setAppDensityDpi(displayMetricsOnMiui.densityDpi);
            sOriginalDeviceInfoOnMiui.setAppScaledDensity(displayMetricsOnMiui.scaledDensity);
            sOriginalDeviceInfoOnMiui.setAppXdpi(displayMetricsOnMiui.xdpi);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            // 监控字体改变
            context.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    // 字体更改后，重新分配 AppScaledDensity
                    if (newConfig != null && newConfig.fontScale > 0) {
                        sOriginalDeviceInfo.setAppScaledDensity(displayMetrics.scaledDensity);
                        if (displayMetricsOnMiui != null && sOriginalDeviceInfoOnMiui != null) {
                            sOriginalDeviceInfoOnMiui.setAppScaledDensity(displayMetricsOnMiui.scaledDensity);
                        }
                    }
                }

                @Override
                public void onLowMemory() {
                }
            });
        }
    }

    /**
     * 在应用全局使用屏幕适配方案
     */
    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static void register(@NonNull final Application application, final float designSize, final int matchBase, final int matchUnit) {
        if (mActivityLifecycleCallback == null) {
            mActivityLifecycleCallback = new Application.ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                    if (activity != null) {
                        match(activity, designSize, matchBase, matchUnit);
                    }
                }

                @Override
                public void onActivityStarted(Activity activity) {

                }

                @Override
                public void onActivityResumed(Activity activity) {

                }

                @Override
                public void onActivityPaused(Activity activity) {

                }

                @Override
                public void onActivityStopped(Activity activity) {

                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

                }

                @Override
                public void onActivityDestroyed(Activity activity) {

                }
            };
            application.registerActivityLifecycleCallbacks(mActivityLifecycleCallback);
        }
    }

    /**
     * 取消全局适配方案
     */
    @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static void unregister(@NonNull final Application application, @NonNull int... matchUnit) {
        if (mActivityLifecycleCallback != null) {
            application.unregisterActivityLifecycleCallbacks(mActivityLifecycleCallback);
            mActivityLifecycleCallback = null;
        }
        for (int unit : matchUnit) {
            cancelMatch(application, unit);
        }
    }

    /**
     * 适配方法
     * 必须调用在 Activity 的 setContentView() 方法之前执行
     *
     * @param context
     * @param designSize
     */
    public static void match(@NonNull Context context, float designSize) {
        match(context, designSize, MATCH_BASE_WIDTH, MATCH_UNIT_DP);
    }

    /**
     * 适配方法
     * 必须调用在 Activity 的 setContentView() 方法之前执行
     *
     * @param context
     * @param designSize
     * @param matchBase
     */
    public static void match(@NonNull Context context, float designSize, int matchBase) {
        match(context, designSize, matchBase, MATCH_UNIT_DP);
    }

    /**
     * 适配方法
     * 必须调用在 Activity 的 setContentView() 方法之前执行
     *
     * @param context
     * @param designSize 设计图尺寸
     * @param matchBase
     * @param matchUnit
     */
    public static void match(@NonNull Context context, float designSize, int matchBase, int matchUnit) {
        if (designSize == 0) {
            throw new UnsupportedOperationException("The designSize cannot be equal to 0");
        }
        if (matchUnit == MATCH_UNIT_DP) {
            matchByDP(context, designSize, matchBase);
        } else if (matchUnit == MATCH_UNIT_PT) {
            matchByPT(context, designSize, matchBase);
        }
    }

    /**
     * 使用 dp 作为适配单位来进行屏幕适配
     * <br>
     * <ul>
     * dp 与 px 的转换
     * <li> px = density * dp </li>
     * <li> density = dpi / 160 </li>
     * <li> px = dp * (dpi / 160) </li>
     * </ul>
     *
     * @param context
     * @param designSize
     * @param base
     */
    private static void matchByDP(@NonNull Context context, float designSize, int base) {
        if (sOriginalDeviceInfo != null) {
            final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            matchByDP(sOriginalDeviceInfo, displayMetrics, designSize, base);
        }
        if (sOriginalDeviceInfoOnMiui != null) {
            final DisplayMetrics displayMetricsOnMiui = getMetricsOnMiui(context.getResources());
            if (displayMetricsOnMiui != null) {
                matchByDP(sOriginalDeviceInfoOnMiui, displayMetricsOnMiui, designSize, base);
            }
        }
    }

    private static void matchByDP(DeviceInfo matchInfo, DisplayMetrics displayMetrics, float designSize, int base) {
        final float targetDensity;
        if (base == MATCH_BASE_WIDTH) {
            targetDensity = matchInfo.getScreenWidth() * 1f / designSize;
        } else if (base == MATCH_BASE_HEIGHT) {
            targetDensity = matchInfo.getScreenHeight() * 1f / designSize;
        } else {
            targetDensity = matchInfo.getScreenWidth() * 1f / designSize;
        }
        final int targetDensityDpi = (int) (targetDensity * 160);
        final float targetScaledDensity = targetDensity * (matchInfo.getAppScaledDensity() / matchInfo.getAppDensity());
        displayMetrics.density = targetDensity;
        displayMetrics.densityDpi = targetDensityDpi;
        displayMetrics.scaledDensity = targetScaledDensity;
    }

    /**
     * 使用 pt 作为适配单位来进行屏幕适配
     * <br>
     * <p> pt 与 px 的转换:
     * pt * metrics.xdpi * (1.0f/72) </p>
     *
     * @param context
     * @param designSize
     * @param base
     */
    private static void matchByPT(@NonNull final Context context, final float designSize, int base) {
        if (sOriginalDeviceInfo != null) {
            final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            matchByPt(sOriginalDeviceInfo, displayMetrics, designSize, base);
        }
        if (sOriginalDeviceInfoOnMiui != null) {
            final DisplayMetrics displayMetricsOnMiui = getMetricsOnMiui(context.getResources());
            if (displayMetricsOnMiui != null) {
                matchByPt(sOriginalDeviceInfoOnMiui, displayMetricsOnMiui, designSize, base);
            }
        }
    }

    private static void matchByPt(final DeviceInfo matchInfo, final DisplayMetrics displayMetrics, final float designSize, final int base) {
        final float targetXdpi;
        if (base == MATCH_BASE_WIDTH) {
            targetXdpi = matchInfo.getScreenWidth() * 72f / designSize;
        } else if (base == MATCH_BASE_HEIGHT) {
            targetXdpi = matchInfo.getScreenHeight() * 72f / designSize;
        } else {
            targetXdpi = matchInfo.getScreenWidth() * 72f / designSize;
        }
        displayMetrics.xdpi = targetXdpi;
    }

    /**
     * 取消适配
     *
     * @param context
     */
    public static void cancelMatch(@NonNull Context context) {
        cancelMatch(context, MATCH_UNIT_DP);
        cancelMatch(context, MATCH_UNIT_PT);
    }

    /**
     * 取消适配
     *
     * @param context
     * @param matchUnit
     */
    public static void cancelMatch(@NonNull Context context, int matchUnit) {
        if (sOriginalDeviceInfo != null) {
            final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            cancelMatch(matchUnit, displayMetrics, sOriginalDeviceInfo);
        }
        if (sOriginalDeviceInfoOnMiui != null) {
            final DisplayMetrics displayMetricsOnMiui = getMetricsOnMiui(context.getResources());
            if (displayMetricsOnMiui != null) {
                cancelMatch(matchUnit, displayMetricsOnMiui, sOriginalDeviceInfoOnMiui);
            }
        }
    }

    private static void cancelMatch(final int matchUnit, final DisplayMetrics displayMetrics, final DeviceInfo matchInfo) {
        if (matchUnit == MATCH_UNIT_DP) {
            if (matchInfo.getAppDensity() != 0 && displayMetrics.density != matchInfo.getAppDensity()) {
                displayMetrics.density = matchInfo.getAppDensity();
            }
            if (matchInfo.getAppDensityDpi() != 0 && displayMetrics.densityDpi != matchInfo.getAppDensityDpi()) {
                displayMetrics.densityDpi = (int) matchInfo.getAppDensityDpi();
            }
            if (matchInfo.getAppScaledDensity() != 0 && displayMetrics.scaledDensity != matchInfo.getAppScaledDensity()) {
                displayMetrics.scaledDensity = matchInfo.getAppScaledDensity();
            }
        } else if (matchUnit == MATCH_UNIT_PT) {
            if (matchInfo.getAppXdpi() != 0 && displayMetrics.xdpi != matchInfo.getAppXdpi()) {
                displayMetrics.xdpi = matchInfo.getAppXdpi();
            }
        }
    }

    public static DeviceInfo getMatchInfo() {
        return sOriginalDeviceInfo;
    }

    public static DeviceInfo getMatchInfoOnMiui() {
        return sOriginalDeviceInfoOnMiui;
    }

    /**
     * 解决 MIUI 更改框架导致的 MIUI7 + Android5.1.1 上出现的失效问题 (以及极少数基于这部分 MIUI 去掉 ART 然后置入 XPosed 的手机)
     * 来源于: https://github.com/Firedamp/Rudeness/blob/master/rudeness-sdk/src/main/java/com/bulong/rudeness/RudenessScreenHelper.java#L61:5
     *
     * @param resources {@link Resources}
     * @return {@link DisplayMetrics}, 可能为 {@code null}
     */
    private static DisplayMetrics getMetricsOnMiui(Resources resources) {
        if ("MiuiResources".equals(resources.getClass().getSimpleName()) || "XResources".equals(resources.getClass().getSimpleName())) {
            try {
                Field field = Resources.class.getDeclaredField("mTmpMetrics");
                field.setAccessible(true);
                return (DisplayMetrics) field.get(resources);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 存储设备的原始信息
     */
    public static class DeviceInfo {
        private int screenWidth;
        private int screenHeight;
        private float appDensity;
        private float appDensityDpi;
        private float appScaledDensity;
        private float appXdpi;

        public int getScreenWidth() {
            return screenWidth;
        }

        public void setScreenWidth(int screenWidth) {
            this.screenWidth = screenWidth;
        }

        public int getScreenHeight() {
            return screenHeight;
        }

        public void setScreenHeight(int screenHeight) {
            this.screenHeight = screenHeight;
        }

        public float getAppDensity() {
            return appDensity;
        }

        public void setAppDensity(float appDensity) {
            this.appDensity = appDensity;
        }

        public float getAppDensityDpi() {
            return appDensityDpi;
        }

        public void setAppDensityDpi(float appDensityDpi) {
            this.appDensityDpi = appDensityDpi;
        }

        public float getAppScaledDensity() {
            return appScaledDensity;
        }

        public void setAppScaledDensity(float appScaledDensity) {
            this.appScaledDensity = appScaledDensity;
        }

        public float getAppXdpi() {
            return appXdpi;
        }

        public void setAppXdpi(float appXdpi) {
            this.appXdpi = appXdpi;
        }
    }
}
