package com.ueueo.rxpermission;

import android.Manifest;

/**
 * 需要运行时申请的权限
 * <p/>
 * group:android.permission-group.CONTACTS
 * permission:android.permission.WRITE_CONTACTS
 * permission:android.permission.GET_ACCOUNTS
 * permission:android.permission.READ_CONTACTS
 * <p/>
 * group:android.permission-group.PHONE
 * permission:android.permission.READ_CALL_LOG
 * permission:android.permission.READ_PHONE_STATE
 * permission:android.permission.CALL_PHONE
 * permission:android.permission.WRITE_CALL_LOG
 * permission:android.permission.USE_SIP
 * permission:android.permission.PROCESS_OUTGOING_CALLS
 * permission:com.android.voicemail.permission.ADD_VOICEMAIL
 * <p/>
 * group:android.permission-group.CALENDAR
 * permission:android.permission.READ_CALENDAR
 * permission:android.permission.WRITE_CALENDAR
 * <p/>
 * group:android.permission-group.CAMERA
 * permission:android.permission.CAMERA
 * <p/>
 * group:android.permission-group.SENSORS
 * permission:android.permission.BODY_SENSORS
 * <p/>
 * group:android.permission-group.LOCATION
 * permission:android.permission.ACCESS_FINE_LOCATION
 * permission:android.permission.ACCESS_COARSE_LOCATION
 * <p/>
 * group:android.permission-group.STORAGE
 * permission:android.permission.READ_EXTERNAL_STORAGE
 * permission:android.permission.WRITE_EXTERNAL_STORAGE
 * <p/>
 * group:android.permission-group.MICROPHONE
 * permission:android.permission.RECORD_AUDIO
 * <p/>
 * group:android.permission-group.SMS
 * permission:android.permission.READ_SMS
 * permission:android.permission.RECEIVE_WAP_PUSH
 * permission:android.permission.RECEIVE_MMS
 * permission:android.permission.RECEIVE_SMS
 * permission:android.permission.SEND_SMS
 * permission:android.permission.READ_CELL_BROADCASTS
 * <p/>
 * Created by Lee on 16/7/29.
 */
public enum Permission {
    /**
     * 通讯录相关权限
     */
    READ_CONTACTS(Manifest.permission.READ_CONTACTS, Manifest.permission_group.CONTACTS, "允许程序读取用户联系人数据"),
    WRITE_CONTACTS(Manifest.permission.WRITE_CONTACTS, Manifest.permission_group.CONTACTS, "允许程序写入但不读取用户联系人数据"),
    GET_ACCOUNTS(Manifest.permission.GET_ACCOUNTS, Manifest.permission_group.CONTACTS, "允许访问一个在Accounts Service中帐户列表"),

    /**
     * 电话相关权限
     */
    READ_CALL_LOG(Manifest.permission.READ_CALL_LOG, Manifest.permission_group.PHONE, "允许程序读取通话记录"),
    WRITE_CALL_LOG(Manifest.permission.WRITE_CALL_LOG, Manifest.permission_group.PHONE, "允许程序写入通话记录"),
    READ_PHONE_STATE(Manifest.permission.READ_PHONE_STATE, Manifest.permission_group.PHONE, "允许程序读取手机状态"),
    CALL_PHONE(Manifest.permission.CALL_PHONE, Manifest.permission_group.PHONE, "允许程序直接拨打电话"),
    USE_SIP(Manifest.permission.USE_SIP, Manifest.permission_group.PHONE, "允许程序使用SIP视频服务"),
    PROCESS_OUTGOING_CALLS(Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission_group.PHONE, "允许程序监视、修改有关播出电话"),
    ADD_VOICEMAIL(Manifest.permission.ADD_VOICEMAIL, Manifest.permission_group.PHONE, "允许程序添加系统中的语音邮件"),

    /**
     * 日历相关权限
     */
    READ_CALENDAR(Manifest.permission.READ_CALENDAR, Manifest.permission_group.CALENDAR, "允许程序读取用户日历数据"),
    WRITE_CALENDAR(Manifest.permission.WRITE_CALENDAR, Manifest.permission_group.CALENDAR, "允许程序写入但不读取用户日历数据"),

    /**
     * 摄像头相关权限
     */
    CAMERA(Manifest.permission.CAMERA, Manifest.permission_group.CAMERA, "允许使用照相设备"),

    /**
     * 身体传感器相关权限
     */
    BODY_SENSORS(Manifest.permission.BODY_SENSORS, Manifest.permission_group.SENSORS, "允许程序使用传感器"),

    /**
     * 地理位置相关权限
     */
    ACCESS_FINE_LOCATION(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission_group.LOCATION, "允许程序访问精良位置(如GPS)"),
    ACCESS_COARSE_LOCATION(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission_group.LOCATION, "允许程序访问CellID或WiFi热点来获取粗略的位置"),

    /**
     * 存储空间相关权限
     */
    READ_EXTERNAL_STORAGE(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission_group.STORAGE, "允许程序读取外部存储设备"),
    WRITE_EXTERNAL_STORAGE(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission_group.STORAGE, "允许程写入但不读取外部存储设备"),

    /**
     * 麦克风相关权限
     */
    RECORD_AUDIO(Manifest.permission.RECORD_AUDIO, Manifest.permission_group.MICROPHONE, "允许程序录制音频"),

    /**
     * 短信相关权限
     */
    READ_SMS(Manifest.permission.READ_SMS, Manifest.permission_group.SMS, "允许程序读取短信息"),
    RECEIVE_WAP_PUSH(Manifest.permission.RECEIVE_WAP_PUSH, Manifest.permission_group.SMS, "允许程序监控收到WAP PUSH信息"),
    RECEIVE_MMS(Manifest.permission.RECEIVE_MMS, Manifest.permission_group.SMS, "允许程序监控,记录或处理收到MMS彩信"),
    RECEIVE_SMS(Manifest.permission.RECEIVE_SMS, Manifest.permission_group.SMS, "允许程序监控,记录或处理收到短信息，"),
    SEND_SMS(Manifest.permission.SEND_SMS, Manifest.permission_group.SMS, "允许程序发送SMS短信");

    //权限名称
    public final String name;
    //权限所属分组
    public final String group;
    //权限说明
    public final String desc;
    //是否已授权
    public boolean granted;

    Permission(String name, String group, String desc) {
        this.name = name;
        this.group = group;
        this.desc = desc;
    }

    public static Permission getPermission(String name) {
        for (Permission permission : values()) {
            if (name != null && permission.name.equals(name)) {
                return permission;
            }
        }
        return null;
    }

    public boolean granted() {
        return granted;
    }

    public Permission setGranted(boolean granted) {
        this.granted = granted;
        return this;
    }
}
