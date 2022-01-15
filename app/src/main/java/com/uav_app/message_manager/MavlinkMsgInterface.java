package com.uav_app.message_manager;

import com.mavlink.common.msg_heartbeat;
import com.mavlink.common.msg_sys_status;

public interface MavlinkMsgInterface {

   void onRecvUavStatus(msg_sys_status sys_status);


   void onRecvHeartbeat(msg_heartbeat heartbeat);
}
