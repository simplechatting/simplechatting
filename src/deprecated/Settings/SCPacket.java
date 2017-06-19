package deprecated.Settings;

import java.io.*;

/**
 * Created by penguin on 17. 6. 14.
 */
public class SCPacket implements Serializable{
    private static final long serialVersionUID = 7526471155622776147L;
    private byte protocol;
    private String user;
    private String roomName;
    private String message;
    private int line;

    public SCPacket(byte protocol, String user, String roomName, String message, int line) {
        this.protocol = protocol;
        this.user = user;
        this.roomName = roomName;
        this.message = message;
        this.line = line;
    }

    public SCPacket(byte protocol, String user, String roomName) {
        this.protocol = protocol;
        this.user = user;
        this.roomName = roomName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
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
        return String.format("SCPacket [protocol=%d, message=%s]", protocol, message);}
}

