import java.io.*;
import java.net.*;

/**
 * Created by penguin on 17. 6. 7.
 */
public class DummyMsgSender {
    public static void main(String args[]){
        try{
            int port = SCServer.port;
            InetAddress address = InetAddress.getByName("localhost");
            DatagramSocket socket = new DatagramSocket(port);
            while(true) {
                String message =
                    /*UserID  */ "00000000" + /* = 8 length num */
                    /*GroupdID*/ "00000000" + /* = 8 length num*/
                    /*message */ "this is message";
                byte[] msg = message.getBytes();
                DatagramPacket packet = new DatagramPacket(msg, msg.length, address, port);
                socket.send(packet);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
