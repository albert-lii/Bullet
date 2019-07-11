package pers.liyi.bullet.utils.box;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import pers.liyi.bullet.utils.box.LogUtils;
import pers.liyi.bullet.utils.box.ShellUtils;
import pers.liyi.bullet.utils.constant.NetworkType;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.CHANGE_WIFI_STATE;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.MODIFY_PHONE_STATE;


public class NetUtils {

    /**
     * 获取活动网络的信息
     * <p>{@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}</p>
     *
     * @return NetworkInfo
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    private static NetworkInfo getActiveNetworkInfo(@NonNull Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
    }

    /**
     * 判断网络是否连接
     *
     * @return {@code true}: 已连接 <br> {@code false}: 未连接
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static boolean isConnected(@NonNull Context context) {
        NetworkInfo info = getActiveNetworkInfo(context);
        return info != null && info.isConnected();
    }

    /**
     * 通过 Ping 判断网络是否可用（使用 Ping 来判断会很耗时）
     * <p>{@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     * <p>需要在异步线程中使用ping，如果ping不起作用，则网络不可用</p>
     * <p>这里使用的 IP 是阿里巴巴的公共 IP:223.5.5.5</p>
     *
     * @return {@code true}: 可用 <br> {@code false}: 不可用
     */
    @RequiresPermission(INTERNET)
    public static boolean isAvailableByPing() {
        return isAvailableByPing(null);
    }

    /**
     * 通过 Ping 判断网络是否可用（使用 Ping 来判断会很耗时）
     * <p>{@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @param ip IP地址，如果IP地址为空，请使用阿里巴巴的公共IP。
     * @return {@code true}: 可用 <br> {@code false}: 不可用
     */
    @RequiresPermission(INTERNET)
    public static boolean isAvailableByPing(String ip) {
        if (ip == null || ip.length() <= 0) {
            // Alibaba's public ip
            ip = "223.5.5.5";
        }
        ShellUtils.CommandResult result = ShellUtils.execCmd(String.format("ping -c 1 %s", ip), false);
        boolean ret = result.result == 0;
        if (result.successMsg != null) {
            LogUtils.d("NetUtils", "isAvailableByPing() called" + result.successMsg);
        }
        if (result.errorMsg != null) {
            LogUtils.d("NetUtils", "isAvailableByPing() called" + result.errorMsg);
        }
        return ret;
    }

    /**
     * 跳转至网络设置界面
     *
     * @param context
     */
    public static void toNetSettings(@NonNull Context context) {
        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        context.startActivity(intent);
    }

    /**
     * 跳转至 wifi 设置界面
     * @param context
     */
    public static void toWifiSettings(@NonNull Context context) {
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        context.startActivity(intent);
    }

    /**
     * 跳转至移动数据设置界面
     *
     * @param context
     */
    public static void toMobileDataSettings(@NonNull Context context) {
        Intent intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
        context.startActivity(intent);
    }

    /**
     * 判断 wifi 是否开启
     *
     * @return {@code true}: 开启 <br>{@code false}: 关闭
     */
    public static boolean isWifiEnabled(@NonNull Context context) {
        @SuppressLint("WifiManagerLeak")
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    /**
     * 开启/关闭 wifi
     *
     * @param enabled {@code true}: 开启 <br>{@code false}: 关闭
     */
    @RequiresPermission(CHANGE_WIFI_STATE)
    public static void setWifiEnabled(@NonNull Context context, boolean enabled) {
        @SuppressLint("WifiManagerLeak")
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (enabled) {
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
        } else {
            if (wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(false);
            }
        }
    }

    /**
     * 判断 wifi 是否连接
     *
     * @return {@code true}: 已连接 <br> {@code false}: 未连接
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static boolean isWifiConnected(@NonNull Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm != null
                && cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 判断 wifi 网络是否可用
     *
     * @return {@code true}: 可用 <br> {@code false}: 不可用
     */
    @RequiresPermission(INTERNET)
    public static boolean isWifiAvailable(@NonNull Context context) {
        return isWifiEnabled(context) && isAvailableByPing();
    }

    /**
     * 判断移动数据是否开启
     *
     * @return {@code true}: enabled <br> {@code false}: disabled
     */
    public static boolean isMobileDataEnabled(@NonNull Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // noinspection ConstantConditions
                return tm.isDataEnabled();
            }
            // noinspection ConstantConditions
            @SuppressLint("PrivateApi")
            Method getMobileDataEnabledMethod = tm.getClass().getDeclaredMethod("getDataEnabled");
            if (null != getMobileDataEnabledMethod) {
                return (boolean) getMobileDataEnabledMethod.invoke(tm);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 开启/关闭 移动数据
     * <p>{@code <uses-permission android:name="android.permission.MODIFY_PHONE_STATE" />}</p>
     *
     * @param context
     * @param enabled {@code true}: 开启 <br> {@code false}: 关闭
     */
    @RequiresPermission(MODIFY_PHONE_STATE)
    public static void setMobileDataEnabled(@NonNull Context context, boolean enabled) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Method setMobileDataEnabledMethod = tm.getClass().getDeclaredMethod("setDataEnabled", boolean.class);
            if (null != setMobileDataEnabledMethod) {
                setMobileDataEnabledMethod.invoke(tm, enabled);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断是否使用移动数据
     * <p>{@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}</p>
     *
     * @param context
     * @return {@code true}: yes<br>{@code false}: no
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static boolean isMobileData(@NonNull Context context) {
        NetworkInfo info = getActiveNetworkInfo(context);
        return null != info
                && info.isAvailable()
                && info.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    /**
     * 获取网络运营商的名称
     * <p>中国移动, 中国联通, 中国电信</p>
     *
     * @param context
     * @return 网络运营商的名称
     */
    public static String getNetworkOperatorName(@NonNull Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm != null ? tm.getNetworkOperatorName() : null;
    }

    /**
     * 获取当前网络类型
     *
     * @param context
     * @return {@link NetworkType}
     */
    @RequiresPermission(ACCESS_NETWORK_STATE)
    public static int getNetworkType(@NonNull Context context) {
        int networkType = NetworkType.NETWORK_NO;
        NetworkInfo info = getActiveNetworkInfo(context);
        if (info != null && info.isAvailable()) {
            if (info.getType() == ConnectivityManager.TYPE_ETHERNET) {
                networkType = NetworkType.NETWORK_ETHERNET;
            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                networkType = NetworkType.NETWORK_WIFI;
            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                switch (info.getSubtype()) {

                    case TelephonyManager.NETWORK_TYPE_GSM:
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        networkType = NetworkType.NETWORK_2G;
                        break;

                    case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        networkType = NetworkType.NETWORK_3G;
                        break;

                    case TelephonyManager.NETWORK_TYPE_IWLAN:
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        networkType = NetworkType.NETWORK_4G;
                        break;

                    default:
                        String subtypeName = info.getSubtypeName();
                        //  中国移动 联通 电信 三种 3G 制式
                        if (subtypeName.equalsIgnoreCase("TD-SCDMA")
                                || subtypeName.equalsIgnoreCase("WCDMA")
                                || subtypeName.equalsIgnoreCase("CDMA2000")) {
                            networkType = NetworkType.NETWORK_3G;
                        } else {
                            networkType = NetworkType.NETWORK_UNKNOWN;
                        }
                        break;
                }
            } else {
                networkType = NetworkType.NETWORK_UNKNOWN;
            }
        }
        return networkType;
    }

    /**
     * 获取 ip 地址
     * <p>{@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @param useIPv4 是否使用 ipv4
     * @return ip 地址
     */
    @RequiresPermission(INTERNET)
    public static String getIpAddress(boolean useIPv4) {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            LinkedList<InetAddress> adds = new LinkedList<>();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                // To prevent phone of xiaomi return "10.0.2.15"
                if (!ni.isUp() || ni.isLoopback()) continue;
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    adds.addFirst(addresses.nextElement());
                }
            }
            for (InetAddress add : adds) {
                if (!add.isLoopbackAddress()) {
                    String hostAddress = add.getHostAddress();
                    boolean isIPv4 = hostAddress.indexOf(':') < 0;
                    if (useIPv4) {
                        if (isIPv4) return hostAddress;
                    } else {
                        if (!isIPv4) {
                            int index = hostAddress.indexOf('%');
                            return index < 0
                                    ? hostAddress.toUpperCase()
                                    : hostAddress.substring(0, index).toUpperCase();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 通过 wifi 获取 ip 地址
     * <p>{@code <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />}</p>
     *
     * @param context
     * @return ip 地址
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    public static String getIpAddressByWifi(@NonNull Context context) {
        @SuppressLint("WifiManagerLeak")
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // noinspection ConstantConditions
        return Formatter.formatIpAddress(wm.getDhcpInfo().ipAddress);
    }

    /**
     * 通过广播获取 ip 地址
     *
     * @return 广播的 ip 地址
     */
    public static String getBroadcastIpAddress() {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            LinkedList<InetAddress> adds = new LinkedList<>();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                if (!ni.isUp() || ni.isLoopback()) continue;
                List<InterfaceAddress> ias = ni.getInterfaceAddresses();
                for (int i = 0; i < ias.size(); i++) {
                    InterfaceAddress ia = ias.get(i);
                    InetAddress broadcast = ia.getBroadcast();
                    if (broadcast != null) {
                        return broadcast.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 通过域名获取 ip 地址
     * <p>{@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @param domain 域名
     * @return ip 地址
     */
    public static String getDomainAddress(@Nullable String domain) {
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getByName(domain);
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 通过 wifi 获取网关
     *
     * @param context
     * @return
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    public static String getGatewayByWifi(@NonNull Context context) {
        @SuppressLint("WifiManagerLeak")
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //noinspection ConstantConditions
        return Formatter.formatIpAddress(wm.getDhcpInfo().gateway);
    }

    /**
     * 通过 wifi 获取网络掩码
     *
     * @param context
     * @return 网络掩码
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    public static String getNetMaskByWifi(@NonNull Context context) {
        @SuppressLint("WifiManagerLeak")
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // noinspection ConstantConditions
        return Formatter.formatIpAddress(wm.getDhcpInfo().netmask);
    }

    /**
     * 通过 wifi 获取服务器地址
     *
     * @param context
     * @return
     */
    @RequiresPermission(ACCESS_WIFI_STATE)
    public static String getServerAddressByWifi(@NonNull Context context) {
        @SuppressLint("WifiManagerLeak")
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // noinspection ConstantConditions
        return Formatter.formatIpAddress(wm.getDhcpInfo().serverAddress);
    }
}
