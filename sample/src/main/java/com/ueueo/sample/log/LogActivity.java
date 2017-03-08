package com.ueueo.sample.log;

import android.os.Bundle;

import com.ueueo.log.UELog;
import com.ueueo.sample.AbsListActivity;

import java.util.HashMap;
import java.util.Map;

public class LogActivity extends AbsListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initItemDatas() {
        addItemData(new ItemObject("methodCount:5 methodOffset:0") {
            @Override
            public void onItemClick() {
                UELog.init("UEUEO", 0);
                UELog.tag("lijinzhe").i("kfjalskdflasdfj");
                UELog.e("日志输出 methodCount:5 methodOffset:0");
            }
        });
        addItemData(new ItemObject("methodCount:5 methodOffset:1") {
            @Override
            public void onItemClick() {
                UELog.init("UEUEO");
                UELog.wtf("日志输出 methodCount:5 methodOffset:1");
            }
        });
        addItemData(new ItemObject("append object") {
            @Override
            public void onItemClick() {
                UELog.append("123123123\n12312312312");
                Map<String, String> map = new HashMap<String, String>();
                map.put("name", "lijinzhe");
                map.put("age", "112");
                UELog.appendObject(map);
                UELog.file(true).i("kjfakfhkshdkfhkh");

                UELog.method(0).file(true).i("11111111111");
                UELog.method(0).i("22222222222");
                UELog.method(0).i("33333333333");
                UELog.method(0).i("444444");
                UELog.method(0).i("555555555");
                UELog.method(0).file(true).i("666666\n66767676");
            }
        });
    }
}
