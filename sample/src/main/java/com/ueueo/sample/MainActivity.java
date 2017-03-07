package com.ueueo.sample;

import android.content.Intent;

import com.ueueo.sample.glidetransfer.GlideTransferActivity;
import com.ueueo.sample.pulltorefresh.PullToRefreshActivity;
import com.ueueo.sample.rxjava.RxJavaActivity;
import com.ueueo.sample.widget.WidgetActivity;

public class MainActivity extends AbsListActivity {

    @Override
    public void initItemDatas() {
        addItemData(new ItemObject("glidetransfer") {
            @Override
            public void onItemClick() {
                Intent intent = new Intent(MainActivity.this, GlideTransferActivity.class);
                startActivity(intent);
            }
        });
        addItemData(new ItemObject("widget") {
            @Override
            public void onItemClick() {
                Intent intent = new Intent(MainActivity.this, WidgetActivity.class);
                startActivity(intent);
            }
        });
        addItemData(new ItemObject("pull to refresh") {
            @Override
            public void onItemClick() {
                Intent intent = new Intent(MainActivity.this, PullToRefreshActivity.class);
                startActivity(intent);
            }
        });
        addItemData(new ItemObject("rxjava") {
            @Override
            public void onItemClick() {
                Intent intent = new Intent(MainActivity.this, RxJavaActivity.class);
                startActivity(intent);
            }
        });
    }
}
