package com.uav_app.front_end.map_activity.managers;

import android.content.Context;
import android.view.View;

import com.uav_app.front_end.map_activity.MapActivity;

/**
 * 本类为全局通信对象，为UI管理对象提供了通信中介，以实现管理对象以及MapActivity之间的解耦
 */
public class Connector {
    private final MapActivity activity;
    private MapManager mapManager;
    private TabManager tabManager;

    public Connector(MapActivity activity) {
        this.activity = activity;
    }

    /**
     * 初始化中介器
     *
     * @param mapManager 地图管理对象
     * @param tabManager tab管理对象
     */
    public void initConnector(MapManager mapManager, TabManager tabManager) {
        this.mapManager = mapManager;
        this.tabManager = tabManager;
    }

    /**
     * 获得tab当前显示的view
     *
     * @return 当前的显示页面
     */
    public TabManager.TabState getCurrentTabView() {
        return tabManager.getCurrentTabView();
    }

    /**
     * 请求刷新选点列表
     */
    public void refreshList() {
        tabManager.refreshList();
    }

    /**
     * 请求修改选点信息
     *
     * @param position 需要修改的位置
     */
    public void modifyPoint(int position) {
        mapManager.modifyPoint(position);
    }

    /**
     * 请求删除选点
     *
     * @param position 需要删除的位置
     */
    public void deletePoint(int position) {
        mapManager.deletePoint(position);
    }

    /**
     * 将地图移动到某点
     *
     * @param wgs84Lng 坐标经度
     * @param wgs84Lat 坐标纬度
     */
    public void moveToPoint(double wgs84Lng, double wgs84Lat) {
        mapManager.moveToPoint(wgs84Lng, wgs84Lat);
    }

    /**
     * 展示航点信息框
     *
     * @param position 需要展示的位置
     */
    public void showPointInfoWindow(int position) {
        mapManager.showPointInfoWindow(position);
    }

    /**
     * 从activity中获取view
     *
     * @param id view的id
     * @return 找到的view
     */
    public <T extends View> T findMotherViewById(int id) {
        return activity.findViewById(id);
    }

    /**
     * 从activity中获取context
     *
     * @return mapActivity的context
     */
    public Context getContext() {
        return activity;
    }
}
