package com.ylw.pullrefreshlayout.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ylw.pullrefreshlayout.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert toolbar != null;
        toolbar.setTitle("MainTitle");
//        toolbar.setSubtitle("SubTitle");
//        toolbar.setLogo(R.mipmap.ic_launcher);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }

    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.list_view_btn:
                intent = new Intent(this, ListViewActivity.class);
                break;
            case R.id.scroll_view_btn:
                intent = new Intent(this, ScrollViewActivity.class);
                break;
            case R.id.web_view_btn:
                intent = new Intent(this, WebViewActivity.class);
                break;
            case R.id.recycler_view_btn:
                intent = new Intent(this, RecyclerViewActivity.class);
                break;
            case R.id.view_pager_btn:
                intent = new Intent(this, ViewPagerActivity.class);
                break;
        }
        startActivity(intent);
    }
}
