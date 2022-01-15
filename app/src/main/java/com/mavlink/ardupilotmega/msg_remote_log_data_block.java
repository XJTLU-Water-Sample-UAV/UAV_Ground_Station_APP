/* AUTO-GENERATED FILE.  DO NOT MODIFY.
 *
 * This class was automatically generated by the
 * java mavlink generator tool. It should not be modified by hand.
 */

// MESSAGE REMOTE_LOG_DATA_BLOCK PACKING
package com.mavlink.ardupilotmega;

import com.mavlink.MAVLinkPacket;
import com.mavlink.messages.MAVLinkMessage;
import com.mavlink.messages.MAVLinkPayload;

/**
 * Send a block of log data to remote location
 */
public class msg_remote_log_data_block extends MAVLinkMessage {

    public static final int MAVLINK_MSG_ID_REMOTE_LOG_DATA_BLOCK = 184;
    public static final int MAVLINK_MSG_LENGTH = 206;
    private static final long serialVersionUID = MAVLINK_MSG_ID_REMOTE_LOG_DATA_BLOCK;


    /**
     * log data block sequence number
     */
    public long seqno;

    /**
     * System ID
     */
    public short target_system;

    /**
     * Component ID
     */
    public short target_component;

    /**
     * log data block
     */
    public short[] data = new short[200];


    /**
     * Constructor for a new message, just initializes the msgid
     */
    public msg_remote_log_data_block() {
        msgid = MAVLINK_MSG_ID_REMOTE_LOG_DATA_BLOCK;
    }

    /**
     * Constructor for a new message, initializes the message with the payload
     * from a mavlink packet
     */
    public msg_remote_log_data_block(MAVLinkPacket mavLinkPacket) {
        this.sysid = mavLinkPacket.sysid;
        this.compid = mavLinkPacket.compid;
        this.msgid = MAVLINK_MSG_ID_REMOTE_LOG_DATA_BLOCK;
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
        packet.msgid = MAVLINK_MSG_ID_REMOTE_LOG_DATA_BLOCK;

        packet.payload.putUnsignedInt(seqno);

        packet.payload.putUnsignedByte(target_system);

        packet.payload.putUnsignedByte(target_component);


        for (int i = 0; i < data.length; i++) {
            packet.payload.putUnsignedByte(data[i]);
        }


        return packet;
    }

    /**
     * Decode a remote_log_data_block message into this class fields
     *
     * @param payload The message to decode
     */
    public void unpack(MAVLinkPayload payload) {
        payload.resetIndex();

        this.seqno = payload.getUnsignedInt();

        this.target_system = payload.getUnsignedByte();

        this.target_component = payload.getUnsignedByte();


        for (int i = 0; i < this.data.length; i++) {
            this.data[i] = payload.getUnsignedByte();
        }


    }

    /**
     * Returns a string with the MSG name and data
     */
    public String toString() {
        return "MAVLINK_MSG_ID_REMOTE_LOG_DATA_BLOCK -" + " seqno:" + seqno + " target_system:" + target_system + " target_component:" + target_component + " data:" + data + "";
    }
}
        