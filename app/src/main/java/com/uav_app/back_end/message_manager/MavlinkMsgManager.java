package com.uav_app.back_end.message_manager;

import com.uav_app.back_end.uav_manager.UavStateManager;
import com.uav_app.back_end.usb_manager.UsbConnectManager;

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
        connectManager = UsbConnectManager.getConnectManager();
        // 创建客户端
        udpClient = new MavsdkUdpClient(UavStateManager.BACKEND_IP_ADDRESS, UavStateManager.BACKEND_PORT, 6000);
    }

    /**
     * 开始接收UDP消息
     */
    public void startRecvMessage() {
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
    public void stopRecvMessage() {
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
    public void uartToUdp(byte[] data) {
        if (data.length != 0 && udpClient.isReceiving()) {
            udpClient.sendUdpMessage(data);
        }
    }
}
