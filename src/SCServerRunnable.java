import java.io.*;
import java.net.*;

/**
 * Created by penguin on 17. 6. 6.
 */
public class SCServerRunnable implements Runnable {
    private byte[] msg = new byte[1024];

    @Override
    public void run() {
        DatagramSocket socket;
        try{
            socket = new DatagramSocket(SCServer.port);
            while(true){
                try {
                    DatagramPacket packet = new DatagramPacket(msg, msg.length);
                    socket.receive(packet);
                    String message = new String(packet.getData(), 0, packet.getLength());
                    System.out.println(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }catch (SocketException e){}
    }
}
