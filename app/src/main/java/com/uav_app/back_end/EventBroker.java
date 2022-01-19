package com.uav_app.back_end;

import com.uav_app.back_end.message_manager.MavlinkMsgManager;
import com.uav_app.back_end.uav_manager.UavStateInterface;
import com.uav_app.back_end.uav_manager.UavStateManager;
import com.uav_app.back_end.usb_manager.UsbConnectInterface;
import com.uav_app.back_end.usb_manager.UsbConnectManager;

import java.util.List;

import io.serial_port_driver.UsbSerialDriver;

public class EventBroker {
    // 本类单例对象
    private static EventBroker broker;
    // 数传管理对象
    private final UsbConnectManager connectManager;
    // MAVLINK消息管理对象
    private final MavlinkMsgManager msgManager;
    // 无人机管理对象
    private final UavStateManager stateManager;
    // 前端监听器
    private EventObserver observer;

    private EventBroker() {
        EventReceiver receiver = new EventReceiver();
        connectManager = UsbConnectManager.getConnectManager();
        msgManager = MavlinkMsgManager.getMessageManager();
        stateManager = UavStateManager.getUavStateManager();
        connectManager.setReceiver(receiver);
        stateManager.setReceiver(receiver);
    }

    public static EventBroker getBroker() {
        if (broker == null) {
            synchronized (UsbConnectManager.class) {
                if (broker == null) {
                    broker = new EventBroker();
                }
            }
        }
        return broker;
    }

    public void subscribe(EventObserver observer) {
        this.observer = observer;
    }

    private void publishEvent(Event event) {
        if (observer != null) {
            observer.onEvent(event);
        }
    }

    public enum Event {
        // 找不到数传
        USB_CANNOT_FOUND,
        // 找不到指定数传
        USB_CANNOT_FOUND_SPECIFIED,
        // 找到多个数传
        USB_FIND_MULTIPLE,
        // 无访问USB设备权限
        USB_NO_PERMISSION,
        // 数传连接成功
        USB_CONNECT_SUCCESS,
        // 数传连接失败
        USB_CONNECT_FAIL,
        // 数传失去连接
        USB_LOSE,
        // 数传IO出现错误
        USB_IO_ERROR,
        // 无人机连接成功
        UAV_CONNECT,
        // 无人机解锁
        UAV_UNLOCK,
        // 无人机起飞
        UAV_TAKEOFF,
        // 无人机失去连接
        UAV_LOSE
    }

    public interface EventObserver {
        void onEvent(Event event);
    }

    private class EventReceiver implements UsbConnectInterface, UavStateInterface {
        @Override
        public void onCanNotFoundDevice() {
            publishEvent(Event.USB_CANNOT_FOUND);
        }

        @Override
        public void onCanNotFoundSpecifiedDevice() {
            publishEvent(Event.USB_CANNOT_FOUND_SPECIFIED);
        }

        @Override
        public void onFindMultipleDevices(List<UsbSerialDriver> driverList) {
            publishEvent(Event.USB_FIND_MULTIPLE);
        }

        @Override
        public void onPermissionNotObtained() {
            publishEvent(Event.USB_NO_PERMISSION);
        }

        @Override
        public void onConnectSuccess() {
            publishEvent(Event.USB_CONNECT_SUCCESS);
        }

        @Override
        public void onConnectFail(Exception e) {
            publishEvent(Event.USB_CONNECT_FAIL);
        }

        @Override
        public void onLoseConnectDevice() {
            publishEvent(Event.USB_LOSE);
        }

        @Override
        public void onSendMessageError(Exception e) {
            publishEvent(Event.USB_IO_ERROR);
        }

        @Override
        public void onStartReceiveMessage() {
            msgManager.startRecvMessage();
        }

        @Override
        public void onStopReceiveMessage() {
            msgManager.stopRecvMessage();
        }

        @Override
        public void onReceiveMessageError(Exception e) {
            publishEvent(Event.USB_IO_ERROR);
        }

        @Override
        public void onIncomingMessage(byte[] data) {
            msgManager.uartToUdp(data);
        }

        @Override
        public void onUavConnect() {

        }

        @Override
        public void onNormalDisconnect() {

        }

        @Override
        public void onAbnormalDisconnect() {

        }
    }
}
