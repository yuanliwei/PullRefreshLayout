package com.ylw.pullrefreshlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import com.ylw.pullrefreshlibrary.refreshview.IRefreshView;
import com.ylw.pullrefreshlibrary.refreshview.RefreshView;

/*
 * 下拉刷新控件
 */
public class PullRefreshLayout extends FrameLayout {

    public static final String TAG = "PullRefreshLayout";

    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;
    private View headView;
    private View bottomView;
    private ViewDragHelper mDragger;
    private View dragView;
    private View contentView;

    private boolean downRefreshing = false;
    private boolean upRefreshing = false;
    private boolean refreshing = false;

    private int vtH;
    private int vbH;
    private int vcH;

    IRefreshView refreshView;

    public PullRefreshLayout(Context context) {
        super(context);
        init(context, null, 0);
    }

    public PullRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public PullRefreshLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        requestLayout();
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (changedView == this && visibility == VISIBLE) {
            requestLayout();
        }
        requestLayout();
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.PullRefreshLayout, defStyle, 0);

        int refreshStyle = a.getInt(R.styleable.PullRefreshLayout_refreshView, 0);
//        mExampleColor = a.getColor(
//                R.styleable.PullRefreshLayout_exampleColor,
//                mExampleColor);
//        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
//        // values that should fall on pixel boundaries.
//        mExampleDimension = a.getDimension(
//                R.styleable.PullRefreshLayout_exampleDimension,
//                mExampleDimension);
//
//        if (a.hasValue(R.styleable.PullRefreshLayout_exampleDrawable)) {
//            mExampleDrawable = a.getDrawable(
//                    R.styleable.PullRefreshLayout_exampleDrawable);
//            mExampleDrawable.setCallback(this);
//        }

        a.recycle();

        switch (refreshStyle) {
            case 0:
                refreshView = new RefreshView();
                break;
            default:
                refreshView = new RefreshView();
                break;
        }

        // Update TextPaint and text measurements from attributes
        refreshView.init(context, this);
        headView = getChildAt(0);
        bottomView = getChildAt(1);
        refreshView.initView(headView, bottomView);


        initHeadBottomViews();

        initDragger();
    }

    private void initHeadBottomViews() {
        headView.setVisibility(INVISIBLE);
        bottomView.setVisibility(INVISIBLE);
    }

    private void initDragger() {
        mDragger = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                // true 拦截拖动事件; false 忽略拖动事件
                return true;
            }

            // 水平拖动
            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                return 0;
            }

            // 垂直拖动
            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                downRefreshing = false;
                upRefreshing = false;

                // 修改View位置
                changeLayout();
                // 计算top的值，不让view拖出边界
                if (enablePullDown && top > vtH) {
                    downRefreshing = true;
                    pullCallBack.onPullStateChange(PullCallBack.STATE_STEP2);
                } else if (enablePullUp && top + vbH < 0) {
                    upRefreshing = true;
                    pullCallBack.onPullStateChange(PullCallBack.STATE_STEP2);
                } else {
                    pullCallBack.onPullStateChange(PullCallBack.STATE_STEP1);
                }
                if (enablePullDown) {
                    headView.setVisibility(VISIBLE);
                }
                return top - dy / 2;
            }

            // 拖动状态改变事件
            @Override
            public void onViewDragStateChanged(int state) {
                // 空闲状态
                if (state == ViewDragHelper.STATE_IDLE) {
                    // 修改View位置
                    changeLayout();
                    // 计算View布局
                    if (!refreshing)
                        countLayout();
                    // 计算一下看是否需要调用 onScrollBottom()方法
                    pullCallBack.canPullDown();
                } else if (state == ViewDragHelper.STATE_DRAGGING) {
                    refreshing = false;
                }
            }

            // 释放拖动控件
            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                // 计算出View最终的位置， 动态的把View归位
                float yPosition = 0;

                if (upRefreshing && pullCallBack.canPullUp()) {
                    yPosition = -vbH;
                    refreshing = true;
                    if (onPullListener != null) onPullListener.onUpRefresh();
                } else if (downRefreshing && pullCallBack.canPullDown()) {
                    yPosition = vtH;
                    refreshing = true;
                    if (onPullListener != null) onPullListener.onDownRefresh();
                    if (onPullDownListener != null) onPullDownListener.onRefresh();
                }
                if (refreshing) {
                    pullCallBack.onPullStateChange(PullCallBack.STATE_STEP3);
                } else {
                    pullCallBack.onPullStateChange(PullCallBack.STATE_STEP1);
                }
                mDragger.settleCapturedViewAt(0, (int) yPosition);
                // 启动动画
                postInvalidate();
            }

            // 不知道这个在干嘛
            @Override
            public int getViewVerticalDragRange(View child) {
                return getMeasuredHeight();
            }
        });
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mDragger.continueSettling(true)) {
            changeLayout();
            postInvalidate();
        }
    }


    private void changeLayout() {
        int w = getWidth();
        int h = getHeight();

        int t = contentView.getTop();
        int b = contentView.getBottom();

        if (t < 0 && !pullCallBack.canPullUp()) {
            contentView.layout(0, t, w, h);
        } else {
            bottomView.layout(0, b, w, b + vbH);
        }
        if (b > h && !pullCallBack.canPullDown()) {
            contentView.layout(0, 0, w, b);
        } else {
            headView.layout(0, t - vtH, w, t);
        }

//        ViewGroup.LayoutParams ltp = headView.getLayoutParams();
//        ViewGroup.LayoutParams lmp = contentView.getLayoutParams();
//        ViewGroup.LayoutParams lbp = bottomView.getLayoutParams();
//        ltp.
    }

    public void countLayout() {
        int h = getHeight();
        ViewGroup.LayoutParams ltp = headView.getLayoutParams();
        ViewGroup.LayoutParams lmp = contentView.getLayoutParams();
        ViewGroup.LayoutParams lbp = bottomView.getLayoutParams();

        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        contentView = getChildAt(2);        //这个必须是WebView
        if (isInEditMode()) return;
        if (vtH == 0) vtH = headView.getMeasuredHeight();
        if (vbH == 0) vbH = bottomView.getMeasuredHeight();
        if (vcH == 0) vcH = contentView.getMeasuredHeight();
//        vCenterHeight = (int) getResources().getDimension(R.dimen.hw_detail_split_center_height);
        //计算title Height
//        vHeadMinHeight = (int) getResources().getDimension(R.dimen.title_height_bg);
//        if (hasInit == false) {
//            hasInit = true;
//            initViewState(hasVideo, showVideo, hasChoice, t_b);
//        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (contentView.getTop() != 0) return;
        if (!(refreshing || refreshingDown)) {
            int h = getMeasuredHeight();
            super.onLayout(changed, l, t, r, b);
            FrameLayout.LayoutParams ltp = (LayoutParams) headView.getLayoutParams();
            FrameLayout.LayoutParams lbp = (LayoutParams) bottomView.getLayoutParams();
            ltp.setMargins(0, -vtH, 0, 0);
            lbp.setMargins(0, h, 0, 0);
        }
//        if (firstLayout && !isInEditMode()) {
//            firstLayout = false;
//            //初始化内部控件
//            initViewState(hasVideo, showVideo, hasChoice, t_b);
//        }
    }

    // 标记正在刷新的时候出现了触摸事件
    boolean refreshingDown = false;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (refreshing || refreshingDown) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    refreshingDown = true;
                    break;
                case MotionEvent.ACTION_UP:
                default:
                    refreshingDown = false;
                    break;
            }
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            initDragView(contentView, event);
        }
        if (event.getHistorySize() > 0) {                             //拦截Touch事件
            if (event.getY() > event.getHistoricalY(0)) {             //向下滑动
                if (pullCallBack.canPullDown()) {
                    return mDragger.shouldInterceptTouchEvent(event);
                } else {
                    return false;
                }
            } else if (event.getY() < event.getHistoricalY(0)) {
                if (pullCallBack.canPullUp()) {
                    return mDragger.shouldInterceptTouchEvent(event);
                } else {
                    return false;
                }
            }
        }
        if (pullCallBack.canPullDown()) {
            return mDragger.shouldInterceptTouchEvent(event);
        } else if (pullCallBack.canPullUp()) {
            return mDragger.shouldInterceptTouchEvent(event);
        } else {
            return false;
        }
    }

    /**
     * 初始化当前拖动的view
     */
    private void initDragView(View v, MotionEvent e) {
        if (mDragger.isViewUnder(v, (int) e.getX(), (int) e.getY())) {
            dragView = v;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (refreshing || refreshingDown) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    refreshingDown = true;
                    break;
                case MotionEvent.ACTION_UP:
                default:
                    refreshingDown = false;
                    break;
            }
            return true;
        }
        mDragger.processTouchEvent(event);
        return true;
    }


    private PullCallBack pullCallBack = new PullCallBack() {
        private boolean canPullDown = true;
        private boolean canPullUp = true;
        boolean hasSetListener = false;

        @Override
        public boolean canPullDown() {
            countIt();
            return canPullDown && enablePullDown;
        }

        @Override
        public boolean canPullUp() {
            countIt();
            return canPullUp && enablePullUp;
        }

        private boolean onScrollBottom = false;

        private void countIt() {
            if (contentView instanceof ScrollView) {
                canPullDown = contentView.getScrollY() == 0;

                View contentview = ((ViewGroup) contentView).getChildAt(0);
                canPullUp = contentview.getMeasuredHeight() == contentView.getScrollY() + contentView.getHeight();
            } else if (contentView instanceof AbsListView) {
                ListView view = (ListView) contentView;
                int cCount = view.getChildCount();
                if (cCount == 0) {
                    canPullDown = true;
                    canPullUp = true;
                }
                if (hasSetListener) return;
                hasSetListener = true;
                view.setOnScrollListener(new AbsListView.OnScrollListener() {
                    private int scrollState = SCROLL_STATE_IDLE;
                    boolean s = false;

                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                        this.scrollState = scrollState;
                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        canPullDown = false;
                        canPullUp = false;
                        if (firstVisibleItem == 0) {
                            View firstVisibleItemView = view.getChildAt(0);
                            if (firstVisibleItemView != null && firstVisibleItemView.getTop() == 0) {
                                canPullDown = true;
                                canPullUp = false;
                                s = false;
                            }
                        } else if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
                            View lastVisibleItemView = view.getChildAt(view.getChildCount() - 1);
                            if (lastVisibleItemView != null && lastVisibleItemView.getBottom() == view.getHeight()) {
                                canPullDown = false;
                                canPullUp = true;

                                if (onScrollBottomListener != null && !s) {
                                    onScrollBottomListener.onScrollBottom();
                                }
                                s = true;
                            }
                        }
                    }
                });
            } else if (contentView instanceof WebView) {
                WebView web = (WebView) contentView;
                canPullDown = false;
                canPullUp = false;
                if (web.getScrollY() == 0) {
                    canPullDown = true;
                }
//                if (web.getContentHeight() * web.getScale() == (web.getHeight() + web.getScrollY())) {
//                    //已经处于底端
//                    canPullUp = true;
//                }
            } else if (contentView instanceof RecyclerView) {
                RecyclerView view = (RecyclerView) contentView;
                canPullDown = false;
                canPullUp = false;

                int Extent = view.computeVerticalScrollExtent();
                int Offset = view.computeVerticalScrollOffset();
                int Range = view.computeVerticalScrollRange();
//                Log.d(TAG, "countIt: Offset + Extent - Range = " + (Offset + Extent - Range));
                if (Offset == 0) {
                    canPullDown = true;
                }
                if (Offset + Extent >= Range) {
                    canPullUp = true;
                    if (onScrollBottomListener != null && !onScrollBottom)
                        onScrollBottomListener.onScrollBottom();
                    onScrollBottom = true;
                } else {
                    onScrollBottom = false;
                }
                if (hasSetListener) return;
                hasSetListener = true;
                view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            countIt();
                        }
                    }
                });
            }

        }

        int lastState = STATE_STEP1;

        @Override
        public void onPullStateChange(int state) {
            if (lastState == state) return;
            switch (state) {
                case STATE_STEP1:
                    toStep1(lastState, state);
                    break;
                case STATE_STEP2:
                    toStep2(lastState, state);
                    break;
                case STATE_STEP3:
                    toStep3(lastState, state);
                    break;
            }
            lastState = state;
        }

        private void toStep1(int lastState, int state) {//TODO
            Log.d(TAG, "toStep1: ==========");
            refreshView.toStep1(getContext(), lastState, state);
        }

        private void toStep2(int lastState, int state) {
            Log.d(TAG, "toStep2: ==========");
            refreshView.toStep2(getContext(), lastState, state);
        }

        private void toStep3(int lastState, int state) {
            refreshView.toStep3(getContext(), lastState, state);
            Log.d(TAG, "toStep3: ==========");
        }

    };

    private AbsListView.OnScrollListener onScrollListener;

    public void setOnScrollListener(AbsListView.OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    public interface PullCallBack {
        int STATE_STEP1 = 0;    // 不能刷新
        int STATE_STEP2 = 1;    // 可以刷新
        int STATE_STEP3 = 2;    // 刷新中
        int STATE_STEP4 = 3;    // 刷新完成

        boolean canPullDown();

        boolean canPullUp();

        void onPullStateChange(int state);

    }

    private OnPullDownListener onPullDownListener;
    private OnPullListener onPullListener;
    private OnScrollBottomListener onScrollBottomListener;

    public interface OnPullDownListener {
        void onRefresh();
    }

    public interface OnScrollBottomListener {
        void onScrollBottom();
    }

    public interface OnPullListener {
        void onDownRefresh();

        void onUpRefresh();
    }

    private boolean enablePullDown = false; // 启用下拉
    private boolean enablePullUp = false;   // 启用上拉

    public void setOnScrollBottomListener(OnScrollBottomListener onScrollBottomListener) {
        this.onScrollBottomListener = onScrollBottomListener;
    }

    // 设置监听 设置后才能进行有下拉刷新动作
    public void setOnPullDownListener(OnPullDownListener onPullDownListener) {
        this.onPullDownListener = onPullDownListener;
        enablePullDown = true;
//        headView.setVisibility(VISIBLE);
    }

    // 设置监听 设置后才能进行有上下拉刷新动作
    public void setOnPullListener(OnPullListener onPullListener) {
        this.onPullListener = onPullListener;
        enablePullDown = true;
        enablePullUp = true;
        headView.setVisibility(VISIBLE);
        bottomView.setVisibility(VISIBLE);
    }

    public void complete() {
        if (upRefreshing || downRefreshing) {
            toStep4();
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    mDragger.smoothSlideViewTo(contentView, 0, 0);
                    invalidate();
                    upRefreshing = false;
                    downRefreshing = false;
                    refreshing = false;
                }
            }, 400);
        }
    }

    private void toStep4() {
        Log.d(TAG, "toStep4: ==========");
        refreshView.toStep4(getContext());
    }

    private void refreshing() {

    }

    public IRefreshView getRefreshView() {
        return refreshView;
    }
}
