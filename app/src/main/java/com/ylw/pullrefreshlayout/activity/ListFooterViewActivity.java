package com.ylw.pullrefreshlayout.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ylw.pullrefreshlayout.R;
import com.ylw.pullrefreshlibrary.PullRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class ListFooterViewActivity extends AppCompatActivity {

    private ListView listView;
    private boolean isLoading = false;
    private List<String> mList1;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.actionModeCloseDrawable, typedValue, true);

        toolbar.setNavigationIcon(typedValue.resourceId);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        final PullRefreshLayout layout = (PullRefreshLayout) findViewById(R.id.pull_layout);

        layout.setOnPullDownListener(new PullRefreshLayout.OnPullDownListener() {
            @Override
            public void onRefresh() {
                isLoading = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isLoading = false;
                        layout.complete();
                    }
                }, 3000);
            }
        });

        layout.setOnScrollBottomListener(new PullRefreshLayout.OnScrollBottomListener() {
            @Override
            public void onScrollBottom() {
//                if (!isLoading) {
//                    isLoading = true;
                getMoreData();
                Log.d("Rollr_Mine", "请求数据...");
                return;
//                }
            }
        });

        listView = (ListView) findViewById(R.id.list_view);
        mList1 = new ArrayList<>();
        mList1.add("1");
        mList1.add("2");
        mList1.add("3");
        mList1.add("4");
        mList1.add("5");
        mList1.add("6");
        mList1.add("7");
        mList1.add("8");
        mList1.add("2");
        mList1.add("3");
        mList1.add("4");
        mList1.add("5");
        mList1.add("6");
        mList1.add("7");
        mList1.add("8");
        mList1.add("1");
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, mList1);
        listView.setAdapter(adapter);

        initListFootView();
    }

    private View footer;
    private ProgressBar footProgressBar;
    private TextView footTextView;

    private void initListFootView() {
        footer = getLayoutInflater().inflate(R.layout.listview_footerview, null);
        footProgressBar = (ProgressBar) footer.findViewById(R.id.listview_footview_progressBar);
        footTextView = (TextView) footer.findViewById(R.id.listview_footview_textview);
        listView.addFooterView(footer);
//        footTextView.setOnClickListener(new View.OnClickListener() {
//            public static final String TAG = "OnPullListener";
//            @Override
//            public void onClick(View v) {
//                if (footTextView.getText().equals(getString(R.string.load_error))) {
//                    //加载数据
//                    footTextView.setText("加载中...");
//                    footProgressBar.setVisibility(View.VISIBLE);
//                    Log.d(TAG, "onUpRefresh: ==================");
//                }
//            }
//        });
        initAutoRefresh();
    }

    private void initAutoRefresh() {
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 当不滚动时
                if (scrollState == SCROLL_STATE_IDLE) {
                    //判断是否滚动到底部
                    if (!isLoading && view.getLastVisiblePosition() == view.getCount() - 1) {
                        isLoading = true;
                        getMoreData();
                        Log.d("Rollr_Mine", "请求数据...");
                        return;
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }

    int i = 0;

    private void getMoreData() {
        if(i>33)return;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<String> mList2 = new ArrayList<>();
                for (int j = 0; j < 10; j++) {
                    mList2.add("" + i++);
                }
                mList1.addAll(mList2);
                adapter.notifyDataSetChanged();
            }
        }, 800);

    }
}
