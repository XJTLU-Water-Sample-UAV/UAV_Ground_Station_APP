package com.uav_app.front_end.map_activity;

import com.amap.api.maps.model.Marker;
import com.uav_app.back_end.uav_manager.nav_point.NavPointManager;
import com.uav_app.back_end.usb_manager.UsbConnectManager;
import com.uav_app.front_end.OperationStateMachine;
import com.uav_app.front_end.map_activity.managers.TabManager;

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

    public void refreshState(OperationStateMachine.State state) {
        switch (state) {
            case STATE_USB_UNCONNECTED:
                tabViewState.tabState = TabManager.TabState.VIEW_USB_UNCONNECTED;
                waitViewState.isPointSelected = false;
                waitViewState.isUavArmed = false;
                mapViewState.isCanBeSelect = false;
                mapViewState.markerList.clear();
                pointManager.deleteAll();
                break;

            case STATE_UAV_UNCONNECTED:
                tabViewState.tabState = TabManager.TabState.VIEW_UAV_UNCONNECTED;
                waitViewState.isPointSelected = false;
                waitViewState.isUavArmed = false;
                mapViewState.isCanBeSelect = false;
                mapViewState.markerList.clear();
                pointManager.deleteAll();
                break;

            case STATE_WAIT_TO_SELECT_POINT:
                tabViewState.tabState = TabManager.TabState.VIEW_WAIT;
                waitViewState.isPointSelected = false;
                mapViewState.isCanBeSelect = false;
                mapViewState.markerList.clear();
                pointManager.deleteAll();
                break;

            case STATE_ON_SELECT:
                tabViewState.tabState = TabManager.TabState.VIEW_SELECT;
                waitViewState.isPointSelected = false;
                mapViewState.isCanBeSelect = true;
                mapViewState.markerList.clear();
                pointManager.deleteAll();
                break;

            case STATE_FINISH_SELECT_POINT:
                tabViewState.tabState = TabManager.TabState.VIEW_WAIT;
                waitViewState.isPointSelected = true;
                mapViewState.isCanBeSelect = false;
                break;

            case STATE_UAV_ARMED:
                tabViewState.tabState = TabManager.TabState.VIEW_WAIT;
                waitViewState.isPointSelected = true;
                waitViewState.isUavArmed = true;
                mapViewState.isCanBeSelect = false;
                mapViewState.markerList.clear();
                pointManager.deleteAll();
                break;

            case STATE_UAV_FLIGHT:
                tabViewState.tabState = TabManager.TabState.VIEW_FLIGHT;
                waitViewState.isPointSelected = true;
                waitViewState.isUavArmed = false;
                mapViewState.isCanBeSelect = false;
                break;
        }
        applyChange();
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
            public boolean isUavArmed = false;
        }
    }

    /**
     * 状态改变的回调监听器
     */
    public interface StateChangeListener {
        void onStateChange(MapActivityState mapActivityState);
    }
}
