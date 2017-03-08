package com.ueueo.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.UUID;

/**
 * Created by Lee on 16/8/15.
 */
public class UEDeviceUtil {
    /**
     * 获取设备唯一ID
     *
     * @return
     */
    public static String getDeviceId(Context context) {
        String identifie = null;
        /*
         * 经过友盟统计，97.1%的手机获取IMEI，2.2%的手机获取MAC地址，0.6%的手机获取AndroidID，没有手机使用生成地方UUID
         */
        if (identifie == null || "".equals(identifie)) {
            // 获取手机IMEI
            TelephonyManager iTMManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            try {
                identifie = iTMManager.getDeviceId();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!TextUtils.isEmpty(identifie)) {
            return "IMEI_" + identifie;
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED) {
            // 当手机IMEI不存在时，获取MAC地址
            WifiManager iWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (iWifiManager != null) {
                WifiInfo wifiInfo = iWifiManager.getConnectionInfo();
                if (wifiInfo != null) {
                    identifie = wifiInfo.getMacAddress();
                    if (!TextUtils.isEmpty(identifie)) {
                        return "MAC_" + identifie;
                    }
                }
            }
        }

        // 当MAC地址也不存在时，获取AndroidID
        identifie = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (!TextUtils.isEmpty(identifie)) {
            return "ANDROID_ID_" + identifie;
        }

        // 当AndriodID也不存在时，生成UUID
        UUID uuid = UUID.randomUUID();
        if (uuid != null) {
            identifie = uuid.toString();
        }

        if (!TextUtils.isEmpty(identifie)) {
            return "UUID_" + identifie;
        } else {
            return "NULL";
        }
    }

    /**
     * 打印手机状态信息
     * <p>需添加权限<uses-permission android:name="android.permission.READ_PHONE_STATE"/>
     */
    public static String getPhoneInfo(Context context) {
        if (context == null) {
            throw new RuntimeException("context must not null");
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (context.getPackageManager().checkPermission(Manifest.permission.READ_PHONE_STATE, context.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                return "Must have permission 'android.permission.READ_PHONE_STATE'";
            }
        } else {
            if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return "Must have permission 'android.permission.READ_PHONE_STATE'";
            }
        }
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("DeviceId(IMEI) = ").append(tm.getDeviceId()).append("\n");
        stringBuilder.append("DeviceSoftwareVersion = ").append(tm.getDeviceSoftwareVersion()).append("\n");
        stringBuilder.append("Line1Number = ").append(tm.getLine1Number()).append("\n");
        stringBuilder.append("NetworkCountryIso = ").append(tm.getNetworkCountryIso()).append("\n");
        stringBuilder.append("NetworkOperator = ").append(tm.getNetworkOperator()).append("\n");
        stringBuilder.append("NetworkOperatorName = ").append(tm.getNetworkOperatorName()).append("\n");
        stringBuilder.append("NetworkType = ").append(tm.getNetworkType()).append("\n");
        stringBuilder.append("honeType = ").append(tm.getPhoneType()).append("\n");
        stringBuilder.append("SimCountryIso = ").append(tm.getSimCountryIso()).append("\n");
        stringBuilder.append("SimOperator = ").append(tm.getSimOperator()).append("\n");
        stringBuilder.append("SimOperatorName = ").append(tm.getSimOperatorName()).append("\n");
        stringBuilder.append("SimSerialNumber = ").append(tm.getSimSerialNumber()).append("\n");
        stringBuilder.append("SimState = ").append(tm.getSimState()).append("\n");
        stringBuilder.append("SubscriberId(IMSI) = ").append(tm.getSubscriberId()).append("\n");
        stringBuilder.append("VoiceMailNumber = ").append(tm.getVoiceMailNumber()).append("\n");
        return stringBuilder.toString();
    }
}
