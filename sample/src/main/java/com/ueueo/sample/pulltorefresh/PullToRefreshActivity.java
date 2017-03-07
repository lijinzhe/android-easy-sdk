package com.ueueo.sample.pulltorefresh;

import android.content.Intent;

import com.ueueo.sample.AbsListActivity;

public class PullToRefreshActivity extends AbsListActivity {

    @Override
    public void initItemDatas() {
        addItemData(new AbsListActivity.ItemObject("ListView") {
            @Override
            public void onItemClick() {
                Intent intent = new Intent(PullToRefreshActivity.this,PTRListViewActivity.class);
                startActivity(intent);
            }
        });
        addItemData(new AbsListActivity.ItemObject("GridView") {
            @Override
            public void onItemClick() {

            }
        });
        addItemData(new AbsListActivity.ItemObject("RecylerView") {
            @Override
            public void onItemClick() {
                Intent intent = new Intent(PullToRefreshActivity.this,PTRRecylerViewActivity.class);
                startActivity(intent);
            }
        });
        addItemData(new AbsListActivity.ItemObject("ScrollView") {
            @Override
            public void onItemClick() {

            }
        });
    }
}
