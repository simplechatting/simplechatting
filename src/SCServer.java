import com.sun.corba.se.spi.activation.*;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

/**
 * Created by penguin on 17. 6. 6.
 */

public class SCServer implements Runnable{
    Selector selector;

    public SCServer() throws IOException{
        // non block io 생성
        DatagramChannel channel = DatagramChannel.open();
        channel.bind(null);
        channel.configureBlocking(false);


        // 셀렉터 생성
        selector = Selector.open();
        // selector에 등록
        channel.register(selector, SelectionKey.OP_ACCEPT);

    }
    @Override
    public void run() {
        int socketOPs = SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE;

        ByteBuffer buf = null;
        try{
            while(selector.select() > 0){
                Set keys = selector.selectedKeys();
                Iterator iter = keys.iterator();

                while(iter.hasNext()){
                    SelectionKey key = (SelectionKey)iter.next();
                    iter.remove();

                    SelectableChannel channel = key.channel();

                    if(channel instanceof  ServerSocketChannel){
                        // accept
                        ServerSocketChannel serverChannel = (ServerSocketChannel)channel;
                        SocketChannel socketChannel = serverChannel.accept();

                        if(socketChannel == null)
                            continue;

                        // nio
                        socketChannel.configureBlocking(false);

                        socketChannel.register(selector, socketOPs);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}