package com.uav_app;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.uav_app.back_end.EventBroker;
import com.uav_app.back_end.message_manager.MavlinkMsgManager;
import com.uav_app.back_end.uav_manager.UavStateManager;
import com.uav_app.back_end.usb_manager.UsbConnectManager;
import com.uav_app.front_end.UIObserver;

public class MyApplication extends Application {
    // 全局APP对象
    @SuppressLint("StaticFieldLeak")
    private static MyApplication mApp;
    // 全局Context对象
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化context对象
        context = getApplicationContext();
        // 初始化USB管理对象
        UsbConnectManager.getConnectManager();
        // 初始化Mavlink对象
        MavlinkMsgManager.getMessageManager();
        // 初始化无人机管理对象
        UavStateManager.getUavStateManager();
        // 获取事件代理对象
        EventBroker.getBroker().subscribe(UIObserver.getUIObserver());
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
    public static Context getContext() {
        return context;
    }
}
