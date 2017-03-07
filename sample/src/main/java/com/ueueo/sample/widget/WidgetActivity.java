package com.ueueo.sample.widget;

import android.content.Intent;

import com.ueueo.sample.AbsListActivity;
import com.ueueo.sample.glidetransfer.GlideTransferActivity;

public class WidgetActivity extends AbsListActivity {


    @Override
    public void initItemDatas() {
        addItemData(new ItemObject("UEImageView") {
            @Override
            public void onItemClick() {
                Intent intent = new Intent(WidgetActivity.this, UEImageViewActivity.class);
                startActivity(intent);
            }
        });
        addItemData(new ItemObject("UETextView") {
            @Override
            public void onItemClick() {
                Intent intent = new Intent(WidgetActivity.this, UETextViewActivity.class);
                startActivity(intent);
            }
        });
    }
}
