package com.uav_app.back_end.usb_manager;

import com.uav_app.MyApplication;
import com.uav_app.tools.AccessParameter;

import io.serial_port_driver.UsbSerialPort;

/**
 * 本类储存串口设备的一些基本参数。
 */
public class UartConstants {
    private static final UartConstants CONSTANTS = new UartConstants();
    // 存取参数的对象
    AccessParameter parameter = new AccessParameter(MyApplication.getApplication().getContext(),
            "DeviceData");
    // 串口芯片基础参数
    private int vendorID;
    private int productID;
    private int baudRate;
    private int dataBits;
    private int stopBits;
    private int parity;

    /**
     * 构造函数，从参数文件中加载数据。如数据为0，设为默认值。
     */
    private UartConstants() {
        // 从文件中加载参数
        vendorID = parameter.readParameters("VendorID");
        productID = parameter.readParameters("ProductID");
        baudRate = parameter.readParameters("BaudRate");
        dataBits = parameter.readParameters("DataBits");
        stopBits = parameter.readParameters("StopBits");
        parity = parameter.readParameters("parity");
        // 若参数为0，设为默认值
        if (vendorID == 0xFFFFFF) {
            vendorID = 1027;
        }
        if (productID == 0xFFFFFF) {
            productID = 24577;
        }
        if (baudRate == 0xFFFFFF) {
            baudRate = 57600;
        }
        if (dataBits == 0xFFFFFF) {
            dataBits = UsbSerialPort.DATABITS_8;
        }
        if (stopBits == 0xFFFFFF) {
            stopBits = UsbSerialPort.STOPBITS_1;
        }
        if (parity == 0xFFFFFF) {
            parity = UsbSerialPort.PARITY_NONE;
        }
    }

    /**
     * 单例化此类对象
     */
    public static UartConstants getUartConstants() {
        return CONSTANTS;
    }

    /**
     * 调用此方法获取设备VID
     *
     * @return 设备的VID
     */
    public int getVendorID() {
        return vendorID;
    }

    /**
     * 调用此方法获取设备PID
     *
     * @return 设备的PID
     */
    public int getProductID() {
        return productID;
    }

    /**
     * 调用此方法获取串口波特率
     *
     * @return 设备的波特率
     */
    public int getBaudRate() {
        return baudRate;
    }

    /**
     * 调用此方法获取串口数据位
     *
     * @return 设备的波特率
     */
    public int getDataBits() {
        return dataBits;
    }

    /**
     * 调用此方法获取串口终止位
     *
     * @return 设备的波特率
     */
    public int getStopBits() {
        return stopBits;
    }

    /**
     * 调用此方法获取串口校验方式
     *
     * @return 设备的波特率
     */
    public int getParity() {
        return parity;
    }

    /**
     * 调用此方法更改设备VID
     *
     * @param vendorID 设备的VID
     */
    public void setVendorID(int vendorID) {
        this.vendorID = vendorID;
        parameter.storageParameters("VendorID", vendorID);
    }

    /**
     * 调用此方法更改设备PID
     *
     * @param productID 设备的PID
     */
    public void setProductID(int productID) {
        this.productID = productID;
        parameter.storageParameters("ProductID", productID);
    }

    /**
     * 调用此方法更改设备波特率
     *
     * @param baudRate 设备的波特率
     */
    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
        parameter.storageParameters("BaudRate", baudRate);
    }

    /**
     * 调用此方法更改设备波特率
     *
     * @param dataBits 串口的数据位
     */
    public void setDataBits(int dataBits) {
        this.dataBits = dataBits;
        parameter.storageParameters("DataBits", dataBits);
    }

    /**
     * 调用此方法更改设备波特率
     *
     * @param stopBits 串口的终止位
     */
    public void setStopBits(int stopBits) {
        this.stopBits = stopBits;
        parameter.storageParameters("StopBits", stopBits);
    }

    /**
     * 调用此方法更改设备波特率
     *
     * @param parity 串口的校验方式
     */
    public void setParity(int parity) {
        this.parity = parity;
        parameter.storageParameters("Parity", parity);
    }
}
