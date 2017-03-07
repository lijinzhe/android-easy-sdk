package com.ueueo.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 电话相关工具
 * Created by Lee on 16/7/20.
 */
public class UEPhoneUtil {

    /**
     * 判断设备是否是手机
     * <p/>
     * 需添加权限
     * <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
     */
    public static boolean isPhone(Context context) {
        if (context == null) {
            throw new RuntimeException("context must not null");
        }
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE;
    }

    /**
     * 获取电话号码
     * <p/>
     * 需要权限
     * <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
     */
    public static String getPhoneNumber(Context context) {
        if (context == null) {
            throw new RuntimeException("context must not null");
        }
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getLine1Number();
    }

    /**
     * 获取当前设备的IMIE
     * <p/>
     * 需添加权限
     * <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
     */
    public static String getIMEI(Context context) {
        if (context == null) {
            throw new RuntimeException("context must not null");
        }
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    /**
     * 跳转至拨号界面
     *
     * @param context
     * @param phoneNumber 默认的电话号码
     */
    public static void dial(Context context, String phoneNumber) {
        context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber)));
    }

    /**
     * 直接拨打电话
     * <p/>
     * 需添加权限
     * <uses-permission android:name="android.permission.CALL_PHONE"/>
     *
     * @param context
     * @param phoneNumber 电话号码
     */
    public static void call(Context context, String phoneNumber) {
        if (context == null) {
            throw new RuntimeException("context must not null");
        }
        if (!TextUtils.isEmpty(phoneNumber)) {
            context.startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + phoneNumber)));
        }
    }

    /**
     * 发送短信
     * <p/>
     * 打开短信编辑界面
     */
    public static void sendSms(Context context, String phoneNumber, String content) {
        if (context == null) {
            throw new RuntimeException("context must not null");
        }
        Uri uri = Uri.parse("smsto:" + (TextUtils.isEmpty(phoneNumber) ? "" : phoneNumber));
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", TextUtils.isEmpty(content) ? "" : content);
        context.startActivity(intent);
    }

    /**
     * 获取手机联系人
     * <p/>
     * 需添加权限
     * <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
     * <uses-permission android:name="android.permission.READ_CONTACTS" />
     */
    public static List<Contact> getAllContacts(Context context) {
        if (context == null) {
            throw new RuntimeException("context must not null");
        }
        ArrayList<Contact> list = new ArrayList<>();
        // 1.获取内容解析者
        ContentResolver resolver = context.getContentResolver();
        // 2.获取内容提供者的地址:com.android.contacts
        // raw_contacts表的地址 :raw_contacts
        // view_data表的地址 : data
        // 3.生成查询地址
        Uri raw_uri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri date_uri = Uri.parse("content://com.android.contacts/data");
        // 4.查询操作,先查询raw_contacts,查询contact_id
        // projection : 查询的字段
        Cursor cursor = resolver.query(raw_uri, new String[]{"contact_id"}, null, null, null);
        Contact contact = null;
        // 5.解析cursor
        while (cursor.moveToNext()) {
            // 6.获取查询的数据
            String contact_id = cursor.getString(0);
            // cursor.getString(cursor.getColumnIndex("contact_id"));//getColumnIndex
            // : 查询字段在cursor中索引值,一般都是用在查询字段比较多的时候
            // 判断contact_id是否为空
            if (!TextUtils.isEmpty(contact_id)) {//null   ""
                // 7.根据contact_id查询view_data表中的数据
                // selection : 查询条件
                // selectionArgs :查询条件的参数
                // sortOrder : 排序
                // 空指针: 1.null.方法 2.参数为null
                Cursor c = resolver.query(date_uri, new String[]{"data1", "mimetype"}, "raw_contact_id=?", new String[]{contact_id}, null);
                // 8.解析c
                contact = new Contact();
                while (c.moveToNext()) {
                    // 9.获取数据
                    String data1 = c.getString(0);
                    String mimetype = c.getString(1);
                    // 10.根据类型去判断获取的data1数据并保存
                    if (mimetype.equals("vnd.android.cursor.item/phone_v2")) {
                        // 电话
                        contact.phone = data1;
                    } else if (mimetype.equals("vnd.android.cursor.item/name")) {
                        // 姓名
                        contact.name = data1;
                    }
                }
                // 11.添加到集合中数据
                list.add(contact);
                // 12.关闭cursor
                c.close();
            }
        }
        // 12.关闭cursor
        cursor.close();
        return list;
    }

    /**
     * 获取手机短信
     * <p/>
     * 需添加权限
     * <uses-permission android:name="android.permission.READ_SMS"/>
     */
    public static List<Message> getAllMessages(Context context) {
        List<Message> messages = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        Uri uri = Uri.parse("content://sms");
        Cursor cursor = resolver.query(uri, new String[]{"address", "date", "type", "body"}, null, null, null);
        int count = cursor.getCount();//获取短信的个数
        try {
            Message message = null;
            while (cursor.moveToNext()) {
                message = new Message();
                message.address = cursor.getString(0);
                message.date = cursor.getString(1);
                message.type = cursor.getString(2);
                message.body = cursor.getString(3);
                messages.add(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return messages;
    }

    /**
     * 联系人
     */
    public static class Contact implements Parcelable {
        public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
            @Override
            public Contact createFromParcel(Parcel source) {
                return new Contact(source);
            }

            @Override
            public Contact[] newArray(int size) {
                return new Contact[size];
            }
        };
        public String name;
        public String phone;

        public Contact() {
        }

        protected Contact(Parcel in) {
            this.name = in.readString();
            this.phone = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.name);
            dest.writeString(this.phone);
        }
    }

    /**
     * 短信
     */
    public static class Message implements Parcelable {
        public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {
            @Override
            public Message createFromParcel(Parcel source) {
                return new Message(source);
            }

            @Override
            public Message[] newArray(int size) {
                return new Message[size];
            }
        };
        public String address;
        public String date;
        public String type;
        public String body;

        public Message() {
        }

        protected Message(Parcel in) {
            this.address = in.readString();
            this.date = in.readString();
            this.type = in.readString();
            this.body = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.address);
            dest.writeString(this.date);
            dest.writeString(this.type);
            dest.writeString(this.body);
        }
    }
}
