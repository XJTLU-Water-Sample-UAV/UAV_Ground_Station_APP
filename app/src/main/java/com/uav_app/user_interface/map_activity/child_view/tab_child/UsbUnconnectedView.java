package com.uav_app.user_interface.map_activity.child_view.tab_child;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;

import com.uav_app.uav_manager.R;
import com.uav_app.usb_manager.UsbConnectManager;
import com.uav_app.user_interface.OperationStateMachine;

public class UsbUnconnectedView extends ChildView {
    private final Button button;

    public UsbUnconnectedView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.mode_usb_unconnect, this);
        // 获取按钮
        button = findViewById(R.id.usbButton);
        button.setOnClickListener(v -> {
            UsbConnectManager.getConnectManager().connectDevice();
            OperationStateMachine.getOperationStateMachine().switchState(OperationStateMachine.SwitchCondition.CONDITION_USB_CONNECT);
        });
    }

    public int getButtonHeight() {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) button.getLayoutParams();
        return button.getHeight() + layoutParams.getMarginStart() + layoutParams.getMarginEnd();
    }

    public int getButtonMargin() {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) button.getLayoutParams();
        return layoutParams.getMarginStart();
    }
}

