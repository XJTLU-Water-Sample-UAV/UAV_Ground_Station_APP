package com.uav_app.back_end;

import com.uav_app.back_end.usb_manager.UsbConnectManager;

public class EventBroker {
    // 本类单例对象
    private static EventBroker broker;

    public static EventBroker getUavStatePublisher() {
        if (broker == null) {
            synchronized (UsbConnectManager.class) {
                if (broker == null) {
                    broker = new EventBroker();
                }
            }
        }
        return broker;
    }

    private EventBroker() {

    }

    public void publishEvent() {

    }

    public void subscribe(EventObserver observer) {

    }

    public enum Events {

    }

    public interface EventObserver {
        void onEvent();
    }
}
