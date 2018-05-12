package com.ylw.pullrefreshlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.widget.RecyclerView;
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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/*
 * 下拉刷新控件
 */
public class PullRefreshLayout extends FrameLayout {

    public static final String TAG = "PullRefreshLayout";
    IRefreshView refreshView;
    boolean disallowIntercept = false;
    // 标记正在刷新的时候出现了触摸事件
    boolean refreshingDown = false;
    float downPosY = 0;
    float downPosX = 0;
    boolean hasCancle = false;
    boolean contentViewDisallowIntercept = false;
    private View headView;
    private View bottomView;
    private ViewDragHelper mDragger;
    private View contentView;
    private View realContentView;
    private boolean downRefreshing = false;
    private boolean upRefreshing = false;
    private boolean refreshing = false;
    private int vtH;
    private int vbH;
    private int vcH;
    private IOnScrollListener onScrollListener;
    private boolean enable = true;
    private IContentPositionCallback contentPositionCallback;
    private OnPullDownListener onPullDownListener;
    private OnPullListener onPullListener;
    private OnScrollBottomListener onScrollBottomListener;
    private OnCompleteListener onCompleteListener;
    private boolean enablePullDown = false; // 启用下拉
    private boolean enablePullUp = false;   // 启用上拉
    private PullCallBack pullCallBack = new PullCallBack() {
        boolean hasSetListener = false;
        int lastState = STATE_STEP1;
        private boolean canPullDown = true;
        private boolean canPullUp = true;
        private boolean onScrollBottom = false;

        @Override
        public boolean canPullDown() {
            countIt();
            return enable && canPullDown && enablePullDown;
        }

        @Override
        public boolean canPullUp() {
            countIt();
            return canPullUp && enablePullUp;
        }

        private void countIt() {
            if (contentPositionCallback != null) {
                canPullDown = false;
                canPullUp = false;
                if (contentPositionCallback.isTop(realContentView)) {
                    canPullDown = true;
                    if (onScrollListener != null) {
                        onScrollListener.onScroll(0);
                    }
                }
            } else if (realContentView instanceof ScrollView) {
                canPullDown = realContentView.getScrollY() == 0;
                if (onScrollListener != null && !canPullDown) {
                    onScrollListener.onScroll(-realContentView.getScrollY());
                }
                View contentview = ((ViewGroup) realContentView).getChildAt(0);
                canPullUp = contentview.getMeasuredHeight() == realContentView.getScrollY() + realContentView.getHeight();
            } else if (realContentView instanceof AbsListView) {
                ListView view = (ListView) realContentView;
                int cCount = view.getChildCount();
                if (cCount == 0) {
                    canPullDown = true;
                    canPullUp = true;
                }
                if (hasSetListener) return;
                hasSetListener = true;
                view.setOnScrollListener(new AbsListView.OnScrollListener() {
                    boolean s = false;

                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
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
                                if (onScrollListener != null && !canPullDown) {
                                    onScrollListener.onScroll(0);
                                }
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
            } else if (realContentView instanceof WebView) {
                WebView web = (WebView) realContentView;
                canPullDown = false;
                canPullUp = false;
                if (web.getScrollY() == 0) {
                    canPullDown = true;
                }
                if (onScrollListener != null && !canPullDown) {
                    onScrollListener.onScroll(-web.getScrollY());
                }
            } else if (realContentView instanceof RecyclerView) {
                RecyclerView view = (RecyclerView) realContentView;
                canPullDown = false;
                canPullUp = false;

                int Offset = view.computeVerticalScrollOffset();
                if (Offset == 0) {
                    canPullDown = true;
                }
                if (onScrollListener != null && !canPullDown) {
                    onScrollListener.onScroll(-Offset);
                }
                RecyclerView.Adapter adapter = view.getAdapter();
                if (adapter != null) {
                    int itemCount = adapter.getItemCount();
                    View lastChild = view.getChildAt(view.getChildCount() - 1);
                    int lastVisibleViewPosition = view.getChildAdapterPosition(lastChild);
                    if (lastVisibleViewPosition == itemCount - 1) {
                        if (onScrollBottomListener != null && !onScrollBottom)
                            onScrollBottomListener.onScrollBottom();
                        onScrollBottom = true;
                    } else {
                        onScrollBottom = false;
//                        Log.d(TAG, "countIt: onScrollBottomListener : false");
                    }
                }
                if (hasSetListener) return;
                hasSetListener = true;
                view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        int Offset = recyclerView.computeVerticalScrollOffset();
                        if (onScrollListener != null && !canPullDown) {
                            onScrollListener.onScroll(-Offset);
                        }
                    }

                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            countIt();
                        }
                    }
                });
            }

        }

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

        @Override
        public void onPullStateChange(int state, float percent) {
            switch (state) {
                case STATE_STEP1:
                    refreshView.updatePercent(percent);
                    break;
            }
        }

        private void toStep1(int lastState, int state) {
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
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
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
        String refreshClass = a.getString(R.styleable.PullRefreshLayout_refreshViewClassName);

        a.recycle();

        if (refreshClass != null) {
            try {
                Class<?> clazz = Class.forName(refreshClass);
                refreshView = (IRefreshView) clazz.newInstance();
            } catch (Exception e) {
                Log.e(TAG, "init: ClassNotFoundException ", e);
            }
        }

        if (refreshView == null) {
            switch (refreshStyle) {
                case 0:
                    refreshView = new RefreshView();
                    break;
                default:
                    refreshView = new RefreshView();
                    break;
            }
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
                if (enablePullDown && top <= vtH) {
                    pullCallBack.onPullStateChange(PullCallBack.STATE_STEP1, top * 1f / vtH);
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
                    // 当一个刷新流程全部走完后调用这个方法
                    if (onCompleteListener != null && !upRefreshing && !downRefreshing && !refreshing)
                        onCompleteListener.onComplete();
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
        if (onScrollListener != null) {
            onScrollListener.onScroll(contentView.getTop());
        }
    }

    public void countLayout() {
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        contentView = getChildAt(2);        //这个必须是WebView
        if (realContentView == null)
            realContentView = contentView;
        if (isInEditMode()) return;
        if (vtH == 0) vtH = headView.getMeasuredHeight();
        if (vbH == 0) vbH = bottomView.getMeasuredHeight();
        if (vcH == 0) vcH = contentView.getMeasuredHeight();
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
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
        this.disallowIntercept = disallowIntercept;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (refreshing) return false;

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            disallowIntercept = false;
            contentViewDisallowIntercept = false;
            downPosY = ev.getY();
            downPosX = ev.getX();
            hasCancle = false;
            onInterceptTouchEvent(ev);
            contentView.dispatchTouchEvent(ev);
            return true;
        }

        if (disallowIntercept && contentViewDisallowIntercept()) {
//            contentView.getv
//            onTouchEvent(ev);
            contentView.dispatchTouchEvent(ev);
            return true;
        }

        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            float dy = ev.getY() - downPosY;
            float dx = Math.abs(ev.getX() - downPosX);
            if (pullCallBack.canPullDown() && dy > 0 && (Math.abs(dy) > dx) || pullCallBack.canPullUp() && dy < 0 && (Math.abs(dy) > dx)) {
                onTouchEvent(ev);
                ev.setAction(MotionEvent.ACTION_CANCEL);
                contentView.dispatchTouchEvent(ev);
                hasCancle = true;
            } else {
                if (!hasCancle)
                    contentView.dispatchTouchEvent(ev);
            }
            return true;
        }

        onTouchEvent(ev);
        contentView.dispatchTouchEvent(ev);

        return true;
    }

    private boolean contentViewDisallowIntercept() {
        if (contentViewDisallowIntercept) return true;
        if (!(contentView instanceof ViewGroup)) return false;
        try {
            Field field = ViewGroup.class.getDeclaredField("FLAG_DISALLOW_INTERCEPT");
            Method m = ViewGroup.class.getDeclaredMethod("hasBooleanFlag", int.class);
            m.setAccessible(true);
            field.setAccessible(true);
            boolean result = (boolean) m.invoke(contentView, field.getInt(contentView));
            if (result) contentViewDisallowIntercept = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        mDragger.shouldInterceptTouchEvent(event);
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragger.processTouchEvent(event);
        return false;
    }

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

    // 当下拉刷新控件完全归位后会调用这个接口
    public void setOnCompleteListener(OnCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
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

    public boolean isRefreshing() {
        return contentView.getTop() != 0;
    }

    public IRefreshView getRefreshView() {
        return refreshView;
    }

    public void setContentView(View contentView) {
        this.realContentView = contentView;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setOnScrollListener(IOnScrollListener listener) {
        this.onScrollListener = listener;
    }

    public void setContentPositionCallback(IContentPositionCallback contentPositionCallback) {
        this.contentPositionCallback = contentPositionCallback;
    }

    interface PullCallBack {
        int STATE_STEP1 = 0;    // 不能刷新
        int STATE_STEP2 = 1;    // 可以刷新
        int STATE_STEP3 = 2;    // 刷新中
        int STATE_STEP4 = 3;    // 刷新完成

        boolean canPullDown();

        boolean canPullUp();

        void onPullStateChange(int state);

        void onPullStateChange(int state, float percent);
    }

    public interface OnPullDownListener {
        void onRefresh();
    }

    public interface OnScrollBottomListener {
        void onScrollBottom();
    }

    public interface OnCompleteListener {
        void onComplete();
    }

    public interface OnPullListener {
        void onDownRefresh();

        void onUpRefresh();
    }

    public interface IContentPositionCallback {
        boolean isTop(View contentView);
    }

    public interface IOnScrollListener {
        void onScroll(int offset);
    }
}
