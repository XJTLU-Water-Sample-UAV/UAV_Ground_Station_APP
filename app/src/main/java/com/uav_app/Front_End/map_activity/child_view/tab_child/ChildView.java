package com.uav_app.Front_End.map_activity.child_view.tab_child;

import android.content.Context;
import android.widget.LinearLayout;

import com.uav_app.Front_End.map_activity.managers.TabManager;

public abstract class ChildView extends LinearLayout {
    TabManager tabManager;

    public ChildView(Context context, TabManager tabManager) {
        super(context);
        this.tabManager = tabManager;
    }

    public void onShow() {
    }

    public void onHide() {
    }

    public void onUp() {
    }

    public void onDown() {
    }
}
