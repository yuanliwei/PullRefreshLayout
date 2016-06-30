package com.ylw.pullrefreshlibrary.refreshview;

import android.content.Context;
import android.view.View;

import com.ylw.pullrefreshlibrary.PullRefreshLayout;

/**
 * Created by wangjing on 2016/6/30.
 */
public interface IRefreshView {
    void init(Context context, PullRefreshLayout pullRefreshLayout);

    void initView(View headView, View bottomView);

    void initPbInvisible();

    void toStep1(Context context,int lastState, int state);

    void toStep2(Context context,int lastState, int state);

    void toStep3(Context context,int lastState, int state);

}
