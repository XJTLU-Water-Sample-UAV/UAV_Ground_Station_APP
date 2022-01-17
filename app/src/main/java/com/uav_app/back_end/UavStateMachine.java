package com.uav_app.back_end;

import com.uav_app.back_end.usb_manager.UsbConnectManager;

public class UavStateMachine {
    // 本类单例对象
    private static UavStateMachine stateMachine;

    public static UavStateMachine getUavStateMachine() {
        if (stateMachine == null) {
            synchronized (UsbConnectManager.class) {
                if (stateMachine == null) {
                    stateMachine = new UavStateMachine();
                }
            }
        }
        return stateMachine;
    }

    private UavStateMachine() {

    }

    public void switchState(UavStateMachine.SwitchCondition condition) {

    }

    private enum State {
        STATE_USB_UNCONNECTED,
        STATE_UAV_UNCONNECTED,
        STATE_UAV_CONNECT
    }

    public enum SwitchCondition {
        CONDITION_USB_CONNECT,
        CONDITION_UAV_CONNECT,
        CONDITION_ON_CLICK_SELECT,
        CONDITION_ON_CLICK_CONFIRM,
        CONDITION_ON_CLICK_CANCEL,
        CONDITION_UAV_UNLOCK,
        CONDITION_UAV_TAKEOFF,
        CONDITION_USB_LOSE,
        CONDITION_UAV_LOSE
    }
}
