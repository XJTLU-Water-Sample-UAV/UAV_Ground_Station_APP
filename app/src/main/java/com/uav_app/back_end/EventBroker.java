package com.uav_app.back_end;

import com.uav_app.back_end.uav_manager.UavStateInterface;
import com.uav_app.back_end.usb_manager.UsbConnectInterface;
import com.uav_app.back_end.usb_manager.UsbConnectManager;

import java.util.List;

import io.serial_port_driver.UsbSerialDriver;

public class EventBroker {
    // 本类单例对象
    private static EventBroker broker;

    public static EventBroker getUavStatePublisher() {
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

    }

    public void publishEvent() {

    }

    public void subscribe(EventObserver observer) {

    }

    public enum Events {

    }

    public interface EventObserver {
        void onEvent();
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
