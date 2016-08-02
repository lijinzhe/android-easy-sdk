package com.easy.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.ueueo.logger.Logger;
import com.ueueo.rxpermission.Permission;
import com.ueueo.rxpermission.RxPermission;

import java.util.HashMap;
import java.util.Map;

import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView textView = (TextView)findViewById(R.id.text);

        Logger.init("lijinzhe");


        Logger.i("pifubao   %s","  hello world!");
        Logger.tag("lijinzhe").i("lijinzhe   %s","  hello world!");
        Logger.i("3333333   %s","  hello world!");


//        Logger.tag("lijinzhe").method(0).header("你好啊").footer("佛卡是江东父老看拉萨借读费").object(new User());

//        Logger.tag("lijinzhe").method(0).footer("11112312312312312").footer("213123123123123").object(new User());

        Map<String,User> userMap = new HashMap<>();
        userMap.put("user1",new User());
        userMap.put("user2",new User());
        Logger.tag("lijinzhe").method(0).header("11112312312312312").headerObject(new User()).object(userMap);

        RxPermission.requestEach(this, Permission.ACCESS_COARSE_LOCATION).subscribe(new Action1<Permission>() {
            @Override
            public void call(Permission permission) {
                textView.setText(""+permission.name+"  "+permission.granted);
                Logger.i("request permission %s %s",permission.name,permission.granted());
            }
        });
    }

    public static class User{
        public int id = 1;
        public String name = "李金哲";
        public int age = 33;
    }
}
