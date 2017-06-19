package deprecated;

import java.io.*;
import java.net.*;

/**
 * Created by penguin on 17. 6. 19.
 */
public class SCClientAttachHandler {
    public void sendFile(InetAddress address, int port, File file) throws IOException {
        DataInputStream dStream = null;
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket packet = null;
        byte data[] = new byte[1024];

        while(true){
            int read = -1;
            if((read = dStream.read(data, 0, data.length)) < 0)
                break;

        }

    }
}
