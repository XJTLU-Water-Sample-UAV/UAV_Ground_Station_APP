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

    /**
     * 无人机解锁
     */
    void onUavArmed();

    /**
     * 无人机上锁
     */
    void onUavDisarmed();


    void onUavCoordChange();
}
