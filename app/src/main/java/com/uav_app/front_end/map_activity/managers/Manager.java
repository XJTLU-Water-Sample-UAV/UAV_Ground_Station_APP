package com.uav_app.front_end.map_activity.managers;

import com.uav_app.front_end.map_activity.MapActivity;

public abstract class Manager {
    protected final MapActivity activity;
    // 监听器ID
    protected final int LISTENER_ID;
    // 中介者模式实现管理类间互相通信
    protected Connector connector;

    protected Manager(MapActivity activity, int LISTENER_ID) {
        this.activity = activity;
        this.LISTENER_ID = LISTENER_ID;
    }

    public void init(Connector connector) {
        this.connector = connector;
    }

    public int getLISTENER_ID() {
        return LISTENER_ID;
    }

    public Connector getConnector() {
        return connector;
    }
}
