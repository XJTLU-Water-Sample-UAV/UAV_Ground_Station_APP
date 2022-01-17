/* AUTO-GENERATED FILE.  DO NOT MODIFY.
 *
 * This class was automatically generated by the
 * java mavlink generator tool. It should not be modified by hand.
 */

// MESSAGE ESC_TELEMETRY_1_TO_4 PACKING
package io.mavlink.ardupilotmega;
import io.mavlink.MAVLinkPacket;
import io.mavlink.Messages.MAVLinkMessage;
import io.mavlink.Messages.MAVLinkPayload;
        
/**
 * ESC Telemetry Data for ESCs 1 to 4, matching data sent by BLHeli ESCs.
 */
public class msg_esc_telemetry_1_to_4 extends MAVLinkMessage {

    public static final int MAVLINK_MSG_ID_ESC_TELEMETRY_1_TO_4 = 11030;
    public static final int MAVLINK_MSG_LENGTH = 44;
    private static final long serialVersionUID = MAVLINK_MSG_ID_ESC_TELEMETRY_1_TO_4;

      
    /**
     * Voltage.
     */
    public int voltage[] = new int[4];
      
    /**
     * Current.
     */
    public int current[] = new int[4];
      
    /**
     * Total current.
     */
    public int totalcurrent[] = new int[4];
      
    /**
     * RPM (eRPM).
     */
    public int rpm[] = new int[4];
      
    /**
     * count of telemetry packets received (wraps at 65535).
     */
    public int count[] = new int[4];
      
    /**
     * Temperature.
     */
    public short temperature[] = new short[4];
    

    /**
     * Generates the payload for a mavlink message for a message of this type
     * @return
     */
    @Override
    public MAVLinkPacket pack() {
        MAVLinkPacket packet = new MAVLinkPacket(MAVLINK_MSG_LENGTH,isMavlink2);
        packet.sysid = sysid;
        packet.compid = compid;
        packet.msgid = MAVLINK_MSG_ID_ESC_TELEMETRY_1_TO_4;
        
        
        for (int i = 0; i < voltage.length; i++) {
            packet.payload.putUnsignedShort(voltage[i]);
        }
                    
        
        for (int i = 0; i < current.length; i++) {
            packet.payload.putUnsignedShort(current[i]);
        }
                    
        
        for (int i = 0; i < totalcurrent.length; i++) {
            packet.payload.putUnsignedShort(totalcurrent[i]);
        }
                    
        
        for (int i = 0; i < rpm.length; i++) {
            packet.payload.putUnsignedShort(rpm[i]);
        }
                    
        
        for (int i = 0; i < count.length; i++) {
            packet.payload.putUnsignedShort(count[i]);
        }
                    
        
        for (int i = 0; i < temperature.length; i++) {
            packet.payload.putUnsignedByte(temperature[i]);
        }
                    
        
        if (isMavlink2) {
            
        }
        return packet;
    }

    /**
     * Decode a esc_telemetry_1_to_4 message into this class fields
     *
     * @param payload The message to decode
     */
    @Override
    public void unpack(MAVLinkPayload payload) {
        payload.resetIndex();
        
         
        for (int i = 0; i < this.voltage.length; i++) {
            this.voltage[i] = payload.getUnsignedShort();
        }
                
         
        for (int i = 0; i < this.current.length; i++) {
            this.current[i] = payload.getUnsignedShort();
        }
                
         
        for (int i = 0; i < this.totalcurrent.length; i++) {
            this.totalcurrent[i] = payload.getUnsignedShort();
        }
                
         
        for (int i = 0; i < this.rpm.length; i++) {
            this.rpm[i] = payload.getUnsignedShort();
        }
                
         
        for (int i = 0; i < this.count.length; i++) {
            this.count[i] = payload.getUnsignedShort();
        }
                
         
        for (int i = 0; i < this.temperature.length; i++) {
            this.temperature[i] = payload.getUnsignedByte();
        }
                
        
        if (isMavlink2) {
            
        }
    }

    /**
     * Constructor for a new message, just initializes the msgid
     */
    public msg_esc_telemetry_1_to_4() {
        this.msgid = MAVLINK_MSG_ID_ESC_TELEMETRY_1_TO_4;
    }
    
    /**
     * Constructor for a new message, initializes msgid and all payload variables
     */
    public msg_esc_telemetry_1_to_4( int[] voltage, int[] current, int[] totalcurrent, int[] rpm, int[] count, short[] temperature) {
        this.msgid = MAVLINK_MSG_ID_ESC_TELEMETRY_1_TO_4;

        this.voltage = voltage;
        this.current = current;
        this.totalcurrent = totalcurrent;
        this.rpm = rpm;
        this.count = count;
        this.temperature = temperature;
        
    }
    
    /**
     * Constructor for a new message, initializes everything
     */
    public msg_esc_telemetry_1_to_4( int[] voltage, int[] current, int[] totalcurrent, int[] rpm, int[] count, short[] temperature, int sysid, int compid, boolean isMavlink2) {
        this.msgid = MAVLINK_MSG_ID_ESC_TELEMETRY_1_TO_4;
        this.sysid = sysid;
        this.compid = compid;
        this.isMavlink2 = isMavlink2;

        this.voltage = voltage;
        this.current = current;
        this.totalcurrent = totalcurrent;
        this.rpm = rpm;
        this.count = count;
        this.temperature = temperature;
        
    }

    /**
     * Constructor for a new message, initializes the message with the payload
     * from a mavlink packet
     *
     */
    public msg_esc_telemetry_1_to_4(MAVLinkPacket mavLinkPacket) {
        this.msgid = MAVLINK_MSG_ID_ESC_TELEMETRY_1_TO_4;
        
        this.sysid = mavLinkPacket.sysid;
        this.compid = mavLinkPacket.compid;
        this.isMavlink2 = mavLinkPacket.isMavlink2;
        unpack(mavLinkPacket.payload);
    }

                
    /**
     * Returns a string with the MSG name and data
     */
    @Override
    public String toString() {
        return "MAVLINK_MSG_ID_ESC_TELEMETRY_1_TO_4 - sysid:"+sysid+" compid:"+compid+" voltage:"+voltage+" current:"+current+" totalcurrent:"+totalcurrent+" rpm:"+rpm+" count:"+count+" temperature:"+temperature+"";
    }
    
    /**
     * Returns a human-readable string of the name of the message
     */
    @Override
    public String name() {
        return "MAVLINK_MSG_ID_ESC_TELEMETRY_1_TO_4";
    }
}
        