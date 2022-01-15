/* AUTO-GENERATED FILE.  DO NOT MODIFY.
 *
 * This class was automatically generated by the
 * java mavlink generator tool. It should not be modified by hand.
 */

// MESSAGE DIGICAM_CONFIGURE PACKING
package com.mavlink.ardupilotmega;

import com.mavlink.MAVLinkPacket;
import com.mavlink.messages.MAVLinkMessage;
import com.mavlink.messages.MAVLinkPayload;

/**
 * Configure on-board Camera Control System.
 */
public class msg_digicam_configure extends MAVLinkMessage {

    public static final int MAVLINK_MSG_ID_DIGICAM_CONFIGURE = 154;
    public static final int MAVLINK_MSG_LENGTH = 15;
    private static final long serialVersionUID = MAVLINK_MSG_ID_DIGICAM_CONFIGURE;


    /**
     * Correspondent value to given extra_param
     */
    public float extra_value;

    /**
     * Divisor number //e.g. 1000 means 1/1000 (0 means ignore)
     */
    public int shutter_speed;

    /**
     * System ID
     */
    public short target_system;

    /**
     * Component ID
     */
    public short target_component;

    /**
     * Mode enumeration from 1 to N //P, TV, AV, M, Etc (0 means ignore)
     */
    public short mode;

    /**
     * F stop number x 10 //e.g. 28 means 2.8 (0 means ignore)
     */
    public short aperture;

    /**
     * ISO enumeration from 1 to N //e.g. 80, 100, 200, Etc (0 means ignore)
     */
    public short iso;

    /**
     * Exposure type enumeration from 1 to N (0 means ignore)
     */
    public short exposure_type;

    /**
     * Command Identity (incremental loop: 0 to 255)//A command sent multiple times will be executed or pooled just once
     */
    public short command_id;

    /**
     * Main engine cut-off time before camera trigger in seconds/10 (0 means no cut-off)
     */
    public short engine_cut_off;

    /**
     * Extra parameters enumeration (0 means ignore)
     */
    public short extra_param;


    /**
     * Constructor for a new message, just initializes the msgid
     */
    public msg_digicam_configure() {
        msgid = MAVLINK_MSG_ID_DIGICAM_CONFIGURE;
    }

    /**
     * Constructor for a new message, initializes the message with the payload
     * from a mavlink packet
     */
    public msg_digicam_configure(MAVLinkPacket mavLinkPacket) {
        this.sysid = mavLinkPacket.sysid;
        this.compid = mavLinkPacket.compid;
        this.msgid = MAVLINK_MSG_ID_DIGICAM_CONFIGURE;
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
        packet.msgid = MAVLINK_MSG_ID_DIGICAM_CONFIGURE;

        packet.payload.putFloat(extra_value);

        packet.payload.putUnsignedShort(shutter_speed);

        packet.payload.putUnsignedByte(target_system);

        packet.payload.putUnsignedByte(target_component);

        packet.payload.putUnsignedByte(mode);

        packet.payload.putUnsignedByte(aperture);

        packet.payload.putUnsignedByte(iso);

        packet.payload.putUnsignedByte(exposure_type);

        packet.payload.putUnsignedByte(command_id);

        packet.payload.putUnsignedByte(engine_cut_off);

        packet.payload.putUnsignedByte(extra_param);

        return packet;
    }

    /**
     * Decode a digicam_configure message into this class fields
     *
     * @param payload The message to decode
     */
    public void unpack(MAVLinkPayload payload) {
        payload.resetIndex();

        this.extra_value = payload.getFloat();

        this.shutter_speed = payload.getUnsignedShort();

        this.target_system = payload.getUnsignedByte();

        this.target_component = payload.getUnsignedByte();

        this.mode = payload.getUnsignedByte();

        this.aperture = payload.getUnsignedByte();

        this.iso = payload.getUnsignedByte();

        this.exposure_type = payload.getUnsignedByte();

        this.command_id = payload.getUnsignedByte();

        this.engine_cut_off = payload.getUnsignedByte();

        this.extra_param = payload.getUnsignedByte();

    }

    /**
     * Returns a string with the MSG name and data
     */
    public String toString() {
        return "MAVLINK_MSG_ID_DIGICAM_CONFIGURE -" + " extra_value:" + extra_value + " shutter_speed:" + shutter_speed + " target_system:" + target_system + " target_component:" + target_component + " mode:" + mode + " aperture:" + aperture + " iso:" + iso + " exposure_type:" + exposure_type + " command_id:" + command_id + " engine_cut_off:" + engine_cut_off + " extra_param:" + extra_param + "";
    }
}
        