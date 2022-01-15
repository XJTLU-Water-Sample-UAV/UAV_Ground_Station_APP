/* AUTO-GENERATED FILE.  DO NOT MODIFY.
 *
 * This class was automatically generated by the
 * java mavlink generator tool. It should not be modified by hand.
 */

// MESSAGE GPS_ACCURACY PACKING
package com.mavlink.ardupilotmega;

import com.mavlink.MAVLinkPacket;
import com.mavlink.messages.MAVLinkMessage;
import com.mavlink.messages.MAVLinkPayload;

/**
 * Accuracy statistics for GPS lock
 */
public class msg_gps_accuracy extends MAVLinkMessage {

    public static final int MAVLINK_MSG_ID_GPS_ACCURACY = 225;
    public static final int MAVLINK_MSG_LENGTH = 22;
    private static final long serialVersionUID = MAVLINK_MSG_ID_GPS_ACCURACY;


    /**
     * GPS-reported horizontal accuracy
     */
    public float h_acc;

    /**
     * GPS-reported speed accuracy
     */
    public float s_acc;

    /**
     * GPS-reported, filtered horizontal velocity
     */
    public float h_vel_filt;

    /**
     * GPS-reported, filtered vertical velocity
     */
    public float v_vel_filt;

    /**
     * GPS position drift
     */
    public float p_drift;

    /**
     * Which instance of GPS we're reporting on
     */
    public short instance;

    /**
     * Which fields pass EKF checks
     */
    public short ekf_check_mask;


    /**
     * Constructor for a new message, just initializes the msgid
     */
    public msg_gps_accuracy() {
        msgid = MAVLINK_MSG_ID_GPS_ACCURACY;
    }

    /**
     * Constructor for a new message, initializes the message with the payload
     * from a mavlink packet
     */
    public msg_gps_accuracy(MAVLinkPacket mavLinkPacket) {
        this.sysid = mavLinkPacket.sysid;
        this.compid = mavLinkPacket.compid;
        this.msgid = MAVLINK_MSG_ID_GPS_ACCURACY;
        unpack(mavLinkPacket.payload);
    }

    /**
     * Generates the payload for a mavlink message for a message of this type
     *
     * @return
     */
    public MAVLinkPacket pack() {
        MAVLinkPacket packet = new MAVLinkPacket(MAVLINK_MSG_LENGTH);
        packet.sysid = 255;
        packet.compid = 190;
        packet.msgid = MAVLINK_MSG_ID_GPS_ACCURACY;

        packet.payload.putFloat(h_acc);

        packet.payload.putFloat(s_acc);

        packet.payload.putFloat(h_vel_filt);

        packet.payload.putFloat(v_vel_filt);

        packet.payload.putFloat(p_drift);

        packet.payload.putUnsignedByte(instance);

        packet.payload.putUnsignedByte(ekf_check_mask);

        return packet;
    }

    /**
     * Decode a gps_accuracy message into this class fields
     *
     * @param payload The message to decode
     */
    public void unpack(MAVLinkPayload payload) {
        payload.resetIndex();

        this.h_acc = payload.getFloat();

        this.s_acc = payload.getFloat();

        this.h_vel_filt = payload.getFloat();

        this.v_vel_filt = payload.getFloat();

        this.p_drift = payload.getFloat();

        this.instance = payload.getUnsignedByte();

        this.ekf_check_mask = payload.getUnsignedByte();

    }

    /**
     * Returns a string with the MSG name and data
     */
    public String toString() {
        return "MAVLINK_MSG_ID_GPS_ACCURACY -" + " h_acc:" + h_acc + " s_acc:" + s_acc + " h_vel_filt:" + h_vel_filt + " v_vel_filt:" + v_vel_filt + " p_drift:" + p_drift + " instance:" + instance + " ekf_check_mask:" + ekf_check_mask + "";
    }
}
        