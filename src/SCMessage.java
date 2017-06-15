import Settings.*;

import java.io.*;
import java.nio.*;
import java.nio.charset.*;

/**
 * Created by penguin on 17. 6. 14.
 */
public class SCMessage {
    static int version = 1;
    static Charset charset = Charset.forName("UTF-8");
    static ByteBuffer buffer = ByteBuffer.allocate(SCSettings.datagramSize);

    public SCMessage(){
        buffer.clear();
    }

    public SCMessage(byte[] data){
        buffer = ByteBuffer.allocate(data.length);
        buffer.clear();
        buffer = ByteBuffer.wrap(data);
    }
}
