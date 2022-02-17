package com.uav_app.front_end.map_activity.managers;

import android.widget.FrameLayout;

import com.uav_app.back_end.uav_manager.R;
import com.uav_app.front_end.map_activity.MapActivity;
import com.uav_app.front_end.map_activity.MapActivityState;

public class ButtonManager extends Manager implements MapActivityState.StateChangeListener {
    // 母面板
    private final FrameLayout button;

    public ButtonManager(MapActivity activity) {
        super(activity, 0x03);
        button = activity.findViewById(R.id.button);
    }

    @Override
    public void init(Connector connector) {
        super.init(connector);


    }

    @Override
    public void onStateChange(MapActivityState mapActivityState) {
    }
}
