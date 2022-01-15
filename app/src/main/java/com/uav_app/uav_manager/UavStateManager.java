package com.uav_app.uav_manager;

import android.os.Looper;
import android.widget.Toast;

import com.mavlink.ardupilotmega.msg_digicam_configure;
import com.mavlink.ardupilotmega.msg_digicam_control;
import com.mavlink.common.msg_command_long;
import com.mavlink.common.msg_heartbeat;
import com.mavlink.common.msg_mission_item;
import com.mavlink.common.msg_set_mode;
import com.mavlink.common.msg_sys_status;
import com.mavlink.enums.MAV_CMD;
import com.mavlink.enums.MAV_FRAME;
import com.mavlink.enums.MAV_MODE;
import com.uav_app.MyApplication;
import com.uav_app.message_manager.MavlinkMsgInterface;
import com.uav_app.message_manager.MavlinkMsgManager;
import com.uav_app.usb_manager.UsbConnectInterface;
import com.uav_app.usb_manager.UsbConnectManager;
import com.uav_app.usb_manager.serial_port_driver.UsbSerialDriver;

import java.util.ArrayList;
import java.util.List;

import io.mavsdk.mission.Mission;
import io.reactivex.Completable;

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
    // 是否解锁
    private boolean isArmed = false;

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

    private UavStateManager() {
        this.uavState = UavState.UAV_NOT_CONNECT;
        this.observerList = new ArrayList<>();
        // 添加监听数传设备断联的观察者
        UsbObserver usbObserver = new UsbObserver();
        UsbConnectManager.getConnectManager().addObserver(usbObserver);
        // 添加监听MAV消息的观察者
        MavlinkMsgObserver mavlinkMsgObserver = new MavlinkMsgObserver();
        MavlinkMsgManager.getMessageManager().addObserver(mavlinkMsgObserver);
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
        // 切换模式
        msg_set_mode set_mode = new msg_set_mode();
        set_mode.target_system = 1;
        set_mode.custom_mode = 0;
        set_mode.base_mode = MAV_MODE.MAV_MODE_STABILIZE_DISARMED;
        // 起飞
        msg_command_long command_long = new msg_command_long();
        command_long.target_system = 1;
        command_long.target_component = 1;
        command_long.command = MAV_CMD.MAV_CMD_NAV_TAKEOFF;
        // 照相
        msg_digicam_configure configure = new msg_digicam_configure();
        msg_digicam_control control = new msg_digicam_control();
        control.shot = 1;
        // 发送
        MavlinkMsgManager.getMessageManager().sendMavlinkMessage(set_mode);
        MavlinkMsgManager.getMessageManager().sendMavlinkMessage(command_long);
        MavlinkMsgManager.getMessageManager().sendMavlinkMessage(configure);
    }

    public void unlockUav() {
        msg_command_long msg = new msg_command_long();
        msg.target_system = 1;
        msg.target_component = 1;
        msg.command = MAV_CMD.MAV_CMD_NAV_TAKEOFF;

        msg.param7 = (float) 1.5;

        MavlinkMsgManager.getMessageManager().sendMavlinkMessage(msg);


        // 解锁无人机
        msg_command_long command_long = new msg_command_long();
        command_long.command = MAV_CMD.MAV_CMD_COMPONENT_ARM_DISARM;
        // 参数一：1为解锁，0为锁定
        command_long.param1 = 1;
        command_long.target_component = 1;
        command_long.target_system = 1;
        MavlinkMsgManager.getMessageManager().sendMavlinkMessage(command_long);

    }

    public void takeoff() {
        // 起飞
        Completable completable;

        /*
        msg_mission_item mavMsg = new msg_mission_item();
        mavMsg.command = MAV_CMD.MAV_CMD_NAV_TAKEOFF;
        mavMsg.frame = MAV_FRAME.MAV_FRAME_GLOBAL_RELATIVE_ALT;
        mavMsg.z = (float) 1.0;
        MavlinkMsgManager.getMessageManager().sendMavlinkMessage(mavMsg);
        */


    }

    public void landing() {
        // 降落
        msg_mission_item mavMsg = new msg_mission_item();
        mavMsg.command = MAV_CMD.MAV_CMD_NAV_LAND;
        MavlinkMsgManager.getMessageManager().sendMavlinkMessage(mavMsg);
    }

    public void sendPoint(float x, float y, float z) {
        msg_mission_item item = new msg_mission_item();

        item.x = x;
        item.y = y;
        item.z = z;


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
            // TODO:处理心跳包


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
            //connectUav();
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
        public void onIncomingMsg(byte[] data, int resultLen) {
        }
    }

    private class MavlinkMsgObserver implements MavlinkMsgInterface {
        private byte loseHeartbeatCount = 0;
        private boolean isReceiving = false;

        public MavlinkMsgObserver() {

        }

        @Override
        public void onRecvUavStatus(msg_sys_status sys_status) {
            // 无人机状态更改
            if (uavState == UavState.UAV_NOT_CONNECT) {
                return;
            }


        }

        @Override
        public void onRecvHeartbeat(msg_heartbeat heartbeat) {


//            Looper.prepare();
//            Toast.makeText(MyApplication.getApplication().getContext(), "abcde", Toast.LENGTH_SHORT).show();
//            Looper.loop();




            /*// 无人机心跳包
            if (uavState == UavState.UAV_NOT_CONNECT) {
                // 无人机连接成功



                uavState = UavState.UAV_STANDING_BY;
                for (int i = 0; i < observerList.size(); i++) {
                    UavStateInterface msgInterface = observerList.get(i);
                    msgInterface.onUavConnect();
                }
                Thread thread = new Thread(() -> {
                    while (uavState != UavState.UAV_NOT_CONNECT) {
                        if (!isReceiving) {
                            loseHeartbeatCount++;
                        }
                        isReceiving = false;
                        if (loseHeartbeatCount == 5) {
                            for (int i = 0; i < observerList.size(); i++) {
                                UavStateInterface msgInterface = observerList.get(i);
                                msgInterface.onAbnormalDisconnect();
                            }
                        } else if (loseHeartbeatCount >= 10) {
                            uavState = UavState.UAV_NOT_CONNECT;
                            loseHeartbeatCount = 0;
                            for (int i = 0; i < observerList.size(); i++) {
                                UavStateInterface msgInterface = observerList.get(i);
                                msgInterface.onAbnormalDisconnect();
                            }

                        }
                        // 线程休眠
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ignored) {
                        }
                    }
                });
                thread.start();
            } else {
                isReceiving = true;
            }
*/
        }
    }
}
