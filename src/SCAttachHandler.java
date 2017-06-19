import java.io.*;
import java.net.*;
import java.net.UnknownHostException;
import java.rmi.*;
import java.rmi.server.*;

/**
 * Created by penguin on 17. 6. 19.
 */
public class SCAttachHandler {
    private static int packetSize = 1024;

    protected SCAttachHandler() throws RemoteException{
        super();
    }

    public static void sendFile(InetAddress address, int port, File file){
        Thread thread = new Thread(){
            @Override
            public void run(){
                try {
                    DatagramSocket socket = null;
                    socket = new DatagramSocket();

                    DatagramPacket packet = null;
                    byte data[] = new byte[packetSize];

                    String flag = "BOF"; //  파일의 시작
                    socket.send(new DatagramPacket(flag.getBytes(),
                            flag.getBytes().length, address, port));

                    flag = file.getName(); // 경로를 제외한 파일의 이름
                    socket.send(new DatagramPacket(flag.getBytes(),
                            flag.getBytes().length, address, port));

                    FileInputStream fStream = new FileInputStream(file);
                    BufferedInputStream bStream = new BufferedInputStream(fStream);
                    DataInputStream dStream = new DataInputStream(bStream);

                    socket.setSoTimeout(1000);

                    while (true) {
                        int read = -1;
                        if ((read = dStream.read(data, 0, data.length)) < 0)
                            break;
                        packet = new DatagramPacket(data, read, address, port);
                        socket.send(packet);
                    }

                    flag = "EOF"; // 파일의 끝
                    socket.send(new DatagramPacket(flag.getBytes(),
                            flag.getBytes().length, address, port));

                    socket.close();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    public static File receiveFile(InetAddress address, int port, String directory){
        final File[] file = {null};
        Thread thread = new Thread(){
            @Override
            public void run(){
                try{
                    DatagramSocket socket = new DatagramSocket(port);
                    DataOutputStream dStream = null;

                    while(true){
                        DatagramPacket packet = new DatagramPacket(new byte[packetSize], packetSize);
                        socket.receive(packet);
                        String data = new String(packet.getData()).trim();
                        System.out.println("파일 수신 중..");

                        if(data.equals("BOF")){
                            packet = new DatagramPacket(new byte[packetSize], packetSize);
                            socket.receive(packet);
                            data = new String(packet.getData()).trim();
                            /** 사용자 지정 폴더에 저장한다 **/
                            File dir = new File("./" + directory);
                            if(!dir.exists())
                                dir.mkdir();
                            // 파일을 저장한다
                            file[0] = new File("./" + directory + "/"+data); // file name

                            FileOutputStream fStream = new FileOutputStream(file[0]);
                            BufferedOutputStream bStream = new BufferedOutputStream(fStream);
                            dStream = new DataOutputStream(bStream);
                        }else if(data.equals("EOF")){
                            dStream.close();
                            socket.close();

                            break;
                        }else{
                            dStream.write(data.getBytes(), 0, data.getBytes().length);
                        }
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            System.out.println("파일 수신 완료");
            }
        };
        thread.start();
        return file[0];
    }

    public static void main(String args[]) throws UnknownHostException {
        // test
        InetAddress address = InetAddress.getByName("localhost");
        int port = 3256;

        sendFile(address, port, new File("testFile"));
        receiveFile(address, port, "test");

    }
}
