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
    private List<SocketChannel> connections = new Vector<>();
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

                    if(key.isAcceptable()){
                        accept(key);
                    }else if(key.isReadable()){

                    }else if(key.isWritable()){

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

    public void accept(SelectionKey key) throws IOException{
        serverChannel = (ServerSocketChannel)key.channel();
        SocketChannel socketChannel = serverChannel.accept();
        connections.add(socketChannel);
        System.out.println("연결됨");
    }

    public void receive(SelectionKey key) throws IOException{
        SocketChannel channel = (SocketChannel)key.attachment();

        ByteBuffer buffer = ByteBuffer.allocate(SCSettings.datagramSize);
        if(channel.read(buffer) == -1)
            return;

        buffer.flip();
        String data = SCSettings.charset.decode(buffer).toString();

        for(SocketChannel channel1 : connections){
            SelectionKey key1 = channel1.keyFor(selector);
            key1.interestOps(SelectionKey.OP_WRITE);
        }
        selector.wakeup();
    }
}
