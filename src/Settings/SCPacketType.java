package Settings;

/**
 * Created by penguin on 17. 6. 16.
 */
public class SCPacketType {
    public static byte LOGIN = 0x01;
    public static byte LOGOUT = 0x02;
    public static byte CREATE_ROOM = 0x04;
    public static byte ENTER_ROOM = 0x08;
    public static byte LEAVE_ROOM = 0x10;
    public static byte SEND_MSG = 0x20;
    public static byte TAKE_MSG = 0x40;
}
