package deprecated.Server;

import Settings.*;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

/**
 * Created by penguin on 17. 6. 15.
 */
public class SCServer implements Runnable{

    public static void main(String args[]) throws IOException{
        SCServer server = new SCServer();
        server.startServer();
        System.out.println("서버 스타트 성공");
        Thread t = new Thread(server);
        t.start();
        System.out.println("서버 런 성공");
    }
    private Selector selector;
    private ServerSocketChannel serverChannel;
    private HashMap<String, SCRoom> rooms = new HashMap<>();
    private HashMap<String, SocketChannel> conns = new HashMap<>();

    public SCServer(){
    }

    public void startServer() throws IOException{
        // selector 생성
        selector = Selector.open();

        // channel 바인드
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(Settings.SCSettings.port));
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    @Override
    public void run() {
        while (true){
            try {
                Thread.sleep(100);
                if(selector.select() > 0) {

                    Iterator<SelectionKey> iter = selector
                            .selectedKeys()
                            .iterator();

                    while (iter.hasNext()) {
                        SelectionKey selected = iter.next();
                        iter.remove();

                        if (!selected.isValid())
                            continue;

                        if (selected.isAcceptable())
                            accept(selected);

                        else if (selected.isReadable())
                            receive(selected);

                        else if (selected.isWritable())
                            ;//; write(key);

                    }
                }
            } catch (IOException e) {
                System.out.println("server stopped");
                if(serverChannel.isOpen())
                    restartServer();
                else
                    System.out.println("IOException occurred.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void restartServer(){
        try{
            System.out.println("restart server");
            serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverChannel.bind(new InetSocketAddress(Settings.SCSettings.port));
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (ClosedChannelException e) {
            System.out.println("fail to restart : ClosedChannelException");
        } catch (IOException e) {
            System.out.println("fail to restart : IOException");
        }
    }

    public void accept(SelectionKey key){
        try {
            serverChannel = (ServerSocketChannel)key.channel();

            SocketChannel channel = null;
            channel = serverChannel.accept();
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ);

            System.out.println("연결됨");
        } catch (IOException e) {
            System.out.println("");
        }
    }

    public void receive(SelectionKey key) throws IOException{
        SocketChannel channel = (SocketChannel)key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(Settings.SCSettings.datagramSize);
        Settings.SCPacket packet = null;

        // 전송 확인
        try {
            int read = -1;
            if((read = channel.read(buffer)) == -1){
                // 사용자의 정상적 종료
                channel.close();
                key.cancel();
                return;
            }
        }catch (IOException e){
            e.printStackTrace();
            try{
                channel.close();
            }catch (IOException e2){
                e.printStackTrace();
            }
            return;
        }

        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer.array());
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            packet = (Settings.SCPacket)objectInputStream.readObject();
            byteArrayInputStream.close();
            objectInputStream.close();
        } catch (ClassNotFoundException e) {
            channel.close();
            key.cancel();
            return;
        }
        System.out.println(packet.toString());

        switch (packet.getProtocol()){
            case Settings.SCPacketType.LOGIN: {        // 로그인
                String userName = packet.getUser();
                conns.put(userName, (SocketChannel)key.channel());
                System.out.printf("[사용자]%s가 로그인\n", packet.getUser());

            }break;
            case Settings.SCPacketType.CREATE_ROOM: { //  방 생성
                String roomName = packet.getRoomName();
                if(rooms.get(roomName) != null) {
                    System.out.printf("[사용자]%s가 존재하는 [방]을 만드려고 시도\n", packet.getUser());
                    break; //  이미 방이 존재
                }else{
                    SCRoom room = new SCRoom();
                    rooms.put(roomName, room);
                    room.roomName = roomName;
                    System.out.printf("[사용자]%s가 [방]%s를 만듦\n", packet.getUser(), packet.getRoomName());

                    broadcast(new Settings.SCPacket(Settings.SCPacketType.BROADCAST_ROOMS, "[SERVER]", roomName));
                }
            }break;
            case Settings.SCPacketType.ENTER_ROOM: {  // 방 입장
                SCRoom room = rooms.get(packet.getRoomName());
                String user = packet.getUser();

                room.userReads.put(user, room.currentMin); // 읽은 줄
                InetAddress address = channel.socket().getInetAddress();
                int port = channel.socket().getPort();
                InetSocketAddress socketAddress = new InetSocketAddress(address, port);
                room.members.put(user, socketAddress); // 유저 연결
                System.out.printf("[사용자]%s가 [방]%s에 입장\n", packet.getUser(), packet.getRoomName());
            }break;
            case Settings.SCPacketType.LEAVE_ROOM: {  // 방 탈퇴
                SCRoom room = rooms.get(packet.getRoomName());
                String user = packet.getUser();
                room.userReads.remove(user);
                room.members.remove(user);
                System.out.printf("[사용자] %s가 [방]%s에서 탈퇴\n", packet.getUser(), packet.getRoomName());
            }break;
        }
    }

    public void broadcast(Settings.SCPacket packet){
        Thread thread = new Thread(){
                @Override
                public void run() {
                    try{
                        for(SocketChannel channel : conns.values()) {
                            ByteArrayOutputStream byteArrayOutputStream =
                                    new ByteArrayOutputStream();
                            ObjectOutputStream objectOutputStream =
                                    new ObjectOutputStream(byteArrayOutputStream);
                            objectOutputStream.writeObject(packet);
                            objectOutputStream.flush();
                            channel.write(ByteBuffer.wrap(byteArrayOutputStream.toByteArray()));
                        }
                    }catch (Exception e){
                        System.out.println("통신 안됨");
                    }
                }
        };
        thread.run();
    }

    public void write(SocketChannel channel, Settings.SCPacket packet){
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

    public void send(SelectionKey key) throws IOException{
        /*
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
*/
    }

    public void registerUser(String username){

    }
}
