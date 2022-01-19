package com.uav_app.back_end.message_manager;

import com.uav_app.back_end.uav_manager.UavStateManager;

/**
 * 本类用于管理应用程序与无人机的通信。
 */
public class MavlinkMsgManager {
    // 本类单例对象
    private volatile static MavlinkMsgManager messageManager;
    // 观察者对象列表
    private MavlinkMsgInterface receiver;
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
        // 创建客户端
        udpClient = new MavsdkUdpClient(UavStateManager.BACKEND_IP_ADDRESS, UavStateManager.BACKEND_PORT, 6000);
    }

    /**
     * 添加MAVLink事件的监听器
     *
     * @param receiver 监听器对象
     */
    public void setReceiver(MavlinkMsgInterface receiver) {
        this.receiver = receiver;
    }

    /**
     * 开始接收UDP消息
     */
    public void startRecvMessage(UdpListener observer) {
        // UDP客户端开始接收消息
        udpClient.startRecvUdpMessage(new MavsdkUdpClient.MsgRecvListener() {
            @Override
            public void onRecvMessage(byte[] data) {
                if (data.length != 0) {
                    observer.onIncomingMessage(data);
                }
            }

            @Override
            public void onRecvError(Exception e) {
                receiver.onRecvUdpError(e);
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
     * 串口消息转发至UDP
     *
     * @param data 传入消息
     */
    public void sendUdpMessage(byte[] data) {
        if (data.length != 0 && udpClient.isReceiving()) {
            udpClient.sendUdpMessage(data, e -> receiver.onSendUdpError(e));
        }
    }

    public boolean isReceiving() {
        return udpClient.isReceiving();
    }

    public interface UdpListener {
        /**
         * 传入收到消息
         */
        void onIncomingMessage(byte[] data);
    }
}
