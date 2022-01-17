package com.uav_app.back_end.usb_manager;

import java.util.List;

import io.serial_port_driver.UsbSerialDriver;

/**
 * 管理USB设备状态的前端回调接口
 */
public interface UsbConnectInterface {
    /**
     * 未找到USB数传设备（用户未插入设备或插入的不是数传电台）
     */
    void onCanNotFoundDevice();

    /**
     * 未找到指定的USB数传设备（用户插入的数传电台PID和VID不满足程序要求）
     */
    void onCanNotFoundSpecifiedDevice();

    /**
     * 找到多个符合条件的USB设备
     *
     * @param driverList 找到的可用设备列表
     */
    void onFindMultipleDevices(List<UsbSerialDriver> driverList);

    /**
     * 无法获取USB设备访问权限
     */
    void onPermissionNotObtained();

    /**
     * USB设备连接成功
     */
    void onConnectSuccess();

    /**
     * USB设备连接失败
     *
     * @param e 失败原因
     */
    void onConnectFail(Exception e);

    /**
     * USB设备失去连接
     */
    void onLoseConnectDevice();

    /**
     * 发送消息出现错误。
     */
    void onSendMessageError(Exception e);

    /**
     * 开始接收消息。
     */
    void onStartReceiveMessage();

    /**
     * 停止接收消息。
     */
    void onStopReceiveMessage();

    /**
     * 接收消息出现错误，此方法在子线程中执行。
     */
    void onReceiveMessageError(Exception e);

    /**
     * 传入收到消息
     */
    void onIncomingMessage(byte[] data);
}
