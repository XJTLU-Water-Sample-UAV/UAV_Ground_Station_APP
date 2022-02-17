package com.uav_app.front_end.map_activity.managers;

import android.widget.FrameLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.uav_app.back_end.uav_manager.R;
import com.uav_app.front_end.map_activity.MapActivity;
import com.uav_app.front_end.map_activity.MapActivityState;

public class ButtonManager extends Manager implements MapActivityState.StateChangeListener {
    // 母面板
    private final FrameLayout buttons;

    public ButtonManager(MapActivity activity) {
        super(activity, 0x03);
        buttons = activity.findViewById(R.id.button);
    }

    public void init(Connector connector, int bottomMargin) {
        super.init(connector);
        // 重设控件面板位置和大小
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) buttons.getLayoutParams();
        params.bottomMargin = bottomMargin;
        buttons.requestLayout();
    }

    @Override
    public void onStateChange(MapActivityState mapActivityState) {
    }
}
