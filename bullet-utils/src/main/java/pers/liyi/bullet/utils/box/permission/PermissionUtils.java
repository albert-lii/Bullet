package pers.liyi.bullet.utils.box.permission;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Size;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


public class PermissionUtils {
    /**
     * Build.MANUFACTURER
     */
    private static final String MANUFACTURER_HUAWEI = "huawei";// 华为
    private static final String MANUFACTURER_XIAOMI = "xiaomi";// 小米
    private static final String MANUFACTURER_VIVO = "vivo";
    private static final String MANUFACTURER_OPPO = "oppo";
    private static final String MANUFACTURER_MEIZU = "meizu";// 魅族
    private static final String MANUFACTURER_SONY = "sony";// 索尼
    private static final String MANUFACTURER_LG = "lg";
    private static final String MANUFACTURER_SAMSUNG = "samsung";// 三星
    private static final String MANUFACTURER_LETV = "letv";// 乐视
    private static final String MANUFACTURER_ZTE = "zte";// 中兴
    private static final String MANUFACTURER_YULONG = "yulong";// 酷派
    private static final String MANUFACTURER_LENOVO = "lenovo";// 联想
    private static final String MANUFACTURER_QIKU = "qiku";// 360
    private static final String MANUFACTURER_360 = "360";// 360

    // 权限请求列表
    private static ArrayList<PermissionRequest> sRequestList = new ArrayList<PermissionRequest>();

    /**
     * 判断是否需要申请权限
     *
     * @return {@code true}: 需要申请权限 <br> {@code false}: 不需要申请权限
     */
    public static boolean isNeedRequest(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 检查是否具指定权限
     */
    public static boolean hasPermissions(@NonNull Context context, @Size(min = 1) @NonNull String... perms) {
        // 对于sdk < 23 始终返回true，让系统处理权限
        if (isNeedRequest()) {
            return true;
        }
        for (String perm : perms) {
            if (ContextCompat.checkSelfPermission(context, perm) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 绑定 activity
     *
     * @param activity activity
     * @return {@link PermissionRequest}
     */
    public static PermissionRequest with(@NonNull Activity activity) {
        return addRequest(activity);
    }

    /**
     * 绑定 fragment
     *
     * @param fragment fragment
     * @return {@link PermissionRequest}
     */
    public static PermissionRequest with(@NonNull Fragment fragment) {
        return addRequest(fragment.getActivity());
    }

    /**
     * 绑定 android.app.Fragment
     *
     * @param fragment fragment
     * @return {@link PermissionRequest}
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static PermissionRequest with(@NonNull android.app.Fragment fragment) {
        return addRequest(fragment.getActivity());
    }

    /**
     * 添加权限请求
     *
     * @param activity activity
     * @return {@link PermissionRequest}
     */
    private static PermissionRequest addRequest(@NonNull Activity activity) {
        PermissionRequest request = new PermissionRequest(activity);
        if (sRequestList == null) {
            sRequestList = new ArrayList<PermissionRequest>();
        }
        sRequestList.add(request);
        return request;
    }

    /**
     * 处理请求授权后返回的结果
     * <p>此方法需要放在 onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,int[] grantResults) 方法中执行</p>
     *
     * @param activity     activity
     * @param requestCode  请求码
     * @param permissions  申请的所有权限
     * @param grantResults 授权结果
     */
    public static void handleRequestPermissionsResult(@NonNull Activity activity, int requestCode, @NonNull String[] permissions, int[] grantResults) {
        // 判断权限请求列表中是否有权限请求
        if (sRequestList != null && sRequestList.size() > 0) {
            PermissionRequest currentReq = null;
            for (PermissionRequest request : sRequestList) {
                // 遍历权限请求列表，如果有权限请求的 requestCode 与返回的 requestCode 一致，
                // 且权限请求的申请的所有权限与返回的申请的所有权限一致，则从权限请求列表中提取出该权限请求
                if ((requestCode == request.getRequestCode()) && permissions.equals(request.getPermissions())) {
                    currentReq = request;
                    break;
                }
            }
            if (currentReq != null) {
                if (currentReq.getPermissionListener() != null) {
                    // 创建被拒绝授权的权限列表
                    ArrayList<String> deniedPermissions = new ArrayList<String>();
                    for (int i = 0; i < grantResults.length; i++) {
                        // 如果授权结果列表中的值不是已授权，则将权限加入被拒绝授权的权限列表
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            deniedPermissions.add(permissions[i]);
                        }
                    }
                    // 如果所有权限都授权成功,调用授权成功的回调
                    if (deniedPermissions.size() <= 0) {
                        if (currentReq.getPermissionListener() != null) {
                            currentReq.getPermissionListener().onPermissionGranted(requestCode, permissions);
                        }
                    } else {
                        String[] perms = deniedPermissions.toArray(new String[deniedPermissions.size()]);
                        // 判断是否有权限被用户在权限弹框中勾选了总是拒绝授权
                        boolean hasAlwaysDenied = hasAlwaysDeniedPermission(activity, perms);
                        if (currentReq.isAutoShowRationable() && hasAlwaysDenied) {
                            showRationalDialog(activity, null);
                        }
                        if (currentReq.getPermissionListener() != null) {
                            // 调用授权失败的回调
                            currentReq.getPermissionListener().onPermissionDenied(requestCode, perms, hasAlwaysDenied);
                        }
                    }
                }
                // 在处理完权限请求结果后，从权限请求列表中移除该权限请求
                sRequestList.remove(currentReq);
            }
        }
    }

    /**
     * 获取被拒绝的权限
     *
     * @param context     上下文对象
     * @param permissions 需要申请的所有权限
     * @return 需要申请的所有权限中未获取的权限
     */
    public static String[] getDeniedPermissions(@NonNull Context context, @NonNull String... permissions) {
        ArrayList<String> deniedPermissions = new ArrayList<String>();
        if (isNeedRequest()) {
            for (String p : permissions) {
                if (ContextCompat.checkSelfPermission(context, p) == PackageManager.PERMISSION_DENIED) {
                    deniedPermissions.add(p);
                }
            }
        }
        return deniedPermissions.toArray(new String[deniedPermissions.size()]);
    }

    /**
     * 获取被选顶为总是拒绝的权限
     */
    public static String[] getAlwaysDeniedPermissions(@NonNull Activity activity, @NonNull String... permissions) {
        ArrayList<String> deniedPermissions = new ArrayList<String>();
        if (isNeedRequest()) {
            for (String p : permissions) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, p)) {
                    deniedPermissions.add(p);
                }
            }
        }
        return deniedPermissions.toArray(new String[deniedPermissions.size()]);
    }

    /**
     * 判断是否在自动弹出的权限弹框中勾选了总是拒绝授权
     *
     * @param activity          activity
     * @param deniedPermissions 需要申请的权限
     * @return {@code true}: 在权限弹出框中勾选了“总是拒绝授权”<br>{@code false}: 在权限弹出框中未勾选“总是拒绝授权”
     */
    public static boolean hasAlwaysDeniedPermission(@NonNull Activity activity, @NonNull String... deniedPermissions) {
        if (isNeedRequest()) {
            for (String permission : deniedPermissions)
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission))
                    return true;
            return false;
        }
        return false;
    }

    /**
     * 显示提示框，用于在缺少权限的情况下，用户拒绝授权，给用户弹出提示信息
     *
     * @param context 上下文对象
     * @param message 提示框中的内容
     */
    public static void showRationalDialog(@NonNull final Context context, @NonNull String message) {
        if (isNeedRequest()) {
            new AlertDialog.Builder(context)
                    .setTitle("提示信息")
                    .setMessage(TextUtils.isEmpty(message) ?
                            "当前应用缺少必要权限，可能导致应用无法正常。请单击【确定】按钮前往设置中心进行权限授权"
                            : message)
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            toPermissionSettings(context);
                        }
                    }).show();
        }
    }

    /**
     * 跳转至权限设置页面
     */
    public static void toPermissionSettings(@NonNull Context context) {
        Intent intent = new Intent();
        switch (Build.MANUFACTURER.toLowerCase()) {
            case MANUFACTURER_HUAWEI:
                intent = Huawei(context);
                break;
            case MANUFACTURER_XIAOMI:
                intent = Xiaomi(context);
                break;
            case MANUFACTURER_OPPO:
                intent = OPPO(context);
                break;
            case MANUFACTURER_MEIZU:
                intent = Meizu(context);
                break;
            case MANUFACTURER_SONY:
                intent = Sony(context);
                break;
            case MANUFACTURER_LG:
                intent = LG(context);
                break;
            case MANUFACTURER_LETV:
                intent = Letv(context);
                break;
            case MANUFACTURER_QIKU:
            case MANUFACTURER_360:
                intent = Qihoo360(context);
                break;
            default:
                intent = ApplicationInfo(context);
                break;
        }
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            // 跳转至普通设置界面
            intent.setAction(Settings.ACTION_SETTINGS);
            context.startActivity(intent);
        }
    }

    private static Intent Huawei(@NonNull Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", context.getPackageName());
        ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");
        intent.setComponent(comp);
        return intent;
    }

    private static Intent Xiaomi(@NonNull Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        ComponentName componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        intent.setComponent(componentName);
        intent.putExtra("extra_pkgname", context.getPackageName());
        return intent;
    }

    private static Intent OPPO(@NonNull Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", context.getPackageName());
        ComponentName comp = new ComponentName("com.color.safecenter", "com.color.safecenter.permission.PermissionManagerActivity");
        intent.setComponent(comp);
        return intent;
    }

    private static Intent Meizu(@NonNull Context context) {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra("packageName", context.getPackageName());
        return intent;
    }

    private static Intent Sony(@NonNull Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", context.getPackageName());
        ComponentName comp = new ComponentName("com.sonymobile.cta", "com.sonymobile.cta.SomcCTAMainActivity");
        intent.setComponent(comp);
        return intent;
    }

    private static Intent LG(@NonNull Context context) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", context.getPackageName());
        ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.Settings$AccessLockSummaryActivity");
        intent.setComponent(comp);
        return intent;
    }

    private static Intent Letv(@NonNull Context context) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", context.getPackageName());
        ComponentName comp = new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.PermissionAndApps");
        intent.setComponent(comp);
        return intent;
    }

    /**
     * 只能打开到自带安全软件
     */
    private static Intent Qihoo360(@NonNull Context context) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("packageName", context.getPackageName());
        ComponentName comp = new ComponentName("com.qihoo360.mobilesafe", "com.qihoo360.mobilesafe.ui.index.AppEnterActivity");
        intent.setComponent(comp);
        return intent;
    }

    /**
     * 应用详情界面
     */
    private static Intent ApplicationInfo(@NonNull Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        return localIntent;
    }
}
