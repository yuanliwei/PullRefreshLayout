package com.ylw.pullrefreshlayout.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ylw.pullrefreshlayout.R;
import com.ylw.pullrefreshlibrary.PullRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private List<View> pageview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
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

        viewPager = (ViewPager) findViewById(R.id.viewPager);

        //查找布局文件用LayoutInflater.inflate
        LayoutInflater inflater =getLayoutInflater();
        View view1 = inflater.inflate(R.layout.content_list_view, null);
        View view2 = inflater.inflate(R.layout.content_web_view, null);
        View view3 = inflater.inflate(R.layout.content_scroll_view, null);

        final ListView listView = (ListView) view1.findViewById(R.id.list_view);
        String[] mStrings = new String[]{"package", "com", "ylw", "pullrefreshlayout", "import", "android", "os", "Bundle", "import", "android", "support", "design", "widget", "FloatingActionButton", "import", "android", "support", "design", "widget", "Snackbar", "import", "android", "support", "v7", "app", "AppCompatActivity", "import", "android", "support", "v7", "widget", "Toolbar", "import", "android", "view", "View", "import", "android", "widget", "ArrayAdapter", "import", "android", "widget", "ListView", "public", "class", "MainActivity", "extends", "AppCompatActivity", "private", "static", "final", "String", "TAG", "MainActivity", "@Override", "protected", "void", "onCreate", "Bundle", "savedInstanceState", "super", "onCreate", "savedInstanceState", "setContentView", "R", "layout", "activity", "main", "Toolbar", "toolbar", "Toolbar", "findViewById", "R", "id", "toolbar", "setSupportActionBar", "toolbar", "assert", "toolbar", "null", "toolbar", "setTitle", "MainTitle", "toolbar", "setSubtitle", "SubTitle", "toolbar", "setLogo", "R", "mipmap", "ic", "launcher", "FloatingActionButton", "fab", "FloatingActionButton", "findViewById", "R", "id", "fab", "fab", "setOnClickListener", "new", "View", "OnClickListener", "@Override", "public", "void", "onClick", "View", "view", "Snackbar", "make", "view", "Replace", "with", "your", "own", "action", "Snackbar", "LENGTH", "LONG", "setAction", "Action", "null", "show", "PullRefreshLayout", "layout", "PullRefreshLayout", "findViewById", "R", "id", "pull", "layout", "final", "ListView", "listView", "ListView", "findViewById", "R", "id", "list", "view", "String", "mStrings", "new", "String", "listView", "setAdapter", "new", "ArrayAdapter", "String", "this", "android", "R", "layout", "simple", "list", "item", "1", "mStrings"};
        listView.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, mStrings));

        final PullRefreshLayout layout1 = (PullRefreshLayout) view1.findViewById(R.id.pull_layout);

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

        layout1.setOnPullListener(new PullRefreshLayout.OnPullListener() {
            public static final String TAG = "OnPullListener";

            @Override
            public void onDownRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        layout1.complete();
                    }
                }, 3000);
                Log.d(TAG, "onDownRefresh: =================");
            }

            @Override
            public void onUpRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        layout1.complete();
                    }
                }, 3000);
                Log.d(TAG, "onUpRefresh: ==================");
            }
        });


        final WebView web = (WebView) view2.findViewById(R.id.web_view);
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
        final PullRefreshLayout layout2 = (PullRefreshLayout) view2.findViewById(R.id.pull_layout);
        layout2.setOnPullDownListener(new PullRefreshLayout.OnPullDownListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        layout2.complete();
                    }
                }, 3000);
                web.loadUrl("http://www.cnblogs.com/jietang/p/5615681.html");
            }
        });

        final PullRefreshLayout layout3 = (PullRefreshLayout) view3.findViewById(R.id.pull_layout);
        layout3.setOnPullDownListener(new PullRefreshLayout.OnPullDownListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        layout3.complete();
                    }
                }, 3000);
            }
        });
        //将view装入数组
        pageview =new ArrayList<View>();
        pageview.add(view1);
        pageview.add(view2);
        pageview.add(view3);


        //数据适配器
        PagerAdapter mPagerAdapter = new PagerAdapter(){

            @Override
            //获取当前窗体界面数
            public int getCount() {
                // TODO Auto-generated method stub
                return pageview.size();
            }

            @Override
            //断是否由对象生成界面
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0==arg1;
            }
            //是从ViewGroup中移出当前View
            public void destroyItem(View arg0, int arg1, Object arg2) {
                ((ViewPager) arg0).removeView(pageview.get(arg1));
            }

            //返回一个对象，这个对象表明了PagerAdapter适配器选择哪个对象放在当前的ViewPager中
            public Object instantiateItem(View arg0, int arg1){
                ((ViewPager)arg0).addView(pageview.get(arg1));
                return pageview.get(arg1);
            }


        };

        //绑定适配器
        viewPager.setAdapter(mPagerAdapter);

    }
}
