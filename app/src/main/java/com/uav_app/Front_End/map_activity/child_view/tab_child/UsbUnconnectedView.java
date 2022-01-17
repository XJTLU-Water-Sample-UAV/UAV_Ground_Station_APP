package com.uav_app.Front_End.map_activity.child_view.tab_child;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;

import com.uav_app.BackEnd.uav_manager.R;
import com.uav_app.BackEnd.usb_manager.UsbConnectManager;
import com.uav_app.Front_End.OperationStateMachine;
import com.uav_app.Front_End.map_activity.managers.TabManager;

@SuppressLint("ViewConstructor")
public class UsbUnconnectedView extends ChildView {
    private final Button button;

    public UsbUnconnectedView(Context context, TabManager tabManager) {
        super(context, tabManager);
        LayoutInflater.from(context).inflate(R.layout.mode_usb_unconnect, this);
        // 获取按钮
        button = findViewById(R.id.usbButton);
        button.setOnClickListener(v -> {
            UsbConnectManager.getConnectManager().connectDevice();
            OperationStateMachine.getOperationStateMachine().switchState(OperationStateMachine.SwitchCondition.CONDITION_USB_CONNECT);
        });
    }

    public int getButtonHeight() {
        LayoutParams layoutParams = (LayoutParams) button.getLayoutParams();
        return button.getHeight() + layoutParams.getMarginStart() + layoutParams.getMarginEnd();
    }

    public int getButtonMargin() {
        LayoutParams layoutParams = (LayoutParams) button.getLayoutParams();
        return layoutParams.getMarginStart();
    }
}

