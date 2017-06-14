import java.nio.charset.*;

/**
 * Created by penguin on 17. 6. 14.
 */
public class SCMsg1 implements SCMsg {
    static int version = 1;
    static Charset charset = Charset.forName("UTF-8");

    /* 공통 부분 */

    @Override
    public SCMsg getMessage(byte[] bytes) {
        String data = new String(bytes, charset);
        int uid = Integer.parseInt(data.substring(0,8));
        int gid = Integer.parseInt(data.substring(8,16));
        int mtype = Integer.parseInt(data.substring(16,18));
        String msg = data.substring(18);
        
        return new SCMsg1(uid, gid, mtype, msg);
    }

    @Override
    public byte[] putMessage(SCMsg message) {
        String data = String.format("%08d%08d%02d%s",
                ((SCMsg1)message).userid,
                ((SCMsg1)message).groudid,
                ((SCMsg1)message).msgType,
                ((SCMsg1)message).message);
        return data.getBytes(charset);
    }

    /* 확장 */

    public int userid;
    public int groudid;
    public int msgType;
    public int line;
    public String message;

    public SCMsg1(){}
    public SCMsg1(int uid, int gid, int mType, String msg){
        userid=uid;
        groudid=gid;
        msgType=mType;
        message=msg;
    }

    public SCMsg1(int uid, int gid, int mType, int l, String msg){
        userid=uid;
        groudid=gid;
        msgType=mType;
        line=l;
        message=msg;
    }
}
