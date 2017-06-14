import java.nio.charset.*;

/**
 * Created by penguin on 17. 6. 14.
 */
public interface SCMsg {
    static int version = 1;
    static Charset charset = Charset.forName("UTF-8");

    SCMsg getMessage(byte bytes[]);

    byte[] putMessage(SCMsg message);
}
