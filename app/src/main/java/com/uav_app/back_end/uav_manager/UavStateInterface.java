package com.uav_app.back_end.uav_manager;

import io.mavsdk.telemetry.Telemetry;

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

    /**
     * 无人机坐标改变
     */
    void onUavCoordChange(Telemetry.Position position);

    /**
     * 无人机状态改变
     */
    void onUavStateChange(UavStateManager.UavState state);
}
