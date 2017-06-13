package com.ylw.pullrefreshlayout.view;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.ylw.pullrefreshlayout.R;
import com.ylw.pullrefreshlibrary.PullRefreshLayout;
import com.ylw.pullrefreshlibrary.refreshview.IRefreshView;

/**
 * Created by wangjing on 2016/6/30.
 */
public class RefreshView implements IRefreshView {

    private TextView percent;
    private int state;

    @Override
    public void init(Context context, PullRefreshLayout pullRefreshLayout) {
        View.inflate(context, R.layout.custom_refresh_head_layout, pullRefreshLayout);
        View.inflate(context, R.layout.custom_refresh_bottom_layout, pullRefreshLayout);
    }

    @Override
    public void initView(View headView, View bottomView) {
        percent = (TextView) headView.findViewById(R.id.percent);
    }

    // 下拉刷新
    @Override
    public void toStep1(Context context, int lastState, int state) {
        this.state = state;
        percent.setText("state:" + state + " percent:" + 0);
    }

    // 松手刷新
    @Override
    public void toStep2(Context context, int lastState, int state) {
        this.state = state;
        percent.setText("state:" + state + " percent:" + 0);
    }

    // 正在刷新
    @Override
    public void toStep3(Context context, int lastState, int state) {
        this.state = state;
        percent.setText("state:" + state + " percent:" + 0);
    }

    // 刷新完成
    @Override
    public void toStep4(Context context) {
        this.state = -1;
        percent.setText("state:" + state + " percent:" + 0);
    }

    // 上次刷新时间
    public void setRefreshTime(String time) {
    }

    @Override
    public void updatePercent(float percent_) {
        percent.setText("state:" + state + " percent:" + percent_);
    }

}
