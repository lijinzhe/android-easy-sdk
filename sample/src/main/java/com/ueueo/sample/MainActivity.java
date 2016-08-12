package com.ueueo.sample;

import android.content.Intent;

import com.ueueo.sample.glidetransfer.GlideTransferActivity;

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
    }
}
