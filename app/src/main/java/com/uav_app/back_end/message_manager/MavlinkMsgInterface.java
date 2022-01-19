package com.uav_app.back_end.message_manager;

public interface MavlinkMsgInterface {
    void onSendUdpError(Exception e);

    void onRecvUdpError(Exception e);
}
