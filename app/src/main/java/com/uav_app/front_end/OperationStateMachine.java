package com.uav_app.front_end;

import com.uav_app.back_end.usb_manager.UsbConnectManager;
import com.uav_app.front_end.map_activity.MapActivityState;
import com.uav_app.front_end.map_activity.managers.TabManager;

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

    public void switchState(SwitchCondition condition) {
        switch (state) {
            // USB没有连接
            case STATE_USB_UNCONNECTED:
                if (condition == SwitchCondition.CONDITION_USB_CONNECT) {
                    state = State.STATE_UAV_UNCONNECTED;
                    // 连上数传设备
                    mapActivityState.tabViewState.tabState = TabManager.TabState.VIEW_UAV_UNCONNECTED;

                }
                break;

            case STATE_UAV_UNCONNECTED:
                if (condition == SwitchCondition.CONDITION_USB_LOSE) {
                    state = State.STATE_USB_UNCONNECTED;
                    // 数传设备丢失
                    mapActivityState.tabViewState.tabState = TabManager.TabState.VIEW_USB_UNCONNECTED;
                } else if (condition == SwitchCondition.CONDITION_UAV_CONNECT) {
                    state = State.STATE_WAIT_TO_SELECT;
                    // UAV连接成功
                    mapActivityState.tabViewState.tabState = TabManager.TabState.VIEW_WAIT;
                }
                break;

            case STATE_WAIT_TO_SELECT:
                if (condition == SwitchCondition.CONDITION_USB_LOSE) {
                    state = State.STATE_USB_UNCONNECTED;
                    // 数传设备丢失
                    mapActivityState.tabViewState.tabState = TabManager.TabState.VIEW_USB_UNCONNECTED;
                } else if (condition == SwitchCondition.CONDITION_UAV_LOSE) {
                    state = State.STATE_UAV_UNCONNECTED;
                    // 无人机丢失
                    mapActivityState.tabViewState.tabState = TabManager.TabState.VIEW_UAV_UNCONNECTED;
                } else if (condition == SwitchCondition.CONDITION_ON_CLICK_SELECT) {
                    state = State.STATE_SELECT;
                    // 开始选点
                    mapActivityState.tabViewState.tabState = TabManager.TabState.VIEW_SELECT;
                    // 删除所有选点
                    mapActivityState.waitViewState.isPointSelected = false;
                    mapActivityState.pointManager.deleteAll();
                    // 使能地图选点
                    mapActivityState.mapViewState.isCanBeSelect = true;
                } else if (condition == SwitchCondition.CONDITION_UAV_TAKEOFF) {
                    state = State.STATE_UAV_FLIGHT;
                    // 无人机起飞
                    mapActivityState.tabViewState.tabState = TabManager.TabState.VIEW_FLIGHT;
                } else if (condition == SwitchCondition.CONDITION_UAV_UNLOCK) {
                    // 无人机解锁
                    mapActivityState.waitViewState.isUavUnlocked = true;
                }
                break;

            case STATE_SELECT:
                if (condition == SwitchCondition.CONDITION_USB_LOSE) {
                    state = State.STATE_USB_UNCONNECTED;
                    // 数传设备丢失
                    mapActivityState.tabViewState.tabState = TabManager.TabState.VIEW_USB_UNCONNECTED;
                    mapActivityState.mapViewState.isCanBeSelect = false;
                } else if (condition == SwitchCondition.CONDITION_UAV_LOSE) {
                    state = State.STATE_UAV_UNCONNECTED;
                    // 无人机丢失
                    mapActivityState.tabViewState.tabState = TabManager.TabState.VIEW_UAV_UNCONNECTED;
                    mapActivityState.mapViewState.isCanBeSelect = false;
                } else if (condition == SwitchCondition.CONDITION_ON_CLICK_CONFIRM) {
                    state = State.STATE_WAIT_TO_SELECT;
                    // 确认选点
                    mapActivityState.tabViewState.tabState = TabManager.TabState.VIEW_WAIT;
                    mapActivityState.waitViewState.isPointSelected = true;
                    mapActivityState.mapViewState.isCanBeSelect = false;
                } else if (condition == SwitchCondition.CONDITION_ON_CLICK_CANCEL) {
                    state = State.STATE_WAIT_TO_SELECT;
                    // 取消选点
                    mapActivityState.tabViewState.tabState = TabManager.TabState.VIEW_WAIT;
                    mapActivityState.mapViewState.isCanBeSelect = false;
                }
                break;

            case STATE_UAV_FLIGHT:
                if (condition == SwitchCondition.CONDITION_USB_LOSE) {
                    state = State.STATE_USB_UNCONNECTED;
                } else if (condition == SwitchCondition.CONDITION_UAV_LOSE) {
                    state = State.STATE_UAV_LOSE;
                }
                break;

            case STATE_USB_LOSE:
                if (condition == SwitchCondition.CONDITION_USB_CONNECT) {
                    state = State.STATE_UAV_LOSE;
                }
                break;

            case STATE_UAV_LOSE:
                if (condition == SwitchCondition.CONDITION_USB_LOSE) {
                    state = State.STATE_USB_UNCONNECTED;
                } else if (condition == SwitchCondition.CONDITION_UAV_CONNECT) {
                    state = State.STATE_UAV_FLIGHT;
                }
                break;
        }
        // 刷新布局
        mapActivityState.applyChange();
    }

    private enum State {
        STATE_USB_UNCONNECTED,
        STATE_UAV_UNCONNECTED,
        STATE_WAIT_TO_SELECT,
        STATE_SELECT,
        STATE_UAV_FLIGHT,
        STATE_USB_LOSE,
        STATE_UAV_LOSE,
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
