package pers.liyi.bullet.utils.box;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.core.content.ContextCompat;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class LocationUtils {
    private static final String TAG = "Bullet-" + LocationUtils.class.getSimpleName();
    private static final int TIME_LIMIT = 1000 * 60 * 2;
    private static String sGoogleMapApi = "http://maps.google.cn/maps/api/geocode/json?latlng=%d,%d&language=zh-CN&sensor=true";

    private static LocationManager sLocationManager;
    private static MyLocationListener sLocationListener;
    private static OnLocationChangeListener sLocationChangedListener;


    /**
     * 判断 GPS 是否可用
     *
     * @param context
     * @return {@code true}: 可用 <br> {@code false}: 不可用
     */
    public static boolean isGpsEnabled(@NonNull Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 判断定位是否可用（网络定位和 GPS 定位有一个可用即可）
     *
     * @param context
     * @return {@code true}: 可用 <br>{@code false}: 不可用
     */
    public static boolean isLocationEnabled(@NonNull Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                || lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 前往 GPS 设置页面
     *
     * @param context
     */
    public static void toGpsSettings(@NonNull Context context) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @RequiresPermission(ACCESS_FINE_LOCATION)
    public static boolean register(@NonNull Context context, OnLocationChangeListener listener) {
        return register(context, 2000, 1, listener);
    }

    /**
     * 注册定位监听
     * <p>在不需要使用的时候需要调用 {@link #unregister()} </p>
     * <p>{@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     * <p>{@code <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />}</p>
     * <p>{@code <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />}</p>
     * <p>如果{@code minDistance} = 0, 则按照 {@code minTime} 来定期更新位置信息</p>
     * <p>如果{@code minDistance} != 0, 则按照 {@code minDistance} 来更新位置信息</p>
     * <p>如果{@code minTime} 和 {@code minDistance} 都为 0, 则随时更新位置信息</p>
     *
     * @param context
     * @param minTime     位置信息更新的最小周期（单位：毫秒）
     * @param minDistance 位置变化的最小距离：当位置距离的值变化超过 minDistance 时，将更新位置信息（单位：米）
     * @param listener    位置更新的回调接口
     * @return {@code true}: 注册成功 <br> {@code false}: 注册失败
     */
    @RequiresPermission(anyOf = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
    public static boolean register(@NonNull Context context, long minTime, long minDistance, OnLocationChangeListener listener) {
        if (listener == null) return false;
        sLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // noinspection ConstantConditions
        if (!sLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                && !sLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            LogUtils.d(TAG, "无法定位，请打开定位服务");
            return false;
        }
        sLocationChangedListener = listener;
        String provider = sLocationManager.getBestProvider(getCriteria(), true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                ToastUtils.show(context,"没有 ACCESS_COARSE_LOCATION 或 ACCESS_FINE_LOCATION 权限");
                return false;
            }
        }
        Location location = sLocationManager.getLastKnownLocation(provider);
        if (location != null) listener.getLastKnownLocation(location);
        if (sLocationListener == null) sLocationListener = new MyLocationListener();
        sLocationManager.requestLocationUpdates(provider, minTime, minDistance, sLocationListener);
        return true;
    }

    /**
     * 取消定位监听注册
     */
    @RequiresPermission(ACCESS_COARSE_LOCATION)
    public static void unregister() {
        if (sLocationManager != null) {
            if (sLocationListener != null) {
                sLocationManager.removeUpdates(sLocationListener);
                sLocationListener = null;
            }
            sLocationManager = null;
        }
        if (sLocationChangedListener != null) {
            sLocationChangedListener = null;
        }
    }

    /**
     * 获取定位配置参数
     *
     * @return {@link Criteria}
     */
    private static Criteria getCriteria() {
        Criteria criteria = new Criteria();
        // 设置定位精确度 Criteria.ACCURACY_COARSE 比较粗略，Criteria.ACCURACY_FINE 则比较精细
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 设置是否要求速度
        criteria.setSpeedRequired(false);
        // 设置是否允许运营商收费
        criteria.setCostAllowed(false);
        // 设置是否需要方位信息
        criteria.setBearingRequired(false);
        // 设置是否需要海拔信息
        criteria.setAltitudeRequired(false);
        // 设置对电源的需求
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }

    /**
     * 通过经纬度获取地理定位
     *
     * @param context
     * @param latitude
     * @param longitude
     * @return {@link Address}
     */
    public static Address getAddress(@NonNull Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) return addresses.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

//    public static Address getAddress(double latitude, double longitude) {
//        try {
//            String api = sGoogleMapApi;
//            api = String.format(api, latitude, longitude);
//            URL url = new URL(api);
//            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
//            urlConn.setConnectTimeout(5 * 1000);
//            urlConn.setReadTimeout(5 * 1000);
//            urlConn.setUseCaches(true);
//            urlConn.setRequestMethod("GET");
//            urlConn.setRequestProperty("Content-Type", "application/json");
//            urlConn.addRequestProperty("Connection", "Keep-Alive");
//            urlConn.connect();
//            // 判断请求是否成功
//            if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
//                StringBuilder builder = new StringBuilder();
//                BufferedReader bufferedReader2 = new BufferedReader(
//                        new InputStreamReader(httpResponse.getEntity()
//                                .getContent()));
//                for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2
//                        .readLine()) {
//                    builder.append(s);
//                }
//                /**
//                 * 这里需要分析服务器回传的json格式数据，
//                 */
//                JSONObject jsonObject = new JSONObject(builder
//                        .toString());
//                JSONArray jsonArray = jsonObject
//                        .getJSONArray("Placemark");
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject jsonObject2 = (JSONObject) jsonArray
//                            .opt(i);
//                    JSONObject jsonObject3 = new JSONObject(jsonObject2
//                            .getString("Point"));
//                    JSONArray jsonArray1 = jsonObject3
//                            .getJSONArray("coordinates");
//                    location[0] = (String) jsonArray1.get(0);
//                    location[1] = (String) jsonArray1.get(1);
//                }
//
//            } else {
//
//            }
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (ProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    /**
     * 通过经纬度获取国家名
     *
     * @param latitude
     * @param longitude
     * @return the country name
     */
    public static String getCountryName(@NonNull Context context, double latitude, double longitude) {
        Address address = getAddress(context, latitude, longitude);
        return address == null ? "unknown" : address.getCountryName();
    }

    /**
     * 通过经纬度获取指定地点信息
     *
     * @param context
     * @param latitude
     * @param longitude
     * @return the locality
     */
    public static String getLocality(@NonNull Context context, double latitude, double longitude) {
        Address address = getAddress(context, latitude, longitude);
        return address == null ? "unknown" : address.getLocality();
    }

    /**
     * 通过经纬度获取指定街道名
     *
     * @param context
     * @param latitude
     * @param longitude
     * @return the street name
     */
    public static String getStreet(@NonNull Context context, double latitude, double longitude) {
        Address address = getAddress(context, latitude, longitude);
        return address == null ? "unknown" : address.getAddressLine(0);
    }

    /**
     * 判断是否是同一个 provider
     *
     * @param provider1
     * @param provider2
     * @return {@code true}: 是 <br> {@code false}: 否
     */
    public static boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    private static class MyLocationListener implements LocationListener {
        /**
         * 当坐标改变时会触发此函数，但是如果 provider 传递相同的坐标，则不会触发
         *
         * @param location
         */
        @Override
        public void onLocationChanged(Location location) {
            if (sLocationChangedListener != null) {
                sLocationChangedListener.onLocationChanged(location);
            }
        }

        /**
         * provider 的可用、暂时不可用和无服务三个状态直接切换时触发此函数
         *
         * @param provider 提供者
         * @param status   状态
         * @param extras   provider可选包
         */
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (sLocationChangedListener != null) {
                sLocationChangedListener.onStateChanged(provider, status, extras);
            }
            switch (status) {
                case LocationProvider.AVAILABLE:
                    LogUtils.d(TAG, "当前GPS状态为可见状态");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    LogUtils.d(TAG, "当前GPS状态为服务区外状态");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    LogUtils.d(TAG, "当前GPS状态为暂停服务状态");
                    break;
            }
        }

        /**
         * provider 被 enable 时触发此函数，比如 GPS 被打开
         */
        @Override
        public void onProviderEnabled(String provider) {
        }

        /**
         * provider 被 disable 时触发此函数，比如 GPS 被关闭
         */
        @Override
        public void onProviderDisabled(String provider) {
        }
    }

    public interface OnLocationChangeListener {

        /**
         * 获取最后一次保留的坐标
         *
         * @param location 坐标
         */
        void getLastKnownLocation(Location location);

        /**
         * 当坐标改变时触发此函数，如果 provider 传进相同的坐标，它就不会被触发
         *
         * @param location 坐标
         */
        void onLocationChanged(Location location);

        /**
         * provider 的可用、暂时不可用和无服务三个状态直接切换时触发此函数
         *
         * @param provider 提供者
         * @param state   状态
         * @param extras   provider可选包
         */
        void onStateChanged(String provider, int state, Bundle extras);// 位置状态发生改变
    }
}
