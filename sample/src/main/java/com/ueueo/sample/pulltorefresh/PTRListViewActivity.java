package com.ueueo.sample.pulltorefresh;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ueueo.pulltorefresh.UEPullToRefreshLayout;
import com.ueueo.pulltorefresh.base.OnLoadMoreListener;
import com.ueueo.pulltorefresh.base.OnRefreshListener;
import com.ueueo.sample.R;

public class PTRListViewActivity extends AppCompatActivity {

    private int size = 10;

    private UEPullToRefreshLayout mPtrFrame;

    private BaseAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ptr_listview);
        final ListView listView = (ListView) findViewById(R.id.rotate_header_list_view);
        listView.setAdapter(mAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return size;
            }

            @Override
            public Object getItem(int position) {
                return position;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    TextView textview = new TextView(PTRListViewActivity.this);
                    textview.setPadding(30, 50, 30, 50);
                    convertView = textview;
                }
                ((TextView) convertView).setText("POSITION:" + position);
                return convertView;
            }
        });

        mPtrFrame = (UEPullToRefreshLayout) findViewById(R.id.rotate_header_list_view_frame);
        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefreshBegin() {
                refresh();
            }
        });
        mPtrFrame.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMoreBegin() {
                loadMore();
            }
        });
        // default is false
        mPtrFrame.setPullToRefreshEnable(true);
        mPtrFrame.setPullToLoadMoreEnable(true);
    }

    private void refresh() {
        mPtrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                size = 10;
                mPtrFrame.refreshComplete();
                mAdapter.notifyDataSetChanged();
            }
        }, 3000);
    }

    private void loadMore() {
        mPtrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                size += 10;
                mPtrFrame.refreshComplete();
                mPtrFrame.setPullToLoadMoreEnable(false);
                mAdapter.notifyDataSetChanged();
            }
        }, 3000);
    }
}
