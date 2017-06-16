import Settings.*;

import java.io.*;
import java.net.*;

/**
 * Created by penguin on 17. 6. 7.
 */
public class DummyMsgSender {
    public static void main(String args[]){
        STARTMAIN();
    }

    public static void STARTMAIN(){
        try{
            InetAddress address = InetAddress.getByName("localhost");
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket packet;
            while(true) {
                String message =
                    /*UserID  */ "00000000" + /* = 8 length num */
                    /*GroupdID*/ "00000000" + /* = 8 length num*/
                    /*message */ "this is message";
                byte[] msg = message.getBytes();
                packet = new DatagramPacket(msg, msg.length, address, SCSettings.port);
                socket.send(packet);
                System.out.println(message);
                Thread.sleep(1000);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
