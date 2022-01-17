package com.uav_app.back_end.message_manager;

import com.uav_app.back_end.uav_manager.UavStateManager;
import com.uav_app.back_end.usb_manager.UsbConnectInterface;
import com.uav_app.back_end.usb_manager.UsbConnectManager;

import java.util.List;

import io.serial_port_driver.UsbSerialDriver;

/**
 * 本类用于管理应用程序与无人机的通信。
 */
public class MavlinkMsgManager {
    // 本类单例对象
    private volatile static MavlinkMsgManager messageManager;
    // USB连接管理
    private final UsbConnectManager connectManager;
    // MAVLink客户端
    private final MavsdkUdpClient udpClient;

    /**
     * 单例模式获取此类对象
     */
    public static MavlinkMsgManager getMessageManager() {
        if (messageManager == null) {
            synchronized (MavlinkMsgManager.class) {
                if (messageManager == null) {
                    messageManager = new MavlinkMsgManager();
                }
            }
        }
        return messageManager;
    }

    /**
     * 构造函数，初始化必要变量
     */
    private MavlinkMsgManager() {
        // 添加监听数传设备消息的观察者
        MavlinkObserver observer = new MavlinkObserver();
        connectManager = UsbConnectManager.getConnectManager();
        connectManager.addObserver(observer);
        // 创建客户端
        udpClient = new MavsdkUdpClient(UavStateManager.BACKEND_IP_ADDRESS, UavStateManager.BACKEND_PORT, 6000);
    }

    /**
     * 开始接收UDP消息
     */
    private void startRecvMessage() {
        // UDP客户端开始接收消息
        udpClient.startRecvUdpMessage(new MavsdkUdpClient.OnMsgReturnedListener() {
            @Override
            public void onRecvMessage(byte[] msg) {
                udpToUart(msg);
            }

            @Override
            public void onRecvError(Exception e) {

            }
        });
    }

    /**
     * 结束接收UDP消息
     */
    private void stopRecvMessage() {
        // UDP客户端停止接收消息
        udpClient.stopRecvUdpMessage();
    }

    /**
     * UDP消息转发至串口
     *
     * @param data 传入消息
     */
    private void udpToUart(byte[] data) {
        if (data.length != 0 && connectManager.isReceiving()) {
            connectManager.sendSerialMessage(data);
        }
    }

    /**
     * 串口消息转发至UDP
     *
     * @param data 传入消息
     */
    private void uartToUdp(byte[] data) {
        if (data.length != 0 && udpClient.isReceiving()) {
            udpClient.sendUdpMessage(data);
        }
    }

    private class MavlinkObserver implements UsbConnectInterface {
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
            startRecvMessage();
        }

        @Override
        public void onStopReceiveMessage() {
            stopRecvMessage();
        }

        @Override
        public void onReceiveMessageError(Exception e) {
        }

        @Override
        public void onIncomingMessage(byte[] data) {
            uartToUdp(data);
        }
    }
}
