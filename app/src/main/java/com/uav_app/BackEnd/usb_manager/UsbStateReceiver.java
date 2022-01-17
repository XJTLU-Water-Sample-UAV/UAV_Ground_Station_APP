package com.uav_app.BackEnd.usb_manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;

/**
 * 管理USB插拔的监听器
 */
public class UsbStateReceiver extends BroadcastReceiver {
    // USB连接管理对象
    private final UsbConnectManager connect;

    /**
     * 构造函数，传入必要参数
     *
     * @param connect USB连接管理对象
     */
    public UsbStateReceiver(UsbConnectManager connect) {
        this.connect = connect;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
            // 拔出usb
            connect.disconnect();
        } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
            // 插入usb
            connect.connectDevice();
        }
    }
}
