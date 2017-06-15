import java.io.*;
import java.nio.charset.*;

/**
 * Created by penguin on 17. 6. 14.
 */
public class SCMessage {
    static int version = 1;
    static Charset charset = Charset.forName("UTF-8");

    public int userid;
    public int groudid;
    public int msgType;
    public int line;
    public String message;

    public SCMessage(){}
    public SCMessage(int uid, int gid, int mType, String msg){
        userid=uid;
        groudid=gid;
        msgType=mType;
        message=msg;
    }
    public SCMessage(int uid, int gid, int mType, int l, String msg){
        userid=uid;
        groudid=gid;
        msgType=mType;
        line=l;
        message=msg;
    }
}
