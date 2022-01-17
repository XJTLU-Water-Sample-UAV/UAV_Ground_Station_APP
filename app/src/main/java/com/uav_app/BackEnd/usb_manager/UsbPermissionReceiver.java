package com.uav_app.BackEnd.usb_manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import java.util.ArrayList;

/**
 * 管理USB权限的监听器
 */
public class UsbPermissionReceiver extends BroadcastReceiver {
    // USB设备管理函数
    private final UsbDevice mUsbDevice;
    // 观察者对象列表
    private final ArrayList<UsbConnectInterface> observerList;

    /**
     * 构造函数，传入必要参数
     *
     * @param mUsbDevice   USB设备对象
     * @param observerList 观察者对象列表
     */
    public UsbPermissionReceiver(UsbDevice mUsbDevice, ArrayList<UsbConnectInterface> observerList) {
        this.mUsbDevice = mUsbDevice;
        this.observerList = observerList;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // 获取USB管理对象
        UsbConnectManager connectManager = UsbConnectManager.getConnectManager();
        String action = intent.getAction();
        // USB权限常数
        String ACTION_USB_PERMISSION = "com.android.usb.USB_PERMISSION";
        if (ACTION_USB_PERMISSION.equals(action)) {
            synchronized (this) {
                // 取消注册广播接收器
                context.unregisterReceiver(this);
                // 获得被授权的设备
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
                        && mUsbDevice.equals(device)) {
                    // 授权成功，连接USB设备
                    connectManager.connectSpecifiedDevice(device.getVendorId(), device.getProductId());
                } else {
                    // 用户点击拒绝，授权失败
                    for (int i = 0; i < observerList.size(); i++) {
                        UsbConnectInterface connectInterface = observerList.get(i);
                        connectInterface.onPermissionNotObtained();
                    }
                    connectManager.disconnect();
                }
            }
        }
    }
}
