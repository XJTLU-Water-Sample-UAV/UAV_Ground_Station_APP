package com.uav_app.front_end.map_activity.child_view.tab_child;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;

import com.uav_app.back_end.uav_manager.R;
import com.uav_app.back_end.usb_manager.UsbConnectManager;
import com.uav_app.front_end.UIStateMachine;
import com.uav_app.front_end.map_activity.managers.TabManager;

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
            UIStateMachine.getOperationStateMachine().switchState(UIStateMachine.SwitchCondition.CONDITION_USB_CONNECT);
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

