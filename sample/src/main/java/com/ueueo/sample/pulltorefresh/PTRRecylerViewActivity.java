package com.ueueo.sample.pulltorefresh;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ueueo.pulltorefresh.UEPullToRefreshLayout;
import com.ueueo.pulltorefresh.base.OnLoadMoreListener;
import com.ueueo.pulltorefresh.base.OnRefreshListener;
import com.ueueo.sample.R;

public class PTRRecylerViewActivity extends AppCompatActivity {

    private int size = 10;

    private UEPullToRefreshLayout mPtrFrame;

    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ptr_recylerview);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rrecyler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter = new RecyclerView.Adapter<MyHolder>() {
            @Override
            public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                TextView textview = new TextView(PTRRecylerViewActivity.this);
                textview.setPadding(30, 50, 30, 50);
                MyHolder myHolder = new MyHolder(textview);
                return myHolder;
            }

            @Override
            public void onBindViewHolder(MyHolder holder, int position) {
                holder.textView.setText("POSITION:" + position);
            }

            @Override
            public int getItemCount() {
                return size;
            }
        });

        mPtrFrame = (UEPullToRefreshLayout) findViewById(R.id.pull_to_refresh_layout);
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

    private class MyHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public MyHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }
    }
}
