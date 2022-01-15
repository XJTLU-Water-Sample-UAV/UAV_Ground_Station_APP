package com.uav_app.uav_manager;

public interface UavStateInterface {
    /**
     * 无人机连接成功
     */
    void onUavConnect();

    /**
     * 无人机信号差
     */
    void onUavSignalBad();

    /**
     * 无人机正常断开连接
     */
    void onNormalDisconnect();

    /**
     * 无人机异常断开连接
     */
    void onAbnormalDisconnect();
}
