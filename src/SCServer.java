/**
 * Created by penguin on 17. 6. 18.
 */

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class SCServer {

    private Selector selector = null;
    private ServerSocketChannel serverChannel = null;
    private ServerSocket serverSocket = null;

    private Map<String, SocketChannel> users =
            Collections.synchronizedMap(new HashMap<String, SocketChannel>());
    private int currentLine = 0;

    public void initServer() {

        try {
            // 셀렉터 생성
            selector = Selector.open();

            // channel 바인드
            serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverSocket = serverChannel.socket();
            serverSocket.bind(new InetSocketAddress(SCSettings.host, SCSettings.port));
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startServer() {
        Thread thread = new Thread(){
            @Override
            public void run(){
                try {
                    while (true) {
                        selector.select(); // 취소 되지 않은 키들을 불러옴
                        Iterator iter = selector.selectedKeys().iterator();
                        while (iter.hasNext()) { // 키 읽음
                            SelectionKey key = (SelectionKey) iter.next();
                            if (key.isAcceptable()) {// 클라이언트 접속
                                accept(key);
                            } else if (key.isReadable()) { // 메시지 수신
                                SCPacket packet = read(key);
                                if(packet != null) // protocol 에 따라서 처리
                                    processPacket(key, packet);
                            }
                            iter.remove();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        System.out.println("서버 시작");

    }

    private void accept(SelectionKey key) {
        try {
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel channel = null;

            channel = server.accept();
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ);
            System.out.printf("새 클라이언트 연결됨 : ");
            System.out.println(channel.socket().getInetAddress());
        } catch (ClosedChannelException e) {
            System.out.println("클라이언트 접속 실패");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private SCPacket read(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
        try {
            int read = -1; // 연결 확인
            if((read = channel.read(buffer)) < 0){
                System.out.println("클라이언트가 정상적으로 접속을 종료");
                channel.close();

                users.values().remove(channel);
                return null;
            }else
                System.out.printf("클라이언트 >>> 서버 : %d비트\n", read);
        } catch (IOException e) {
            try {
                channel.close();
            } catch (IOException e1) {}
            users.values().remove(channel);
            System.out.println(" 클라언트와의 연결의 해제됨");
        }
        SCPacket packet = null;
        try {
            packet = SCPacket.serialize(buffer);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        clearBuffer(buffer);
        return packet;
    }

    private void processPacket(SelectionKey key, SCPacket packet) throws IOException {
        switch (packet.getProtocol()){
            case SCPacketType.LOGIN:
                users.put(packet.getUser(), (SocketChannel) key.channel());
                broadcast(new SCPacket(SCPacketType.MSG_ORDERED, "SERVER", "새로운 클라이언트 : " + packet.getUser(), currentLine++));
                break;
            case SCPacketType.LOGOUT:
                users.remove(packet.getUser());
                broadcast(new SCPacket(SCPacketType.MSG_ORDERED, "SERVER", "연결 종료 : " + packet.getUser(), currentLine++));
                break;
            case SCPacketType.MSG_SEND:
                broadcast(new SCPacket(SCPacketType.MSG_ORDERED, packet.getUser(), packet.getMessage(), currentLine++));
                break;
            case SCPacketType.UPLOAD_FILE:
                // 서버로 보내지는 파일임
                write(packet.getUser(), new SCPacket(SCPacketType.UPLOAD_READY, SCSettings.host));
                SCAttachHandler.receiveFile(InetAddress.getByName(SCSettings.host), SCSettings.file_port, "SERVER");

        }
    }

    private void broadcast(SCPacket packet) throws IOException {

        ByteBuffer buffer = SCPacket.deserialize(packet);

        for(String user : users.keySet()) {
            SocketChannel sc = (SocketChannel) users.get(user);
            if (sc != null) {
                sc.write(buffer);
                buffer.rewind();
            }
        }
    }

    private void write(String user, SCPacket packet) throws IOException {
        SocketChannel channel = users.get(user);
        ByteBuffer buffer = SCPacket.deserialize(packet);

        channel.write(buffer);
    }

    private void clearBuffer(ByteBuffer buffer) {
        if (buffer != null) {
            buffer.clear();
            buffer = null;
        }
    }

    ///////////////////////////// Main ////////////////////////////

    public static void main(String[] args) {
        SCServer scs = new SCServer();
        scs.initServer();
        scs.startServer();
    }

}
