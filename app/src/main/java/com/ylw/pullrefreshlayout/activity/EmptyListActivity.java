package com.ylw.pullrefreshlayout.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ylw.pullrefreshlayout.R;
import com.ylw.pullrefreshlibrary.PullRefreshLayout;

public class EmptyListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        final PullRefreshLayout layout = (PullRefreshLayout) findViewById(R.id.pull_layout);

        layout.setOnPullListener(new PullRefreshLayout.OnPullListener() {
            public static final String TAG = "OnPullListener";

            @Override
            public void onDownRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        layout.complete();
                    }
                }, 3000);
                layout.getRefreshView().setRefreshTime("" + System.currentTimeMillis() / 1000L);
                Log.d(TAG, "onDownRefresh: =================");
            }

            @Override
            public void onUpRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        layout.complete();
                    }
                }, 3000);
                Log.d(TAG, "onUpRefresh: ==================");
            }
        });

        final ListView listView = (ListView) findViewById(R.id.list_view);
        String[] mStrings = new String[]{};
        listView.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, mStrings));
    }

}
