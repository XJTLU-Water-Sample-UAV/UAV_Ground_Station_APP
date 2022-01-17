package com.uav_app.Front_End.map_activity.child_view.tab_child;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;

import com.uav_app.BackEnd.uav_manager.R;
import com.uav_app.Front_End.OperationStateMachine;
import com.uav_app.Front_End.map_activity.managers.TabManager;

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
