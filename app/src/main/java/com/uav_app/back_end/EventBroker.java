package com.uav_app.back_end;

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
    UsbConnectManager connectManager;
    // 无人机管理对象
    UavStateManager stateManager;
    // 前端监听器
    EventObserver observer;

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

    private EventBroker() {
        EventReceiver receiver = new EventReceiver();
        connectManager = UsbConnectManager.getConnectManager();
        stateManager = UavStateManager.getUavStateManager();
        connectManager.setReceiver(receiver);
        stateManager.setReceiver(receiver);
    }

    public void publishEvent() {

    }

    public void subscribe(EventObserver observer) {
        this.observer = observer;
    }

    public enum Event {
        USB_CANNOT_FOUND,
        USB_CANNOT_FOUND_SPECIFIED,
        USB_NO_PERMISSION,
        USB_CONNECT,
        USB_LOSE,
        UAV_CONNECT,
        UAV_UNLOCK,
        UAV_TAKEOFF,
        UAV_LOSE
    }

    public interface EventObserver {
        void onEvent(Event event);
    }

    private class EventReceiver implements UsbConnectInterface, UavStateInterface {
        @Override
        public void onUavConnect() {

        }

        @Override
        public void onUavSignalBad() {

        }

        @Override
        public void onNormalDisconnect() {

        }

        @Override
        public void onAbnormalDisconnect() {

        }

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

        }

        @Override
        public void onSendMessageError(Exception e) {

        }

        @Override
        public void onStartReceiveMessage() {

        }

        @Override
        public void onStopReceiveMessage() {

        }

        @Override
        public void onReceiveMessageError(Exception e) {

        }

        @Override
        public void onIncomingMessage(byte[] data) {

        }
    }
}
