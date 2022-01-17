package com.uav_app.back_end.uav_manager;

/**
 * 表示无人机状态的枚举类
 */
public enum UavState {
    // 无人机未连接地面站
    UAV_NOT_CONNECT,
    // 无人机状态就绪
    UAV_STANDING_BY,
    // 起飞
    UAV_TAKE_OFF,
    // 正在前往取样点
    UAV_ROUTING,
    // 采样中
    UAV_SAMPLING,
    // 返回中
    UAV_RETURNING,
    // 降落中
    UAV_LANDING,
    // 任务完成等待回收
    UAV_MISSION_ACCOMPLISHED
}
