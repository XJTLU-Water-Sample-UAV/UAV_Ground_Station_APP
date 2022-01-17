package com.uav_app.usb_manager;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import com.uav_app.MyApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.serial_port_driver.UsbSerialDriver;
import io.serial_port_driver.UsbSerialPort;
import io.serial_port_driver.UsbSerialProber;

/**
 * 本类用于管理USB数传设备的连接
 */
public class UsbConnectManager {
    // 本类单例对象
    private volatile static UsbConnectManager connectManager;
    // USB连接观察者对象列表
    private final ArrayList<UsbConnectInterface> observerList;
    // 负责管理USB设备的类
    private UsbManager manager;
    // 找到的USB设备
    private UsbDevice mUsbDevice;
    // USB设备连接管理
    private UsbDeviceConnection mDeviceConnection;
    // USB串口设备连接管理
    private UsbSerialDriver driver;
    // 串口通信管理
    private UsbSerialPort mUsbSerialPort;
    // 是否连接成功
    private volatile boolean isConnect = false;
    // 发送信息管理对象
    private final SendingThreadManager sendingThreadManager;
    // 接收信息管理对象
    private final ReceivingThreadManager receivingThreadManager;

    /**
     * 构造函数，该类用于向其他类提供连接串口的入口
     */
    private UsbConnectManager() {
        observerList = new ArrayList<>();
        sendingThreadManager = new SendingThreadManager();
        receivingThreadManager = new ReceivingThreadManager();
    }

    /**
     * 单例模式获取此类对象
     */
    public static UsbConnectManager getConnectManager() {
        if (connectManager == null) {
            synchronized (UsbConnectManager.class) {
                if (connectManager == null) {
                    connectManager = new UsbConnectManager();
                }
            }
        }
        return connectManager;
    }

    /**
     * 添加USB事件的监听器
     *
     * @param mUsbConnectInterface 监听器对象
     */
    public void addObserver(UsbConnectInterface mUsbConnectInterface) {
        observerList.add(mUsbConnectInterface);
    }

    /**
     * 此方法用于连接数传电台设备串口。
     * 当未找到设备时，回调onCanNotFoundDevice()方法。
     * 当找到多个可用设备时，回调onFindMoreThanOneDevice()方法。
     */
    public void connectDevice() {
        if (isConnect) {
            return;
        }
        // 获取负责管理USB设备的类
        manager = (UsbManager) MyApplication.getApplication().getContext()
                .getSystemService(Context.USB_SERVICE);
        // 获取可用的USB串口设备列表
        UsbSerialProber serialProber = UsbSerialProber.getDefaultProber();
        List<UsbSerialDriver> driverList = serialProber.findAllDrivers(manager);
        if (driverList.size() == 0) {
            // 没有找到任何USB设备
            for (int i = 0; i < observerList.size(); i++) {
                UsbConnectInterface connectInterface = observerList.get(i);
                connectInterface.onCanNotFoundDevice();
            }
        } else if (driverList.size() == 1) {
            // 找到USB设备
            this.driver = driverList.get(0);
            this.mUsbDevice = driver.getDevice();
            checkUsbPermission();
        } else {
            // 找到多个可用设备
            for (int i = 0; i < observerList.size(); i++) {
                UsbConnectInterface connectInterface = observerList.get(i);
                connectInterface.onFindMultipleDevices(driverList);
            }
        }
    }

    /**
     * 此方法用于连接指定PID和VID的数传电台设备串口。
     * 当未找到任何设备时，回调onCanNotFoundDevice()方法。
     * 当未找到指定设备时，回调onCanNotFoundSpecifiedDevice()方法。
     *
     * @param vendorID  设备的VID
     * @param productID 设备的PID
     */
    public void connectSpecifiedDevice(int vendorID, int productID) {
        if (isConnect) {
            return;
        }
        // 获取负责管理USB设备的类
        manager = (UsbManager) MyApplication.getApplication().getContext()
                .getSystemService(Context.USB_SERVICE);
        // 获取可用的USB串口设备列表
        UsbSerialProber serialProber = UsbSerialProber.getDefaultProber();
        List<UsbSerialDriver> driverList = serialProber.findAllDrivers(manager);
        if (driverList.size() == 0) {
            // 没有找到任何USB设备
            for (int i = 0; i < observerList.size(); i++) {
                UsbConnectInterface connectInterface = observerList.get(i);
                connectInterface.onCanNotFoundDevice();
            }
        } else {
            // 找到可用USB设备
            for (int i = 0; i < driverList.size(); i++) {
                UsbSerialDriver driver = driverList.get(i);
                UsbDevice mUsbDevice = driver.getDevice();
                if (mUsbDevice.getVendorId() == vendorID && mUsbDevice.getProductId() == productID) {
                    // 找到指定的USB设备
                    this.driver = driver;
                    this.mUsbDevice = mUsbDevice;
                    checkUsbPermission();
                    return;
                }
            }
            // 没有找到指定设备
            for (int i = 0; i < observerList.size(); i++) {
                UsbConnectInterface connectInterface = observerList.get(i);
                connectInterface.onCanNotFoundSpecifiedDevice();
            }
        }
    }

    /**
     * 此方法用于检查设备访问权限
     */
    private void checkUsbPermission() {
        if (manager.hasPermission(mUsbDevice)) {
            // 有权限，连接设备
            connect();
        } else {
            // 没有权限，获取设备访问权限
            getUsbPermission(mUsbDevice);
        }
    }

    /**
     * 此方法用于向用户申请USB口使用权限
     *
     * @param mUSBDevice 储存USB设备信息的对象
     */
    private void getUsbPermission(UsbDevice mUSBDevice) {
        // 初始化USB权限更改时接收的广播
        UsbPermissionReceiver mUsbPermissionReceiver = new UsbPermissionReceiver(mUsbDevice,
                observerList);
        // 注册广播接收器
        String ACTION_USB_PERMISSION = "com.android.usb.USB_PERMISSION";
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        // 向接收器添加USB设备连接和断连广播
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        MyApplication.getApplication().getContext().registerReceiver(mUsbPermissionReceiver, filter);
        // 获取设备访问权限
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getBroadcast(MyApplication.getApplication()
                        .getContext(),
                0, new Intent(ACTION_USB_PERMISSION), 0);
        manager.requestPermission(mUSBDevice, pendingIntent);
    }

    /**
     * 此方法用于连接串口设备
     */
    private void connect() {
        // 打开设备，获取UsbDeviceConnection对象，连接设备，用于后面的通讯
        mDeviceConnection = manager.openDevice(mUsbDevice);
        // 打开串口驱动
        List<UsbSerialPort> mUsbSerialPorts = driver.getPorts();
        if (mUsbSerialPorts.size() == 1) {
            mUsbSerialPort = mUsbSerialPorts.get(0);
            try {
                // 连接成功
                mUsbSerialPort.open(mDeviceConnection);
                UartConstants constants = UartConstants.getUartConstants();
                mUsbSerialPort.setParameters(constants.getBaudRate(), constants.getDataBits(),
                        constants.getStopBits(), constants.getParity());
                isConnect = true;
                // 执行连接成功回调
                for (int i = 0; i < observerList.size(); i++) {
                    UsbConnectInterface connectInterface = observerList.get(i);
                    connectInterface.onConnectSuccess();
                }
                // 开始接收消息
                startReceiveMessage();
            } catch (IOException ioe) {
                // 连接失败
                for (int i = 0; i < observerList.size(); i++) {
                    UsbConnectInterface connectInterface = observerList.get(i);
                    connectInterface.onConnectFail(ioe);
                }
                disconnect();
            }
        }
    }

    /**
     * 此方法用于断开与串口设备的连接
     */
    public void disconnect() {
        if (isConnect) {
            // 停止接收消息
            stopReceiveMessage();
            // 关闭串口
            try {
                mUsbSerialPort.close();
            } catch (Exception ignored) {
                // 丢弃抛出的异常，因为无论关闭窗口成功或失败，程序都将继续执行关闭USB接口函数
            }
            // 断开连接
            mDeviceConnection.close();
            // 回调断连接口函数
            for (int i = 0; i < observerList.size(); i++) {
                UsbConnectInterface connectInterface = observerList.get(i);
                connectInterface.onLoseConnectDevice();
            }
        }
        isConnect = false;
    }

    /**
     * 此方法用于向数传接收端发送消息
     *
     * @param data 传入需要发送的字节
     */
    public void sendSerialMessage(byte[] data) {
        if (isConnect) {
            sendingThreadManager.addMessage(data, mUsbSerialPort);
        }
    }

    /**
     * 此方法用于开启接收消息的线程
     */
    public void startReceiveMessage() {
        if (isConnect) {
            receivingThreadManager.startReceiveMessage(mUsbSerialPort);
        }
    }

    /**
     * 此方法用于停止接收消息的线程
     */
    public void stopReceiveMessage() {
        receivingThreadManager.stopReceiveMessage();
    }

    /**
     * 此方法用于判断设备是否连接
     *
     * @return 设备是否连接
     */
    public boolean isConnect() {
        return isConnect;
    }

    /**
     * 此方法用于判断设备是否正在接收消息
     *
     * @return 是否正在接收消息
     */
    public boolean isReceiving() {
        return receivingThreadManager.isReceiving();
    }

    /**
     * 此方法用于获取当前连接的USB设备属性
     *
     * @return USB设备
     */
    public UsbDevice getUsbDevice() throws Exception {
        // 如果未连接USB设备，抛出异常
        if (!isConnect) {
            throw new Exception("USB device is not connected!");
        }
        return mUsbDevice;
    }

    /**
     * 管理发送线程的类
     */
    private class SendingThreadManager {
        private final List<byte[]> list = new ArrayList<>();
        private Thread sendThread;

        /**
         * 将需要发送的消息添加至队列。若发送线程空闲，则立即将消息发出，否则需等待队列消息发送完毕。
         *
         * @param message        需要发送的消息
         * @param mUsbSerialPort 串口管理对象
         */
        public void addMessage(byte[] message, UsbSerialPort mUsbSerialPort) {
            list.add(message);
            if (sendThread == null || !sendThread.isAlive()) {
                sendThread = new Thread(() -> {
                    while (list.size() > 0) {
                        // 发送数据
                        try {
                            mUsbSerialPort.purgeHwBuffers(true, false);
                            mUsbSerialPort.write(list.get(0), 2000);
                        } catch (IOException e) {
                            for (int i = 0; i < observerList.size(); i++) {
                                UsbConnectInterface messageInterface = observerList.get(i);
                                messageInterface.onSendMessageError(e);
                            }
                        }
                        list.remove(0);
                        // 休眠0.3秒
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                sendThread.start();
            }
        }
    }

    /**
     * 管理接收线程的类
     */
    private class ReceivingThreadManager {
        private volatile boolean isParsing = false;
        private Thread receiveThread = null;

        /**
         * 开启接收消息的线程
         *
         * @param mUsbSerialPort USB串口管理对象
         */
        public void startReceiveMessage(UsbSerialPort mUsbSerialPort) {
            if (receiveThread == null) {
                receiveThread = new Thread(() -> {
                    // 初始化缓冲区
                    byte[] data = new byte[16 * 1024];
                    while (!receiveThread.isInterrupted()) {
                        // 接收结果长度
                        int resultLen;
                        // 接收消息
                        try {
                            isParsing = true;
                            resultLen = mUsbSerialPort.read(data, 0);
                        } catch (Exception e) {
                            isParsing = false;
                            // 回调接收错误线程
                            if (UsbConnectManager.getConnectManager().isConnect()) {
                                for (int i = 0; i < observerList.size(); i++) {
                                    UsbConnectInterface messageInterface = observerList.get(i);
                                    messageInterface.onReceiveMessageError(e);
                                }
                            }
                            return;
                        }
                        isParsing = false;
                        // 拷贝有效数据
                        byte[] result = new byte[resultLen];
                        System.arraycopy(data, 0, result, 0, resultLen);
                        // 解析消息
                        if (UsbConnectManager.getConnectManager().isConnect()) {
                            for (int i = 0; i < observerList.size(); i++) {
                                UsbConnectInterface messageInterface = observerList.get(i);
                                messageInterface.onIncomingMessage(result);
                            }
                        }
                        Arrays.fill(data, (byte) 0);
                    }
                });
                receiveThread.start();
                for (int i = 0; i < observerList.size(); i++) {
                    UsbConnectInterface messageInterface = observerList.get(i);
                    messageInterface.onStartReceiveMessage();
                }
            }
        }

        /**
         * 停止接收消息
         */
        public void stopReceiveMessage() {
            if (receiveThread != null && receiveThread.isAlive()) {
                // 发送打断指令
                if (isParsing) {
                    receiveThread.stop();
                } else {
                    receiveThread.interrupt();
                }
            }
            receiveThread = null;
            for (int i = 0; i < observerList.size(); i++) {
                UsbConnectInterface messageInterface = observerList.get(i);
                messageInterface.onStopReceiveMessage();
            }
        }

        /**
         * 判断是否正在接收消息
         *
         * @return 是否正在接收消息
         */
        public boolean isReceiving() {
            return receiveThread != null && receiveThread.isAlive();
        }
    }
}