package pers.liyi.bullet.utils.box;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import pers.liyi.bullet.utils.constant.AppStatus;

public class AppUtils {
    private static final String TAG = "Bullet-" + AppUtils.class.getSimpleName();

    /**
     * 判断应用是否存活
     *
     * @param context
     * @param packageName 包名
     * @return {@code true}: 存活 <br> {@code false}: 死亡
     */
    public static boolean isAppAlive(@NonNull Context context, @NonNull String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();
        for (int i = 0; i < processInfos.size(); i++) {
            if (processInfos.get(i).processName.equals(packageName)) {
                LogUtils.i(TAG, String.format("AppAliveInfo ===> App %s is running", packageName));
                return true;
            }
        }
        LogUtils.i(TAG, String.format("AppAliveInfo ===> App %s has been killed", packageName));
        return false;
    }

    /**
     * 获取应用的状态
     *
     * @param context
     * @param packageName 包名
     * @return {@link AppStatus#APP_FOREGROUND}: 应用在前台运行
     * {@link AppStatus#APP_BACKGROUND}: 应用在后台运行
     * {@link AppStatus#APP_DEAD}: 应用已经死亡
     */
    public int getAppStatus(@NonNull Context context, @NonNull String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo aInfo : processInfos) {
            if (aInfo.processName.equals(packageName)) {
                if (aInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return AppStatus.APP_FOREGROUND;
                } else if (aInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_CACHED) {
                    return AppStatus.APP_BACKGROUND;
                }
            }
        }
        return AppStatus.APP_DEAD;
    }

    /**
     * 判断应用是否安装
     *
     * @param context
     * @param packageName 包名
     * @return {@code true}: 已安装 <br> {@code false}: 没有安装
     */
    public static boolean isAppInstalled(@NonNull Context context, @NonNull String packageName) {
        return context.getPackageManager().getLaunchIntentForPackage(packageName) != null;
    }

    /**
     * 判断应用是否安装
     *
     * @param context
     * @param action   intent action, 例如：ACTION_VIEW.
     * @param category 类别.
     * @return {@code true}: 已安装 <br>{ @code false}: 没有安装
     */
    public static boolean isAppInstalled(@NonNull Context context, @NonNull String action, @NonNull String category) {
        Intent intent = new Intent(action);
        intent.addCategory(category);
        PackageManager pm = context.getPackageManager();
        ResolveInfo info = pm.resolveActivity(intent, 0);
        return info != null;
    }

    /**
     * 启动应用
     *
     * @param context
     * @param packageName 包名
     */
    public static void launchApp(@NonNull Context context, @NonNull String packageName) {
        context.startActivity(getLaunchAppIntent(context, packageName, true));
    }

    /**
     * 启动应用
     *
     * @param activity
     * @param packageName 包名
     * @param requestCode 当 activity 存在时，如果 requestCode = 0，那么 requestCode 会在 onActivityResult() 方法中被返回
     */
    public static void launchApp(@NonNull Activity activity, @NonNull String packageName, int requestCode) {
        activity.startActivityForResult(getLaunchAppIntent(activity, packageName), requestCode);
    }

    /**
     * 重启应用
     */
    public static void relaunchApp(@NonNull Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        if (intent == null) return;
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        context.startActivity(mainIntent);
        System.exit(0);
    }

    /**
     * 启动应用的详情设置页面
     *
     * @param context 包名
     */
    public static void launchAppDetailsSettings(@NonNull Context context) {
        launchAppDetailsSettings(context, context.getPackageName());
    }

    /**
     * 启动应用的详情设置页面
     *
     * @param packageName 包名
     */
    public static void launchAppDetailsSettings(@NonNull Context context, @NonNull String packageName) {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.parse("package:" + packageName));
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    /**
     * 静默安装应用程序
     * <p>{@code <uses-permission android:name="android.permission.INSTALL_PACKAGES" />}</p>
     *
     * @param filePath APK 文件路径
     * @return {@code true}: 安装成功 <br> {@code false}: 安装失败
     */
    public static boolean installAppSilent(@NonNull String filePath) {
        return installAppSilent(getFileByPath(filePath), null);
    }

    /**
     * 静默安装应用程序
     * <p>{@code <uses-permission android:name="android.permission.INSTALL_PACKAGES" />}</p>
     *
     * @param file APK 文件
     * @return {@code true}: 安装成功 <br> {@code false}: 安装失败
     */
    public static boolean installAppSilent(File file) {
        return installAppSilent(file, null);
    }

    /**
     * 静默安装应用程序
     * <p>{@code <uses-permission android:name="android.permission.INSTALL_PACKAGES" />}</p>
     *
     * @param filePath APK 文件路径
     * @param params   安装参数，例如：-r, -s
     * @return {@code true}: 安装成功 <br> {@code false}: 安装失败
     */
    public static boolean installAppSilent(@NonNull String filePath, final String params) {
        return installAppSilent(getFileByPath(filePath), params);
    }

    /**
     * 静默安装应用程序
     * <p>{@code <uses-permission android:name="android.permission.INSTALL_PACKAGES" />}</p>
     *
     * @param file   APK 文件
     * @param params 安装参数，例如：-r, -s
     * @return {@code true}: 安装成功 <br>{@code false}: 安装失败
     */
    public static boolean installAppSilent(File file, final String params) {
        return installAppSilent(file, params, isDeviceRooted());
    }

    /**
     * 静默安装应用程序
     * <br>
     * 如果没有 root 权限，必须持有以下权限
     * {@code <uses-permission android:name="android.permission.INSTALL_PACKAGES" />}</p>
     *
     * @param file     APK 文件
     * @param params   安装参数，例如：-r, -s
     * @param isRooted 是否使用 root
     * @return {@code true}: 安装成功 <br>{@code false}: 安装失败
     */
    public static boolean installAppSilent(File file, String params, boolean isRooted) {
        if (!isFileExists(file)) return false;
        String filePath = '"' + file.getAbsolutePath() + '"';
        String command = "LD_LIBRARY_PATH=/vendor/lib*:/system/lib* pm install " +
                (params == null ? "" : params + " ")
                + filePath;
        ShellUtils.CommandResult commandResult = ShellUtils.execCmd(command, isRooted);
        if (commandResult.successMsg != null
                && commandResult.successMsg.toLowerCase().contains("success")) {
            return true;
        } else {
            LogUtils.e(TAG, "installAppSilent successMsg ===> " + commandResult.successMsg +
                    ", errorMsg: " + commandResult.errorMsg);
            return false;
        }
    }

    /**
     * 卸载应用程序
     *
     * @param packageName 包名
     */
    public static void uninstallApp(@NonNull Context context, @NonNull String packageName) {
        context.startActivity(getUninstallAppIntent(packageName, true));
    }

    /**
     * 卸载应用程序
     *
     * @param activity
     * @param packageName 包名
     * @param requestCode 当 activity 存在时，如果 requestCode = 0，那么 requestCode 会在 onActivityResult() 方法中被返回
     */
    public static void uninstallApp(@NonNull Activity activity, @NonNull String packageName, int requestCode) {
        activity.startActivityForResult(getUninstallAppIntent(packageName), requestCode);
    }

    /**
     * 静默卸载应用程序
     * <p>{@code <uses-permission android:name="android.permission.DELETE_PACKAGES" />}</p>
     *
     * @param packageName 包名
     * @return {@code true}: 卸载成功 <br> {@code false}: 卸载失败
     */
    public static boolean uninstallAppSilent(@NonNull String packageName) {
        return uninstallAppSilent(packageName, false);
    }

    /**
     * 静默卸载应用程序
     * {@code <uses-permission android:name="android.permission.DELETE_PACKAGES" />}</p>
     *
     * @param packageName 包名
     * @param isKeepData  是否要保留应用程序数据
     * @return {@code true}: 卸载成功 <br> {@code false}: 卸载失败
     */
    public static boolean uninstallAppSilent(@NonNull String packageName, boolean isKeepData) {
        return uninstallAppSilent(packageName, isKeepData, isDeviceRooted());
    }

    /**
     * 静默卸载应用程序
     * <p>{@code <uses-permission android:name="android.permission.DELETE_PACKAGES" />}</p>
     *
     * @param packageName 包名
     * @param isKeepData  是否要保留应用程序数据
     * @param isRooted    是否使用 root
     * @return {@code true}: 卸载成功 <br> {@code false}: 卸载失败
     */
    public static boolean uninstallAppSilent(@NonNull String packageName, boolean isKeepData, boolean isRooted) {
        String command = "LD_LIBRARY_PATH=/vendor/lib*:/system/lib* pm uninstall "
                + (isKeepData ? "-k " : "")
                + packageName;
        ShellUtils.CommandResult commandResult = ShellUtils.execCmd(command, isRooted);
        if (commandResult.successMsg != null
                && commandResult.successMsg.toLowerCase().contains("success")) {
            return true;
        } else {
            LogUtils.e(TAG, "UninstallAppSilent successMsg: " + commandResult.successMsg +
                    ", errorMsg: " + commandResult.errorMsg);
            return false;
        }
    }

    /**
     * 判断是否系统应用程序
     *
     * @param context
     * @return {@code true}: 是 <br>{@code false}: 否
     */
    public static boolean isSystemApp(@NonNull Context context) {
        return isSystemApp(context, context.getPackageName());
    }

    /**
     * 判断是否系统应用程序
     *
     * @param context
     * @param packageName 包名
     * @return {@code true}: 是 <br>{@code false}: 否
     */
    public static boolean isSystemApp(@NonNull Context context, @NonNull String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
            return ai != null && (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取应用程序的签名
     *
     * @param context
     * @param packageName 包名
     * @return 应用程序的签名
     */
    public static Signature[] getAppSignature(@NonNull Context context, @NonNull String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            @SuppressLint("PackageManagerGetSignatures")
            PackageInfo pi = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            return pi == null ? null : pi.signatures;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取应用程序的 SHA1 签名
     *
     * @param context
     * @param packageName 包名
     * @return 应用程序的 SHA1 签名
     */
    public static String getAppSignatureSHA1(@NonNull Context context, @NonNull String packageName) {
        return getAppSignatureHash(context, packageName, "SHA1");
    }

    /**
     * 获取应用程序的 SHA256 签名
     *
     * @param context
     * @param packageName 包名
     * @return 应用程序的 SHA256 签名
     */
    public static String getAppSignatureSHA256(@NonNull Context context, @NonNull String packageName) {
        return getAppSignatureHash(context, packageName, "SHA256");
    }

    /**
     * 获取应用程序的 MD5 签名
     *
     * @param context
     * @param packageName 包名
     * @return 应用程序的 MD5 签名
     */
    public static String getAppSignatureMD5(@NonNull Context context, @NonNull String packageName) {
        return getAppSignatureHash(context, packageName, "MD5");
    }

    /**
     * 获取应用程序的信息
     *
     * @param context
     * @return {@link AppInfo} 应用程序信息类
     */
    public static AppInfo getAppInfo(@NonNull Context context) {
        return getAppInfo(context, context.getPackageName());
    }

    /**
     * 获取应用程序的信息
     *
     * @param context
     * @param packageName 包名
     * @return {@link AppInfo} 应用程序信息类
     */
    public static AppInfo getAppInfo(@NonNull Context context, @NonNull String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return getAppInfo(pm, pi);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取应用程序的信息
     *
     * @param context
     * @return {@link AppInfo} 应用程序信息类
     */
    public static List<AppInfo> getAppsInfo(@NonNull Context context) {
        List<AppInfo> list = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        // 获取系统中安装的所有的应用程序的信息
        List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
        for (PackageInfo pi : installedPackages) {
            AppInfo ai = getAppInfo(pm, pi);
            if (ai != null) {
                list.add(ai);
            }
        }
        return list;
    }

    /**
     * 获取应用程序的 uid
     * <br>
     * <p>uid 是应用在安装时系统分配给应用的唯一标识，一个应用只有一个 uid，但是可以有多个 pid；在应用卸载重装后，系统重新给应用分配 uid</p>
     * <p>注：应用覆盖安装升级时，是不会改变 uid 的，在应用升级时，新应用会读取旧应用的 uid</p>
     *
     * @return uid
     */
    public static int getUid() {
        return android.os.Process.myUid();
    }

    /**
     * 获取应用程序的 uid
     *
     * @param context
     * @param packageName 包名
     * @return Uid
     */
    public static int getUid(@NonNull Context context, @NonNull String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            @SuppressLint("WrongConstant")
            ApplicationInfo ai = pm.getApplicationInfo(packageName, PackageManager.GET_ACTIVITIES);
            return ai.uid;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 获取应用程序的 uid
     *
     * @return Uid list
     */
    public static List getUids(@NonNull Context context) {
        List<Integer> uidList = new ArrayList<Integer>();
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packinfos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES
                | PackageManager.GET_PERMISSIONS);
        for (PackageInfo info : packinfos) {
            String[] premissions = info.requestedPermissions;
            if (premissions != null && premissions.length > 0) {
                for (String premission : premissions) {
                    if ("android.permission.INTERNET".equals(premission)) {
                        int uid = info.applicationInfo.uid;
                        uidList.add(uid);
                    }
                }
            }
        }
        return uidList;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////  Private Methods
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static boolean isFileExists(File file) {
        return file != null && file.exists();
    }

    private static File getFileByPath(@NonNull String filePath) {
        return new File(filePath);
    }

    private static boolean isDeviceRooted() {
        String su = "su";
        String[] locations = {"/system/bin/", "/system/xbin/", "/sbin/", "/system/sd/xbin/",
                "/system/bin/failsafe/", "/data/local/xbin/", "/data/local/bin/", "/data/local/"};
        for (String location : locations) {
            if (new File(location + su).exists()) {
                return true;
            }
        }
        return false;
    }

    private static Intent getLaunchAppIntent(@NonNull Context context, @NonNull String packageName) {
        return getLaunchAppIntent(context, packageName, false);
    }

    private static Intent getLaunchAppIntent(@NonNull Context context, @NonNull String packageName, boolean isNewTask) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent == null) return null;
        return isNewTask ? intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) : intent;
    }

    private static Intent getUninstallAppIntent(@NonNull String packageName) {
        return getUninstallAppIntent(packageName, false);
    }

    private static Intent getUninstallAppIntent(@NonNull String packageName, boolean isNewTask) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + packageName));
        return isNewTask ? intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) : intent;
    }

    private static String getAppSignatureHash(@NonNull Context context, @NonNull String packageName, @NonNull String algorithm) {
        Signature[] signature = getAppSignature(context, packageName);
        if (signature == null || signature.length <= 0) return "";
        return bytes2HexString(hashTemplate(signature[0].toByteArray(), algorithm))
                .replaceAll("(?<=[0-9A-F]{2})[0-9A-F]{2}", ":$0");
    }

    private static byte[] hashTemplate(final byte[] data, final String algorithm) {
        if (data == null || data.length <= 0) return null;
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(data);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static final char HEX_DIGITS[] =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private static String bytes2HexString(final byte[] bytes) {
        if (bytes == null) return "";
        int len = bytes.length;
        if (len <= 0) return "";
        char[] ret = new char[len << 1];
        for (int i = 0, j = 0; i < len; i++) {
            ret[j++] = HEX_DIGITS[bytes[i] >> 4 & 0x0f];
            ret[j++] = HEX_DIGITS[bytes[i] & 0x0f];
        }
        return new String(ret);
    }

    private static AppInfo getAppInfo(PackageManager pm, PackageInfo pi) {
        if (pm == null || pi == null) {
            return null;
        }
        ApplicationInfo ai = pi.applicationInfo;
        String name = ai.loadLabel(pm).toString();
        Drawable icon = ai.loadIcon(pm);
        String packagePath = ai.sourceDir;
        String packageName = pi.packageName;
        String versionName = pi.versionName;
        int versionCode = pi.versionCode;
        boolean isSystemApp = (ApplicationInfo.FLAG_SYSTEM & ai.flags) != 0;
        return new AppInfo(name, icon, packagePath, packageName, versionName, versionCode, isSystemApp);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////  应用程序信息模型
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final static class AppInfo {
        private String name;
        private Drawable icon;
        private String packagePath;
        private String packageName;
        private String versionName;
        private int versionCode;
        // 判断是否是系统应用程序
        private boolean isSystemApp;

        public AppInfo(String name, Drawable icon, String packagePath, String packageName,
                       String versionName, int versionCode, boolean isSystemApp) {
            this.name = name;
            this.icon = icon;
            this.packagePath = packagePath;
            this.packageName = packageName;
            this.versionName = versionName;
            this.versionCode = versionCode;
            this.isSystemApp = isSystemApp;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Drawable getIcon() {
            return icon;
        }

        public void setIcon(Drawable icon) {
            this.icon = icon;
        }

        public String getPackagePath() {
            return packagePath;
        }

        public void setPackagePath(String packagePath) {
            this.packagePath = packagePath;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public int getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public boolean isSystemApp() {
            return isSystemApp;
        }

        public void setSystemApp(boolean systemApp) {
            isSystemApp = systemApp;
        }

        @Override
        public String toString() {
            return "pkg name: " + getPackageName() +
                    "\napp icon: " + getIcon() +
                    "\napp name: " + getName() +
                    "\napp path: " + getPackagePath() +
                    "\napp v name: " + getVersionName() +
                    "\napp v code: " + getVersionCode() +
                    "\nis system: " + isSystemApp();
        }
    }
}
