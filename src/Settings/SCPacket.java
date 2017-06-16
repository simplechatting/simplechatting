package Settings;

import java.io.*;

/**
 * Created by penguin on 17. 6. 14.
 */
public class SCPacket implements Serializable{
    private static final long serialVersionUID = 7526471155622776147L;
    private byte protocol;
    private String message;

    public SCPacket(byte protocol, String message) {
        this.protocol = protocol;
        this.message = message;
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
        return String.format("SCPacket [protocol=%d, message=%s]", protocol, message);}
}

