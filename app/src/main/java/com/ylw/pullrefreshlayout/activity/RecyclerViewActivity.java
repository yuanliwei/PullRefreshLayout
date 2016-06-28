package com.ylw.pullrefreshlayout.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ylw.pullrefreshlayout.R;
import com.ylw.pullrefreshlayout.adapter.RecyclerAdapter;
import com.ylw.pullrefreshlibrary.PullRefreshLayout;

public class RecyclerViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyle_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final PullRefreshLayout layout = (PullRefreshLayout) findViewById(R.id.pull_layout);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);


        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            int type = 0;

            @Override
            public void onClick(View view) {
                Snackbar.make(view, "change RecyclerView.LayoutManager", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                RecyclerView.LayoutManager layoutManager = null;
                switch (type) {
                    case 0:
                        layoutManager = new LinearLayoutManager(RecyclerViewActivity.this);
                        type = 1;
                        break;
                    case 1:
                        layoutManager = new GridLayoutManager(RecyclerViewActivity.this, 5);
                        type = 2;
                        break;
                    default:
                        layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
                        type = 0;
                        break;
                }
                recyclerView.setLayoutManager(layoutManager);
            }
        });

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        layout.setOnPullDownListener(new PullRefreshLayout.OnPullDownListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        layout.complete();
                    }
                }, 3000);
            }
        });

        String[] mStrings = new String[]{"package", "com", "ylw", "pullrefreshlayout", "import", "android", "os", "Bundle", "import", "android", "support", "design", "widget", "FloatingActionButton", "import", "android", "support", "design", "widget", "Snackbar", "import", "android", "support", "v7", "app", "AppCompatActivity", "import", "android", "support", "v7", "widget", "Toolbar", "import", "android", "view", "View", "import", "android", "widget", "ArrayAdapter", "import", "android", "widget", "ListView", "public", "class", "MainActivity", "extends", "AppCompatActivity", "private", "static", "final", "String", "TAG", "MainActivity", "@Override", "protected", "void", "onCreate", "Bundle", "savedInstanceState", "super", "onCreate", "savedInstanceState", "setContentView", "R", "layout", "activity", "main", "Toolbar", "toolbar", "Toolbar", "findViewById", "R", "id", "toolbar", "setSupportActionBar", "toolbar", "assert", "toolbar", "null", "toolbar", "setTitle", "MainTitle", "toolbar", "setSubtitle", "SubTitle", "toolbar", "setLogo", "R", "mipmap", "ic", "launcher", "FloatingActionButton", "fab", "FloatingActionButton", "findViewById", "R", "id", "fab", "fab", "setOnClickListener", "new", "View", "OnClickListener", "@Override", "public", "void", "onClick", "View", "view", "Snackbar", "make", "view", "Replace", "with", "your", "own", "action", "Snackbar", "LENGTH", "LONG", "setAction", "Action", "null", "show", "PullRefreshLayout", "layout", "PullRefreshLayout", "findViewById", "R", "id", "pull", "layout", "final", "ListView", "listView", "ListView", "findViewById", "R", "id", "list", "view", "String", "mStrings", "new", "String", "listView", "setAdapter", "new", "ArrayAdapter", "String", "this", "android", "R", "layout", "simple", "list", "item", "1", "mStrings"};
        RecyclerAdapter adapter = new RecyclerAdapter();
        adapter.setDatas(mStrings);
        recyclerView.setAdapter(adapter);
    }

}
