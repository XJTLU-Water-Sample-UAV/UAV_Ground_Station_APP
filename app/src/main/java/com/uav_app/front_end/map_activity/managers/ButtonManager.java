package com.uav_app.front_end.map_activity.managers;

import com.uav_app.front_end.map_activity.MapActivity;
import com.uav_app.front_end.map_activity.MapActivityState;

public class ButtonManager extends Manager implements MapActivityState.StateChangeListener {
    public ButtonManager(MapActivity activity) {
        super(activity, 0x03);
    }

    @Override
    public void onStateChange(MapActivityState mapActivityState) {

    }
}
