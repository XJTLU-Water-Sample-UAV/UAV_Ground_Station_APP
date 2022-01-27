package com.uav_app.front_end.map_activity.child_view.tab_child;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;

import com.uav_app.back_end.uav_manager.R;
import com.uav_app.back_end.uav_manager.UavStateManager;
import com.uav_app.front_end.OperationStateMachine;
import com.uav_app.front_end.map_activity.MapActivityState;
import com.uav_app.front_end.map_activity.managers.TabManager;

@SuppressLint("ViewConstructor")
public class WaitView extends ChildView implements MapActivityState.StateChangeListener {
    // 选择按钮
    private final Button selectButton;
    // 解锁按钮
    private final Button unlockButton;
    // 监听器ID
    private static final int LISTENER_ID = 0x03;

    public WaitView(Context context, TabManager tabManager) {
        super(context, tabManager);
        LayoutInflater.from(context).inflate(R.layout.mode_wait, this);
        MapActivityState.getMapActivityState().addListener(LISTENER_ID, this);
        selectButton = findViewById(R.id.selectButton);
        unlockButton = findViewById(R.id.unlockButton);
        selectButton.setOnClickListener(v -> {
            if (MapActivityState.getMapActivityState().getWaitViewState().isPointSelected = true) {
                UavStateManager.getUavStateManager().clearMission();
            }
            OperationStateMachine.getOperationStateMachine()
                    .nextState(OperationStateMachine.SwitchCondition.CONDITION_ON_CLICK_SELECT);
        });
        unlockButton.setOnClickListener(v -> {
            UavStateManager.getUavStateManager().armUav();
            /*
            if (MapActivityState.getMapActivityState().waitViewState.isUavUnlocked) {
                OperationStateMachine.getOperationStateMachine().switchState(OperationStateMachine
                        .SwitchCondition.CONDITION_UAV_TAKEOFF);
                UavStateManager.getUavStateManager().takeoff();

            } else {
                OperationStateMachine.getOperationStateMachine().switchState(OperationStateMachine
                        .SwitchCondition.CONDITION_UAV_UNLOCK);
                UavStateManager.getUavStateManager().unlockUav();
            }
            */
        });

        Button takeoffButton = findViewById(R.id.takeoffButton);
        takeoffButton.setOnClickListener(v -> {
            UavStateManager.getUavStateManager().startMission();
        });
    }

    @Override
    public void onStateChange(MapActivityState mapActivityState) {
        if (mapActivityState.getWaitViewState().isPointSelected) {
            selectButton.setText(R.string.reselectButtonText);
        } else {
            selectButton.setText(R.string.selectButtonText);
        }
        if (mapActivityState.getWaitViewState().isUavArmed) {
            unlockButton.setText(R.string.takeoffButtonText);
            unlockButton.setEnabled(mapActivityState.getWaitViewState().isPointSelected);
        } else {
            unlockButton.setText(R.string.unlockButtonText);
            unlockButton.setEnabled(true);
        }
    }
}
