package com.ueueo.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;

/**
 * 网络类型工具类 <br>
 * 需要添加如下权限：<br>
 * <uses-permission android:name="android.permission.INTERNET" />
 * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
 *
 * @author Lee
 * @date 2014-3-19
 */
public class NetworkUtil {

    /**
     * 打开wifi网络设置界面
     *
     * @param context
     */
    public static void openWiFiSetting(Context context) {
        if (context == null) {
            throw new RuntimeException("context must not null");
        }
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            //3.0以下打开设置界面
            context.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
        } else {
            context.startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
        }
    }

    /**
     * 打开网络设置界面
     *
     * @param context
     */
    public static void openNetworkSetting(Context context) {
        if (context == null) {
            throw new RuntimeException("context must not null");
        }
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            context.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
        } else {
            context.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
        }
    }

    /**
     * 判断是否有网络
     *
     * @return
     */
    public static boolean isConnected(Context context) {
        if (context == null) {
            throw new RuntimeException("context must not null");
        }
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netwrokInfo = cm.getActiveNetworkInfo();
        return netwrokInfo == null ? false : netwrokInfo.isConnected();
    }

    /**
     * 判断是否是wifi
     */
    public static boolean isWiFiConnected(Context context) {
        if (context == null) {
            throw new RuntimeException("context must not null");
        }
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm != null && cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 获取网络类型
     *
     * @return
     */
    public static NetType getNetType(Context context) {
        if (context == null) {
            throw new RuntimeException("context must not null");
        }
        NetType netType = null;
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            int nType = networkInfo.getType();
            switch (nType) {
                case ConnectivityManager.TYPE_MOBILE:
                    int subType = networkInfo.getSubtype();
                    if (android.os.Build.VERSION.SDK_INT < 15) {
                        switch (subType) {
                            case TelephonyManager.NETWORK_TYPE_GPRS:
                            case TelephonyManager.NETWORK_TYPE_EDGE:
                            case TelephonyManager.NETWORK_TYPE_CDMA:
                            case TelephonyManager.NETWORK_TYPE_1xRTT:
                            case TelephonyManager.NETWORK_TYPE_IDEN:
                                netType = NetType.NET_2G;
                                break;
                            case TelephonyManager.NETWORK_TYPE_UMTS:
                            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                            case TelephonyManager.NETWORK_TYPE_HSDPA:
                            case TelephonyManager.NETWORK_TYPE_HSUPA:
                            case TelephonyManager.NETWORK_TYPE_HSPA:
                            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                                netType = NetType.NET_3G;
                                break;
                            default:
                                netType = NetType.NET_UNKNOW;
                                break;
                        }
                    } else {
                        // 4.1开始判断4G网络
                        switch (subType) {
                            case TelephonyManager.NETWORK_TYPE_GPRS:
                            case TelephonyManager.NETWORK_TYPE_EDGE:
                            case TelephonyManager.NETWORK_TYPE_CDMA:
                            case TelephonyManager.NETWORK_TYPE_1xRTT:
                            case TelephonyManager.NETWORK_TYPE_IDEN:
                                netType = NetType.NET_2G;
                                break;
                            case TelephonyManager.NETWORK_TYPE_UMTS:
                            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                            case TelephonyManager.NETWORK_TYPE_HSDPA:
                            case TelephonyManager.NETWORK_TYPE_HSUPA:
                            case TelephonyManager.NETWORK_TYPE_HSPA:
                            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                            case TelephonyManager.NETWORK_TYPE_EHRPD:
                            case TelephonyManager.NETWORK_TYPE_HSPAP:
                                netType = NetType.NET_3G;
                                break;
                            case TelephonyManager.NETWORK_TYPE_LTE:
                                netType = NetType.NET_4G;
                                break;
                            default:
                                netType = NetType.NET_UNKNOW;
                                break;
                        }
                    }
                    break;
                case ConnectivityManager.TYPE_WIFI:
                    netType = NetType.NET_WIFI;
                    break;
                default:
                    netType = NetType.NET_UNKNOW;
                    break;
            }
            return netType;
        } else {
            return NetType.NET_NONE;
        }

    }

    /**
     * 查看运营商代理类型
     *
     * @return 返回 {@link APNType}
     */
    public static APNType getAPNType(Context context) {
        if (context == null) {
            throw new RuntimeException("context must not null");
        }
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            String extraInfo = networkInfo.getExtraInfo();
            if (extraInfo.startsWith(APNType.APN_3GNET.value())) {
                return APNType.APN_3GNET;
            } else if (extraInfo.startsWith(APNType.APN_3GWAP.value())) {
                return APNType.APN_3GWAP;
            } else if (extraInfo.startsWith(APNType.APN_CMNET.value())) {
                return APNType.APN_CMNET;
            } else if (extraInfo.startsWith(APNType.APN_CMWAP.value())) {
                return APNType.APN_CMWAP;
            } else if (extraInfo.startsWith(APNType.APN_CTNET.value())) {
                return APNType.APN_CTNET;
            } else if (extraInfo.startsWith(APNType.APN_CTWAP.value())) {
                return APNType.APN_CTWAP;
            } else if (extraInfo.startsWith(APNType.APN_UNINET.value())) {
                return APNType.APN_UNINET;
            } else if (extraInfo.startsWith(APNType.APN_UNIWAP.value())) {
                return APNType.APN_UNIWAP;
            }
        }
        return APNType.APN_UNKNOW;
    }

    /**
     * 获取移动运营商
     *
     * @param context
     * @return
     */
    public static Operator getOperator(Context context) {
        if (context == null) {
            throw new RuntimeException("context must not null");
        }
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            String extraInfo = networkInfo.getExtraInfo();
            if (extraInfo.startsWith(APNType.APN_3GNET.value())) {
                return Operator.CHINA_UNICOM;
            } else if (extraInfo.startsWith(APNType.APN_3GWAP.value())) {
                return Operator.CHINA_UNICOM;
            } else if (extraInfo.startsWith(APNType.APN_CMNET.value())) {
                return Operator.CHINA_MOBILE;
            } else if (extraInfo.startsWith(APNType.APN_CMWAP.value())) {
                return Operator.CHINA_MOBILE;
            } else if (extraInfo.startsWith(APNType.APN_CTNET.value())) {
                return Operator.CHINA_TELECOM;
            } else if (extraInfo.startsWith(APNType.APN_CTWAP.value())) {
                return Operator.CHINA_TELECOM;
            } else if (extraInfo.startsWith(APNType.APN_UNINET.value())) {
                return Operator.CHINA_UNICOM;
            } else if (extraInfo.startsWith(APNType.APN_UNIWAP.value())) {
                return Operator.CHINA_UNICOM;
            }
        }
        return Operator.UNKNOW;
    }

    /**
     * 移动运营商
     */
    public enum Operator {
        /**
         * 中国移动
         */
        CHINA_MOBILE,
        /**
         * 中国联通
         */
        CHINA_UNICOM,
        /**
         * 中国电信
         */
        CHINA_TELECOM,
        /**
         * 未知运营商
         */
        UNKNOW
    }

    public enum NetType {
        /**
         * wifi网络
         */
        NET_WIFI,
        /**
         * 4g网络
         */
        NET_4G,
        /**
         * 3g网络
         */
        NET_3G,
        /**
         * 2g网络
         */
        NET_2G,
        /**
         * 未知网络类型
         */
        NET_UNKNOW,
        /**
         * 无网络
         */
        NET_NONE
    }

    public enum APNType {
        /**
         * 中国电信ctnet
         */
        APN_CTNET("ctnet"),
        /**
         * 中国电信ctwap
         */
        APN_CTWAP("ctwap"),
        /**
         * 中国移动cmnet
         */
        APN_CMNET("cmnet"),
        /**
         * 中国移动cmwap
         */
        APN_CMWAP("cmwap"),
        /**
         * 中国联通uni net
         */
        APN_UNINET("uninet"),
        /**
         * 中国联通uni wap
         */
        APN_UNIWAP("uniwap"),
        /**
         * 中国联通3g net
         */
        APN_3GNET("3gnet"),
        /**
         * 中国联通3g wap
         */
        APN_3GWAP("3gwap"),
        /**
         * 未知类型
         */
        APN_UNKNOW("unknow");

        private String value;

        APNType(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }
    }
}
