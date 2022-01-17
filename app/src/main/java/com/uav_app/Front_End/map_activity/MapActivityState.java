package com.uav_app.Front_End.map_activity;

import com.amap.api.maps.model.Marker;
import com.uav_app.BackEnd.uav_manager.nav_point.NavPointManager;
import com.uav_app.BackEnd.usb_manager.UsbConnectManager;
import com.uav_app.Front_End.map_activity.managers.TabManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapActivityState {
    // 本类单例对象
    private volatile static MapActivityState mapActivityState;
    // 地图状态
    public final MapViewState mapViewState;
    // 面板状态
    public final TabViewState tabViewState;
    // 等待界面状态
    public final TabViewState.WaitViewState waitViewState;
    // 选点管理器
    public final NavPointManager pointManager;
    // 监听器列表
    private final HashMap<Integer, StateChangeListener> listenerMap;

    public static MapActivityState getMapActivityState() {
        if (mapActivityState == null) {
            synchronized (UsbConnectManager.class) {
                if (mapActivityState == null) {
                    mapActivityState = new MapActivityState();
                }
            }
        }
        return mapActivityState;
    }

    private MapActivityState() {
        tabViewState = new TabViewState();
        mapViewState = new MapViewState();
        waitViewState = new TabViewState.WaitViewState();
        pointManager = new NavPointManager();
        listenerMap = new HashMap<>();
    }

    public void applyChange() {
        for (Map.Entry<Integer, StateChangeListener> entry : listenerMap.entrySet()) {
            entry.getValue().onStateChange(this);
        }
    }

    public void addListener(int listenerId, StateChangeListener listener) {
        listenerMap.put(listenerId, listener);
    }

    public void removeListener(int listenerId) {
        listenerMap.remove(listenerId);
    }

    public NavPointManager getPointManager() {
        return pointManager;
    }

    /**
     * 地图状态子类
     */
    public static class MapViewState {
        // 是否可以选点
        public boolean isCanBeSelect = false;
        // 选点列表
        public ArrayList<Marker> markerList = new ArrayList<>();
    }

    /**
     * 面板状态子类
     */
    public static class TabViewState {
        // 视图状态
        public TabManager.TabState tabState = TabManager.TabState.VIEW_USB_UNCONNECTED;

        /**
         * 等待界面状态子类
         */
        public static class WaitViewState {
            // 是否已经完成选点
            public boolean isPointSelected = false;
            // 是否已经解锁无人机
            public boolean isUavUnlocked = false;
        }
    }

    /**
     * 状态改变的回调监听器
     */
    public interface StateChangeListener {
        void onStateChange(MapActivityState mapActivityState);
    }
}
