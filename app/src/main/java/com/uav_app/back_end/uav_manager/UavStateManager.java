package com.uav_app.back_end.uav_manager;

import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.List;

import io.mavsdk.System;
import io.mavsdk.mavsdkserver.MavsdkServer;
import io.mavsdk.mission.Mission;
import io.reactivex.disposables.Disposable;

/**
 * 本类用于管理和控制无人机的状态
 */
public class UavStateManager {
    // 本类单例对象
    private volatile static UavStateManager uavStateManager;
    // 观察者对象列表
    private UavStateInterface receiver;
    // 无人机状态
    private final UavState uavState;
    // MAVSDK子栈列表
    private final List<Disposable> disposables = new ArrayList<>();
    // 无人机系统后端对象
    private final System drone;
    // 后端IP地址
    public static final String BACKEND_IP_ADDRESS = "127.0.0.1";
    // 服务器监听MAVLink消息的端口号
    public static final int BACKEND_PORT = 8000;

    public static UavStateManager getUavStateManager() {
        if (uavStateManager == null) {
            synchronized (UavStateManager.class) {
                if (uavStateManager == null) {
                    uavStateManager = new UavStateManager();
                }
            }
        }
        return uavStateManager;
    }

    @SuppressLint("CheckResult")
    private UavStateManager() {
        this.uavState = UavState.UAV_NOT_CONNECT;
        // 创建服务器后端
        MavsdkServer mavsdkServer = new MavsdkServer();
        int mavsdkServerPort = mavsdkServer.run("udp://:" + BACKEND_PORT);
        drone = new System(BACKEND_IP_ADDRESS, mavsdkServerPort);
        // 发布无人机连接监听请求
        disposables.add(drone.getCore().getConnectionState().distinctUntilChanged().subscribe(connectionState -> {
            boolean isConnect = connectionState.getIsConnected();
            if (isConnect) {
                receiver.onUavConnect();
            } else {
                receiver.onUavDisconnect();
            }
        }));
        // 发布飞行模式监听请求
        disposables.add(drone.getTelemetry().getFlightMode().distinctUntilChanged()
                .subscribe(flightMode -> {

                }));
        // 发布是否解锁监听请求
        disposables.add(drone.getTelemetry().getArmed().distinctUntilChanged()
                .subscribe(armed -> {
                    if (armed) {
                        receiver.onUavArmed();
                    } else {
                        receiver.onUavDisarmed();
                    }
                }));
        // 发布位置监听请求
        disposables.add(drone.getTelemetry().getPosition().subscribe(position -> {
            receiver.onUavCoordChange(position);
        }));
    }

    /**
     * 添加USB事件的监听器
     *
     * @param receiver 监听器对象
     */
    public void setReceiver(UavStateInterface receiver) {
        this.receiver = receiver;
    }

    /**
     * 获取无人机当前状态
     *
     * @return 无人机当前状态
     */
    public UavState getUavState() {
        return uavState;
    }

    public void unlockUav() {
        drone.getAction().arm().onErrorComplete().subscribe();
    }

    public void takeoff() {
        drone.getAction().takeoff().onErrorComplete().subscribe();
    }

    public void landing() {
        drone.getAction().land().subscribe();
    }

    public void sendPoint(float x, float y, float z) {

    }

    /**
     * 表示无人机状态的枚举类
     */
    public enum UavState {
        // 无人机未连接地面站
        UAV_NOT_CONNECT,
        // 无人机状态就绪
        UAV_STANDING_BY,
        // 起飞中
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
}
