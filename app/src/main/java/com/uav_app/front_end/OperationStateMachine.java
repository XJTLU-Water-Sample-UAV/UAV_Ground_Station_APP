package com.uav_app.front_end;

import com.uav_app.back_end.usb_manager.UsbConnectManager;
import com.uav_app.front_end.map_activity.MapActivityState;

/*
 * 用于管理UI跳转的FSM
 **/
public class OperationStateMachine {
    // 本类单例对象
    private static OperationStateMachine stateMachine;
    // 状态指针
    private State state;
    // 状态参数
    private final MapActivityState mapActivityState;

    public static OperationStateMachine getOperationStateMachine() {
        if (stateMachine == null) {
            synchronized (UsbConnectManager.class) {
                if (stateMachine == null) {
                    stateMachine = new OperationStateMachine();
                }
            }
        }
        return stateMachine;
    }

    private OperationStateMachine() {
        this.state = State.STATE_USB_UNCONNECTED;
        this.mapActivityState = MapActivityState.getMapActivityState();
    }

    public void nextState(SwitchCondition condition) {
        // 切换状态
        switch (state) {
            case STATE_USB_UNCONNECTED:
                if (condition == SwitchCondition.CONDITION_USB_CONNECT) {
                    state = State.STATE_UAV_UNCONNECTED;
                }
                break;

            case STATE_UAV_UNCONNECTED:
                if (condition == SwitchCondition.CONDITION_USB_LOSE) {
                    state = State.STATE_USB_UNCONNECTED;
                } else if (condition == SwitchCondition.CONDITION_UAV_CONNECT) {
                    state = State.STATE_WAIT_TO_SELECT_POINT;
                }
                break;

            case STATE_WAIT_TO_SELECT_POINT:
                if (condition == SwitchCondition.CONDITION_USB_LOSE) {
                    state = State.STATE_USB_UNCONNECTED;
                } else if (condition == SwitchCondition.CONDITION_UAV_LOSE) {
                    state = State.STATE_UAV_UNCONNECTED;
                } else if (condition == SwitchCondition.CONDITION_ON_CLICK_SELECT) {
                    state = State.STATE_ON_SELECT;
                }
                break;

            case STATE_ON_SELECT:
                if (condition == SwitchCondition.CONDITION_USB_LOSE) {
                    state = State.STATE_USB_UNCONNECTED;
                } else if (condition == SwitchCondition.CONDITION_UAV_LOSE) {
                    state = State.STATE_UAV_UNCONNECTED;
                } else if (condition == SwitchCondition.CONDITION_ON_CLICK_CONFIRM) {
                    state = State.STATE_WAIT_TO_SELECT_POINT;
                } else if (condition == SwitchCondition.CONDITION_ON_CLICK_CANCEL) {
                    state = State.STATE_WAIT_TO_SELECT_POINT;
                }
                break;

            case STATE_FINISH_SELECT_POINT:
                if (condition == SwitchCondition.CONDITION_UAV_ARMED) {
                    state = State.STATE_UAV_ARMED;
                }
                break;

            case STATE_UAV_ARMED:
                if (condition == SwitchCondition.CONDITION_UAV_TAKEOFF) {
                    state = State.STATE_UAV_FLIGHT;
                }
                break;

            case STATE_UAV_FLIGHT:
                if (condition == SwitchCondition.CONDITION_USB_LOSE) {
                    state = State.STATE_USB_UNCONNECTED;
                } else if (condition == SwitchCondition.CONDITION_UAV_LOSE) {
                    state = State.STATE_UAV_UNCONNECTED;
                }
                break;
        }
        // 刷新布局
        mapActivityState.refreshState(state);
    }

    public enum State {
        STATE_USB_UNCONNECTED,
        STATE_UAV_UNCONNECTED,
        STATE_WAIT_TO_SELECT_POINT,
        STATE_ON_SELECT,
        STATE_FINISH_SELECT_POINT,
        STATE_UAV_ARMED,
        STATE_UAV_FLIGHT,
    }

    public enum SwitchCondition {
        CONDITION_USB_CONNECT,
        CONDITION_UAV_CONNECT,
        CONDITION_ON_CLICK_SELECT,
        CONDITION_ON_CLICK_CONFIRM,
        CONDITION_ON_CLICK_CANCEL,
        CONDITION_UAV_ARMED,
        CONDITION_UAV_TAKEOFF,
        CONDITION_USB_LOSE,
        CONDITION_UAV_LOSE
    }
}
