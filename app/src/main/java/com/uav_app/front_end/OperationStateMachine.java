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
                // USB设备未连接
                if (condition == SwitchCondition.CONDITION_USB_CONNECT) {
                    state = State.STATE_UAV_UNCONNECTED;
                }
                break;

            case STATE_UAV_UNCONNECTED:
                // 无人机未连接
                if (condition == SwitchCondition.CONDITION_USB_DISCONNECT) {
                    state = State.STATE_USB_UNCONNECTED;
                } else if (condition == SwitchCondition.CONDITION_UAV_CONNECT) {
                    state = State.STATE_WAIT_TO_SELECT_POINT;
                }
                break;

            case STATE_WAIT_TO_SELECT_POINT:
                // 无人机等待选点中
                if (condition == SwitchCondition.CONDITION_USB_DISCONNECT) {
                    state = State.STATE_USB_UNCONNECTED;
                } else if (condition == SwitchCondition.CONDITION_UAV_DISCONNECT) {
                    state = State.STATE_UAV_UNCONNECTED;
                } else if (condition == SwitchCondition.CONDITION_ON_CLICK_SELECT) {
                    state = State.STATE_ON_SELECT;
                }
                break;

            case STATE_ON_SELECT:
                // 选点中
                if (condition == SwitchCondition.CONDITION_USB_DISCONNECT) {
                    state = State.STATE_USB_UNCONNECTED;
                } else if (condition == SwitchCondition.CONDITION_UAV_DISCONNECT) {
                    state = State.STATE_UAV_UNCONNECTED;
                } else if (condition == SwitchCondition.CONDITION_ON_CLICK_CONFIRM) {
                    state = State.STATE_FINISH_SELECT_POINT;
                } else if (condition == SwitchCondition.CONDITION_ON_CLICK_CANCEL) {
                    state = State.STATE_WAIT_TO_SELECT_POINT;
                }
                break;

            case STATE_FINISH_SELECT_POINT:
                // 完成选点
                if (condition == SwitchCondition.CONDITION_USB_DISCONNECT) {
                    state = State.STATE_USB_UNCONNECTED;
                } else if (condition == SwitchCondition.CONDITION_UAV_DISCONNECT) {
                    state = State.STATE_UAV_UNCONNECTED;
                } else if (condition == SwitchCondition.CONDITION_UAV_ARMED) {
                    state = State.STATE_UAV_ARMED;
                } else if (condition == SwitchCondition.CONDITION_ON_CLICK_SELECT) {
                    state = State.STATE_ON_SELECT;
                }
                break;

            case STATE_UAV_ARMED:
                // 无人机解锁
                if (condition == SwitchCondition.CONDITION_UAV_TAKEOFF) {
                    state = State.STATE_UAV_FLIGHT;
                } else if (condition == SwitchCondition.CONDITION_UAV_DISARMED) {
                    state = State.STATE_FINISH_SELECT_POINT;
                }
                break;

            case STATE_UAV_FLIGHT:
                // 无人机起飞
                if (condition == SwitchCondition.CONDITION_USB_DISCONNECT) {
                    state = State.STATE_USB_UNCONNECTED;
                } else if (condition == SwitchCondition.CONDITION_UAV_DISCONNECT) {
                    state = State.STATE_UAV_UNCONNECTED;
                }
                break;
        }
        // 通知UI刷新布局
        mapActivityState.refreshState(state);
    }

    /*
     * 状态机状态
     **/
    public enum State {
        // USB设备未连接
        STATE_USB_UNCONNECTED,
        // 无人机未连接
        STATE_UAV_UNCONNECTED,
        // 无人机等待选点中
        STATE_WAIT_TO_SELECT_POINT,
        // 选点中
        STATE_ON_SELECT,
        // 完成选点
        STATE_FINISH_SELECT_POINT,
        // 无人机解锁
        STATE_UAV_ARMED,
        // 无人机起飞
        STATE_UAV_FLIGHT,
    }

    /*
     * 跳转条件（输入条件）
     **/
    public enum SwitchCondition {
        CONDITION_USB_CONNECT,
        CONDITION_UAV_CONNECT,
        CONDITION_ON_CLICK_SELECT,
        CONDITION_ON_CLICK_CONFIRM,
        CONDITION_ON_CLICK_CANCEL,
        CONDITION_UAV_ARMED,
        CONDITION_UAV_DISARMED,
        CONDITION_UAV_TAKEOFF,
        CONDITION_USB_DISCONNECT,
        CONDITION_UAV_DISCONNECT
    }
}
