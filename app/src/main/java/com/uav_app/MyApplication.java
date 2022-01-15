package com.uav_app;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;

import com.uav_app.message_manager.MavlinkMsgManager;
import com.uav_app.uav_manager.UavStateManager;
import com.uav_app.usb_manager.UsbConnectManager;
import com.uav_app.usb_manager.UsbStateReceiver;
import com.uav_app.user_interface.OperationStateMachine;
import com.uav_app.user_interface.UIObserver;
import com.uav_app.user_interface.map_activity.MapActivityState;

public class MyApplication extends Application {
    // 全局APP对象
    @SuppressLint("StaticFieldLeak")
    private static MyApplication mApp;
    // 全局Context对象
    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化context对象
        context = getApplicationContext();
        // 构建UI控件监听器对象
        UIObserver observer = UIObserver.getUIObserver();
        // 初始化Mavlink和USB管理对象
        UsbConnectManager connectManager = UsbConnectManager.getConnectManager();
        MavlinkMsgManager.getMessageManager();
        OperationStateMachine.getOperationStateMachine();
        MapActivityState.getMapActivityState();
        // 初始USB化广播
        UsbStateReceiver mUsbStateReceiver = new UsbStateReceiver(connectManager);
        IntentFilter usbDeviceStateFilter = new IntentFilter();
        usbDeviceStateFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        usbDeviceStateFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        context.registerReceiver(mUsbStateReceiver, usbDeviceStateFilter);
        // 初始化无人机管理对象
        UavStateManager uavStateManager = UavStateManager.getUavStateManager();
        // 添加UI控件监听器
        connectManager.addObserver(observer);
        uavStateManager.addObserver(observer);
    }

    /**
     * 获取全局MyApplication对象
     *
     * @return 全局MyApplication对象
     */
    @SuppressLint("PrivateApi")
    public static MyApplication getApplication() {
        if (mApp == null) {
            try {
                mApp = (MyApplication) Class.forName("android.app.ActivityThread")
                        .getMethod("currentApplication").invoke(null, (Object[]) null);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
        return mApp;
    }

    /**
     * 调用此方法获取消息管理对象。
     *
     * @return 全局Context
     */
    public Context getContext() {
        return context;
    }
}
