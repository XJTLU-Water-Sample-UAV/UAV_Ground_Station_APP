package com.uav_app.message_manager;

import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.mavlink.MAVLinkPacket;
import com.mavlink.Parser;
import com.mavlink.common.msg_heartbeat;
import com.mavlink.common.msg_sys_status;
import com.mavlink.messages.MAVLinkMessage;
import com.uav_app.MyApplication;
import com.uav_app.usb_manager.UsbConnectInterface;
import com.uav_app.usb_manager.UsbConnectManager;
import com.uav_app.usb_manager.serial_port_driver.UsbSerialDriver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 本类用于管理应用程序与无人机的通信。
 */
public class MavlinkMsgManager {
    // 本类单例对象
    private volatile static MavlinkMsgManager messageManager;
    // USB连接管理器
    private final UsbConnectManager connectManager;
    // 观察者对象列表
    private final ArrayList<MavlinkMsgInterface> observerList;
    // 用于解包的类
    private final Parser parser;
    // 解析列表
    private final List<Integer> parseList;
    // 暂存列表
    private final List<Integer> storeList;
    // 是否正在解析
    private volatile boolean ifParsing = false;

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
        this.observerList = new ArrayList<>();
        // activity上下文
        this.connectManager = UsbConnectManager.getConnectManager();
        this.parser = new Parser();
        this.parseList = new ArrayList<>();
        this.storeList = new ArrayList<>();
        // 添加监听数传设备消息的观察者
        MavlinkObserver observer = new MavlinkObserver();
        UsbConnectManager.getConnectManager().addObserver(observer);
    }

    /**
     * 添加消息事件的监听器
     *
     * @param mMessageInterface 监听器对象
     */
    public void addObserver(MavlinkMsgInterface mMessageInterface) {
        observerList.add(mMessageInterface);
    }

    /**
     * 打包发送MAVLink消息
     *
     * @param message 消息内容
     */
    public void sendMavlinkMessage(MAVLinkMessage message) {
        MAVLinkPacket packet = message.pack();
        byte[] msg = packet.encodePacket();
        Toast.makeText(MyApplication.getApplication().getContext(), Arrays.toString(msg), Toast.LENGTH_SHORT).show();
        connectManager.sendMessage(msg);
    }

    /**
     * 解析Mavlink消息
     *
     * @param data      传入数据
     * @param resultLen 消息长度
     */
    private void parse(byte[] data, int resultLen) {
        // 将byte转换为int
        for (int i = 0; i < resultLen; i++) {
            if (data[i] < 0) {
                parseList.add((int) data[i] + 256);
            } else {
                parseList.add((int) data[i]);
            }
        }
        ifParsing = true;
        // 逐字节解析
        while (parseList.size() > 0) {
            int c = parseList.get(0);
            parseList.remove(0);
            // 逐字节填入接收到的内容
            MAVLinkPacket mavLinkPacket = parser.mavlink_parse_char(c);
            if (mavLinkPacket != null) {
                // 收到完整的数据包
                Thread processThread = new Thread(() -> processMessage(mavLinkPacket.unpack()));
                processThread.start();
                // 清空暂存列表
                storeList.clear();
            } else if (parser.state == Parser.MAV_states.MAVLINK_PARSE_STATE_IDLE) {
                // 未进入数据包解析
                if (storeList.size() > 1) {
                    storeList.add(0, c);
                    for (int i = 0; i < storeList.size() - 1; i++) {
                        parseList.add(0, storeList.get(i));
                    }
                }
                storeList.clear();
            } else {
                // 正在解析数据包
                storeList.add(0, c);
            }
        }
        ifParsing = false;
    }

    /**
     * 处理MAVLink消息函数
     *
     * @param mavLinkMessage 传入消息类型
     */
    private void processMessage(MAVLinkMessage mavLinkMessage) {
        switch (mavLinkMessage.msgid) {
            case msg_sys_status.MAVLINK_MSG_ID_SYS_STATUS:
                msg_sys_status sys_status = (msg_sys_status) mavLinkMessage;
                // 状态包
                for (int i = 0; i < observerList.size(); i++) {
                    MavlinkMsgInterface msgInterface = observerList.get(i);
                    msgInterface.onRecvUavStatus(sys_status);
                }
                break;

            case msg_heartbeat.MAVLINK_MSG_ID_HEARTBEAT:
                msg_heartbeat heartbeat = (msg_heartbeat) mavLinkMessage;
                // 心跳包
                for (int i = 0; i < observerList.size(); i++) {
                    MavlinkMsgInterface msgInterface = observerList.get(i);
                    msgInterface.onRecvHeartbeat(heartbeat);
                }
                break;
        }
    }

    /**
     * 停止解析消息
     */
    private void closeParse() {
        Thread thread = new Thread(() -> {
            while (ifParsing) {
                // 等待直到最后一组数据解析完成
            }
            parseList.clear();
            storeList.clear();
        });
        thread.start();
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
        }

        @Override
        public void onStopReceiveMessage() {
            closeParse();
        }

        @Override
        public void onReceiveMessageError(Exception e) {
        }

        @Override
        public void onIncomingMsg(byte[] data, int resultLen) {
            parse(data, resultLen);
        }
    }
}
