package deprecated;

import java.io.*;
import java.nio.*;

/**
 * Created by penguin on 17. 6. 14.
 */
public class SCPacket implements Serializable{
    private static final long serialVersionUID = 7526471155622776147L;
    private byte protocol;
    private String user;
    private String message;
    private int line=-1;

    public static SCPacket readStream(ByteBuffer buffer) throws IOException, ClassNotFoundException {
        buffer.flip(); // direct buffer 의 경우
        byte[] buf = new byte[buffer.remaining()];
        buffer.get(buf);
        return SCPacket.readStream(buf);
    }

    public static SCPacket readStream(byte[] buf_array) throws IOException, ClassNotFoundException {
        ByteArrayInputStream baStream = new ByteArrayInputStream(buf_array);
        ObjectInputStream objStream = new ObjectInputStream(baStream);
        SCPacket packet = (SCPacket)objStream.readObject();
        baStream.close();
        objStream.close();
        return packet;
    }

    public static ByteBuffer writeStream(SCPacket packet) throws IOException {
        ByteArrayOutputStream baStream = new ByteArrayOutputStream();
        ObjectOutputStream objStream = new ObjectOutputStream(baStream);
        objStream.writeObject(packet);
        objStream.flush();
        return ByteBuffer.wrap(baStream.toByteArray());
    }

    public SCPacket(byte protocol, String user, String message, int line) {
        this.protocol = protocol;
        this.user = user;
        this.message = message;
        this.line = line;
    }

    public SCPacket(byte protocol, String user, String message){
        this.protocol = protocol;
        this.user = user;
        this.message = message;
    }

    public SCPacket(byte protocol, String user) {
        this.protocol = protocol;
        this.user = user;
    }


    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }



    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }



    public byte getProtocol() {
        return protocol;
    }

    public void setProtocol(byte protocol) {
        this.protocol = protocol;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String toString(){
        return String.format("SCPacket [protocol=%d, user=%s, message=%s, line=%d]", protocol, user, message, line);}
}

