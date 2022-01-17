package com.uav_app.back_end;

import com.uav_app.back_end.uav_manager.UavState;
import com.uav_app.back_end.uav_manager.UavStateInterface;
import com.uav_app.back_end.uav_manager.UavStateManager;
import com.uav_app.back_end.usb_manager.UsbConnectInterface;
import com.uav_app.back_end.usb_manager.UsbConnectManager;

import java.util.List;

import io.serial_port_driver.UsbSerialDriver;

public class UavStatePublisher {
    // 本类单例对象
    private static UavStatePublisher publisher;

    public static UavStatePublisher getUavStatePublisher() {
        if (publisher == null) {
            synchronized (UsbConnectManager.class) {
                if (publisher == null) {
                    publisher = new UavStatePublisher();
                }
            }
        }
        return publisher;
    }

    private UavStatePublisher() {
        // 观察者对象
        PublisherObserver observer = new PublisherObserver();
        UsbConnectManager.getConnectManager().addObserver(observer);
        UavStateManager.getUavStateManager().addObserver(observer);
    }

    private class PublisherObserver implements UsbConnectInterface, UavStateInterface {
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
    }

}
