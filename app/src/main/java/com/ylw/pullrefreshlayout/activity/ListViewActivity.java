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

public class ListViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
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

//        layout.setOnPullDownListener(new PullRefreshLayout.OnPullDownListener() {
//            @Override
//            public void onRefresh() {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        layout.complete();
//                    }
//                }, 3000);
//            }
//        });

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
        String[] mStrings = new String[]{"package", "com", "ylw", "pullrefreshlayout", "import", "android", "os", "Bundle", "import", "android", "support", "design", "widget", "FloatingActionButton", "import", "android", "support", "design", "widget", "Snackbar", "import", "android", "support", "v7", "app", "AppCompatActivity", "import", "android", "support", "v7", "widget", "Toolbar", "import", "android", "view", "View", "import", "android", "widget", "ArrayAdapter", "import", "android", "widget", "ListView", "public", "class", "MainActivity", "extends", "AppCompatActivity", "private", "static", "final", "String", "TAG", "MainActivity", "@Override", "protected", "void", "onCreate", "Bundle", "savedInstanceState", "super", "onCreate", "savedInstanceState", "setContentView", "R", "layout", "activity", "main", "Toolbar", "toolbar", "Toolbar", "findViewById", "R", "id", "toolbar", "setSupportActionBar", "toolbar", "assert", "toolbar", "null", "toolbar", "setTitle", "MainTitle", "toolbar", "setSubtitle", "SubTitle", "toolbar", "setLogo", "R", "mipmap", "ic", "launcher", "FloatingActionButton", "fab", "FloatingActionButton", "findViewById", "R", "id", "fab", "fab", "setOnClickListener", "new", "View", "OnClickListener", "@Override", "public", "void", "onClick", "View", "view", "Snackbar", "make", "view", "Replace", "with", "your", "own", "action", "Snackbar", "LENGTH", "LONG", "setAction", "Action", "null", "show", "PullRefreshLayout", "layout", "PullRefreshLayout", "findViewById", "R", "id", "pull", "layout", "final", "ListView", "listView", "ListView", "findViewById", "R", "id", "list", "view", "String", "mStrings", "new", "String", "listView", "setAdapter", "new", "ArrayAdapter", "String", "this", "android", "R", "layout", "simple", "list", "item", "1", "mStrings"};
        listView.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, mStrings));
    }

}
