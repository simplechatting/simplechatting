/**
 * Created by penguin on 17. 6. 16.
 */
public class SCPacketType {
    public final static byte LOGIN = 0x01;
    public final static byte LOGOUT = 0x02;
    public final static byte MSG_SEND = 0x03;
    public final static byte MSG_ORDERED = 0x04;
    public final static byte UPLOAD_FILE = 0x05;
    public final static byte DOWNLOAD_FILE = 0x06;
    public final static byte UPLOAD_READY = 0x07;
    public final static byte DONWLOAD_READY = 0x08;
    public final static byte SEND_FILE = 0x09;
    public final static byte SEND_REQUEST = 0x0A;
}
