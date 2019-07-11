package pers.liyi.bullet.utils.box.permission;


import android.app.Activity;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

/**
 * 权限请求类
 */
public class PermissionRequest {
    private Activity mActivity;
    private int mRequestCode;
    private String[] mPermissions;
    private boolean autoShowRationable;
    private OnPermissionListener mListener;

    public PermissionRequest(@NonNull Activity activity) {
        this.mActivity = activity;
    }

    /**
     * 设置权限请求的请求码
     *
     * @param requestCode 权限请求码
     * @return {@link PermissionRequest}
     */
    public PermissionRequest requestCode(int requestCode) {
        this.mRequestCode = requestCode;
        return this;
    }

    /**
     * 设置需要申请的权限
     *
     * @param permissions 需要申请的权限
     * @return {@link PermissionRequest}
     */
    public PermissionRequest permissions(@NonNull String... permissions) {
        this.mPermissions = permissions;
        return this;
    }

    /**
     * 设置权限请求的回调接口
     *
     * @param listener 权限请求的回调接口
     * @return {@link PermissionRequest}
     */
    public PermissionRequest callback(OnPermissionListener listener) {
        this.mListener = listener;
        return this;
    }

    /**
     * 是否自动显示拒绝授权时的提示
     *
     * @param autoShowRationable {@code true}: 显示 <br> {@code false}: 不显示
     * @return {@link PermissionRequest}
     */
    public PermissionRequest autoShowRationable(boolean autoShowRationable) {
        this.autoShowRationable = autoShowRationable;
        return this;
    }

    /**
     * 执行权限请求
     */
    public void execute() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] deniedPermissions = PermissionUtils.getDeniedPermissions(mActivity, mPermissions);
            if (deniedPermissions.length > 0) {
                ActivityCompat.requestPermissions(mActivity, deniedPermissions, mRequestCode);
            } else {
                if (mListener != null) {
                    mListener.onPermissionGranted(mRequestCode, mPermissions);
                }
            }
        } else {
            if (mListener != null) {
                mListener.onPermissionGranted(mRequestCode, mPermissions);
            }
        }
    }

    /**
     * 获取权限请求码
     *
     * @return 权限请求码
     */
    public int getRequestCode() {
        return mRequestCode;
    }

    /**
     * 获取申请的权限
     *
     * @return 申请的权限
     */
    public String[] getPermissions() {
        return mPermissions;
    }

    /**
     * 获取是否自动显示拒绝授权时的提示
     *
     * @return {@code true}: 显示 <br> {@code false}: 不显示
     */
    public boolean isAutoShowRationable() {
        return autoShowRationable;
    }

    /**
     * 获取权限请求的回调方法
     *
     * @return 权限请求的回调
     */
    public OnPermissionListener getPermissionListener() {
        return mListener;
    }
}
