package pers.liyi.bullet.retrofit;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;


public class NetworkUtil {

    @RequiresPermission(ACCESS_NETWORK_STATE)
    private static NetworkInfo getActiveNetworkInfo(@NonNull Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
    }

    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static boolean isConnected(@NonNull Context context) {
        NetworkInfo info = getActiveNetworkInfo(context);
        return info != null && info.isConnected();
    }
}
