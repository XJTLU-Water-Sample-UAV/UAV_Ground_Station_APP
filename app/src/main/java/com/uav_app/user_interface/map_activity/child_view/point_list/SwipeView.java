package com.uav_app.user_interface.map_activity.child_view.point_list;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Scroller;
import android.widget.TextView;

import com.uav_app.uav_manager.R;

/**
 * 抽屉View，向右滑动可以显示删除和编辑按钮
 */
public class SwipeView extends ViewGroup {
    // 滑动控制器
    private final Scroller scroller;
    // 按下时的触摸位置
    private float downX = 0;
    // 滑动时的触摸位置
    private float moveX = 0;
    // 最小判定滑动距离
    private float minMoveDistance = 10;
    // 最大滑动距离
    private float maxMoveDistance = 10;
    // 临界滑动距离
    private float criticalMoveDistance = 10;
    // 是否正在滑动
    private boolean isSliding = false;
    // 按钮是否展开
    private boolean isButtonOpen = false;
    // 滑动事件监听器
    private OnSlidingListener onSlidingListener;

    /**
     * 重写构造函数
     *
     * @param context
     */
    public SwipeView(Context context) {
        super(context);
        scroller = new Scroller(getContext());
    }

    /**
     * 重写构造函数
     *
     * @param context
     * @param attrs
     */
    public SwipeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        scroller = new Scroller(getContext());
    }

    /**
     * 重写构造函数
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public SwipeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scroller = new Scroller(getContext());
    }

    /**
     * 设置最小滑动判断值
     *
     * @param minMoveDistance 传入最小滑动判断距离
     */
    public void setMinMoveDistance(float minMoveDistance) {
        this.minMoveDistance = minMoveDistance;
    }

    /**
     * 设置主界面文字
     *
     * @param text 需要显示的文字
     */
    public void setText(String text) {
        TextView view = findViewById(R.id.pointText);
        view.setText(text);
    }

    /**
     * 设置文字界面点击监听
     *
     * @param listener 点击事件监听器
     */
    public void setTextOnClickListener(OnClickListener listener) {
        TextView view = findViewById(R.id.pointText);
        view.setOnClickListener(listener);
    }

    /**
     * 设置修改按钮点击监听
     *
     * @param listener 点击事件监听器
     */
    public void setModifyOnClickListener(OnClickListener listener) {
        Button button = findViewById(R.id.item_modify_btn);
        button.setOnClickListener(listener);
    }

    /**
     * 设置删除按钮点击监听
     *
     * @param listener 点击事件监听器
     */
    public void setDelOnClickListener(OnClickListener listener) {
        Button button = findViewById(R.id.item_del_btn);
        button.setOnClickListener(listener);
    }

    /**
     * 设置滑动事件监听
     *
     * @param listener 滑动事件监听器
     */
    public void setOnSlidingListener(OnSlidingListener listener) {
        this.onSlidingListener = listener;
    }

    /**
     * 打开按钮菜单
     */
    public void openMenus() {
        smoothScrollTo((int) maxMoveDistance);
        isButtonOpen = true;
    }

    /**
     * 关闭按钮菜单
     */
    public void closeMenus() {
        smoothScrollTo(0);
        isButtonOpen = false;
    }

    /**
     * 缓慢滚动到指定位置
     *
     * @param destX 目标位置
     */
    private void smoothScrollTo(int destX) {
        int scrollX = getScrollX();
        int delta = destX - scrollX;
        // 500ms内滑动至destX
        scroller.startScroll(scrollX, 0, delta, 0, 300);
        invalidate();
    }

    /**
     * 重写滚动计算函数
     */
    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        }
    }

    /**
     * 重写事件分发函数，检测滑动和点击
     *
     * @param ev 传入点击事件
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 计算滑行的值
                downX = ev.getRawX();
                maxMoveDistance = getChildAt(1).getMeasuredWidth();
                criticalMoveDistance = ((float) getChildAt(1).getMeasuredWidth()) / 4;
                break;

            case MotionEvent.ACTION_MOVE:
                // 计算滑动距离
                if (isButtonOpen) {
                    moveX = ev.getRawX() - downX;
                } else {
                    moveX = downX - ev.getRawX();
                }
                // 如果移动大于临界距离并且没有进入滑动，判定为滑动开始
                if (moveX >= minMoveDistance && !isSliding) {
                    isSliding = true;
                    if (onSlidingListener != null) {
                        onSlidingListener.onSliding();
                    }
                }
                break;
        }
        // 不拦截该事件
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 如果正在滑动，拦截触摸事件
        return isSliding;
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent ev) {
        // 判断是否正在滑动
        if (isSliding) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    if (!scroller.isFinished()) {
                        return true;
                    }
                    // 判断是否展开
                    if (isButtonOpen) {
                        if (moveX > maxMoveDistance) {
                            // 滑动至右边界
                            scrollTo(0, 0);
                        } else if (moveX < 0) {
                            // 滑动至左边界
                            scrollTo((int) maxMoveDistance, 0);
                        } else {
                            // 滑动至当前触摸的位置
                            scrollTo((int) (maxMoveDistance - moveX), 0);
                        }
                    } else {
                        if (moveX > maxMoveDistance) {
                            // 滑动至左边界
                            scrollTo((int) maxMoveDistance, 0);
                        } else if (moveX < 0) {
                            // 滑动至右边界
                            scrollTo(0, 0);
                        } else {
                            // 滑动至当前触摸的位置
                            scrollTo((int) moveX, 0);
                        }
                    }
                    break;

                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    float lastDistance = downX - ev.getRawX();
                    // 设置最小触发滑动的距离
                    if (lastDistance >= criticalMoveDistance) {
                        openMenus();
                    } else {
                        closeMenus();
                    }
                    isSliding = false;
                    if (onSlidingListener != null) {
                        onSlidingListener.onEndSlide();
                    }
                    break;
            }
        }
        return true;
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        // 计算view所需空间
        View child = getChildAt(0);
        MarginLayoutParams params = (MarginLayoutParams) child.getLayoutParams();
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int margin = params.topMargin + params.bottomMargin;
        setMeasuredDimension(width, getChildAt(0).getMeasuredHeight() + margin);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int cCount = getChildCount();
        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            if (i == 0) {
                child.layout(l, t, r, b);
            } else if (i == 1) {
                int startY = t + ((b - t - child.getMeasuredHeight()) / 2);
                child.layout(r, startY, r + child.getMeasuredWidth(), startY + child.getMeasuredHeight());
            }
        }
    }

    public interface OnSlidingListener {
        void onSliding();

        void onEndSlide();
    }
}
