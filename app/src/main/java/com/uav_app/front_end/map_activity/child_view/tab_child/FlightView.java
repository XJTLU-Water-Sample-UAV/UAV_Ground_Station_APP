package com.uav_app.front_end.map_activity.child_view.tab_child;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;

import com.uav_app.back_end.uav_manager.R;
import com.uav_app.front_end.map_activity.managers.TabManager;

@SuppressLint("ViewConstructor")
public class FlightView extends ChildView {
    private final Button showButton;

    public FlightView(Context context, TabManager tabManager) {
        super(context, tabManager);
        LayoutInflater.from(context).inflate(R.layout.mode_flight, this);
        this.showButton = findViewById(R.id.showInfoButton);
        showButton.setOnClickListener(v -> {
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

