package com.uav_app.user_interface.map_activity.child_view.tab_child;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;

import com.uav_app.uav_manager.R;
import com.uav_app.uav_manager.UavStateManager;
import com.uav_app.user_interface.map_activity.managers.TabManager;

@SuppressLint("ViewConstructor")
public class FlightView extends ChildView {
    private final TabManager tabManager;
    private final Button showButton;

    public FlightView(Context context, TabManager tabManager) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.mode_flight, this);
        this.tabManager = tabManager;
        this.showButton = findViewById(R.id.showInfoButton);
        showButton.setOnClickListener(v -> {


            UavStateManager.getUavStateManager().landing();
            /*
            if (tabManager.isTabShow()) {
                tabManager.closeTab();
            } else {
                tabManager.openTab();
            }
            */


        });
    }

    @Override
    public void onUp() {
        showButton.setText("关闭详情");
    }

    @Override
    public void onDown() {
        showButton.setText("展开详情");
    }
}
