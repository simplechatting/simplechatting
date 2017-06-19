package deprecated; /**
 * Created by penguin on 17. 6. 18.
 */

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class SCClient {

    private Selector selector = null;
    private SocketChannel sc = null;
    private String uesrname;
    private int currentLine;
    public GUI view;

    public SCClient() {}

    public void startServer() throws IOException{

        selector = Selector.open();

        sc = SocketChannel.open(new InetSocketAddress(SCSettings.host, SCSettings.port));
        sc.configureBlocking(false);

        // TODO: ACCEPT 안되는 원인 찾기
        sc.register(selector, SelectionKey.OP_READ);

        startReceive();
    }

    public void login(String username){
        this.uesrname = username;
        write(new SCPacket(SCPacketType.LOGIN, username));
    }

    public void sendMsg(String msg){
        write(new SCPacket(SCPacketType.MSG_SEND, uesrname, msg));
    }

    public void logout(){}

    private void startReceive() {
        Thread reader = new Thread(){
            @Override
            public void run(){
                try {
                    while (true) {
                        // System.out.println("작동 중");
                        selector.select(); // 취소 되지 않은 키들을 불러옴
                        Iterator iter = selector.selectedKeys().iterator();
                        while (iter.hasNext()) {
                            SelectionKey key = (SelectionKey) iter.next();
                            if (key.isReadable()) {
                                read(key);
                            }
                            iter.remove();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        reader.start();
    }

    private void read(SelectionKey key) {
        SocketChannel sc = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
        int read = -1;
        try {
            if((read = sc.read(buffer)) < 0){
                System.out.println("서버가 정상적으로 종료되었습니다");
                sc.close();
                return;
            }
        } catch (IOException e) {
            try {
                sc.close();
            } catch (IOException e1) {
            }
        }
        SCPacket packet = null;
        try {
            packet = SCPacket.readStream(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("Message - " + packet);

        clearBuffer(buffer);

        switch (packet.getProtocol()){
            case SCPacketType.MSG_ORDERED:
                view.addMsg(packet);
        }
    }

    private void clearBuffer(ByteBuffer buffer) {

        if (buffer != null) {
            buffer.clear();
            buffer = null;
        }
    }

    private void write(SCPacket packet){
        try{
            sc.write(SCPacket.writeStream(packet));
        }catch (Exception e){
            System.out.println("통신 안됨");
        }
        selector.wakeup();
    }

    ///////////////////////////// Main ////////////////////////////
    public static void main(String[] args) throws IOException{
        SCClient client = new SCClient();
        client.startServer();
        JFrame frame = new JFrame("chat");
        GUI view = new GUI(client);
        client.view = view;
        frame.setContentPane(view.panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}