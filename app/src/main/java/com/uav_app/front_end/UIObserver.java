package com.uav_app.front_end;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import com.uav_app.MyApplication;
import com.uav_app.back_end.EventBroker;
import com.uav_app.back_end.uav_manager.UavStateManager;

public class UIObserver implements EventBroker.EventObserver {
    // 本类单例对象
    @SuppressLint("StaticFieldLeak")
    private volatile static UIObserver observer;
    // 全局context
    private final Context context;
    // 状态机
    private final OperationStateMachine stateMachine;

    private UIObserver() {
        this.context = MyApplication.getContext();
        this.stateMachine = OperationStateMachine.getOperationStateMachine();
    }

    public static UIObserver getUIObserver() {
        if (observer == null) {
            synchronized (UavStateManager.class) {
                if (observer == null) {
                    observer = new UIObserver();
                }
            }
        }
        return observer;
    }

    @Override
    public void onEvent(EventBroker.Event event) {
        switch (event) {
            case USB_CANNOT_FOUND:
                Toast.makeText(context, "找不到可用数传", Toast.LENGTH_SHORT).show();
                break;

            case USB_CANNOT_FOUND_SPECIFIED:
                Toast.makeText(context, "找不到指定数传", Toast.LENGTH_SHORT).show();
                break;

            case USB_FIND_MULTIPLE:
                break;

            case USB_NO_PERMISSION:
                Toast.makeText(context, "无法获取USB设备的访问权限", Toast.LENGTH_SHORT).show();
                break;

            case USB_CONNECT_SUCCESS:
                Toast.makeText(context, "数传连接成功", Toast.LENGTH_SHORT).show();
                stateMachine.nextState(OperationStateMachine.SwitchCondition.CONDITION_USB_CONNECT);
                break;

            case USB_CONNECT_FAIL:
                Toast.makeText(context, "数传连接失败，请检查是否被其他应用占用", Toast.LENGTH_SHORT).show();
                break;

            case USB_LOSE:
                Toast.makeText(context, "数传断开连接", Toast.LENGTH_SHORT).show();
                stateMachine.nextState(OperationStateMachine.SwitchCondition.CONDITION_USB_LOSE);
                break;

            case USB_IO_ERROR:
                Looper.prepare();
                Toast.makeText(context, "数据收发异常，请检查数传连接", Toast.LENGTH_SHORT).show();
                Looper.loop();
                break;

            case UAV_CONNECT:
                stateMachine.nextState(OperationStateMachine.SwitchCondition.CONDITION_UAV_CONNECT);
                break;

            case UAV_DISCONNECT:
                stateMachine.nextState(OperationStateMachine.SwitchCondition.CONDITION_UAV_DISCONNECT);
                break;

            case UAV_ARMED:
                stateMachine.nextState(OperationStateMachine.SwitchCondition.CONDITION_UAV_ARMED);
                break;

            case UAV_DISARMED:
                stateMachine.nextState(OperationStateMachine.SwitchCondition.CONDITION_UAV_DISARMED);
                break;
        }
    }
}
