package com.ueueo.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
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
                if(wifiInfo != null) {
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
}
