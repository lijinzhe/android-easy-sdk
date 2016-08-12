package com.ueueo.sample;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lee on 16/8/12.
 */
public abstract class AbsListActivity extends AppCompatActivity {
    private ArrayList<ItemObject> mItems = new ArrayList<ItemObject>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView listview = new ListView(this);
        setContentView(listview);
        initItemDatas();
        ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
        for (ItemObject item : mItems) {
            Map<String, String> m = new HashMap<String, String>();
            m.put("text", item.text);
            data.add(m);
        }
        listview.setAdapter(new SimpleAdapter(this, data, android.R.layout.simple_list_item_1, new String[] {"text"}, new int[] {android.R.id.text1}));
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mItems.get(position).onItemClick();
            }
        });
    }

    public abstract void initItemDatas();

    protected void addItemData(ItemObject item) {
        if (item != null) {
            mItems.add(item);
        }
    }

    public static abstract class ItemObject {
        public String text;

        public ItemObject(String text) {
            this.text = text;
        }

        public abstract void onItemClick();
    }

    private Toast mToast = null;

    public final void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mToast != null) {
                        mToast.setText(message);
                        mToast.setDuration(Toast.LENGTH_SHORT);
                    } else {
                        mToast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                        mToast.show();
                    }
                    View iView = mToast.getView();
                    mToast.show();
                    mToast.setView(iView);
                } catch (Exception e) {
                }
            }
        });
    }

    public final void showToast(final int resId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mToast != null) {
                        mToast.setText(resId);
                        mToast.setDuration(Toast.LENGTH_SHORT);
                    } else {
                        mToast = Toast.makeText(getApplicationContext(), resId, Toast.LENGTH_SHORT);
                        mToast.show();
                    }
                    View iView = mToast.getView();
                    mToast.show();
                    mToast.setView(iView);
                } catch (Exception e) {
                }
            }
        });
    }
}

