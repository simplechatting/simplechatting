package deprecated.Settings;

/**
 * Created by penguin on 17. 6. 16.
 */
public class SCPacketType {
    public final static byte LOGIN = 0x01;
    public final static byte LOGOUT = 0x02;
    public final static byte CREATE_ROOM = 0x03;
    public final static byte ENTER_ROOM = 0x04;
    public final static byte LEAVE_ROOM = 0x05;
    public final static byte SEND_MSG = 0x06;
    public final static byte TAKE_MSG = 0x07;
    public final static byte BROADCAST_ROOMS = 0x08;
}
