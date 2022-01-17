package com.uav_app.front_end;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import com.uav_app.MyApplication;
import com.uav_app.back_end.uav_manager.UavStateInterface;
import com.uav_app.back_end.uav_manager.UavStateManager;
import com.uav_app.back_end.usb_manager.UsbConnectInterface;

import java.util.List;

import io.serial_port_driver.UsbSerialDriver;

public class UiObserver implements UsbConnectInterface, UavStateInterface {
    // 本类单例对象
    @SuppressLint("StaticFieldLeak")
    private volatile static UiObserver observer;
    // 全局context
    private final Context context;
    // 状态机
    private final UiStateMachine stateMachine;

    private UiObserver() {
        this.context = MyApplication.getApplication().getContext();
        this.stateMachine = UiStateMachine.getOperationStateMachine();
    }

    public static UiObserver getUIObserver() {
        if (observer == null) {
            synchronized (UavStateManager.class) {
                if (observer == null) {
                    observer = new UiObserver();
                }
            }
        }
        return observer;
    }

    @Override
    public void onCanNotFoundDevice() {
        Toast.makeText(context, "找不到可用数传", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCanNotFoundSpecifiedDevice() {
        Toast.makeText(context, "找不到指定数传", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFindMultipleDevices(List<UsbSerialDriver> driverList) {

    }

    @Override
    public void onPermissionNotObtained() {
        Toast.makeText(context, "无法获取USB设备的访问权限", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectSuccess() {
        Toast.makeText(context, "数传连接成功", Toast.LENGTH_SHORT).show();
        stateMachine.switchState(UiStateMachine.SwitchCondition.CONDITION_USB_CONNECT);
    }

    @Override
    public void onConnectFail(Exception e) {
        Toast.makeText(context, "数传连接失败，请检查是否被其他应用占用", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoseConnectDevice() {
        Toast.makeText(context, "数传断开连接", Toast.LENGTH_SHORT).show();
        stateMachine.switchState(UiStateMachine.SwitchCondition.CONDITION_USB_LOSE);
    }

    @Override
    public void onSendMessageError(Exception e) {
        Looper.prepare();
        Toast.makeText(context, "飞控指令发送错误，请检查数传连接", Toast.LENGTH_SHORT).show();
        Looper.loop();
    }

    @Override
    public void onStartReceiveMessage() {

    }

    @Override
    public void onStopReceiveMessage() {

    }

    @Override
    public void onReceiveMessageError(Exception e) {
        Looper.prepare();
        Toast.makeText(context, "飞控指令接收错误，请检查数传连接", Toast.LENGTH_SHORT).show();
        Looper.loop();
    }

    @Override
    public void onIncomingMessage(byte[] data) {
    }

    @Override
    public void onUavConnect() {
        Looper.prepare();
        Toast.makeText(context, "无人机连接成功", Toast.LENGTH_SHORT).show();
        Looper.loop();
    }

    @Override
    public void onUavSignalBad() {
        Toast.makeText(context, "无人机信号差", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNormalDisconnect() {
        Toast.makeText(context, "无人机断开连接成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAbnormalDisconnect() {
        //Toast.makeText(context, "无人机失去连接", Toast.LENGTH_SHORT).show();
    }
}
