package com.uav_app.user_interface.map_activity.child_view.tab_child;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;

import com.uav_app.uav_manager.R;
import com.uav_app.uav_manager.UavStateManager;
import com.uav_app.user_interface.OperationStateMachine;
import com.uav_app.user_interface.map_activity.MapActivityState;

@SuppressLint("ViewConstructor")
public class WaitView extends ChildView implements MapActivityState.StateChangeListener {
    // 选择按钮
    private final Button selectButton;
    // 解锁按钮
    private final Button unlockButton;
    // 监听器ID
    private static final int LISTENER_ID = 0x03;

    public WaitView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.mode_wait, this);
        MapActivityState.getMapActivityState().addListener(LISTENER_ID, this);
        selectButton = findViewById(R.id.selectButton);
        unlockButton = findViewById(R.id.unlockButton);
        selectButton.setOnClickListener(v -> OperationStateMachine.getOperationStateMachine()
                .switchState(OperationStateMachine.SwitchCondition.CONDITION_ON_CLICK_SELECT));
        unlockButton.setOnClickListener(v -> {
            if (MapActivityState.getMapActivityState().waitViewState.isUavUnlocked) {
                OperationStateMachine.getOperationStateMachine().switchState(OperationStateMachine
                        .SwitchCondition.CONDITION_UAV_TAKEOFF);
                UavStateManager.getUavStateManager().takeoff();

            } else {
                OperationStateMachine.getOperationStateMachine().switchState(OperationStateMachine
                        .SwitchCondition.CONDITION_UAV_UNLOCK);
                UavStateManager.getUavStateManager().unlockUav();
            }
        });
    }

    @Override
    public void onStateChange(MapActivityState mapActivityState) {
        if (mapActivityState.waitViewState.isPointSelected) {
            selectButton.setText(R.string.reselectButtonText);
        } else {
            selectButton.setText(R.string.selectButtonText);
        }
        if (mapActivityState.waitViewState.isUavUnlocked) {
            unlockButton.setText(R.string.takeoffButtonText);
            unlockButton.setEnabled(mapActivityState.waitViewState.isPointSelected);
        } else {
            unlockButton.setText(R.string.unlockButtonText);
            unlockButton.setEnabled(true);
        }
    }
}
