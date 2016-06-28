package com.ylw.pullrefreshlayout.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ylw.pullrefreshlayout.R;
import com.ylw.pullrefreshlibrary.PullRefreshLayout;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        final WebView web = (WebView) findViewById(R.id.web_view);
        WebSettings browserSettings = web.getSettings();

        browserSettings.setUseWideViewPort(false);
        browserSettings.setDomStorageEnabled(true);
        browserSettings.setLoadWithOverviewMode(true);
        browserSettings.setAppCacheEnabled(true);// 缓存
        browserSettings.setDefaultTextEncodingName("UTF-8");
        browserSettings.setJavaScriptEnabled(true);
        web.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        layout.setOnPullDownListener(new PullRefreshLayout.OnPullDownListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        layout.complete();
                    }
                }, 3000);
                web.loadUrl("http://www.cnblogs.com/jietang/p/5615681.html");
            }
        });
    }

}
