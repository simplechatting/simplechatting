package DummyClient;

import Settings.*;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;

/**
 * Created by penguin on 17. 6. 15.
 */
public class DummyClient implements Runnable{
    public DummyClientDB model;
    public DummyClientGUI view;

    public static void main(String args[]){
        // model
        DummyClientDB model = new DummyClientDB();

        // controller
        DummyClient controller = new DummyClient();

        // view
        JFrame frame = new JFrame("DummyClient.DummyClientGUI");
        DummyClientGUI view = new DummyClientGUI();
        frame.setContentPane(view.GUI);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        // 연결
        model.view = view;
        model.controller = controller;

        controller.model = model;
        controller.view = view;

        view.model = model;
        view.controler = controller;

    }

    ////////////////// 네트워크 작업 //////////////////
    private SocketChannel channel;
    private boolean nowRunning = false;

    public void startClient(String username) throws IOException{
        // channel 준비
        channel = SocketChannel.open();
        channel.configureBlocking(true);
        channel.connect(new InetSocketAddress(SCSettings.host, SCSettings.port));
        SCPacket packet = new SCPacket(SCPacketType.CREATE_ROOM, username);
        send(packet);
        run();
    }

    public void stopClient() {
        try {
            channel.close();
            nowRunning = false;
            System.out.println("통신 종료");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        nowRunning = true;
        while (nowRunning) {
            try {
                ByteBuffer buffer = ByteBuffer.allocate(SCSettings.datagramSize);
                SCPacket packet = null;

                int i=-1;
                i = channel.read(buffer);
                if(i == -1)
                    throw new IOException();

                try {
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer.array());
                    ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                    packet = (SCPacket)objectInputStream.readObject();
                } catch (ClassNotFoundException e) {
                    return;
                }
                System.out.println(packet.toString());

                // TODO: 옵션 구분 1 메시지 수신

                // TODO: 옵션 구분 2 메시지 전송 요청

                // TODO: 옵션 구분 3

            } catch (IOException e) {
                System.out.println("통신 안됨");
                return;
            }
        }
    }

    public void send(SCPacket packet){
        Thread thread = new Thread(){
            @Override
            public void run(){
                try{
                    ByteArrayOutputStream byteArrayOutputStream =
                            new ByteArrayOutputStream();
                    ObjectOutputStream objectOutputStream =
                            new ObjectOutputStream(byteArrayOutputStream);
                    objectOutputStream.writeObject(packet);
                    objectOutputStream.flush();
                    channel.write(ByteBuffer.wrap(byteArrayOutputStream.toByteArray()));
                }catch (Exception e){
                    System.out.println("통신 안됨");
                }
            }
        };
        thread.run();
    }
}
