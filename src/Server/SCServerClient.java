package Server;

import java.nio.channels.*;

/**
 * Created by penguin on 17. 6. 15.
 */
public class SCServerClient {
    SocketChannel channel;
    String sendMessage;
    public SCServerClient(SocketChannel channel){
        this.channel = channel;
    }


}
