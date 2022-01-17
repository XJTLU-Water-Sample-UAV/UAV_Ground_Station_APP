package com.uav_app.user_interface.map_activity.child_view.tab_child;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;

import com.uav_app.uav_manager.R;
import com.uav_app.user_interface.OperationStateMachine;
import com.uav_app.user_interface.map_activity.managers.TabManager;

@SuppressLint("ViewConstructor")
public class UavUnconnectedView extends ChildView {
    public UavUnconnectedView(Context context, TabManager tabManager) {
        super(context, tabManager);
        LayoutInflater.from(context).inflate(R.layout.mode_uav_unconnect, this);
        Button button = findViewById(R.id.connectButton);
        button.setOnClickListener(v -> OperationStateMachine.getOperationStateMachine().switchState(
                OperationStateMachine.SwitchCondition.CONDITION_UAV_CONNECT));
    }
}
