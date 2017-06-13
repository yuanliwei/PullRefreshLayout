package com.ylw.pullrefreshlibrary.refreshview;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ylw.pullrefreshlibrary.PullRefreshLayout;
import com.ylw.pullrefreshlibrary.R;

/**
 * Created by wangjing on 2016/6/30.
 */
public class RefreshView implements IRefreshView {

    private ImageView ivUpRefresh;
    private ImageView ivDownRefresh;
    private ProgressBar pbRefresh;
    private ProgressBar pbBottomRefresh;
    private TextView tvUpRefreshTip;
    private TextView tvUpRefreshLoad;
    private TextView tvUpRefreshTime;
    private TextView tvDownRefreshTip;
    private TextView tvDownRefreshLoad;
    private TextView tvDownRefreshTime;

    @Override
    public void init(Context context, PullRefreshLayout pullRefreshLayout) {
        View.inflate(context, R.layout.refresh_head_layout, pullRefreshLayout);
        View.inflate(context, R.layout.refresh_bottom_layout, pullRefreshLayout);
    }

    @Override
    public void initView(View headView, View bottomView) {
        ivUpRefresh = (ImageView) headView.findViewById(R.id.iv_refresh_head_down);
        pbRefresh = (ProgressBar) headView.findViewById(R.id.pb_refresh_head);
        tvUpRefreshTip = (TextView) headView.findViewById(R.id.tv_refresh_up_tip);
        tvUpRefreshLoad = (TextView) headView.findViewById(R.id.tv_refresh_up_load);
        tvUpRefreshTime = (TextView) headView.findViewById(R.id.tv_refresh_up_time);
        tvDownRefreshTip = (TextView) bottomView.findViewById(R.id.tv_refresh_down_tip);
        tvDownRefreshLoad = (TextView) bottomView.findViewById(R.id.tv_refresh_down_load);
        tvDownRefreshTime = (TextView) bottomView.findViewById(R.id.tv_refresh_down_time);
        ivDownRefresh = (ImageView) bottomView.findViewById(R.id.iv_refresh_head_up);
        pbBottomRefresh = (ProgressBar) bottomView.findViewById(R.id.pb_refresh_bottom);
        pbRefresh.setVisibility(View.INVISIBLE);
        pbBottomRefresh.setVisibility(View.INVISIBLE);
    }


    @Override
    public void toStep1(Context context, int lastState, int state) {
        ivUpRefresh.setVisibility(View.VISIBLE);
        ivDownRefresh.setVisibility(View.VISIBLE);
        ivUpRefresh.setBackgroundResource(R.drawable.refresh_down);
        ivDownRefresh.setBackgroundResource(R.drawable.refresh_up);
        tvUpRefreshTip.setText("下拉刷新...");
        tvDownRefreshTip.setText("上拉刷新...");
        tvUpRefreshLoad.setVisibility(View.INVISIBLE);
        tvUpRefreshTip.setVisibility(View.VISIBLE);
        tvUpRefreshTime.setVisibility(View.VISIBLE);
        tvDownRefreshLoad.setVisibility(View.INVISIBLE);
        tvDownRefreshTip.setVisibility(View.VISIBLE);
        tvDownRefreshTime.setVisibility(View.VISIBLE);
        pbRefresh.setVisibility(View.INVISIBLE);
        pbBottomRefresh.setVisibility(View.INVISIBLE);
        roate2(ivUpRefresh, context);
        roate2(ivDownRefresh, context);
    }

    @Override
    public void toStep2(Context context, int lastState, int state) {
        ivUpRefresh.setVisibility(View.VISIBLE);
        ivDownRefresh.setVisibility(View.VISIBLE);
        tvUpRefreshTip.setText("放开以刷新...");
        tvDownRefreshTip.setText("放开以刷新...");
        tvUpRefreshLoad.setVisibility(View.INVISIBLE);
        tvUpRefreshTip.setVisibility(View.VISIBLE);
        tvUpRefreshTime.setVisibility(View.VISIBLE);
        tvDownRefreshLoad.setVisibility(View.INVISIBLE);
        tvDownRefreshTip.setVisibility(View.VISIBLE);
        tvDownRefreshTime.setVisibility(View.VISIBLE);
        pbRefresh.setVisibility(View.INVISIBLE);
        pbBottomRefresh.setVisibility(View.INVISIBLE);
        roate1(ivUpRefresh, context);
        roate1(ivDownRefresh, context);
    }

    @Override
    public void toStep3(Context context, int lastState, int state) {
        ivUpRefresh.setVisibility(View.INVISIBLE);
        ivUpRefresh.clearAnimation();
        ivDownRefresh.setVisibility(View.INVISIBLE);
        ivDownRefresh.clearAnimation();
        tvUpRefreshLoad.setText("正在刷新...");
        tvDownRefreshLoad.setText("正在刷新...");
        tvUpRefreshLoad.setVisibility(View.VISIBLE);
        tvUpRefreshTip.setVisibility(View.INVISIBLE);
        tvUpRefreshTime.setVisibility(View.INVISIBLE);
        tvDownRefreshLoad.setVisibility(View.VISIBLE);
        tvDownRefreshTip.setVisibility(View.INVISIBLE);
        tvDownRefreshTime.setVisibility(View.INVISIBLE);
        pbRefresh.setVisibility(View.VISIBLE);
        pbBottomRefresh.setVisibility(View.VISIBLE);
    }

    @Override
    public void toStep4(Context context) {
        ivUpRefresh.setVisibility(View.VISIBLE);
        ivUpRefresh.clearAnimation();
        ivUpRefresh.setBackgroundResource(R.drawable.refresh_finish);
        ivDownRefresh.setVisibility(View.VISIBLE);
        ivDownRefresh.clearAnimation();
        ivDownRefresh.setBackgroundResource(R.drawable.refresh_finish);
        tvUpRefreshLoad.setVisibility(View.VISIBLE);
        tvUpRefreshLoad.setText("刷新完成！");
        tvUpRefreshTip.setVisibility(View.INVISIBLE);
        tvUpRefreshTime.setVisibility(View.INVISIBLE);
        tvDownRefreshLoad.setVisibility(View.VISIBLE);
        tvDownRefreshLoad.setText("刷新完成！");
        tvDownRefreshTip.setVisibility(View.INVISIBLE);
        tvDownRefreshTime.setVisibility(View.INVISIBLE);
        pbRefresh.setVisibility(View.INVISIBLE);
        pbBottomRefresh.setVisibility(View.INVISIBLE);
    }

    public void setRefreshTime(String time) {
        tvUpRefreshTime.setText("上次刷新时间：" + time);
    }

    @Override
    public void updatePercent(float percent) {

    }

    public void roate1(View v, Context context) {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.roate_180_full_after_1);
        v.startAnimation(anim);
    }

    public void roate2(View v, Context context) {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.roate_180_full_after_2);
        v.startAnimation(anim);
    }

}
