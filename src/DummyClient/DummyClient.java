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
public class DummyClient implements Runnable {
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

    public void startClient(String username) throws IOException{
        // channel 준비
        channel = SocketChannel.open();
        channel.configureBlocking(true);
        channel.connect(new InetSocketAddress(SCSettings.host, SCSettings.port));
        send("00" + username);
    }

    @Override
    public void run() {
        while(true){
            try{
                ByteBuffer buffer = ByteBuffer.allocate(SCSettings.datagramSize);

                if(channel.read(buffer) == -1)
                    continue;

                buffer.flip();
                String data = SCSettings.charset.decode(buffer).toString();

                // TODO: 옵션 구분 1 메시지 수신

                // TODO: 옵션 구분 2 메시지 전송 요청

                // TODO: 옵션 구분 3

            }catch (IOException e){
                System.out.println("통신 안됨");
            }
        }
    }

    public void send(String data){
        Thread thread = new Thread(){
            @Override
            public void run(){
                try{
                    ByteBuffer buffer = SCSettings.charset.encode(data);
                    channel.write(buffer);
                }catch (Exception e){
                    System.out.println("통신 안됨");
                }
            }
        };
    }
}
