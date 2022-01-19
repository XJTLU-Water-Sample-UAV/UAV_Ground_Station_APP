package com.uav_app.back_end.uav_manager;

public interface UavStateInterface {
    /**
     * 无人机连接成功
     */
    void onUavConnect();

    /**
     * 无人机断开连接
     */
    void onUavDisconnect();
}
