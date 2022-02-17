package com.uav_app.front_end.map_activity.managers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.amap.api.maps.MapView;
import com.uav_app.back_end.uav_manager.R;
import com.uav_app.front_end.map_activity.MapActivity;
import com.uav_app.front_end.map_activity.MapActivityState;
import com.uav_app.front_end.map_activity.child_view.tab_child.ChildView;
import com.uav_app.front_end.map_activity.child_view.tab_child.FlightView;
import com.uav_app.front_end.map_activity.child_view.tab_child.SelectView;
import com.uav_app.front_end.map_activity.child_view.tab_child.UavUnconnectedView;
import com.uav_app.front_end.map_activity.child_view.tab_child.UsbUnconnectedView;
import com.uav_app.front_end.map_activity.child_view.tab_child.WaitView;

import java.util.HashMap;
import java.util.Objects;

public class TabManager extends Manager implements MapActivityState.StateChangeListener {
    // 子view的哈希表
    private final HashMap<TabState, ChildView> viewMap;
    // 当前的视图
    private TabState currentView;
    // 母面板
    private final FrameLayout tab;
    // 背景面板
    private final View background;
    // 组件面板属性
    private ConstraintLayout.LayoutParams params;
    // 组件面板关闭、展开位置
    private int packUpLocation = 0, showLocation = 0, hideLocation = 0;
    // 面板动画监听器
    private ValueAnimator showAnimator, closeAnimator;
    // 组件面板是否展开
    private boolean isTabShow = false;

    @SuppressLint("InflateParams")
    public TabManager(MapActivity activity) {
        super(activity, 0x01);
        // 获取需要从其他xml加载的组件
        viewMap = new HashMap<>();
        viewMap.put(TabState.VIEW_USB_UNCONNECTED, new UsbUnconnectedView(activity, this));
        viewMap.put(TabState.VIEW_UAV_UNCONNECTED, new UavUnconnectedView(activity, this));
        viewMap.put(TabState.VIEW_WAIT, new WaitView(activity, this));
        viewMap.put(TabState.VIEW_SELECT, new SelectView(activity, this));
        viewMap.put(TabState.VIEW_FLIGHT, new FlightView(activity, this));
        // 获取其他面板
        tab = activity.findViewById(R.id.tab);
        background = activity.findViewById(R.id.background);
        // 设置遮罩层颜色和透明度
        activity.findViewById(R.id.background).setBackgroundColor(Color.WHITE);
        activity.findViewById(R.id.background).setAlpha(0);
        // 加载USB未连接面板
        showChildView(TabState.VIEW_USB_UNCONNECTED);
    }

    public void init(Connector connector, int mapHeight, int status_bar_height) {
        super.init(connector);
        MapActivityState.getMapActivityState().addListener(LISTENER_ID, this);
        // 设置面板收起和展开位置
        UsbUnconnectedView usbUnconnectedView = (UsbUnconnectedView) viewMap.get(TabState.VIEW_USB_UNCONNECTED);
        assert usbUnconnectedView != null;
        this.hideLocation = usbUnconnectedView.getButtonHeight() * 3;
        this.packUpLocation = mapHeight - hideLocation;
        this.showLocation = usbUnconnectedView.getButtonMargin() + status_bar_height;
        // 重设控件面板位置和大小
        params = (ConstraintLayout.LayoutParams) tab.getLayoutParams();
        params.topMargin = packUpLocation;
        params.height = mapHeight - (usbUnconnectedView.getButtonMargin() * 2) - status_bar_height;
        // 刷新视图
        tab.requestLayout();
        // 初始化面板动画监听器
        ValueAnimator.AnimatorUpdateListener tabListener = animation -> {
            float value = (float) animation.getAnimatedValue();
            // 改变后的值发赋值给对象的属性值
            params.topMargin = (int) (packUpLocation - ((packUpLocation - showLocation) * value));
            // 计算遮罩层透明度
            background.setAlpha(value);
            // 刷新视图
            tab.requestLayout();
        };
        // 初始化面板展开动画
        showAnimator = ValueAnimator.ofFloat(0, 1);
        showAnimator.setDuration(400);
        showAnimator.addUpdateListener(tabListener);
        showAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // 设置地图不可见
                MapView mapActivity = connector.findMotherViewById(R.id.map);
                mapActivity.setVisibility(MapView.INVISIBLE);
            }
        });
        // 初始化面板关闭动画
        closeAnimator = ValueAnimator.ofFloat(1, 0);
        closeAnimator.setDuration(400);
        closeAnimator.addUpdateListener(tabListener);
    }

    private void showChildView(TabState targetView) {
        ChildView tView = viewMap.get(targetView);
        assert tView != null;
        tab.addView(tView);
        tView.onShow();
        currentView = targetView;
    }

    private void hideCurrentView() {
        ChildView cView = viewMap.get(currentView);
        assert cView != null;
        cView.onHide();
        tab.removeAllViews();
    }

    public void openTab() {
        if (isTabShow) {
            return;
        }
        // 打开展开
        isTabShow = true;
        Objects.requireNonNull(viewMap.get(currentView)).onUp();
        // 启动动画
        showAnimator.start();
    }

    public void closeTab() {
        if (!isTabShow) {
            return;
        }
        doClose();
        // 启动动画
        closeAnimator.start();
    }

    private void doClose() {
        // 关闭展开
        isTabShow = false;
        Objects.requireNonNull(viewMap.get(currentView)).onDown();
        // 设置地图可见
        MapView mapActivity = connector.findMotherViewById(R.id.map);
        mapActivity.setVisibility(MapView.VISIBLE);
    }

    private void transferTab(TabState targetView) {
        // 如果加载的是当前界面，直接返回
        if (currentView == targetView) {
            return;
        }
        // 计算动画开始位置参数
        int startLocation;
        if (isTabShow) {
            startLocation = showLocation;
            doClose();
        } else {
            startLocation = packUpLocation;
        }
        int stopLocation = packUpLocation + hideLocation;
        // 设置动画效果监听器
        ValueAnimator.AnimatorUpdateListener listener = animation -> {
            int value = (int) animation.getAnimatedValue();
            params.topMargin = value;
            tab.requestLayout();
            if (value < packUpLocation) {
                // 计算遮罩层透明度
                float alpha = ((float) (packUpLocation - value)) / ((float) (packUpLocation - showLocation));
                background.setAlpha(alpha);
            }
        };
        // 设置切换动画
        ValueAnimator animator = ValueAnimator.ofInt(startLocation, stopLocation);
        animator.setDuration(300);
        animator.addUpdateListener(listener);
        // 设置动画结束时执行的操作
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // 背景透明度设为0
                background.setAlpha(0);
                // 隐藏当前面板
                hideCurrentView();
                // 显示目标面板
                showChildView(targetView);
                // 执行展开动画
                ValueAnimator animator = ValueAnimator.ofInt(stopLocation, packUpLocation);
                animator.setDuration(300);
                animator.addUpdateListener(listener);
                animator.start();
            }
        });
        animator.start();
    }

    public boolean isTabShow() {
        return isTabShow;
    }

    public TabState getCurrentTabView() {
        return currentView;
    }

    @Override
    public void onStateChange(MapActivityState mapActivityState) {
        // 判断当前连接情况以动态加载面板
        transferTab(mapActivityState.getTabViewState().tabState);
    }

    public void refreshList() {
        SelectView selectView = (SelectView) viewMap.get(TabState.VIEW_SELECT);
        assert selectView != null;
        selectView.refreshList();
    }

    public enum TabState {
        VIEW_USB_UNCONNECTED,
        VIEW_UAV_UNCONNECTED,
        VIEW_WAIT,
        VIEW_SELECT,
        VIEW_FLIGHT
    }
}
