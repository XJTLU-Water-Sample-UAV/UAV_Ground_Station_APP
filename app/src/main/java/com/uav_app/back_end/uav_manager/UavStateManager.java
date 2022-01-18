package com.uav_app.back_end.uav_manager;

import android.annotation.SuppressLint;
import android.os.Looper;
import android.widget.Toast;

import com.amap.api.maps.model.LatLng;
import com.uav_app.MyApplication;
import com.uav_app.back_end.usb_manager.UsbConnectInterface;
import com.uav_app.back_end.usb_manager.UsbConnectManager;

import java.util.ArrayList;
import java.util.List;

import io.mavsdk.System;
import io.mavsdk.mavsdkserver.MavsdkServer;
import io.reactivex.disposables.Disposable;
import io.serial_port_driver.UsbSerialDriver;

/**
 * 本类用于管理和控制无人机的状态
 */
public class UavStateManager {
    // 本类单例对象
    private volatile static UavStateManager uavStateManager;
    // 观察者对象列表
    private final ArrayList<UavStateInterface> observerList;
    // 无人机状态
    private UavState uavState;
    // MAVSDK子栈列表
    private final List<Disposable> disposables = new ArrayList<>();
    // 后端IP地址
    public static final String BACKEND_IP_ADDRESS = "127.0.0.1";
    // 服务器监听MAVLink消息的端口号
    public static final int BACKEND_PORT = 8000;
    // 无人机系统后端对象
    private final System drone;

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
        this.observerList = new ArrayList<>();
        // 添加监听数传设备断联的观察者
        UsbObserver usbObserver = new UsbObserver();
        UsbConnectManager.getConnectManager().setReceiver(usbObserver);
        // 创建服务器后端
        MavsdkServer mavsdkServer = new MavsdkServer();
        int mavsdkServerPort = mavsdkServer.run("udp://:" + BACKEND_PORT);
        drone = new System(BACKEND_IP_ADDRESS, mavsdkServerPort);
        // 发布无人机连接监听请求
        disposables.add(drone.getCore().getConnectionState().distinctUntilChanged().subscribe(connectionState -> {
            boolean isConnect = connectionState.getIsConnected();
            if (isConnect) {
                new Thread() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        Toast.makeText(MyApplication.getApplication(), "无人机已经连接", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }.start();
            } else {


            }
        }));
        // 发布飞行模式监听请求
        disposables.add(drone.getTelemetry().getFlightMode().distinctUntilChanged()
                .subscribe(flightMode -> {

                }));
        disposables.add(drone.getTelemetry().getArmed().distinctUntilChanged()
                .subscribe(armed -> {

                }));
        disposables.add(drone.getTelemetry().getPosition().subscribe(position -> {
            LatLng latLng = new LatLng(position.getLatitudeDeg(), position.getLongitudeDeg());

        }));
    }

    /**
     * 添加USB事件的监听器
     *
     * @param stateInterface 监听器对象
     */
    public void addObserver(UavStateInterface stateInterface) {
        observerList.add(stateInterface);
    }

    /**
     * 获取无人机当前状态
     *
     * @return 无人机当前状态
     */
    public UavState getUavState() {
        return uavState;
    }

    /**
     * 尝试连接无人机，连接结果将异步返回
     */
    public void connectUav() {

    }

    public void unlockUav() {
        drone.getAction().arm().onErrorComplete().subscribe();
        //drone.getCamera().takePhoto().subscribe();
    }

    public void takeoff() {
//        drone.getAction().arm().onErrorComplete().andThen(drone.getAction().takeoff()).subscribe();
        drone.getAction().takeoff().onErrorComplete().subscribe();
    }

    public void landing() {
        drone.getAction().land().subscribe();
    }

    public void sendPoint(float x, float y, float z) {

    }

    /**
     * 断开与无人机的连接
     */
    public void disconnectUav() {
        if (uavState == UavState.UAV_NOT_CONNECT) {
            return;
        } else if (uavState == UavState.UAV_MISSION_ACCOMPLISHED) {
            // 回调无人机正常断开连接函数
            for (int i = 0; i < observerList.size(); i++) {
                UavStateInterface stateInterface = observerList.get(i);
                stateInterface.onNormalDisconnect();
            }
        } else {
            // 回调无人机非正常断开连接函数
            for (int i = 0; i < observerList.size(); i++) {
                UavStateInterface stateInterface = observerList.get(i);
                stateInterface.onAbnormalDisconnect();
            }
        }
        // 将无人机连接状态设置为断开
        uavState = UavState.UAV_NOT_CONNECT;
    }

    private class UsbObserver implements UsbConnectInterface {
        @Override
        public void onCanNotFoundDevice() {
        }

        @Override
        public void onCanNotFoundSpecifiedDevice() {
        }

        @Override
        public void onFindMultipleDevices(List<UsbSerialDriver> driverList) {
        }

        @Override
        public void onPermissionNotObtained() {
        }

        @Override
        public void onConnectSuccess() {
        }

        @Override
        public void onConnectFail(Exception e) {
        }

        @Override
        public void onLoseConnectDevice() {
            // 断开无人机连接
            disconnectUav();
        }

        @Override
        public void onSendMessageError(Exception e) {
        }

        @Override
        public void onStartReceiveMessage() {
            // 开始连接无人机
            connectUav();
        }

        @Override
        public void onStopReceiveMessage() {
            // 断开无人机连接
            disconnectUav();
        }

        @Override
        public void onReceiveMessageError(Exception e) {
        }

        @Override
        public void onIncomingMessage(byte[] data) {
        }
    }
}
