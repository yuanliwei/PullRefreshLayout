package com.ylw.pullrefreshlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ScrollView;

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

    private void init(Context context, AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.PullRefreshLayout, defStyle, 0);

//        mExampleString = a.getString(
//                R.styleable.PullRefreshLayout_exampleString);
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

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        // Update TextPaint and text measurements from attributes
        View.inflate(context, R.layout.refresh_head_layout, this);
        View.inflate(context, R.layout.refresh_bottom_layout, this);
        headView = getChildAt(0);
        bottomView = getChildAt(1);

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
                } else if (enablePullUp && top + vbH < 0) {
                    upRefreshing = true;
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
        int h = getMeasuredHeight();
        super.onLayout(changed, l, t, r, b);
        FrameLayout.LayoutParams ltp = (LayoutParams) headView.getLayoutParams();
        FrameLayout.LayoutParams lbp = (LayoutParams) bottomView.getLayoutParams();
        ltp.setMargins(0, -vtH, 0, 0);
        lbp.setMargins(0, h, 0, 0);
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

        private void countIt() {
            if (contentView instanceof ScrollView) {
                canPullDown = contentView.getScrollY() == 0;

                View contentview = ((ViewGroup) contentView).getChildAt(0);
                canPullUp = contentview.getMeasuredHeight() == contentView.getScrollY() + contentView.getHeight();
            } else if (contentView instanceof AbsListView) {
                ListView view = (ListView) contentView;
                if (hasSetListener) return;
                hasSetListener = true;
                view.setOnScrollListener(new AbsListView.OnScrollListener() {
                    private int scrollState = SCROLL_STATE_IDLE;

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
                            }
                        } else if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
                            View lastVisibleItemView = view.getChildAt(view.getChildCount() - 1);
                            if (lastVisibleItemView != null && lastVisibleItemView.getBottom() == view.getHeight()) {
                                canPullDown = false;
                                canPullUp = true;
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
                }
            }

        }
    };


    public interface PullCallBack {
        boolean canPullDown();

        boolean canPullUp();

    }

    private OnPullDownListener onPullDownListener;
    private OnPullListener onPullListener;

    public interface OnPullDownListener {
        void onRefresh();
    }

    public interface OnPullListener {
        void onDownRefresh();

        void onUpRefresh();
    }

    private boolean enablePullDown = false; // 启用下拉
    private boolean enablePullUp = false;   // 启用上拉

    // 设置监听 设置后才能进行有下拉刷新动作
    public void setOnPullDownListener(OnPullDownListener onPullDownListener) {
        this.onPullDownListener = onPullDownListener;
        enablePullDown = true;
        headView.setVisibility(VISIBLE);
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
            mDragger.smoothSlideViewTo(contentView, 0, 0);
            postInvalidate();
            upRefreshing = false;
            downRefreshing = false;
            refreshing = false;
        }
    }

    private void refreshing() {

    }


}
