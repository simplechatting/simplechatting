/**
 * Created by penguin on 17. 6. 18.
 */

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class SCClient {

    private Selector selector = null;
    private SocketChannel sc = null;
    private String username;
    private int currentLine;
    private boolean fileTransferReady = false;

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
        this.username = username;
        write(new SCPacket(SCPacketType.LOGIN, username));
    }

    public void sendMsg(String msg){
        write(new SCPacket(SCPacketType.MSG_SEND, username, msg));
    }

    public void logout(){}

    public void uploadFile(File file){
        view.addMsg(new SCPacket(SCPacketType.MSG_ORDERED, username, "파일을 업로드합니다 : " + file.getName()));
        write(new SCPacket(SCPacketType.UPLOAD_FILE, username));
        Thread uploadSequence = new Thread(){
            @Override
            public void run(){
                for(int i=0; !fileTransferReady; i++){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    view.addMsg(new SCPacket(SCPacketType.MSG_ORDERED, "SERVER", "업로드 준비중 ... " + i + "초"));
                    if(i>3) {
                        view.addMsg(new SCPacket(SCPacketType.MSG_ORDERED, "SERVER", "지금은 업로드를 할 수 없습니다"));
                        return;
                    }
                }
                view.addMsg(new SCPacket(SCPacketType.MSG_ORDERED, "SERVER", "업로드 준비완료"));
                try {
                    SCAttachHandler.sendFile(InetAddress.getByName(SCSettings.host), SCSettings.file_port, file);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                fileTransferReady = false;
            }
        };
        uploadSequence.start();
    }

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
            packet = SCPacket.serialize(buffer);
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
                break;
            case SCPacketType.UPLOAD_READY:
                fileTransferReady = true;
                break;
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
            sc.write(SCPacket.deserialize(packet));
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