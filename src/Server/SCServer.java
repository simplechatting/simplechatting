package Server;

import DummyRoom.*;
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
        server.run();
        System.out.println("서버 런 성공");
    }
    private Selector selector;
    private ServerSocketChannel serverChannel;
    private List<SCServerClient> connections = new Vector<>();
    private HashMap<Integer, DummyRoom> scRooms = new HashMap<>();

    public SCServer(){
    }

    public void startServer() throws IOException{
        // selector 생성
        selector = Selector.open();

        // channel 바인드
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(SCSettings.port));
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    @Override
    public void run() {
        int i=0;
        while (true){
            try {
                if(i++ > 5000) {
                    System.out.println("서버 작동 중");
                    i = 0;
                }
                if(selector.select() == 0)
                    continue;
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iter = keys.iterator();

                while(iter.hasNext()){
                    SelectionKey key = iter.next();
                    iter.remove();

                    if(!key.isValid()) {
                        continue;
                    }

                    if(key.isAcceptable()){
                        accept(key);
                    }

                    else if(key.isReadable()){
                        receive(key);
                    }

                    else if(key.isWritable()){
                        send(key);
                    }
                }
            } catch (IOException e) {
                System.out.println("server stopped");
                if(serverChannel.isOpen())
                    restartServer();
                else
                    System.out.println("IOException occurred.");
            }
        }
    }

    public void restartServer(){
        try{
            System.out.println("restart server");
            serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverChannel.bind(new InetSocketAddress(SCSettings.port));
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (ClosedChannelException e) {
            System.out.println("fail to restart : ClosedChannelException");
        } catch (IOException e) {
            System.out.println("fail to restart : IOException");
        }
    }

    public void accept(SelectionKey key){
        serverChannel = (ServerSocketChannel)key.channel();
        SocketChannel channel = null;

        try {
            channel = serverChannel.accept();
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ);

            SCServerClient client = new SCServerClient(channel);
            connections.add(client);

            System.out.println("연결됨");
        } catch (IOException e) {
            System.out.println("");
        }
    }

    public void receive(SelectionKey key) throws IOException{
        SocketChannel channel = (SocketChannel)key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(SCSettings.datagramSize);
        SCPacket packet = null;

        // 전송 확인
        try {
            int read = -1;
            if((read = channel.read(buffer)) == -1){
                channel.close();
                key.cancel();
                return;
            }
        }catch (IOException e){
            connections.remove(channel);
            try{
                channel.close();
            }catch (IOException e2){}
            return;
        }

        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer.array());
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            packet = (SCPacket)objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            channel.close();
            key.cancel();
            return;
        }
        System.out.println(packet.toString());

        // CASE 분류


        selector.wakeup();
    }

    public void send(SelectionKey key) throws IOException{

    }
}
